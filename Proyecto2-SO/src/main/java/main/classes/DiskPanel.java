/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.classes;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Font;
import javax.swing.JPanel;


public class DiskPanel extends JPanel {

    private final Disk disk;
    private final int blockSize = 30; // Tamaño de cada bloque (en píxeles)
    private final int blocksPerRow = 25; // Número de bloques por fila para el diseño

    // Constructor que recibe la instancia del disco
    public DiskPanel(Disk disk) {
        this.disk = disk;
        int rows = (int) Math.ceil((double) disk.getTotalBlocks() / blocksPerRow);
        int width = blocksPerRow * (blockSize + 2) + 10; // +2 para el espaciado
        int height = rows * (blockSize + 2) + 40; // +2 para el espaciado, +40 para etiquetas
        setPreferredSize(new Dimension(width, height));
    }

    // Sobreescribir el método paintComponent para realizar el dibujo
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int totalBlocks = disk.getTotalBlocks();
        int x, y;
        
        // Configurar la fuente para las etiquetas de los bloques
        g.setFont(new Font("Monospaced", Font.PLAIN, 10));

        // Dibujar cada bloque
        for (int i = 0; i < totalBlocks; i++) {
            
            // Calcular la posición (x, y) del bloque en la cuadrícula
            int row = i / blocksPerRow;
            int col = i % blocksPerRow;
            
            // Calcular la posición en píxeles. Agregamos un pequeño espaciado (+2)
            x = 5 + col * (blockSize + 2);
            y = 5 + row * (blockSize + 2);
            
            // 1. Obtener el color para el bloque (usa el getter de tu clase Disk)
            Color blockColor = disk.getColorForBlock(i);
            
            // 2. Dibujar el bloque rellenado
            g.setColor(blockColor);
            g.fillRect(x, y, blockSize, blockSize);
            
            // 3. Dibujar el borde del bloque para mejor distinción
            g.setColor(Color.BLACK);
            g.drawRect(x, y, blockSize, blockSize);
            
            // 4. Opcional: Escribir el índice del bloque en el centro
            // Esto puede ser útil si los bloques son lo suficientemente grandes
            g.setColor(Color.DARK_GRAY);
            String indexStr = String.valueOf(i);
            // Ajuste simple para centrar el texto (aproximadamente)
            g.drawString(indexStr, x + (blockSize / 2) - 4, y + (blockSize / 2) + 4);
        }
        
        // Opcional: Dibujar la leyenda (abajo)
       // drawLegend(g, totalBlocks, x, y);
    }
    
    // Método auxiliar para dibujar una leyenda
    private void drawLegend(Graphics g, int totalBlocks, int lastX, int lastY) {
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.setColor(Color.BLACK);
        
        int rows = (int) Math.ceil((double) totalBlocks / blocksPerRow);
        int legendY = 15 + rows * (blockSize + 2); // Colocar debajo de la última fila

        g.drawString("Leyenda:", 5, legendY);

        int boxSize = 10;
        int currentX = 60;
        
        // Libre
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(currentX, legendY - boxSize, boxSize, boxSize);
        g.setColor(Color.BLACK);
        g.drawRect(currentX, legendY - boxSize, boxSize, boxSize);
        g.drawString("Libre", currentX + boxSize + 5, legendY);
        
        // Ocupado (ejemplo, asume que el proceso asignará un color diferente a LIGHT_GRAY)
        currentX += 80;
        g.setColor(new Color(100, 150, 255)); // Un color de ejemplo para "ocupado"
        g.fillRect(currentX, legendY - boxSize, boxSize, boxSize);
        g.setColor(Color.BLACK);
        g.drawRect(currentX, legendY - boxSize, boxSize, boxSize);
        g.drawString("Ocupado", currentX + boxSize + 5, legendY);
    }

    /**
     * Llama a este método para actualizar la vista después de una operación
     * (asignar o liberar bloques).
     */
    public void updateView() {
        repaint();
    }
}