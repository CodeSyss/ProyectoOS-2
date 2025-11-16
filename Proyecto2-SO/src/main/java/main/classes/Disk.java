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
    private int[] allocationTable; //Identificador de disponibilidad del bloque y su tabla de asignacion

    private Process[] blockOwners; //asignar color en posicion correspondiente

    private int totalBlocks;

    public Disk(int totalBlocks) {
        this.totalBlocks = totalBlocks;
        this.allocationTable = new int[totalBlocks];
        this.blockOwners = new Process[totalBlocks];

        for (int i = 0; i < totalBlocks; i++) {
            this.allocationTable[i] = 0; // 0 = FREE
            this.blockOwners[i] = null;
        }
    }
    
    
    //Asignar bloques 
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
                    System.out.println("Disk: Asignado" + blocksNeeded + " bloques para " + owner.getProcessName() + ". Primer bloque: " + firstBlock);
                    return firstBlock; // Success!
                }
            }
        }
        System.err.println("Disk: ¡Espacio insuficiente! Error al asignar" + blocksNeeded + " bloques.");
        return -1;
    }

    //Liberar bloques 
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

    public Color getColorForBlock(int index) {
        if (index < 0 || index >= totalBlocks || blockOwners[index] == null) {
            return Color.LIGHT_GRAY; // Color for "FREE"
        }
        return blockOwners[index].getColor();
    }
    // Solucionar en su momento
    //Color por defecto para bloques libres 
    //Color verde para bloques ocupados  
}
