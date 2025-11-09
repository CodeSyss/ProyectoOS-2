/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package helpers;

/**
 *
 * @author payto
 */
public class MyList<T> {

    private Object[] elements;
    private int size; 
    private static final int DEFAULT_CAPACITY = 7;
    
    public MyList() {
        this.elements = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }


    public void add(T element) {
        if (size == elements.length) {
            ensureCapacity();
        }
        elements[size] = element;
        size++;
    }

    private void ensureCapacity() {
        int newCapacity = elements.length * 2;
        Object[] newArray = new Object[newCapacity];

        for (int i = 0; i < size; i++) {
            newArray[i] = elements[i];
        }

        elements = newArray;
    }


    @SuppressWarnings("unchecked")
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        
        return (T) elements[index];
    }

    @SuppressWarnings("unchecked")
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        T removedElement = (T) elements[index];

        int numElementsToMove = size - index - 1;
        if (numElementsToMove > 0) {
            System.arraycopy(elements, index + 1, elements, index, numElementsToMove);
        }

        size--;
        elements[size] = null; 

        return removedElement;
    }

    public boolean remove(T element) {
        for (int i = 0; i < size; i++) {
            if (element == null) {
                if (elements[i] == null) {
                    remove(i);
                    return true;
                }
            } else if (element.equals(elements[i])) {
                remove(i); // Calls the remove(index) method
                return true;
            }
        }
        return false;
    }


    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        elements = new Object[DEFAULT_CAPACITY];
        size = 0;
    }
}
