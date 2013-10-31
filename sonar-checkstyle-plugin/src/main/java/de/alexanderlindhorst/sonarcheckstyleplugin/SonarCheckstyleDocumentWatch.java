package de.alexanderlindhorst.sonarcheckstyleplugin;

import java.util.List;

import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.cookies.EditorCookie;
import org.openide.text.Line;
import org.openide.text.Line.Set;
import com.google.common.collect.Lists;

/**
 * @author lindhrst (original author)
 */
public class SonarCheckstyleDocumentWatch {

    public List<SonarCheckstyleAnnotation> active = Lists.newArrayList();

    private void clearOldAnnotations() {
        for (SonarCheckstyleAnnotation sonarCheckstyleAnnotation : active) {
            sonarCheckstyleAnnotation.detach();            
        }
        active.clear();
    }
    
    private void plasterEverythingWithAnnotations(EditorCookie cookie) {
        Set lineSet = cookie.getLineSet();
        List<? extends Line> lines = lineSet.getLines();
        for (Line line : lines) {
            SonarCheckstyleAnnotation annotation=new SonarCheckstyleAnnotation();
            active.add(annotation);
            annotation.attach(line);
        }
    }
    
    public void updateAnnotations(EditorCookie cookie) {
        if (active.isEmpty()) {
            plasterEverythingWithAnnotations(cookie);
        } else {
            clearOldAnnotations();
        }
    }
}
