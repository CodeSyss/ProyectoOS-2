/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.classes;

/**
 *
 * @author ile1
 */
public abstract class FileSystemNode {


    private String name;
    private Directory parent;

    public FileSystemNode(String name) {
        this.name = name;
        this.parent = null; 
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Directory getParent() {
        return parent;
    }

 
    public void setParent(Directory parent) {
        this.parent = parent;
    }

    public abstract boolean isDirectory();

    @Override
    public String toString() {
        return name;
    }
    
    
}
