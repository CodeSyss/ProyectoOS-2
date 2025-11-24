/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.classes;

import helpers.CustomQueue;

/**
 *
 * @author payto
 */
public class Scheduler {
    private CustomQueue<IoRequest> queue;
    private Disk disk;
    private Simulator controller;

    private String currentAlgorithm = "FIFO";
    private boolean scanDirectionUp = true;

    public Scheduler(CustomQueue<IoRequest> queue, Disk disk, Simulator controller) {
        this.queue = queue;
        this.disk = disk;
        this.controller = controller;
    }

    public void setAlgorithm(String algorithm) {
        this.currentAlgorithm = algorithm;
    }

    public boolean procesarSiguienteSolicitud() {
        if (queue.isEmpty()) {
            return false;
        }

        IoRequest requestToProcess = null;

        // Selección de la solicitud según el algoritmo
        if (currentAlgorithm.equals("FIFO")) {
            requestToProcess = queue.dequeue();
        } else {
            // Para algoritmos que requieren reordenamiento (SSTF, SCAN, C-SCAN)
            requestToProcess = selectRequestByAlgorithm();
            if (requestToProcess != null) {
                queue.remove(requestToProcess);
            }
        }

        if (requestToProcess == null) {
            return false;
        }

        // Procesar la solicitud seleccionada
        Process process = requestToProcess.getRequestingProcess();
        process.setState(Process.ProcessState.BLOCKED);

        // Actualizar la posición del cabezal al cilindro destino (simulado)
        disk.setHeadPosition(requestToProcess.getCylinderIndex());

        int bloqueInicial = -1;
        if (requestToProcess.getType() == IoRequest.OperationType.CREATE_FILE) {
            bloqueInicial = disk.assignBlocks(process, requestToProcess.getBlockCount());

        } else if (requestToProcess.getType() == IoRequest.OperationType.CREATE_DIRECTORY) {
            bloqueInicial = disk.assignBlocks(process, 1);
        }

        if (bloqueInicial != -1) {
            controller.onCreationSuccess(
                    process,
                    requestToProcess.getPath(),
                    requestToProcess.getType(),
                    bloqueInicial,
                    requestToProcess.getBlockCount());

        } else {
            process.setState(Process.ProcessState.FINISHED); // O un estado de ERROR
        }

        return true;
    }

    private IoRequest selectRequestByAlgorithm() {
        if (queue.isEmpty())
            return null;

        int currentHead = disk.getHeadPosition();
        IoRequest bestRequest = null;

        if (currentAlgorithm.equals("SSTF")) {
            // Shortest Service Time First: El más cercano al cabezal actual
            int minDistance = Integer.MAX_VALUE;

            for (IoRequest req : queue.iterable()) {
                int distance = Math.abs(req.getCylinderIndex() - currentHead);
                if (distance < minDistance) {
                    minDistance = distance;
                    bestRequest = req;
                }
            }

        } else if (currentAlgorithm.equals("SCAN")) {
            bestRequest = findClosestInDirection(currentHead, scanDirectionUp);

            if (bestRequest == null) {
                scanDirectionUp = !scanDirectionUp;
                bestRequest = findClosestInDirection(currentHead, scanDirectionUp);
            }

        } else if (currentAlgorithm.equals("C-SCAN")) {
            bestRequest = findClosestInDirection(currentHead, true);

            if (bestRequest == null) {
                bestRequest = findLowestCylinderRequest();
            }
        }

        if (bestRequest == null && !queue.isEmpty()) {
            bestRequest = queue.peek();
        }

        return bestRequest;
    }

    private IoRequest findClosestInDirection(int currentHead, boolean up) {
        IoRequest bestReq = null;
        int minDistance = Integer.MAX_VALUE;

        for (IoRequest req : queue.iterable()) {
            int target = req.getCylinderIndex();
            if (up) {
                if (target >= currentHead) {
                    int dist = target - currentHead;
                    if (dist < minDistance) {
                        minDistance = dist;
                        bestReq = req;
                    }
                }
            } else { // down
                if (target <= currentHead) {
                    int dist = currentHead - target;
                    if (dist < minDistance) {
                        minDistance = dist;
                        bestReq = req;
                    }
                }
            }
        }
        return bestReq;
    }

    private IoRequest findLowestCylinderRequest() {
        IoRequest lowest = null;
        int minCyl = Integer.MAX_VALUE;
        for (IoRequest req : queue.iterable()) {
            if (req.getCylinderIndex() < minCyl) {
                minCyl = req.getCylinderIndex();
                lowest = req;
            }
        }
        return lowest;
    }
}