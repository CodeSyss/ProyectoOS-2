package gui.clasess;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class UITheme {

    // Paleta de Colores
    public static final Color COLOR_PRIMARY = new Color(0, 120, 215); // Azul moderno
    public static final Color COLOR_SECONDARY = new Color(240, 240, 240); // Gris claro
    public static final Color COLOR_BACKGROUND = new Color(255, 255, 255); // Blanco
    public static final Color COLOR_TEXT = new Color(50, 50, 50); // Gris oscuro
    public static final Color COLOR_ACCENT = new Color(0, 153, 255); // Azul brillante
    public static final Color COLOR_DANGER = new Color(220, 53, 69); // Rojo para eliminar

    // Fuentes
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    public static void setupUI() {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            System.err.println("No se pudo aplicar Nimbus LookAndFeel");
        }
    }

    public static void styleButton(JButton button, boolean isDestructive) {
        button.setFont(FONT_BOLD);
        button.setForeground(Color.WHITE);
        button.setBackground(isDestructive ? COLOR_DANGER : COLOR_PRIMARY);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_NORMAL);
        table.setRowHeight(25);
        table.setSelectionBackground(new Color(232, 242, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(COLOR_SECONDARY);
        header.setForeground(COLOR_TEXT);
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(javax.swing.JLabel.LEFT);
    }

    public static void styleTree(JTree tree) {
        tree.setFont(FONT_NORMAL);
        tree.setRowHeight(25);
        tree.setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    public static void stylePanel(JPanel panel) {
        panel.setBackground(COLOR_BACKGROUND);
    }
}
