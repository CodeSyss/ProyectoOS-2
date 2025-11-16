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

    public Scheduler(CustomQueue<IoRequest> queue, Disk disk, Simulator controller) {
        this.queue = queue;
        this.disk = disk;
        this.controller = controller;
    }

    public boolean procesarSiguienteSolicitud() {
        if (queue.isEmpty()) {
            return false; 
        }

        IoRequest request = queue.dequeue();
        
        Process process = request.getRequestingProcess();
        process.setState(Process.ProcessState.BLOCKED);
        
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
                request.getBlockCount()
            );

        } else {
            process.setState(Process.ProcessState.FINISHED); // O un estado de ERROR
        }
        
        return true; 
    }
}