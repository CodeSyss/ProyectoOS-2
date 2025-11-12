/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.classes;

/**
 *
 * @author ile1
 */ 

import helpers.MyList;

public class Directory extends FileSystemNode {


    private MyList<FileSystemNode> children;

    private int startingBlock;
    private Process ownerProcess;


    public Directory(String name, int startingBlock, Process ownerProcess) {
        super(name); 
        this.startingBlock = startingBlock;
        this.ownerProcess = ownerProcess;
        this.children = new MyList<>(); 
    }



    public void addChild(FileSystemNode node) {
        this.children.add(node);
        // actualizar el bloque del directorio en el disco
        // para añadir el nombre del hijo.
    }

    public void removeChild(FileSystemNode node) {
        this.children.remove(node);
    }

    public MyList<FileSystemNode> getChildren() {
        return children;
    }

    // --- Getters del "ticket" ---

    public int getStartingBlock() {
        return startingBlock;
    }

    public Process getOwnerProcess() {
        return ownerProcess;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }
}