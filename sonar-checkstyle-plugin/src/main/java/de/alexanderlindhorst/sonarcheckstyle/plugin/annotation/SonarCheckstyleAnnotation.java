package de.alexanderlindhorst.sonarcheckstyle.plugin.annotation;

import org.openide.text.Annotation;

import com.puppycrawl.tools.checkstyle.api.LocalizedMessage;

/**
 * @author lindhrst (original author)
 */
public class SonarCheckstyleAnnotation extends Annotation {

    private final LocalizedMessage errorMessage;

    public SonarCheckstyleAnnotation(LocalizedMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String getAnnotationType() {
        return "de-alexanderlindhorst-sonarcheckstyle-plugin-sonarcheckstyleannotation";
    }

    @Override
    public String getShortDescription() {
        StringBuilder builder = new StringBuilder();
        builder.append(errorMessage.getSeverityLevel()).append(" - ");
        builder.append(errorMessage.getMessage());
        builder.append(" [").append(errorMessage.getLineNo()).append('/').append(errorMessage.getColumnNo()).append(
                "]");
        return builder.toString();
    }
}
