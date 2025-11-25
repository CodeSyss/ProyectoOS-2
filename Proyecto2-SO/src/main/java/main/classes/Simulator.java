/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.classes;

import helpers.CustomQueue;
import helpers.MyList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import gui.clasess.MainJFrame;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import java.io.FileWriter;
import java.io.IOException;
import javax.swing.Timer;

/**
 *
 * @author payto
 */
// Nuestro Simulador de archivos que escucha la informacion desda la GUI
public class Simulator implements ActionListener {

    private MainJFrame gui;
    private Directory rootNodeData;

    private Timer simulationTimer;

    // Lista para procesos
    private final MyList<Process> masterProcessList;
    // Cola para E/S a disco
    private final CustomQueue<IoRequest> diskRequest;

    private final Scheduler scheduler;
    private Disk disk;

    public Simulator(MainJFrame gui, Disk disk) {

        this.gui = gui;
        this.disk = disk; // Usar el disco pasado desde MainJFrame
        this.masterProcessList = new MyList<>();
        this.diskRequest = new CustomQueue<>();

        this.scheduler = new Scheduler(this.diskRequest, this.disk, this);

        this.rootNodeData = new Directory("root", 0, null);

        DefaultMutableTreeNode rootNodeWrapper = new DefaultMutableTreeNode(rootNodeData);
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNodeWrapper);
        this.gui.getJTreeArchivos().setModel(treeModel);
        addListeners();

        // INICIA EL RELOJ
        // Se disparará cada 2000ms (2 segundos)
        int delay = 2000;
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                tickDeSimulacion();
            }
        };
        this.simulationTimer = new Timer(delay, taskPerformer);
        this.simulationTimer.start();

    }

    public void tickDeSimulacion() {
        // Actualizar el algoritmo seleccionado desde la GUI
        String selectedAlgo = (String) this.gui.getComboPlanificador().getSelectedItem();
        if (selectedAlgo != null) {
            scheduler.setAlgorithm(selectedAlgo);
        }

        boolean algoCambio = scheduler.procesarSiguienteSolicitud();
        if (algoCambio) {
            updateGUI();
        }
    }

    public void requestCreateFile(String path, int blockCount, String username) {

        // Asignar un cilindro aleatorio para propósitos de simulación de planificación
        int randomCylinder = (int) (Math.random() * 350);

        String processName = username + " (Create " + path + ")";
        Process p = new Process(processName);
        p.setState(Process.ProcessState.READY);
        p.setCurrentOperation("CREAR " + path + " (" + blockCount + " blq) [Cyl: " + randomCylinder + "]");

        this.masterProcessList.add(p);

        IoRequest request = new IoRequest(
                p,
                IoRequest.OperationType.CREATE_FILE,
                path,
                blockCount,
                randomCylinder);

        this.diskRequest.enqueue(request);
        // p.setState(Process.ProcessState.BLOCKED); // REMOVED as per user request
        // (Stay READY)

        updateGUI();
    }

    public void requestCreateDirectory(String path, String username) {

        int randomCylinder = (int) (Math.random() * 350);

        String processName = username + " (Create Dir: " + path + ")";
        Process p = new Process(processName);
        p.setState(Process.ProcessState.READY);
        p.setCurrentOperation("CREAR DIR " + path + " [Cyl: " + randomCylinder + "]");

        this.masterProcessList.add(p);

        IoRequest request = new IoRequest(
                p,
                IoRequest.OperationType.CREATE_DIRECTORY,
                path,
                1, // <-- Un Directorio siempre ocupa 1 bloque (decisión de diseño)
                randomCylinder);

        this.diskRequest.enqueue(request);
        // p.setState(Process.ProcessState.BLOCKED); // REMOVED as per user request
        // (Stay READY)

        updateGUI();
    }

    private void addListeners() {
        this.gui.getBtnCrearArchivo().addActionListener(this);
        this.gui.getBtnCrearDirectorio().addActionListener(this);
        this.gui.getBtnEliminar().addActionListener(this);
        this.gui.getRadioModoAdmin().addActionListener(this);
        this.gui.getRadioModoUsuario().addActionListener(this);
        this.gui.getBtnCrearProcesosAleatorios().addActionListener(this);
        this.gui.getBtnCargarProcesosCsv().addActionListener(this);
    }

    public void onCreationSuccess(Process process, String path, IoRequest.OperationType type, int startBlock,
            int blockCount) {

        FileSystemNode newNodeData;

        String nombre = path;

        DefaultMutableTreeNode parentWrapper = (DefaultMutableTreeNode) this.gui.getJTreeArchivos()
                .getLastSelectedPathComponent();

        if (parentWrapper == null || !(parentWrapper.getUserObject() instanceof Directory)) {
            parentWrapper = (DefaultMutableTreeNode) this.gui.getJTreeArchivos().getModel().getRoot();
        }

        Directory parentData = (Directory) parentWrapper.getUserObject();

        if (type == IoRequest.OperationType.CREATE_DIRECTORY) {
            newNodeData = new Directory(nombre, startBlock, process);
        } else {
            newNodeData = new File(nombre, startBlock, blockCount, process);
        }

        parentData.addChild(newNodeData);
        newNodeData.setParent(parentData);

        DefaultMutableTreeNode newNodeWrapper = new DefaultMutableTreeNode(newNodeData);
        DefaultTreeModel treeModel = (DefaultTreeModel) this.gui.getJTreeArchivos().getModel();
        treeModel.insertNodeInto(newNodeWrapper, parentWrapper, parentWrapper.getChildCount());

        process.setState(Process.ProcessState.FINISHED);

        this.gui.getJTreeArchivos().scrollPathToVisible(
                new javax.swing.tree.TreePath(newNodeWrapper.getPath()));
    }

@Override
public void actionPerformed(ActionEvent e) {
    Object source = e.getSource();

    if (source == this.gui.getBtnCrearArchivo()) {
        accionCrearArchivo();
    } else if (source == this.gui.getBtnCrearDirectorio()) {
        accionCrearDirectorio();
    } else if (source == this.gui.getBtnEliminar()) {
        accionEliminar();
    } else if (source == this.gui.getRadioModoAdmin()) {
        logicaModoAdmin();
    } else if (source == this.gui.getRadioModoUsuario()) {
        logicaModoUsuario();
    } else if (source == this.gui.getBtnCrearProcesosAleatorios()) {
        accionCrearProcesosAleatorios();
    } else if (source == this.gui.getBtnCargarProcesosCsv()) {
        accionCargarProcesosCsv();
    }
}

private void accionCrearProcesosAleatorios() {
    try (FileWriter writer = new FileWriter("procesos.csv")) {
        // Escribir encabezado del CSV
        writer.write("ID,Nombre,Estado,Operacion,Bloques_Solicitados,Usuario\n");
        
        // Crear y guardar 10 procesos aleatorios
        for (int i = 1; i <= 10; i++) {
            int numBloques = (int) (Math.random() * 10) + 1;
            String nombreArchivo = "Proceso_Auto_" + i;

            // Escribir directamente en el CSV
            writer.write(String.format("\"P%d\",\"%s\",\"Solicitado\",\"Crear archivo\",%d,\"Admin\"\n", 
                i, nombreArchivo, numBloques));
            
            requestCreateFile(nombreArchivo, numBloques, "Admin");
        }
        
        JOptionPane.showMessageDialog(gui, 
            "Se han solicitado 10 procesos aleatorios y guardado en 'procesos.csv'.", 
            "Información",
            JOptionPane.INFORMATION_MESSAGE);
            
    } catch (IOException e) {
        System.err.println("Error al guardar procesos en CSV: " + e.getMessage());
        JOptionPane.showMessageDialog(gui, 
            "Error al guardar el archivo CSV: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}


private void accionCargarProcesosCsv() {
    try (BufferedReader reader = new BufferedReader(new FileReader("procesos.csv"))) {
        String linea;
        boolean primeraLinea = true;
        int procesosCargados = 0;
        
        while ((linea = reader.readLine()) != null) {
            // Saltar la línea de encabezado
            if (primeraLinea) {
                primeraLinea = false;
                continue;
            }
            
            // Procesar la línea
            String[] campos = linea.split(",");
            
            if (campos.length >= 6) {
                // Limpiar comillas y espacios
                String id = campos[0].replace("\"", "").trim();
                String nombreArchivo = campos[1].replace("\"", "").trim();
                String estado = campos[2].replace("\"", "").trim();
                String operacion = campos[3].replace("\"", "").trim();
                int bloques = Integer.parseInt(campos[4].replace("\"", "").trim());
                String usuario = campos[5].replace("\"", "").trim();
                
                // Usar requestCreateFile para enviar el proceso al sistema
                requestCreateFile(nombreArchivo, bloques, usuario);
                procesosCargados++;
                
                System.out.println("Proceso cargado: " + nombreArchivo + " (" + bloques + " bloques)");
            }
        }
        
        // Mostrar mensaje de éxito
        JOptionPane.showMessageDialog(gui, 
            "Se han cargado " + procesosCargados + " procesos desde 'procesos.csv' al sistema", 
            "Información",
            JOptionPane.INFORMATION_MESSAGE);
        
    } catch (FileNotFoundException e) {
        System.err.println("Archivo no encontrado: " + e.getMessage());
        JOptionPane.showMessageDialog(gui, 
            "Archivo 'procesos.csv' no encontrado", 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    } catch (IOException e) {
        System.err.println("Error al leer el archivo CSV: " + e.getMessage());
        JOptionPane.showMessageDialog(gui, 
            "Error al leer el archivo CSV: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    } catch (NumberFormatException e) {
        System.err.println("Error en el formato de los datos: " + e.getMessage());
        JOptionPane.showMessageDialog(gui, 
            "Error en el formato de los datos del CSV", 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}
          

    public void updateGUI() {

        DefaultTableModel modelProcesos = (DefaultTableModel) this.gui.getTablaProcesos().getModel();
        modelProcesos.setRowCount(0);

        for (int i = 0; i < masterProcessList.size(); i++) {
            Process p = masterProcessList.get(i);
            modelProcesos.addRow(new Object[] {
                    p.getProcessID(),
                    p.getProcessName(),
                    p.getState(),
                    p.getCurrentOperation() // Nueva columna
            });
        }

        updateAllocationTable();
        this.gui.getPanelVisorDisco().repaint();

        // Actualizar Estadísticas
        this.gui.updateStats(disk.getUsedBlocksCount(), disk.getFreeBlocksCount());
    }

    private void updateAllocationTable() {
        DefaultTableModel modelAsignacion = (DefaultTableModel) this.gui.getTablaAsignacion().getModel();
        modelAsignacion.setRowCount(0);

        collectFilesRecursive(rootNodeData, modelAsignacion);
    }

    private void collectFilesRecursive(FileSystemNode node, DefaultTableModel model) {
        if (node instanceof File) {
            File file = (File) node;
            model.addRow(new Object[] {
                    file.getName(),
                    file.getBlockCount(),
                    file.getStartingBlock(),
                    file.getOwnerProcess() != null ? file.getOwnerProcess().getColor() : null
            });
        } else if (node instanceof Directory) {
            Directory dir = (Directory) node;
            // Agregar el directorio también
            if (dir.getOwnerProcess() != null) {
                model.addRow(new Object[] {
                        "[DIR] " + dir.getName(),
                        1,
                        dir.getStartingBlock(),
                        dir.getOwnerProcess().getColor()
                });
            }
            // Recorrer hijos
            MyList<FileSystemNode> children = dir.getChildren();
            for (int i = 0; i < children.size(); i++) {
                collectFilesRecursive(children.get(i), model);
            }
        }
    }

    // Metodos cuando se le da click a un boton desde la GUI
    private void accionCrearArchivo() {
        // Obtiene el directorio padre seleccionado en el JTree
        DefaultMutableTreeNode nodoPadreWrapper = (DefaultMutableTreeNode) this.gui.getJTreeArchivos()
                .getLastSelectedPathComponent();

        if (nodoPadreWrapper == null || !(nodoPadreWrapper.getUserObject() instanceof Directory)) {
            JOptionPane.showMessageDialog(gui, "Por favor, seleccione un directorio válido.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        String nombreArchivo = JOptionPane.showInputDialog(gui, "Nombre del archivo:");
        if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
            return;
        }

        int numBloques;
        try {
            numBloques = Integer.parseInt(JOptionPane.showInputDialog(gui, "Número de bloques:"));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(gui, "Número de bloques inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        requestCreateFile(nombreArchivo, numBloques, "Admin");
    }

    private void accionCrearDirectorio() {
        // Obtiene el directorio padre seleccionado en el JTree
        DefaultMutableTreeNode nodoPadreWrapper = (DefaultMutableTreeNode) this.gui.getJTreeArchivos()
                .getLastSelectedPathComponent();

        if (nodoPadreWrapper == null || !(nodoPadreWrapper.getUserObject() instanceof Directory)) {
            JOptionPane.showMessageDialog(gui, "Por favor, seleccione un directorio válido.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nombreDir = JOptionPane.showInputDialog(gui, "Nombre del directorio:");
        if (nombreDir == null || nombreDir.trim().isEmpty()) {
            return;
        }
        requestCreateDirectory(nombreDir, "Admin");
    }

    private void accionEliminar() {
        DefaultMutableTreeNode nodoSeleccionado = (DefaultMutableTreeNode) this.gui.getJTreeArchivos()
                .getLastSelectedPathComponent();

        if (nodoSeleccionado == null) {
            JOptionPane.showMessageDialog(gui, "Por favor, seleccione un archivo o directorio para eliminar.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        FileSystemNode nodeData = (FileSystemNode) nodoSeleccionado.getUserObject();

        if (nodeData == rootNodeData) {
            JOptionPane.showMessageDialog(gui, "No se puede eliminar el directorio raíz.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(gui,
                "¿Está seguro de eliminar '" + nodeData.getName() + "'?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (nodeData instanceof File) {
            File file = (File) nodeData;
            disk.freeBlocks(file.getStartingBlock());
        } else if (nodeData instanceof Directory) {
            Directory dir = (Directory) nodeData;
            freeDirectoryBlocks(dir);
        }

        Directory parent = (Directory) nodeData.getParent();
        if (parent != null) {
            parent.removeChild(nodeData);
        }

        DefaultTreeModel treeModel = (DefaultTreeModel) this.gui.getJTreeArchivos().getModel();
        treeModel.removeNodeFromParent(nodoSeleccionado);

        updateGUI();
    }

    private void freeDirectoryBlocks(Directory dir) {
        // Liberar bloques de todos los hijos primero
        MyList<FileSystemNode> children = dir.getChildren();
        for (int i = 0; i < children.size(); i++) {
            FileSystemNode child = children.get(i);
            if (child instanceof File) {
                File file = (File) child;
                disk.freeBlocks(file.getStartingBlock());
            } else if (child instanceof Directory) {
                freeDirectoryBlocks((Directory) child);
            }
        }
        // Liberar el bloque del directorio mismo
        if (dir.getOwnerProcess() != null) {
            disk.freeBlocks(dir.getStartingBlock());
        }
    }

    private void logicaModoAdmin() {
        System.out.println("Controlador: Modo Admin activado.");
        this.gui.getRadioModoUsuario().setSelected(false);

        this.gui.getBtnCrearDirectorio().setVisible(true); // jButton1
        this.gui.getBtnEliminar().setVisible(true); // jButton2
        this.gui.getBtnCrearArchivo().setVisible(true); // jButton3
        this.gui.getBtnCrearProcesosAleatorios().setVisible(true); // Nuevo botón
        this.gui.getBtnCargarProcesosCsv().setVisible(true); 
        
        this.gui.getComboPlanificador().setVisible(true); // jComboBox1
        this.gui.getLabelPlanificador().setVisible(true); // jLabel2
    }

    private void logicaModoUsuario() {
        System.out.println("Controlador: Modo Usuario activado.");

        this.gui.getRadioModoAdmin().setSelected(false);
        this.gui.getBtnCrearDirectorio().setVisible(false); // jButton1
        this.gui.getBtnEliminar().setVisible(false); // jButton2
        this.gui.getBtnCrearArchivo().setVisible(false); // jButton3
        this.gui.getBtnCrearProcesosAleatorios().setVisible(false); // Nuevo botón

        this.gui.getComboPlanificador().setVisible(false); // jComboBox1
        this.gui.getLabelPlanificador().setVisible(false); // jLabel2
    }

}
