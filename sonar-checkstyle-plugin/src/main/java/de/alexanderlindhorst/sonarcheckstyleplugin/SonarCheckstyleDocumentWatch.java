package de.alexanderlindhorst.sonarcheckstyleplugin;

import java.util.List;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lindhrst (original author)
 */
public class SonarCheckstyleDocumentWatch implements FileChangeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SonarCheckstyleDocumentWatch.class);

    private Line getFirstLine(EditorCookie cookie) {
        Set lineSet = cookie.getLineSet();
        List<? extends Line> lines = lineSet.getLines();
        if (lines.isEmpty()) {
            return null;
        }
        return lines.get(0);
    }

    private void clearOldAnnotations(EditorCookie cookie) {
        Line first = getFirstLine(cookie);
        if (first == null) {
            return;
        }
        SonarCheckstyleAnnotation annotation = first.getLookup().lookup(SonarCheckstyleAnnotation.class);
        if (annotation != null) {
            annotation.detach();
        }
    }

    private void plasterEverythingWithAnnotations(EditorCookie cookie) {
        Line first = getFirstLine(cookie);
        if (first == null) {
            return;
        }
        SonarCheckstyleAnnotation annotation = new SonarCheckstyleAnnotation();
        annotation.attach(first);
    }

    private EditorCookie getEditorCookieFromFileObject(FileObject fileObject) throws DataObjectNotFoundException {
        if (fileObject.isVirtual()) {
            return null;
        }
        DataObject dataObject = DataObject.find(fileObject);
        EditorCookie cookie = dataObject.getLookup().lookup(EditorCookie.class);
        return cookie;
    }

    @Override
    public void fileFolderCreated(FileEvent fe) {
        LOGGER.debug("fileFolderCreated - nothing to do");
    }

    @Override
    public void fileDataCreated(FileEvent fe) {
        LOGGER.debug("fileChanged - plastering it");
        EditorCookie cookie = null;
        try {
            cookie = getEditorCookieFromFileObject(fe.getFile());
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (cookie == null) {
            return;
        }
        plasterEverythingWithAnnotations(cookie);
    }

    @Override
    public void fileChanged(FileEvent fe) {
        LOGGER.debug("fileChanged - plastering it");
        EditorCookie cookie = null;
        try {
            cookie = getEditorCookieFromFileObject(fe.getFile());
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (cookie == null) {
            return;
        }
        plasterEverythingWithAnnotations(cookie);
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
