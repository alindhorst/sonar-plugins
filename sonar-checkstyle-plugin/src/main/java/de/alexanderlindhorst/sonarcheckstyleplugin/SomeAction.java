/*
 *  LICENSE INFORMATION:
 */
package de.alexanderlindhorst.sonarcheckstyleplugin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.EditorCookie;

@ActionID(category = "Tools",id = "de.alexanderlindhorst.sonarcheckstyleplugin.SomeAction")
@ActionRegistration(displayName = "SomeAction")
@ActionReferences({@ActionReference(path="Menu/File")})
public final class SomeAction implements ActionListener {

    private final EditorCookie context;
    private final SonarCheckstyleDocumentWatch watch;

    public SomeAction(EditorCookie context) {
        this.context = context;
        watch = new SonarCheckstyleDocumentWatch();
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        watch.updateAnnotations(context);
    }
}
