/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.classes;

/**
 *
 * @author fabys
 */

public class BufferBlock {
    private final int diskBlockNumber;
    private byte[] data;
    private boolean dirty;
    private long lastAccessTime;
    private int accessCount;
    private final long insertionTime;
    
    public BufferBlock(int diskBlockNumber, byte[] data) {
        this.diskBlockNumber = diskBlockNumber;
        this.data = data.clone(); // Copia defensiva
        this.dirty = false;
        this.lastAccessTime = System.currentTimeMillis();
        this.accessCount = 1;
        this.insertionTime = System.currentTimeMillis();
    }
    
    // Getters
    public int getDiskBlockNumber() { return diskBlockNumber; }
    public byte[] getData() { return data.clone(); } // Siempre devolver copia
    public boolean isDirty() { return dirty; }
    public long getLastAccessTime() { return lastAccessTime; }
    public int getAccessCount() { return accessCount; }
    public long getInsertionTime() { return insertionTime; }
    
    // Setters
    public void setData(byte[] newData) {
        this.data = newData.clone();
        this.dirty = true;
    }
    
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
    
    public void updateAccessTime() {
        this.lastAccessTime = System.currentTimeMillis();
        this.accessCount++;
    }
    
    @Override
    public String toString() {
        return String.format("BufferBlock[disk=%d, dirty=%s, accesses=%d]", 
                           diskBlockNumber, dirty, accessCount);
    }
}