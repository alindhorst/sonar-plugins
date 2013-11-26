package de.alexanderlindhorst.sonarcheckstyle.plugin.annotation;

import java.util.List;

import org.openide.text.Annotation;

import com.google.common.collect.Lists;
import com.puppycrawl.tools.checkstyle.api.LocalizedMessage;

/**
 * @author lindhrst (original author)
 */
public class SonarCheckstyleAnnotation extends Annotation {

    private final List<LocalizedMessage> errorMessages = Lists.newArrayList();

    public SonarCheckstyleAnnotation(LocalizedMessage errorMessage) {
        addErrorMessage(errorMessage);
    }

    public final void addErrorMessage(LocalizedMessage errorMessage) {
        errorMessages.add(errorMessage);
    }

    @Override
    public final String getAnnotationType() {
        return "de-alexanderlindhorst-sonarcheckstyle-plugin-sonarcheckstyleannotation";
    }

    @Override
    public String getShortDescription() {
        StringBuilder builder = new StringBuilder();
        boolean hasSeveral = errorMessages.size() > 1;
        if (hasSeveral) {
            builder.append(errorMessages.size()).append(" problems:");
        }
        for (LocalizedMessage errorMessage : errorMessages) {
            if (hasSeveral) {
                builder.append("\n\t- ");
            }
            builder.append(errorMessage.getSeverityLevel().getName().toUpperCase()).append(": ");
            builder.append(errorMessage.getMessage());
        }
        return builder.toString();
    }
}
