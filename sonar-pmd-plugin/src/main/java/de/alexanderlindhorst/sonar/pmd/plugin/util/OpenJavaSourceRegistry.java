package de.alexanderlindhorst.sonar.pmd.plugin.util;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.netbeans.api.java.source.JavaSource;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Line;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import de.alexanderlindhorst.sonar.pmd.plugin.annotation.SonarPmdAnnotation;
import de.alexanderlindhorst.sonarpmdprocessor.PerFilePMDAuditRunner;

import net.sourceforge.pmd.IRuleViolation;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import static de.alexanderlindhorst.sonar.pmd.plugin.util.SonarPMDPluginUtils.getUnderlyingFile;
import static de.alexanderlindhorst.sonar.pmd.plugin.util.SonarPMDPluginUtils.getUnderlyingJavaFile;

/**
 * @author lindhrst (original author)
 */
public final class OpenJavaSourceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenJavaSourceRegistry.class);
    private static final Map<JavaSource, List<SonarPmdAnnotation>> ANNOTATION_REGISTRY = Maps.newHashMap();
    private static final Map<TopComponent, JavaSource> TOP_COMPONENT_REGISTRY = Maps.newHashMap();

    private OpenJavaSourceRegistry() {
        //utility class
    }

    public static boolean isKnownTopComponent(TopComponent component) {
        return TOP_COMPONENT_REGISTRY.containsKey(component);
    }

    public static boolean isKnownJavaSource(JavaSource source) {
        return TOP_COMPONENT_REGISTRY.values().contains(source) && ANNOTATION_REGISTRY.containsKey(source);
    }

    public static void markTopComponentOpened(TopComponent topComponent) {
        JavaSource source = getUnderlyingJavaFile(topComponent);
        List<SonarPmdAnnotation> annotations = ANNOTATION_REGISTRY.get(source);
        if (annotations == null) {
            annotations = new ArrayList<SonarPmdAnnotation>();
            ANNOTATION_REGISTRY.put(source, annotations);
        }
        if (!TOP_COMPONENT_REGISTRY.keySet().contains(topComponent)) {
            TOP_COMPONENT_REGISTRY.put(topComponent, source);
        }
        FileObject underlyingFile = getUnderlyingFile(topComponent);
        clearOldAnnotationsFor(source);
        applyAnnotationsFor(underlyingFile);
    }

    public static void markTopComponentClosed(TopComponent topComponent) {
        JavaSource source = TOP_COMPONENT_REGISTRY.get(topComponent);
        clearOldAnnotationsFor(source);
        ANNOTATION_REGISTRY.remove(source);
        TOP_COMPONENT_REGISTRY.remove(topComponent);
    }

    public static void applyAnnotationsFor(FileObject fileObject) {
        JavaSource source = JavaSource.forFileObject(fileObject);
        List<SonarPmdAnnotation> annotations = ANNOTATION_REGISTRY.get(source);

        PerFilePMDAuditRunner auditRunner = processFile(fileObject);
        if (auditRunner == null) {
            return;
        }
        Line.Set lineSet = getLineCookieFromFileObject(fileObject).getLineSet();
        for (IRuleViolation violation : auditRunner.getViolations()) {
            int targetIndex = violation.getBeginLine() - 1;
            if (targetIndex < 0) {
                targetIndex = 0;
            }
            Line current = lineSet.getCurrent(targetIndex);
            SonarPmdAnnotation annotation = new SonarPmdAnnotation(violation.getDescription());
            annotation.attach(current);
            annotations.add(annotation);
        }
    }

    public static void clearOldAnnotationsFor(FileObject fileObject) {
        LOGGER.debug("Attempting to clean annotations for {}", fileObject);
        JavaSource source = JavaSource.forFileObject(fileObject);
        clearOldAnnotationsFor(source);
    }

    private static void clearOldAnnotationsFor(JavaSource source) {
        List<SonarPmdAnnotation> registeredAnnotations = ANNOTATION_REGISTRY.get(source);
        if (registeredAnnotations == null) {
            LOGGER.debug("Nothing to clear");
            return;
        }
        for (SonarPmdAnnotation annotation : registeredAnnotations) {
            annotation.detach();
        }
        registeredAnnotations.clear();
    }

    private static PerFilePMDAuditRunner processFile(FileObject fileObject) {
        try {
            URL configUrl = SonarPMDPluginUtils.loadConfigUrl();
            String configContent = SonarPMDPluginUtils.loadConfigurationContent();
            LOGGER.debug("retrieved configuration: {}", configContent);
            RuleSet ruleSet;
            if (configContent == null) {
                LOGGER.debug("No config given, reverting to basic ruleset");
                ruleSet = null;
            } else {
                //real config
                LOGGER.debug("processing file using configuration {}", configUrl);
                ruleSet = new RuleSetFactory().createRuleSet(new ByteArrayInputStream(configContent.getBytes()));
            }
            return new PerFilePMDAuditRunner(ruleSet, Utilities.toFile(fileObject.toURI()));
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
        return null;
    }

    private static LineCookie getLineCookieFromFileObject(FileObject fileObject) {
        if (fileObject.isVirtual()) {
            return null;
        }
        DataObject dataObject;
        try {
            dataObject = DataObject.find(fileObject);
        } catch (DataObjectNotFoundException dataObjectNotFoundException) {
            Exceptions.printStackTrace(dataObjectNotFoundException);
            return null;
        }
        return dataObject.getLookup().lookup(LineCookie.class);
    }
}
