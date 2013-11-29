package de.alexanderlindhorst.sonar.pmd.plugin.util;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.netbeans.api.java.source.JavaSource;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.google.common.collect.Maps;

import de.alexanderlindhorst.sonar.pmd.plugin.annotation.SonarPmdAnnotation;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.RuleContext;
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

//        PerFileAuditRunner auditRunner = processFile(fileObject);
//        if (auditRunner == null) {
//            return;
//        }
//        Line.Set lineSet = getLineCookieFromFileObject(fileObject).getLineSet();
//        for (LocalizedMessage localizedMessage : auditRunner.getErrorMessages()) {
//            int targetIndex = localizedMessage.getLineNo() - 1;
//            if (targetIndex < 0) {
//                targetIndex = 0;
//            }
//            Line current = lineSet.getCurrent(targetIndex);
//            SonarPmdAnnotation annotation = new SonarPmdAnnotation(localizedMessage.getMessage());
//            annotation.attach(current);
//            annotations.add(annotation);
//        }
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

//    private static PerFileAuditRunner processFile(FileObject fileObject) {
//        PerFileAuditRunner auditRunner = null;
//        try {
//            URL configUrl = SonarPMDPluginUtils.loadConfigUrl();
//            String configContent = SonarPMDPluginUtils.loadConfigurationContent();
//            LOGGER.debug("retrieved configuration: {}", configContent);
//            if (configContent == null) {
//                auditRunner = new PerFileAuditRunner(null, Utilities.toFile(fileObject.toURI()));
//            } else {
//                InputSource inputSource = new InputSource(new StringReader(configContent));
//                Configuration config = ConfigurationLoader.loadConfiguration(inputSource, null, true);
//                LOGGER.debug("processing file using configuration {} ({})", configUrl, config);
//                auditRunner = new PerFileAuditRunner(config, Utilities.toFile(fileObject.toURI()));
//            }
//        } catch (CheckstyleException checkstyleException) {
//            Exceptions.printStackTrace(checkstyleException);
//            return auditRunner;
//        }
//        auditRunner.run();
//        if (auditRunner.hasAuditProblems()) {
//            for (Throwable throwable : auditRunner.getAuditExceptions()) {
//                Exceptions.attachMessage(throwable, throwable.getLocalizedMessage());
//            }
//        }
//        return auditRunner;
//    }
    private static void processFile(FileObject fileObject) {
        try {
            URL configUrl = SonarPMDPluginUtils.loadConfigUrl();
            String configContent = SonarPMDPluginUtils.loadConfigurationContent();
            LOGGER.debug("retrieved configuration: {}", configContent);
            PMD pmd = new PMD();
            RuleContext context = new RuleContext();
            RuleSet ruleSet;
            if (configContent == null) {
                //null config
                ruleSet = null; //get some default
//                auditRunner = new PerFileAuditRunner(null, Utilities.toFile(fileObject.toURI()));
            } else {
                //real config
                InputSource inputSource = new InputSource(new StringReader(configContent));
                ruleSet = new RuleSetFactory().createRuleSet(new ByteArrayInputStream(configContent.getBytes()));
//                LOGGER.debug("processing file using configuration {} ({})", configUrl, config);
            }
            pmd.processFile(fileObject.getInputStream(), ruleSet, context);
        } catch (Exception e) {
        }
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
