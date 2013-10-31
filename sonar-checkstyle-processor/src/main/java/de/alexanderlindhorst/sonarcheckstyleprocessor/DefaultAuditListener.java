package de.alexanderlindhorst.sonarcheckstyleprocessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;

/**
 * @author lindhrst (original author)
 */
public class DefaultAuditListener implements AuditListener {
    //TODO: fetch config, store to disk for fallback use, copy checkstyle.Main

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAuditListener.class);

    @Override
    public void auditStarted(AuditEvent auditEvent) {
        LOGGER.debug("auditStarted called for AuditEvent "+auditEvent);
    }

    @Override
    public void auditFinished(AuditEvent auditEvent) {
        LOGGER.debug("auditFinished called for AuditEvent "+auditEvent);
    }

    @Override
    public void fileStarted(AuditEvent auditEvent) {
        LOGGER.debug("fileStarted called for AuditEvent "+auditEvent);
    }

    @Override
    public void fileFinished(AuditEvent auditEvent) {
        LOGGER.debug("fileFinished called for AuditEvent "+auditEvent);
    }

    @Override
    public void addError(AuditEvent auditEvent) {
        LOGGER.debug("addError called for AuditEvent "+auditEvent);
    }

    @Override
    public void addException(AuditEvent auditEvent, Throwable thrwbl) {
        LOGGER.debug("addException called for AuditEvent "+auditEvent);
    }
}
