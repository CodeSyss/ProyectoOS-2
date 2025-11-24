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
    private boolean scanDirectionUp = true; // true = subiendo (hacia bloques mayores), false = bajando

    private IoRequest currentRequest = null;
    private int remainingTime = 0;

    public Scheduler(CustomQueue<IoRequest> queue, Disk disk, Simulator controller) {
        this.queue = queue;
        this.disk = disk;
        this.controller = controller;
    }

    public void setAlgorithm(String algorithm) {
        this.currentAlgorithm = algorithm;
    }

    public boolean procesarSiguienteSolicitud() {
        // 1. Si ya estamos procesando una solicitud, continuar con ella
        if (currentRequest != null) {
            remainingTime--;
            if (remainingTime > 0) {
                return true; // Seguimos procesando, no hay cambios visuales mayores pero el tiempo avanza
            } else {
                // Terminó el tiempo de simulación (Seek/Transfer)
                finalizeRequest(currentRequest);
                currentRequest = null;
                return true; // Hubo un cambio (finalizó)
            }
        }

        // 2. Si no hay solicitud en curso, buscar la siguiente en la cola
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

        // 3. Iniciar el procesamiento de la nueva solicitud
        currentRequest = requestToProcess;
        remainingTime = 3; // Simular 3 ticks de tiempo (ej. 6 segundos)

        Process process = currentRequest.getRequestingProcess();
        process.setState(Process.ProcessState.RUNNING); // "Ejecutando" la operación de E/S

        // Actualizar la posición del cabezal al cilindro destino (simulado)
        disk.setHeadPosition(currentRequest.getCylinderIndex());

        return true;
    }

    private void finalizeRequest(IoRequest request) {
        Process process = request.getRequestingProcess();
        int bloqueInicial = -1;

        if (request.getType() == IoRequest.OperationType.CREATE_FILE) {
            bloqueInicial = disk.assignBlocks(process, request.getBlockCount());

        } else if (request.getType() == IoRequest.OperationType.CREATE_DIRECTORY) {
            bloqueInicial = disk.assignBlocks(process, 1);
        }

        if (bloqueInicial != -1) {
            controller.onCreationSuccess(
                    process,
                    request.getPath(),
                    request.getType(),
                    bloqueInicial,
                    request.getBlockCount());

        } else {
            process.setState(Process.ProcessState.FINISHED); // O un estado de ERROR
        }
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
            // SCAN (Elevator): Moverse en una dirección hasta el final, luego rebotar
            bestRequest = findClosestInDirection(currentHead, scanDirectionUp);

            // Si no hay nada en la dirección actual, invertir y buscar de nuevo
            if (bestRequest == null) {
                scanDirectionUp = !scanDirectionUp;
                bestRequest = findClosestInDirection(currentHead, scanDirectionUp);
            }

        } else if (currentAlgorithm.equals("C-SCAN")) {
            // C-SCAN: Moverse siempre en una dirección (subiendo). Al llegar al final,
            // volver al 0.
            // Aquí asumimos siempre dirección UP.
            bestRequest = findClosestInDirection(currentHead, true);

            // Si no hay nada "arriba", buscar el más bajo de todos (wrap around)
            if (bestRequest == null) {
                bestRequest = findLowestCylinderRequest();
            }
        }

        // Fallback por si acaso (ej. algoritmo desconocido), usar FIFO logic (primer
        // elemento)
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