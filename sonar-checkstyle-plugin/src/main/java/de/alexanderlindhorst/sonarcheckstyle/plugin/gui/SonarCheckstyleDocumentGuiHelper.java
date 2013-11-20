package de.alexanderlindhorst.sonarcheckstyle.plugin.gui;

import java.util.Collection;
import java.util.Map;

import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Line;
import org.openide.text.Line.Set;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.LocalizedMessage;

import de.alexanderlindhorst.sonarcheckstyle.plugin.annotation.SonarCheckstyleAnnotation;
import de.alexanderlindhorst.sonarcheckstyleprocessor.PerFileAuditRunner;

/**
 * @author lindhrst (original author)
 */
public class SonarCheckstyleDocumentGuiHelper implements FileChangeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SonarCheckstyleDocumentGuiHelper.class);

    public void processAnnotationsFor(FileObject fileObject) {
        LineCookie cookie = getLineCookieFromFileObject(fileObject);
        if (cookie == null) {
            LOGGER.debug("Couldn't find cookie for {}", fileObject.toURI());
            return;
        }
        clearOldAnnotations(cookie);
        applyAnnotations(cookie, fileObject);
    }

    private void clearOldAnnotations(LineCookie cookie) {
        Set lineSet = cookie.getLineSet();
        for (Line line : lineSet.getLines()) {
            Collection<? extends SonarCheckstyleAnnotation> annotations = line.getLookup().lookupAll(
                    SonarCheckstyleAnnotation.class);
            for (SonarCheckstyleAnnotation annotation : annotations) {
                annotation.detach();
            }
        }
    }

    private void applyAnnotations(LineCookie cookie, FileObject fileObject) {
        PerFileAuditRunner auditRunner = processFile(fileObject);
        Map<Integer, SonarCheckstyleAnnotation> lineAnnotationMap = Maps.newHashMap();

        Set lineSet = cookie.getLineSet();
        for (LocalizedMessage localizedMessage : auditRunner.getErrorMessages()) {
            int targetIndex = localizedMessage.getLineNo() - 1;
            if (targetIndex < 0) {
                targetIndex = 0;
            }
            Line current = lineSet.getCurrent(targetIndex);
            SonarCheckstyleAnnotation annotation = lineAnnotationMap.get(targetIndex);
            if (annotation == null) {
                //new annotation at this line
                annotation = new SonarCheckstyleAnnotation(localizedMessage);
                annotation.attach(current);
                lineAnnotationMap.put(targetIndex, annotation);
            } else {
                //annotation is already there
                annotation.addErrorMessage(localizedMessage);
            }
        }
    }

    private PerFileAuditRunner processFile(FileObject fileObject) {
        PerFileAuditRunner auditRunner = null;
        try {
            auditRunner = new PerFileAuditRunner(null, Utilities.toFile(fileObject.toURI()));
        } catch (CheckstyleException checkstyleException) {
            LOGGER.error("Couldn't perform checkstyle audit", checkstyleException);
            Exceptions.attachMessage(checkstyleException, checkstyleException.getLocalizedMessage());
            return auditRunner;
        }
        auditRunner.run();
        if (auditRunner.hasAuditProblems()) {
            for (Throwable throwable : auditRunner.getAuditExceptions()) {
                Exceptions.attachMessage(throwable, throwable.getLocalizedMessage());
            }
        }
        return auditRunner;
    }

    private LineCookie getLineCookieFromFileObject(FileObject fileObject) {
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

    @Override
    public void fileFolderCreated(FileEvent fe) {
        LOGGER.debug("fileFolderCreated - nothing to do");
    }

    /*
     * Implement Listener Methods
     */
    @Override
    public void fileDataCreated(FileEvent fe) {
        LOGGER.debug("fileChanged {}", fe.getFile().toURI());
        processAnnotationsFor(fe.getFile());
    }

    @Override
    public void fileChanged(FileEvent fe) {
        LOGGER.debug("fileChanged {}", fe.getFile());
        processAnnotationsFor(fe.getFile());
    }

    @Override
    public void fileDeleted(FileEvent fe) {
        LOGGER.debug("fileDeleted - nothing to do");
    }

    @Override
    public void fileRenamed(FileRenameEvent fe) {
        LOGGER.debug("fileRenamed - nothing to do");
    }

    @Override
    public void fileAttributeChanged(FileAttributeEvent fe) {
        LOGGER.debug("fileAttributeChanged - nothing to do");
    }
}
