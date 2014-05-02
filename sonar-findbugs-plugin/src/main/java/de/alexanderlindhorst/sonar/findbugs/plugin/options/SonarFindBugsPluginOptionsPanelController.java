package de.alexanderlindhorst.sonar.findbugs.plugin.options;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@OptionsPanelController.SubRegistration(
        location = "Advanced",
        displayName = "#AdvancedOption_DisplayName_SonarCheckstylePlugin",
        keywords = "#AdvancedOption_Keywords_SonarCheckstylePlugin",
        keywordsCategory = "Advanced/SonarCheckstylePlugin")
@org.openide.util.NbBundle.Messages({"AdvancedOption_DisplayName_SonarCheckstylePlugin=Sonar Checkstyle Plugin",
    "AdvancedOption_Keywords_SonarCheckstylePlugin=Sonar Checkstyle"})
public final class SonarFindBugsPluginOptionsPanelController extends OptionsPanelController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SonarFindBugsPluginOptionsPanelController.class);
    private SonarFindBugsPluginPanel panel;
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

    private SonarFindBugsPluginPanel getPanel() {
        if (panel == null) {
            panel = new SonarFindBugsPluginPanel(this);
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
