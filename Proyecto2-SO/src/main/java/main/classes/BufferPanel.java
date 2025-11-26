/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.classes;

/**
 *
 * @author fabys
 */

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BufferPanel extends JPanel {
    private BufferManager bufferManager;
    private final int blockSize = 40;
    private final int blocksPerRow = 5;
    private final int padding = 10;

    public BufferPanel(BufferManager bufferManager) {
        this.bufferManager = bufferManager;
        setPreferredSize(new Dimension(blocksPerRow * (blockSize + padding) + 20, 300));
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createTitledBorder("Buffer Manager"));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        drawLegend(g);
        drawBufferBlocks(g);
        drawStatistics(g);
    }

    private void drawLegend(Graphics g) {
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        // Leyenda de colores
        int y = 20;
        g.drawString("Leyenda:", 10, y);
        
        // Clean block
        g.setColor(Color.GREEN);
        g.fillRect(70, y - 12, 15, 15);
        g.setColor(Color.BLACK);
        g.drawRect(70, y - 12, 15, 15);
        g.drawString("Limpio", 90, y);
        
        // Dirty block
        g.setColor(Color.RED);
        g.fillRect(150, y - 12, 15, 15);
        g.setColor(Color.BLACK);
        g.drawRect(150, y - 12, 15, 15);
        g.drawString("Dirty", 170, y);
        
        // Empty slot
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(220, y - 12, 15, 15);
        g.setColor(Color.BLACK);
        g.drawRect(220, y - 12, 15, 15);
        g.drawString("Vacío", 240, y);
    }

    private void drawBufferBlocks(Graphics g) {
        List<BufferManager.BufferInfo> bufferInfo = bufferManager.getBufferInfo();
        int startY = 50;
        
        g.setFont(new Font("Monospaced", Font.PLAIN, 10));
        
        for (int i = 0; i < bufferManager.getBufferSize(); i++) {
            int row = i / blocksPerRow;
            int col = i % blocksPerRow;
            int x = padding + col * (blockSize + padding);
            int y = startY + row * (blockSize + padding);
            
            // Buscar información de este slot
            BufferManager.BufferInfo info = findBufferInfo(bufferInfo, i);
            
            if (info != null) {
                // Bloque ocupado
                Color blockColor = info.isDirty ? Color.RED : Color.GREEN;
                g.setColor(blockColor);
                g.fillRect(x, y, blockSize, blockSize);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, blockSize, blockSize);
                
                // Texto dentro del bloque
                g.setColor(Color.BLACK);
                g.drawString("D" + info.diskBlock, x + 2, y + 12);
                g.drawString("A" + info.accessCount, x + 2, y + 24);
                if (info.isDirty) {
                    g.drawString("DIRTY", x + 2, y + 36);
                }
            } else {
                // Slot vacío
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(x, y, blockSize, blockSize);
                g.setColor(Color.DARK_GRAY);
                g.drawRect(x, y, blockSize, blockSize);
                g.drawString("Vacío", x + 5, y + 20);
            }
        }
    }

    private void drawStatistics(Graphics g) {
        BufferMetrics metrics = bufferManager.getMetrics();
        int startY = 50 + ((bufferManager.getBufferSize() + blocksPerRow - 1) / blocksPerRow) * (blockSize + padding) + 20;
        
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.setColor(Color.BLACK);
        g.drawString("Estadísticas del Buffer:", 10, startY);
        
        g.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g.drawString(String.format("Política: %s", bufferManager.getPolicyName()), 10, startY + 15);
        g.drawString(String.format("Uso: %d/%d slots", bufferManager.getCurrentSize(), bufferManager.getBufferSize()), 10, startY + 30);
        g.drawString(String.format("Hit Ratio: %.2f%%", metrics.getHitRatioPercent()), 10, startY + 45);
        g.drawString(String.format("Lecturas: %d (Hits: %d, Miss: %d)", 
            metrics.getTotalReads(), metrics.getBufferHits(), metrics.getBufferMisses()), 10, startY + 60);
        g.drawString(String.format("Escrituras: %d (Dirty: %d)", 
            metrics.getTotalWrites(), metrics.getDirtyBlocksWritten()), 10, startY + 75);
    }

    private BufferManager.BufferInfo findBufferInfo(List<BufferManager.BufferInfo> infoList, int bufferSlot) {
        for (BufferManager.BufferInfo info : infoList) {
            if (info.bufferSlot == bufferSlot) {
                return info;
            }
        }
        return null;
    }

    public void updateView() {
        repaint();
    }
}