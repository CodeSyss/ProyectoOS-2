/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.classes;

import java.awt.Color;

/**
 *
 * @author payto
 */
public class Disk {
    private int[] allocationTable; // Identificador de disponibilidad del bloque y su tabla de asignacion

    private Process[] blockOwners; // asignar color en posicion correspondiente

    private static final int BLOCK_SIZE = 512;
    
    private int totalBlocks;
    private int headPosition = 0; // Posición actual del cabezal

    public Disk(int totalBlocks) {
        this.totalBlocks = totalBlocks;
        this.allocationTable = new int[totalBlocks];
        this.blockOwners = new Process[totalBlocks];

        for (int i = 0; i < totalBlocks; i++) {
            this.allocationTable[i] = 0; // 0 = FREE
            this.blockOwners[i] = null;
        }
    }

    public int getBlockSize() {
        return BLOCK_SIZE;
    }
    
    public int getNextBlock(int currentBlock) {
        if (currentBlock < 0 || currentBlock >= totalBlocks) {
            return -1;
        }
        int next = allocationTable[currentBlock];
        return (next == -1 || next == 0) ? -1 : next;
    }
    
    public byte[] readBlock(int blockNumber) {
        if (blockNumber < 0 || blockNumber >= totalBlocks) {
            throw new IllegalArgumentException("Número de bloque inválido: " + blockNumber);
        }
        if (allocationTable[blockNumber] == 0) {
            throw new IllegalStateException("Bloque " + blockNumber + " está libre");
        }
        
        // Simulamos datos - en un sistema real aquí leerías del almacenamiento
        // Por ahora devolvemos datos de ejemplo basados en el número de bloque
        byte[] data = new byte[BLOCK_SIZE];
        String blockInfo = "Block_" + blockNumber + "_Process_" + 
                          (blockOwners[blockNumber] != null ? blockOwners[blockNumber].getProcessName() : "Unknown");
        byte[] infoBytes = blockInfo.getBytes();
        System.arraycopy(infoBytes, 0, data, 0, Math.min(infoBytes.length, BLOCK_SIZE));
        
        return data;
    }
    
    public void writeBlock(int blockNumber, byte[] data) {
        if (blockNumber < 0 || blockNumber >= totalBlocks) {
            throw new IllegalArgumentException("Número de bloque inválido: " + blockNumber);
        }
        
        // En un sistema real aquí escribirías al almacenamiento físico
        // Por ahora solo registramos la operación
        System.out.println("Disk: Escritura física en bloque " + blockNumber + 
                          " (Tamaño: " + data.length + " bytes)");
        
        // Marcamos el bloque como ocupado si no lo estaba
        if (allocationTable[blockNumber] == 0) {
            allocationTable[blockNumber] = -1;
        }
    }
    
    // Asignar bloques
    public int assignBlocks(Process owner, int blocksNeeded) {
        if (blocksNeeded <= 0) {
            return -1;
        }

        int firstBlock = -1;
        int previousBlock = -1;
        int blocksFound = 0;

        for (int i = 0; i < this.totalBlocks; i++) {
            if (allocationTable[i] == 0) {
                blocksFound++;

                allocationTable[i] = -1;
                blockOwners[i] = owner;

                if (firstBlock == -1) {
                    firstBlock = i;
                } else {
                    allocationTable[previousBlock] = i;
                }

                previousBlock = i;

                if (blocksFound == blocksNeeded) {
                    System.out.println("Disk: Asignado" + blocksNeeded + " bloques para " + owner.getProcessName()
                            + ". Primer bloque: " + firstBlock);
                    return firstBlock; // Success!
                }
            }
        }
        System.err.println("Disk: ¡Espacio insuficiente! Error al asignar" + blocksNeeded + " bloques.");
        return -1;
    }

    // Liberar bloques
    public void freeBlocks(int startBlock) {
        int currentBlock = startBlock;
        int nextBlock;

        while (currentBlock != -1 && currentBlock < totalBlocks) {
            nextBlock = allocationTable[currentBlock];

            allocationTable[currentBlock] = 0;
            blockOwners[currentBlock] = null;

            currentBlock = nextBlock;
        }
        System.out.println("Disk: Cadena de bloques liberada a partir de " + startBlock);
    }

    // --- GETTERS (for the GUI) ---
    public int getTotalBlocks() {
        return totalBlocks;
    }

    public int getHeadPosition() {
        return headPosition;
    }

    public void setHeadPosition(int headPosition) {
        this.headPosition = headPosition;
    }

    public Color getColorForBlock(int index) {
        if (index < 0 || index >= totalBlocks || blockOwners[index] == null) {
            return Color.LIGHT_GRAY; // Color for "FREE"
        }
        return blockOwners[index].getColor();
    }

    // --- STATISTICS ---
    public int getUsedBlocksCount() {
        int used = 0;
        for (int i = 0; i < totalBlocks; i++) {
            if (allocationTable[i] != 0) {
                used++;
            }
        }
        return used;
    }

    public int getFreeBlocksCount() {
        return totalBlocks - getUsedBlocksCount();
    }
}
