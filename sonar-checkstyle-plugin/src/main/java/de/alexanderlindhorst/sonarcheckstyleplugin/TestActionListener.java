package de.alexanderlindhorst.sonarcheckstyleplugin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;

@ActionID(category = "File", id = "de.alexanderlindhorst.sonarcheckstyleplugin.TestActionListener")
@ActionRegistration(displayName = "TestAction")
@ActionReference(path = "Menu/File")
public class TestActionListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(null, "Dunnit!", "Success!", JOptionPane.INFORMATION_MESSAGE);
    }
}
