/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.classes;

/**
 *
 * @author payto
 */

// Operaciones de entrada y salida a disco
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
    private int cylinderIndex; // Cilindro destino para simulación de planificación

    private FileSystemNode nodeToDelete; // Para DELETE
    private FileSystemNode nodeToUpdate; // Para UPDATE
    private String newName; 

    public IoRequest(Process requestingProcess, OperationType type, String path, int blockCount, int cylinderIndex) {
        this.requestingProcess = requestingProcess;
        this.type = type;
        this.path = path;
        this.blockCount = blockCount;
        this.cylinderIndex = cylinderIndex;
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

    public int getCylinderIndex() {
        return cylinderIndex;
    }

    public void setNodeToDelete(FileSystemNode node) {
        this.nodeToDelete = node;
    }

    public FileSystemNode getNodeToDelete() {
        return nodeToDelete;
    }

    // Getters y setters para UPDATE
    public void setNodeToUpdate(FileSystemNode node) {
        this.nodeToUpdate = node;
    }

    public FileSystemNode getNodeToUpdate() {
        return nodeToUpdate;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getNewName() {
        return newName;
    }
}
