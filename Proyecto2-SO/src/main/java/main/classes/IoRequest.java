/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.classes;

/**
 *
 * @author payto
 */

//Operaciones de entrada y salida a disco
public class IoRequest {

    public enum OperationType {
        CREATE_FILE,
        CREATE_DIRECTORY,
        READ_FILE,
        UPDATE_FILE,
        DELETE
    }

    private Process requestingProcess; 
    private OperationType type;   
    private String path;          
    private int blockCount;       


    public IoRequest(Process requestingProcess, OperationType type, String path, int blockCount) {
        this.requestingProcess = requestingProcess;
        this.type = type;
        this.path = path;
        this.blockCount = blockCount;
    }


    public Process getRequestingProcess() {
        return requestingProcess;
    }

    public OperationType getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public int getBlockCount() {
        return blockCount;
    }
}
