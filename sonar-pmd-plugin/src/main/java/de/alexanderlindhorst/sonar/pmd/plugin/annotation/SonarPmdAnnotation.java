package de.alexanderlindhorst.sonar.pmd.plugin.annotation;


import org.openide.text.Annotation;


/**
 * @author lindhrst (original author)
 */
public class SonarPmdAnnotation extends Annotation {
    private String message;

    public SonarPmdAnnotation(String errorMessage) {
        message=errorMessage;
    }

    @Override
    public final String getAnnotationType() {
        return "de-alexanderlindhorst-sonar-pmd-plugin-sonar-pmd-annotation";
    }

    @Override
    public String getShortDescription() {
        return message;
    }
}
