/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.classes;

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author fabys
 */


public class BufferManager {
    private final BufferBlock[] buffer;
    private final int bufferSize;
    private ReplacementPolicy replacementPolicy;
    private final Map<Integer, Integer> blockToIndexMap;
    private final BufferMetrics metrics;
    private final Disk disk;
    
    public BufferManager(int bufferSize, ReplacementPolicy policy, Disk disk) {
        this.bufferSize = bufferSize;
        this.buffer = new BufferBlock[bufferSize];
        this.replacementPolicy = policy;
        this.blockToIndexMap = new HashMap<>();
        this.metrics = new BufferMetrics();
        this.disk = disk;
    }
    
    /**
     * Lee un bloque usando el buffer (primero busca en buffer, si no en disco)
     */
    public byte[] readBlock(int blockNumber) {
        metrics.recordRead();
        
        // 1. Verificar si está en buffer
        Integer bufferIndex = blockToIndexMap.get(blockNumber);
        if (bufferIndex != null) {
            BufferBlock block = buffer[bufferIndex];
            block.updateAccessTime();
            metrics.recordHit();
            System.out.println("Buffer: HIT - Bloque " + blockNumber + " encontrado en buffer");
            return block.getData();
        }
        
        // 2. Buffer MISS - cargar del disco
        metrics.recordMiss();
        System.out.println("Buffer: MISS - Cargando bloque " + blockNumber + " del disco");
        byte[] diskData = disk.readBlock(blockNumber);
        
        // 3. Almacenar en buffer para próximas accesos
        storeInBuffer(blockNumber, diskData, false);
        
        return diskData;
    }
    
    /**
     * Escribe datos en un bloque usando buffer
     */
    public void writeBlock(int blockNumber, byte[] data) {
        metrics.recordWrite();
        
        Integer bufferIndex = blockToIndexMap.get(blockNumber);
        if (bufferIndex != null) {
            // Bloque ya está en buffer - actualizar
            BufferBlock block = buffer[bufferIndex];
            block.setData(data);
            block.updateAccessTime();
            System.out.println("Buffer: Escritura en bloque " + blockNumber + " (en buffer, marcado como dirty)");
        } else {
            // Nuevo bloque - almacenar en buffer
            storeInBuffer(blockNumber, data, true);
            System.out.println("Buffer: Escritura en bloque " + blockNumber + " (nuevo en buffer)");
        }
    }
    
    /**
     * Almacena un bloque en el buffer
     */
    private void storeInBuffer(int blockNumber, byte[] data, boolean isDirty) {
        int bufferIndex = findFreeBufferSlot();
        
        if (bufferIndex == -1) {
            // Buffer lleno - aplicar política de reemplazo
            bufferIndex = replacementPolicy.selectVictim(buffer);
            evictBufferBlock(bufferIndex);
        }
        
        // Crear y almacenar nuevo bloque en buffer
        BufferBlock newBlock = new BufferBlock(blockNumber, data);
        if (isDirty) {
            newBlock.setDirty(true);
        }
        
        buffer[bufferIndex] = newBlock;
        blockToIndexMap.put(blockNumber, bufferIndex);
        
        System.out.println("Buffer: Bloque " + blockNumber + " almacenado en slot " + bufferIndex);
    }
    
    /**
     * Encuentra un slot libre en el buffer
     */
    private int findFreeBufferSlot() {
        for (int i = 0; i < bufferSize; i++) {
            if (buffer[i] == null) {
                return i;
            }
        }
        return -1; // Buffer lleno
    }
    
    /**
     * Expulsa un bloque del buffer (si está dirty, lo escribe al disco)
     */
    private void evictBufferBlock(int bufferIndex) {
        BufferBlock victim = buffer[bufferIndex];
        if (victim != null) {
            System.out.println("Buffer: Expulsando bloque " + victim.getDiskBlockNumber() + 
                             " del slot " + bufferIndex + " (dirty: " + victim.isDirty() + ")");
            
            if (victim.isDirty()) {
                disk.writeBlock(victim.getDiskBlockNumber(), victim.getData());
                metrics.recordDirtyWrite();
                System.out.println("Buffer: Bloque dirty " + victim.getDiskBlockNumber() + " escrito al disco");
            }
            
            blockToIndexMap.remove(victim.getDiskBlockNumber());
            buffer[bufferIndex] = null;
        }
    }
    
 
public java.util.List<BufferInfo> getBufferInfo() {
    java.util.List<BufferInfo> info = new java.util.ArrayList<>();
    for (int i = 0; i < bufferSize; i++) {
        BufferBlock block = buffer[i];
        if (block != null) {
            info.add(new BufferInfo(
                i,
                block.getDiskBlockNumber(),
                block.isDirty(),
                block.getLastAccessTime(),
                block.getAccessCount(),
                block.getData().length
            ));
        }
    }
    return info;
}
    
    /**
     * Sincroniza todos los bloques dirty con el disco
     */
    public void flush() {
        System.out.println("Buffer: Sincronizando buffer con disco...");
        int dirtyCount = 0;
        for (int i = 0; i < bufferSize; i++) {
            if (buffer[i] != null && buffer[i].isDirty()) {
                disk.writeBlock(buffer[i].getDiskBlockNumber(), buffer[i].getData());
                buffer[i].setDirty(false);
                metrics.recordDirtyWrite();
                dirtyCount++;
            }
        }
        System.out.println("Buffer: " + dirtyCount + " bloques dirty sincronizados con disco");
    }
    
    /**
     * Cambia la política de reemplazo
     */
    public void setReplacementPolicy(ReplacementPolicy policy) {
        this.replacementPolicy = policy;
        System.out.println("Buffer: Política cambiada a " + policy.getPolicyName());
    }
    
    // ===== MÉTODOS PARA LA UI =====
    
    public BufferMetrics getMetrics() {
        return metrics;
    }
    
    public int getCurrentSize() {
        return blockToIndexMap.size();
    }
    
    public int getBufferSize() {
        return bufferSize;
    }
    
    public String getPolicyName() {
        return replacementPolicy.getPolicyName();
    }
    
    /**
     * Obtiene información del buffer para mostrar en la UI
     */
    
    /**
     * Limpia completamente el buffer
     */
    public void clear() {
        flush(); // Escribir todos los dirty blocks primero
        Arrays.fill(buffer, null);
        blockToIndexMap.clear();
        metrics.reset();
        System.out.println("Buffer: Limpiado completamente");
    }
    
    /**
     * Clase para información de la UI del buffer
     */
    public static class BufferInfo {
        public final int bufferSlot;
        public final int diskBlock;
        public final boolean isDirty;
        public final long lastAccess;
        public final int accessCount;
        public final int dataSize;
        
        public BufferInfo(int bufferSlot, int diskBlock, boolean isDirty, 
                         long lastAccess, int accessCount, int dataSize) {
            this.bufferSlot = bufferSlot;
            this.diskBlock = diskBlock;
            this.isDirty = isDirty;
            this.lastAccess = lastAccess;
            this.accessCount = accessCount;
            this.dataSize = dataSize;
        }
    }
}