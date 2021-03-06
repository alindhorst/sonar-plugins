package de.alexanderlindhorst.sonar.findbugs.plugin.options;

import java.net.MalformedURLException;
import java.net.URL;

import org.openide.util.Exceptions;

import de.alexanderlindhorst.sonar.checkstyle.plugin.options.gui.SonarCheckstylePluginConfigPane;
import de.alexanderlindhorst.sonarfindbugs.plugin.util.SonarFindBugsPluginUtils;

final class SonarFindBugsPluginPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    private final SonarFindBugsPluginOptionsPanelController controller;

    SonarFindBugsPluginPanel(SonarFindBugsPluginOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this
     * method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        configPane = new de.alexanderlindhorst.sonar.checkstyle.plugin.options.gui.SonarCheckstylePluginConfigPane();

        setLayout(new java.awt.BorderLayout());
        add(configPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    void load() {
        URL loadConfigUrl = SonarFindBugsPluginUtils.loadConfigUrl();
        configPane.setConfigUrl(loadConfigUrl);
    }

    void store() {
        try {
            URL configUrl = configPane.getConfigUrl();
            SonarFindBugsPluginUtils.storeConfig(configUrl == null ? null : configUrl.toExternalForm());
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    boolean valid() {
        return true;
    }

    SonarCheckstylePluginConfigPane getConfigPane() {
        return configPane;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.alexanderlindhorst.sonar.checkstyle.plugin.options.gui.SonarCheckstylePluginConfigPane configPane;
    // End of variables declaration//GEN-END:variables
}
