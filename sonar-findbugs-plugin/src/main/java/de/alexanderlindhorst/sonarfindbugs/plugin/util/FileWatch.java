package de.alexanderlindhorst.sonarfindbugs.plugin.util;

import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.alexanderlindhorst.sonarfindbugs.plugin.util.SonarFindBugsPluginUtils.processAnnotationsFor;

/**
 * @author lindhrst (original author)
 */
public class FileWatch implements FileChangeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileChangeListener.class);

    @Override
    public void fileFolderCreated(FileEvent fe) {
        LOGGER.debug("fileFolderCreated {}", fe.getFile().getPath());
    }

    @Override
    public void fileDataCreated(FileEvent fe) {
        LOGGER.debug("fileDataCreated {}", fe.getFile().getPath());
    }

    @Override
    public void fileChanged(FileEvent fe) {
        LOGGER.debug("fileChanged {}", fe.getFile().getPath());
        FileObject file = fe.getFile();
        processAnnotationsFor(file);
    }

    @Override
    public void fileDeleted(FileEvent fe) {
        LOGGER.debug("fileDeleted {}", fe.getFile().getPath());
    }

    @Override
    public void fileRenamed(FileRenameEvent fe) {
        LOGGER.debug("fileRenamed {}", fe.getFile().getPath());
    }

    @Override
    public void fileAttributeChanged(FileAttributeEvent fe) {
        LOGGER.debug("fileAttributeChanged {}", fe.getFile().getPath());
    }
}
