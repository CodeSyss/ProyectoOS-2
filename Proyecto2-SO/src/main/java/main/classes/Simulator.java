/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.classes;

import helpers.CustomQueue;
import helpers.MyList;
import proyecto2SO.MainJFrame;

/**
 *
 * @author payto
 */
//Nuestro Simulador de archivos 
public class Simulator {

    private MainJFrame gui;
    private Directory rootNodeData; 
    
    
    //Lista para procesos
    private final MyList<Process> masterProcessList;
    // Cola para E/S a disco
    private final CustomQueue<IoRequest> diskRequest;

    private final Scheduler scheduler;
    private Disk disk;
    //17*17= 289 bloques de preferencia

    public Simulator() {
        this.masterProcessList = new MyList<>();
        this.diskRequest = new CustomQueue<>();
        this.scheduler = new Scheduler();

        this.disk = new Disk(289); //289 bloques en disco
        
        this.rootNodeData = new Directory("root", 0, null);
        
        // 2. El Controlador crea el ENVOLTORIO raíz
        //DefaultMutableTreeNode rootNodeWrapper = new DefaultMutableTreeNode(rootNodeData);
        
        // 3. El Controlador crea el MODELO (traductor)
        //DefaultTreeModel treeModel = new DefaultTreeModel(rootNodeWrapper);
        
        // 4. El Controlador "instala" el modelo en el JTree de la GUI
        //this.gui.getJTree().setModel(treeModel);
        
        // ... (Aquí es donde añades los ActionListeners a los botones) ...
        //addListeners();
        
        
    }

    public void requestCreateFile(String path, int blockCount, String username) {

        String processName = username + " (Create " + path + ")";
        Process p = new Process(processName);
        p.setState(Process.ProcessState.READY);

        this.masterProcessList.add(p);

        IoRequest request = new IoRequest(
                p,
                IoRequest.OperationType.CREATE_FILE,
                path,
                blockCount
        );

        this.diskRequest.enqueue(request);

        updateGUI();
    }

    public void requestCreateDirectory(String path, String username) {

        String processName = username + " (Create Dir: " + path + ")";
        Process p = new Process(processName);
        p.setState(Process.ProcessState.READY);

        this.masterProcessList.add(p);


        IoRequest request = new IoRequest(
                p, 
                IoRequest.OperationType.CREATE_DIRECTORY, 
                path,
                1 // <-- Un Directorio siempre ocupa 1 bloque (decisión de diseño)
        );

        this.diskRequest.enqueue(request);
        
        updateGUI();
    }
    
    public void updateGUI() {

    }       

}
