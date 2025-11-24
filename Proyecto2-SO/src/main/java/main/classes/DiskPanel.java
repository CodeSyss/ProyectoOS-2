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
    private final int legendHeight = 50; // Altura reservada para la leyenda (aumentada)

    // Constructor que recibe la instancia del disco
    public DiskPanel(Disk disk) {
        this.disk = disk;
        int rows = (int) Math.ceil((double) disk.getTotalBlocks() / blocksPerRow);
        int width = blocksPerRow * (blockSize + 2) + 10; // +2 para el espaciado
        int height = legendHeight + rows * (blockSize + 2) + 10; // Leyenda arriba + bloques
        setPreferredSize(new Dimension(width, height));
        setBackground(gui.clasess.UITheme.COLOR_BACKGROUND);
    }

    // Sobreescribir el método paintComponent para realizar el dibujo
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int totalBlocks = disk.getTotalBlocks();

        drawLegend(g);
        g.setFont(new Font("Monospaced", Font.PLAIN, 10));

        for (int i = 0; i < totalBlocks; i++) {

            int row = i / blocksPerRow;
            int col = i % blocksPerRow;
            
            int x = 5 + col * (blockSize + 2);
            int y = legendHeight + 5 + row * (blockSize + 2);
            Color blockColor = disk.getColorForBlock(i);

            g.setColor(blockColor);
            g.fillRect(x, y, blockSize, blockSize);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, blockSize, blockSize);
        }
    }

    // Método auxiliar para dibujar una leyenda en la parte superior
    private void drawLegend(Graphics g) {
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.setColor(Color.BLACK);

        int legendY = 20; 

        g.drawString("Leyenda:", 5, legendY);

        int boxSize = 15;
        int currentX = 75;

        // Libre (gris)
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(currentX, legendY - boxSize + 2, boxSize, boxSize);
        g.setColor(Color.BLACK);
        g.drawRect(currentX, legendY - boxSize + 2, boxSize, boxSize);
        g.drawString("Libre", currentX + boxSize + 5, legendY);

        // Ocupado - Mostrar varios colores de ejemplo
        currentX += 80;

        Color[] exampleColors = {
                new Color(100, 150, 255), // Azul
                new Color(255, 150, 100), // Naranja
                new Color(150, 255, 100) // Verde
        };

        int colorBoxSize = 12;
        int spacing = 2;

        for (int i = 0; i < exampleColors.length; i++) {
            g.setColor(exampleColors[i]);
            g.fillRect(currentX + i * (colorBoxSize + spacing),
                    legendY - boxSize + 2, colorBoxSize, colorBoxSize);
            g.setColor(Color.BLACK);
            g.drawRect(currentX + i * (colorBoxSize + spacing),
                    legendY - boxSize + 2, colorBoxSize, colorBoxSize);
        }

        g.setColor(Color.BLACK);
        g.drawString("Ocupado (cada proceso tiene un color único)",
                currentX + (colorBoxSize + spacing) * exampleColors.length + 5, legendY);

        // Nota adicional
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g.drawString("Tip: El color coincide con la columna 'Color' en la Tabla de Asignación",
                5, legendY + 15);
    }

    public void updateView() {
        repaint();
    }
}