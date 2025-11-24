package gui.clasess;

import helpers.FileSystemRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import main.classes.Disk;
import main.classes.DiskPanel;
import main.classes.Simulator;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
/**
 *
 * @author fabys
 */
public class MainJFrame extends javax.swing.JFrame {

        Disk myDisk = new Disk(350);
        DiskPanel diskView = new DiskPanel(myDisk);

        private static final java.util.logging.Logger logger = java.util.logging.Logger
                        .getLogger(MainJFrame.class.getName());

        private Simulator simulator;

        // --- ¡DECLARA LOS COMPONENTES QUE FALTAN! ---
        private JSplitPane jSplitPanePrincipal;
        private JTree jTreeArchivos;
        private JTable jTableProcesos;
        private JTable jTableAsignacion;
        private DefaultTableModel modeloProcesos;
        private DefaultTableModel modeloAsignacion;

        // Botón nuevo
        private javax.swing.JButton btnCrearProcesosAleatorios;

        public MainJFrame() {
                // Configurar el tema antes de iniciar componentes
                UITheme.setupUI();

                initComponents();

                // Aplicar estilos generales
                this.getContentPane().setBackground(UITheme.COLOR_BACKGROUND);

                // Estilizar Botones
                UITheme.styleButton(jButton1, false); // Crear Directorio
                UITheme.styleButton(jButton2, true); // Eliminar (Destructivo)
                UITheme.styleButton(jButton3, false); // Crear Archivo

                // Inicializar y estilizar el nuevo botón
                btnCrearProcesosAleatorios = new javax.swing.JButton("Crear 10 Procesos");
                UITheme.styleButton(btnCrearProcesosAleatorios, false);

                // Estilizar Labels
                jLabel1.setFont(UITheme.FONT_BOLD); // Modo
                jLabel2.setFont(UITheme.FONT_BOLD); // Planificador

                // Estilizar Título (Explorador de Archivos)
                jLabel3.setFont(UITheme.FONT_TITLE);
                jLabel3.setForeground(UITheme.COLOR_PRIMARY);
                jLabel3.setBorder(new javax.swing.border.EmptyBorder(10, 10, 10, 10));

                jTreeArchivos = new JTree();
                UITheme.styleTree(jTreeArchivos);

                // --- PANEL IZQUIERDO (Árbol + Título) ---
                JPanel pnlLeft = new JPanel(new BorderLayout());
                pnlLeft.setBackground(UITheme.COLOR_BACKGROUND);
                // Mover el título (jLabel3) al panel izquierdo, arriba del árbol
                pnlLeft.add(jLabel3, BorderLayout.NORTH);
                pnlLeft.add(new JScrollPane(jTreeArchivos), BorderLayout.CENTER);

                // 2. Crear las JTables
                modeloProcesos = new DefaultTableModel(
                                new Object[] { "ID", "Nombre", "Estado" }, 0);
                jTableProcesos = new JTable(modeloProcesos);
                UITheme.styleTable(jTableProcesos);

                modeloAsignacion = new DefaultTableModel(
                                new Object[] { "Archivo", "Bloques", "Bloque Inicial", "Color" }, 0) {
                        @Override
                        public Class<?> getColumnClass(int column) {
                                if (column == 3) { // Columna de Color
                                        return Color.class;
                                }
                                return Object.class;
                        }
                };
                jTableAsignacion = new JTable(modeloAsignacion);
                UITheme.styleTable(jTableAsignacion);

                // Aplicar el renderer personalizado para la columna de color
                jTableAsignacion.getColumnModel().getColumn(3).setCellRenderer(new ColorCellRenderer());
                jTableAsignacion.getColumnModel().getColumn(3).setPreferredWidth(60);
                jTableAsignacion.getColumnModel().getColumn(3).setMaxWidth(80);

                // 3. Añadir JTables a las pestañas
                jTabbedPane1.add("Gestor de Procesos", new JScrollPane(jTableProcesos));
                jTabbedPane1.add("Tabla de Asignación", new JScrollPane(jTableAsignacion));
                jTabbedPane1.setFont(UITheme.FONT_BOLD);

                // Añadir Visor de Disco
                javax.swing.JScrollPane scrollPaneDisk = new javax.swing.JScrollPane(diskView);
                scrollPaneDisk.setPreferredSize(new java.awt.Dimension(500, 400));
                scrollPaneDisk.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                scrollPaneDisk.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                scrollPaneDisk.setBorder(new javax.swing.border.LineBorder(UITheme.COLOR_SECONDARY, 1));
                jTabbedPane1.add("Visualización del Disco", scrollPaneDisk);

                // 4. Crear el Panel Dividido (Split Pane)
                jSplitPanePrincipal = new JSplitPane(
                                JSplitPane.HORIZONTAL_SPLIT,
                                pnlLeft, // Izquierda: Panel con Título y Árbol
                                jTabbedPane1 // Derecha: Pestañas
                );
                jSplitPanePrincipal.setDividerLocation(280); // Un poco más ancho para el título
                jSplitPanePrincipal.setBorder(null);

                // 5. Layout Principal
                this.getContentPane().setLayout(new BorderLayout(10, 10));

                // --- PANEL DE CONTROLES (Arriba) ---
                JPanel pnlControls = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 15, 10));
                pnlControls.setBackground(UITheme.COLOR_BACKGROUND);
                pnlControls.setBorder(new javax.swing.border.MatteBorder(0, 0, 1, 0, UITheme.COLOR_SECONDARY));

                // Agrupar Radio Buttons
                javax.swing.ButtonGroup groupModo = new javax.swing.ButtonGroup();
                groupModo.add(jRadioButton1);
                groupModo.add(jRadioButton2);

                // Añadir componentes
                pnlControls.add(jLabel1); // Label "Modo"
                pnlControls.add(jRadioButton1); // Admin
                pnlControls.add(jRadioButton2); // Usuario

                pnlControls.add(new javax.swing.JSeparator(javax.swing.SwingConstants.VERTICAL));
                pnlControls.add(javax.swing.Box.createHorizontalStrut(10));

                pnlControls.add(jLabel2); // Label "Planificador"
                pnlControls.add(jComboBox1); // Combo

                pnlControls.add(javax.swing.Box.createHorizontalStrut(20));

                pnlControls.add(jButton3); // Crear Archivo
                pnlControls.add(jButton1); // Crear Directorio
                pnlControls.add(jButton2); // Eliminar
                pnlControls.add(btnCrearProcesosAleatorios); // Nuevo botón

                this.getContentPane().add(pnlControls, BorderLayout.NORTH);
                this.getContentPane().add(jSplitPanePrincipal, BorderLayout.CENTER);

                jTreeArchivos.setCellRenderer(new FileSystemRenderer());

                // --- CONFIGURACIÓN DE VENTANA ---
                // Tamaño "mínimo" (normal) por defecto, NO maximizado
                this.setSize(1100, 700);
                this.setLocationRelativeTo(null); // Centrar
        }

        public JTree getJTreeArchivos() {
                return jTreeArchivos;
        }

        public JTable getTablaProcesos() {
                return jTableProcesos;
        }

        public JTable getTablaAsignacion() {
                return jTableAsignacion;
        }

        public DiskPanel getPanelVisorDisco() {
                return diskView;
        }

        public javax.swing.JButton getBtnCrearArchivo() {
                return jButton3;
        }

        public javax.swing.JButton getBtnCrearDirectorio() {
                return jButton1;
        }

        public javax.swing.JButton getBtnEliminar() {
                return jButton2;
        }

        public javax.swing.JButton getBtnCrearProcesosAleatorios() {
                return btnCrearProcesosAleatorios;
        }

        public javax.swing.JRadioButton getRadioModoAdmin() {
                return jRadioButton1;
        }

        public javax.swing.JRadioButton getRadioModoUsuario() {
                return jRadioButton2;
        }

        public void setSimulator(Simulator simulator) {
                this.simulator = simulator;
        }

        public javax.swing.JComboBox<String> getComboPlanificador() {
                return jComboBox1;
        }

        public javax.swing.JLabel getLabelPlanificador() {
                return jLabel2;
        }

        /**
         * This method is called from within the constructor to initialize the form.
         * WARNING: Do NOT modify this code. The content of this method is always
         * regenerated by the Form Editor.
         */
        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated
        // Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                jMenu1 = new javax.swing.JMenu();
                jRadioButton1 = new javax.swing.JRadioButton();
                jRadioButton2 = new javax.swing.JRadioButton();
                jLabel1 = new javax.swing.JLabel();
                jLabel2 = new javax.swing.JLabel();
                jComboBox1 = new javax.swing.JComboBox<>();
                jButton1 = new javax.swing.JButton();
                jButton2 = new javax.swing.JButton();
                jPanel1 = new javax.swing.JPanel();
                jLabel3 = new javax.swing.JLabel();
                jTabbedPane1 = new javax.swing.JTabbedPane();
                jButton3 = new javax.swing.JButton();

                jMenu1.setText("jMenu1");

                setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

                jRadioButton1.setText("Administrador");
                jRadioButton2.setText("Usuario");
                jLabel1.setText("Modo:");
                jLabel2.setText("Planificador:");

                jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(
                                new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

                jButton1.setText("Crear Directorio");
                jButton2.setText("Eliminar");
                jButton3.setText("Crear Archivo");

                jPanel1.setBackground(new java.awt.Color(255, 255, 255));
                jLabel3.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 14)); // NOI18N
                jLabel3.setText("Explorador de Archivos");

                jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));

        }// </editor-fold>//GEN-END:initComponents

        /**
         * @param args the command line arguments
         */
        public static void main(String args[]) {
                /* Set the Nimbus look and feel */
                // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
                // (optional) ">
                /*
                 * If Nimbus (introduced in Java SE 6) is not available, stay with the default
                 * look and feel.
                 * For details see
                 * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
                 */
                try {
                        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
                                        .getInstalledLookAndFeels()) {
                                if ("Nimbus".equals(info.getName())) {
                                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                                        break;
                                }
                        }
                } catch (ClassNotFoundException ex) {
                        java.util.logging.Logger.getLogger(MainJFrame.class.getName())
                                        .log(java.util.logging.Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                        java.util.logging.Logger.getLogger(MainJFrame.class.getName())
                                        .log(java.util.logging.Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                        java.util.logging.Logger.getLogger(MainJFrame.class.getName())
                                        .log(java.util.logging.Level.SEVERE, null, ex);
                } catch (javax.swing.UnsupportedLookAndFeelException ex) {
                        java.util.logging.Logger.getLogger(MainJFrame.class.getName())
                                        .log(java.util.logging.Level.SEVERE, null, ex);
                }
                // </editor-fold>

                /* Create and display the form */
                java.awt.EventQueue.invokeLater(new Runnable() {
                        public void run() {
                                MainJFrame gui = new MainJFrame();
                                gui.setVisible(true);

                                // Crear el simulador pasándole la GUI y el disco que acabamos de crear
                                Simulator simulator = new Simulator(gui, gui.myDisk);
                                gui.setSimulator(simulator);
                        }
                });
        }

        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JButton jButton1;
        private javax.swing.JButton jButton2;
        private javax.swing.JButton jButton3;
        private javax.swing.JComboBox<String> jComboBox1;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JMenu jMenu1;
        private javax.swing.JPanel jPanel1;
        private javax.swing.JRadioButton jRadioButton1;
        private javax.swing.JRadioButton jRadioButton2;
        private javax.swing.JTabbedPane jTabbedPane1;
        // End of variables declaration//GEN-END:variables
}
