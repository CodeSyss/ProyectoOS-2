/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.classes;

import helpers.CustomQueue;
import helpers.MyList;

/**
 *
 * @author payto
 */
//Nuestro Simulador de archivos 
public class Simulator {

    //Lista para procesos
    private final MyList<Process> MasterProcessList;
    // Cola para E/S a disco
    private final CustomQueue<IoRequest> DiskRequests;

    public Simulator() {
        this.MasterProcessList = new MyList<>();
        this.DiskRequests = new CustomQueue<>();
    }
}
