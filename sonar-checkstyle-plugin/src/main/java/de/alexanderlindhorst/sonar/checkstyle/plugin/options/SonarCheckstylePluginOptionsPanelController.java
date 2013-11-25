package de.alexanderlindhorst.sonar.checkstyle.plugin.options;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

import javax.swing.JComponent;

@OptionsPanelController.SubRegistration(
        location = "Advanced",
        displayName = "#AdvancedOption_DisplayName_SonarCheckstylePlugin",
        keywords = "#AdvancedOption_Keywords_SonarCheckstylePlugin",
        keywordsCategory = "Advanced/SonarCheckstylePlugin")
@org.openide.util.NbBundle.Messages({"AdvancedOption_DisplayName_SonarCheckstylePlugin=Sonar Checkstyle Plugin",
    "AdvancedOption_Keywords_SonarCheckstylePlugin=Sonar Checkstyle"})
public final class SonarCheckstylePluginOptionsPanelController extends OptionsPanelController {

    private SonarCheckstylePluginPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    @Override
    public void update() {
        getPanel().load();
        changed = false;
    }

    @Override
    public void applyChanges() {
        getPanel().store();
        changed = false;
    }

    @Override
    public void cancel() {
    }

    @Override
    public boolean isValid() {
        return getPanel().valid();
    }

    @Override
    public boolean isChanged() {
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

    private SonarCheckstylePluginPanel getPanel() {
        if (panel == null) {
            panel = new SonarCheckstylePluginPanel(this);
        }
        return panel;
    }

    void changed() {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }
}
