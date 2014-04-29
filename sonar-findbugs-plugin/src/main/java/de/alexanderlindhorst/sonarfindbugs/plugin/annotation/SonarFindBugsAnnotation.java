package de.alexanderlindhorst.sonarfindbugs.plugin.annotation;

import org.openide.text.Annotation;

import edu.umd.cs.findbugs.BugInstance;

import static java.lang.String.format;

/**
 * @author lindhrst (original author)
 */
public class SonarFindBugsAnnotation extends Annotation {

    private final BugInstance bugInstance;

    public SonarFindBugsAnnotation(BugInstance bugInstance) {
        this.bugInstance = bugInstance;
    }

    @Override
    public final String getAnnotationType() {
        return "de-alexanderlindhorst-sonarfindbugs-plugin-sonarfindbugsannotation";
    }

    @Override
    public String getShortDescription() {
        return format("%s: %s", bugInstance.getPriorityString(), bugInstance.getMessage());
    }
}
