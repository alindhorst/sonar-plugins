/*
 * Copyright (C) 2013 alindhorst.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package de.alexanderlindhorst.sonar.pmd.plugin.options;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@OptionsPanelController.SubRegistration(
        displayName = "#AdvancedOption_DisplayName_SonarPMD",
        keywords = "#AdvancedOption_Keywords_SonarPMD",
        keywordsCategory = "Advanced/SonarPMD"
)
@org.openide.util.NbBundle.Messages({"AdvancedOption_DisplayName_SonarPMD=Sonar PMD", "AdvancedOption_Keywords_SonarPMD=Sonar PMD"})
public final class SonarPMDOptionsPanelController extends OptionsPanelController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SonarPMDOptionsPanelController.class);

    private SonarPMDPluginPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    @Override
    public void update() {
        LOGGER.debug("update");
        getPanel().load();
        changed = false;
        getPanel().getConfigPane().addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                changed();
            }
        });
    }

    @Override
    public void applyChanges() {
        LOGGER.debug("applyChanges");
        getPanel().store();
        changed = false;
    }

    @Override
    public void cancel() {
        LOGGER.debug("cancel");
    }

    @Override
    public boolean isValid() {
        LOGGER.debug("isValid");
        return getPanel().valid();
    }

    @Override
    public boolean isChanged() {
        LOGGER.debug("isChanged");
        return changed;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("...ID") if you have a help set
    }

    @Override
    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    private SonarPMDPluginPanel getPanel() {
        if (panel == null) {
            panel = new SonarPMDPluginPanel(this);
        }
        return panel;
    }

    void changed() {
        LOGGER.debug("changed");
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }

}
