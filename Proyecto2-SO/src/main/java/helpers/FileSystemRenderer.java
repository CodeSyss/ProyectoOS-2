/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package helpers;

/**
 *
 * @author payto
 */
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager; 
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import main.classes.Directory;
import main.classes.File;

public class FileSystemRenderer extends DefaultTreeCellRenderer {

    private Icon directoryIcon;
    private Icon fileIcon;

    public FileSystemRenderer() {
        this.directoryIcon = UIManager.getIcon("Tree.closedIcon");
        this.fileIcon = UIManager.getIcon("Tree.leafIcon");
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
        
        super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object nodeDataObject = node.getUserObject();
        
        if (nodeDataObject instanceof Directory) {
            setIcon(directoryIcon); 
        
        } else if (nodeDataObject instanceof File) {
            setIcon(fileIcon); 
        
        } else {
        }

        return this;
    }
}