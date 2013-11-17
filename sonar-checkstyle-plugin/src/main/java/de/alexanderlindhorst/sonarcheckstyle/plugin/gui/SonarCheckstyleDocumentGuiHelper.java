package de.alexanderlindhorst.sonarcheckstyle.plugin.gui;

import java.util.Collection;

import org.openide.cookies.EditorCookie;
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

import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.LocalizedMessage;
import de.alexanderlindhorst.sonarcheckstyle.plugin.annotation.SonarCheckstyleAnnotation;
import de.alexanderlindhorst.sonarcheckstyleprocessor.PerFileAuditRunner;

/**
 * @author lindhrst (original author)
 */
public class SonarCheckstyleDocumentGuiHelper implements FileChangeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SonarCheckstyleDocumentGuiHelper.class);

    private void clearOldAnnotations(EditorCookie cookie) {
        Set lineSet = cookie.getLineSet();
        for (Line line : lineSet.getLines()) {
            Collection<? extends SonarCheckstyleAnnotation> annotations = line.getLookup().lookupAll(
                    SonarCheckstyleAnnotation.class);
            for (SonarCheckstyleAnnotation annotation : annotations) {
                annotation.detach();
            }
        }
    }

    private void applyAnnotations(EditorCookie cookie, FileObject fileObject) {
        clearOldAnnotations(cookie);
        PerFileAuditRunner auditRunner = processFile(fileObject);

        Set lineSet = cookie.getLineSet();
        for (LocalizedMessage localizedMessage : auditRunner.getErrorMessages()) {
            SonarCheckstyleAnnotation annotation = new SonarCheckstyleAnnotation(localizedMessage);
            int targetIndex = localizedMessage.getLineNo() - 1;
            if (targetIndex < 0) {
                targetIndex = 0;
            }
            Line current = lineSet.getCurrent(targetIndex);
            annotation.attach(current);
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

    private EditorCookie getEditorCookieFromFileObject(FileObject fileObject) {
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
        EditorCookie cookie = dataObject.getLookup().lookup(EditorCookie.class);
        return cookie;
    }

    @Override
    public void fileFolderCreated(FileEvent fe) {
        LOGGER.debug("fileFolderCreated - nothing to do");
    }

    /* Implement Listener Methods */
    @Override
    public void fileDataCreated(FileEvent fe) {
        LOGGER.debug("fileChanged {}", fe.getFile().toURI());
        EditorCookie cookie = getEditorCookieFromFileObject(fe.getFile());
        if (cookie == null) {
            return;
        }
        applyAnnotations(cookie, fe.getFile());
    }

    @Override
    public void fileChanged(FileEvent fe) {
        LOGGER.debug("fileChanged: {}", fe.getFile().toURI());
        EditorCookie cookie = getEditorCookieFromFileObject(fe.getFile());
        if (cookie == null) {
            return;
        }
        applyAnnotations(cookie, fe.getFile());
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
