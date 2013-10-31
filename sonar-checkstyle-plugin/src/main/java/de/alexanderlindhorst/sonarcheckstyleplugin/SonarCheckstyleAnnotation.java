package de.alexanderlindhorst.sonarcheckstyleplugin;

import org.openide.text.Annotation;

/**
 * @author lindhrst (original author)
 */
public class SonarCheckstyleAnnotation extends Annotation {
    
    @Override
    public String getAnnotationType() {
        return "de-alexanderlindhorst-sonarcheckstyleplugin-sonarcheckstyleannotation";
    }

    @Override
    public String getShortDescription() {
        return "This is a wannabe sonar/checkstyle issue";
    }
}
