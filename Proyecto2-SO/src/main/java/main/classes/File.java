/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.classes;

/**
 *
 * @author ile1
 */
public class File extends FileSystemNode {

    private int startingBlock;
    private int blockCount;


    private Process ownerProcess;

    public File(String name, int startingBlock, int blockCount, Process ownerProcess) {
        super(name);
        
        this.startingBlock = startingBlock;
        this.blockCount = blockCount;
        this.ownerProcess = ownerProcess;
    }
    
    public int getStartingBlock() {
        return startingBlock;
    }

    public int getBlockCount() {
        return blockCount;
    }

    public Process getOwnerProcess() {
        return ownerProcess;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }
}
