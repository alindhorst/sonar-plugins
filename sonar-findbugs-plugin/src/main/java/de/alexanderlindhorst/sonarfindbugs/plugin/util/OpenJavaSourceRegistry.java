package de.alexanderlindhorst.sonarfindbugs.plugin.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Line;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import de.alexanderlindhorst.sonarfindbugs.plugin.annotation.SonarFindBugsAnnotation;
import de.alexanderlindhorst.sonarfindbugsprocessor.FindbugsResultProvider;

import edu.umd.cs.findbugs.BugInstance;

import static de.alexanderlindhorst.sonarfindbugs.plugin.util.SonarFindBugsPluginUtils.getUnderlyingFile;
import static de.alexanderlindhorst.sonarfindbugs.plugin.util.SonarFindBugsPluginUtils.getUnderlyingJavaFile;

/**
 * @author lindhrst (original author)
 */
public final class OpenJavaSourceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenJavaSourceRegistry.class);
    private static final Map<JavaSource, List<SonarFindBugsAnnotation>> ANNOTATION_REGISTRY = Maps.newHashMap();
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
        List<SonarFindBugsAnnotation> annotations = ANNOTATION_REGISTRY.get(source);
        if (annotations == null) {
            annotations = new ArrayList<SonarFindBugsAnnotation>();
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
        List<SonarFindBugsAnnotation> annotations = ANNOTATION_REGISTRY.get(source);

        FindbugsResultProvider resultProvider = processFile(fileObject);
        if (resultProvider == null) {
            return;
        }
        Line.Set lineSet = getLineCookieFromFileObject(fileObject).getLineSet();
        for (BugInstance bugInstance : resultProvider.getIssues()) {
            int targetIndex = bugInstance.getPrimarySourceLineAnnotation().getStartLine();
            if (targetIndex < 0) {
                targetIndex = 0;
            }
            Line current = lineSet.getCurrent(targetIndex);
            SonarFindBugsAnnotation annotation = new SonarFindBugsAnnotation(bugInstance);
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
        List<SonarFindBugsAnnotation> registeredAnnotations = ANNOTATION_REGISTRY.get(source);
        if (registeredAnnotations == null) {
            LOGGER.debug("Nothing to clear");
            return;
        }
        for (SonarFindBugsAnnotation annotation : registeredAnnotations) {
            annotation.detach();
        }
        registeredAnnotations.clear();
    }

    private static FindbugsResultProvider processFile(FileObject fileObject) {
        Project project = FileOwnerQuery.getOwner(fileObject);
        List<String> sourceRootDirs = figureOutProjectSources(project);
        List<String> classPaths = figureOutClassPaths(fileObject);
        FindbugsResultProvider provider = new FindbugsResultProvider(sourceRootDirs, classPaths, null, null, null);
        throw new UnsupportedOperationException("Not yet.");
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

    private static List<String> figureOutProjectSources(Project project) {
        Sources sources = ProjectUtils.getSources(project);
        SourceGroup[] sourceGroups = sources.getSourceGroups(Sources.TYPE_GENERIC);
        List<String> sourceRootDirs = new ArrayList<String>();
        for (SourceGroup sourceGroup : sourceGroups) {
            sourceRootDirs.add(sourceGroup.getRootFolder().toURL().toExternalForm());
        }
        LOGGER.info("Found the following source directories: {}", sourceRootDirs);
        return sourceRootDirs;
    }

    private static List<String> figureOutClassPaths(FileObject fileObject) {
        ClassPath classPath = ClassPath.getClassPath(fileObject, ClassPath.COMPILE);
        List<String> classPaths = new ArrayList<String>();
        for (FileObject fileObject1 : classPath.getRoots()) {
            classPaths.add(fileObject1.toURL().toExternalForm());
        }
        return classPaths;
    }
}
