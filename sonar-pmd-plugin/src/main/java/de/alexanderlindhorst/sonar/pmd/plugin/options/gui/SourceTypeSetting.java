/*
 *  LICENSE INFORMATION:
 */
package de.alexanderlindhorst.sonar.pmd.plugin.options.gui;

import java.awt.Component;
import java.awt.SystemColor;

import org.openide.util.NbBundle;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import net.sourceforge.pmd.SourceType;

/**
 *
 * @author lindhrst
 */
enum SourceTypeSetting {

    JAVA_13("JAVA_13", SourceType.JAVA_13),
    JAVA_14("JAVA_14", SourceType.JAVA_14),
    JAVA_15("JAVA_15", SourceType.JAVA_15),
    JAVA_16("JAVA_16", SourceType.JAVA_16),
    JAVA_17("JAVA_17", SourceType.JAVA_17);
    private String id;
    private SourceType sourceType;

    private SourceTypeSetting(String id, SourceType sourceType) {
        this.id = id;
        this.sourceType = sourceType;
    }

    public String getId() {
        return id;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public static ListCellRenderer<SourceTypeSetting> getListCellRenderer() {
        return new Renderer();
    }

    public static ComboBoxModel<SourceTypeSetting> getComboBoxModel() {
        DefaultComboBoxModel<SourceTypeSetting> model = new DefaultComboBoxModel<SourceTypeSetting>();
        for (SourceTypeSetting setting : values()) {
            model.addElement(setting);
        }
        return model;
    }

    private static class Renderer implements ListCellRenderer<SourceTypeSetting> {

        @Override
        public Component getListCellRendererComponent(
                JList<? extends SourceTypeSetting> list, SourceTypeSetting value, int index, boolean isSelected,
                boolean cellHasFocus) {
            JLabel label = new JLabel();
            label.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
            if (isSelected) {
                label.setBackground(SystemColor.textHighlight);
                label.setForeground(SystemColor.textHighlightText);
            } else {
                label.setBackground(SystemColor.text);
                label.setForeground(SystemColor.textText);
            }
            label.setText(NbBundle.getMessage(SonarPMDPluginConfigPane.class,
                    "SonarPMDPluginConfigPane.sourceTypeDropDown." + value.getId()));
            return label;
        }
    }
}
