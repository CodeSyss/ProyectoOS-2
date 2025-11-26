/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.classes;

/**
 *
 * @author fabys
 */

public class BufferMetrics {
    private int totalReads = 0;
    private int bufferHits = 0;
    private int bufferMisses = 0;
    private int totalWrites = 0;
    private int dirtyBlocksWritten = 0;
    
    public void recordRead() { totalReads++; }
    public void recordHit() { bufferHits++; }
    public void recordMiss() { bufferMisses++; }
    public void recordWrite() { totalWrites++; }
    public void recordDirtyWrite() { dirtyBlocksWritten++; }
    
    public double getHitRatio() {
        return totalReads == 0 ? 0.0 : (double) bufferHits / totalReads;
    }
    
    public void reset() {
        totalReads = 0;
        bufferHits = 0;
        bufferMisses = 0;
        totalWrites = 0;
        dirtyBlocksWritten = 0;
    }
    
    @Override
    public String toString() {
        return String.format(
            "Lecturas: %d, Aciertos: %d, Fallos: %d, Ratio: %.2f%%, Escrituras Dirty: %d",
            totalReads, bufferHits, bufferMisses, getHitRatio() * 100, dirtyBlocksWritten
        );
    }
    
    // Getters para la UI
    public int getTotalReads() { return totalReads; }
    public int getBufferHits() { return bufferHits; }
    public int getBufferMisses() { return bufferMisses; }
    public int getTotalWrites() { return totalWrites; }
    public int getDirtyBlocksWritten() { return dirtyBlocksWritten; }
    public double getHitRatioPercent() { return getHitRatio() * 100; }
}