/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.classes;

/**
 *
 * @author payto
 */
import java.awt.Color;
import java.util.Random;

public class Process {

    public enum ProcessState {
        NEW,
        READY,
        BLOCKED,
        FINISHED
    }

    private static int nextProcessID = 1;

    private int processID;
    private String processName;
    private ProcessState state;
    private Color color;

    public Process(String processName) {
        this.processID = nextProcessID++;
        this.processName = processName;
        this.state = ProcessState.NEW;

        // Asigna un color aleatorio y único para el visor de disco
        Random rand = new Random();
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        this.color = new Color(r, g, b).brighter();
    }

    public static int getNextProcessID() {
        return nextProcessID;
    }

    public static void setNextProcessID(int nextProcessID) {
        Process.nextProcessID = nextProcessID;
    }

    public int getProcessID() {
        return processID;
    }

    public void setProcessID(int processID) {
        this.processID = processID;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public ProcessState getState() {
        return state;
    }

    public void setState(ProcessState state) {
        this.state = state;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    private String currentOperation = "-";

    public String getCurrentOperation() {
        return currentOperation;
    }

    public void setCurrentOperation(String currentOperation) {
        this.currentOperation = currentOperation;
    }

    @Override
    public String toString() {
        return "P" + processID + " (" + processName + ")";
    }
}
