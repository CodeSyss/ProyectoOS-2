package gui.clasess;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renderer personalizado para mostrar colores en la tabla de asignación
 */
public class ColorCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        JLabel label = (JLabel) super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        if (value instanceof Color) {
            Color color = (Color) value;
            label.setBackground(color);
            label.setText(""); // No mostrar texto, solo el color
            label.setOpaque(true);
        } else {
            label.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            label.setOpaque(isSelected);
        }

        return label;
    }
}
