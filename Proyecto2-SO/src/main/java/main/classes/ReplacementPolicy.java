/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.classes;

import java.util.function.Function;

/**
 *
 * @author fabys
 */

public enum ReplacementPolicy {
    FIFO("FIFO (First-In-First-Out)", 
         buffer -> {
             long oldestTime = Long.MAX_VALUE;
             int victimIndex = 0;
             for (int i = 0; i < buffer.length; i++) {
                 if (buffer[i] != null && buffer[i].getInsertionTime() < oldestTime) {
                     oldestTime = buffer[i].getInsertionTime();
                     victimIndex = i;
                 }
             }
             return victimIndex;
         }),
    
    LRU("LRU (Least Recently Used)", 
        buffer -> {
            long oldestAccess = Long.MAX_VALUE;
            int victimIndex = 0;
            for (int i = 0; i < buffer.length; i++) {
                if (buffer[i] != null && buffer[i].getLastAccessTime() < oldestAccess) {
                    oldestAccess = buffer[i].getLastAccessTime();
                    victimIndex = i;
                }
            }
            return victimIndex;
        }),
    
    LFU("LFU (Least Frequently Used)", 
        buffer -> {
            int minAccessCount = Integer.MAX_VALUE;
            int victimIndex = 0;
            for (int i = 0; i < buffer.length; i++) {
                if (buffer[i] != null && buffer[i].getAccessCount() < minAccessCount) {
                    minAccessCount = buffer[i].getAccessCount();
                    victimIndex = i;
                }
            }
            return victimIndex;
        }),
    
    RANDOM("Random", 
           buffer -> {
               // Selecciona un bloque ocupado al azar
               java.util.List<Integer> occupiedSlots = new java.util.ArrayList<>();
               for (int i = 0; i < buffer.length; i++) {
                   if (buffer[i] != null) {
                       occupiedSlots.add(i);
                   }
               }
               if (occupiedSlots.isEmpty()) return 0;
               return occupiedSlots.get((int) (Math.random() * occupiedSlots.size()));
           }),
    
    MRU("MRU (Most Recently Used)", 
        buffer -> {
            long newestAccess = Long.MIN_VALUE;
            int victimIndex = 0;
            for (int i = 0; i < buffer.length; i++) {
                if (buffer[i] != null && buffer[i].getLastAccessTime() > newestAccess) {
                    newestAccess = buffer[i].getLastAccessTime();
                    victimIndex = i;
                }
            }
            return victimIndex;
        });

    private final String policyName;
    private final Function<BufferBlock[], Integer> selectionAlgorithm;

    ReplacementPolicy(String policyName, Function<BufferBlock[], Integer> selectionAlgorithm) {
        this.policyName = policyName;
        this.selectionAlgorithm = selectionAlgorithm;
    }

    public int selectVictim(BufferBlock[] buffer) {
        return selectionAlgorithm.apply(buffer);
    }

    public String getPolicyName() {
        return policyName;
    }
    
    // Método útil para obtener políticas por nombre
    public static ReplacementPolicy fromName(String name) {
        for (ReplacementPolicy policy : values()) {
            if (policy.name().equalsIgnoreCase(name) || 
                policy.getPolicyName().toLowerCase().contains(name.toLowerCase())) {
                return policy;
            }
        }
        return FIFO; // Default
    }
}
