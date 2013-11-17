package de.alexanderlindhorst.sonarcheckstyleprocessor;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.LocalizedMessage;

/**
 * @author lindhrst (original author)
 */
class ResultProvider implements AuditListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResultProvider.class);
    private List<LocalizedMessage> errorMessages = Lists.newArrayList();
    private List<Throwable> auditExceptions = Lists.newArrayList();

    @Override
    public void auditStarted(AuditEvent auditEvent) {
        LOGGER.debug("auditStarted called for AuditEvent " + auditEvent);
    }

    @Override
    public void auditFinished(AuditEvent auditEvent) {
        LOGGER.debug("auditFinished called for AuditEvent " + auditEvent);
    }

    @Override
    public void fileStarted(AuditEvent auditEvent) {
        LOGGER.debug("fileStarted called for AuditEvent " + auditEvent);
    }

    @Override
    public void fileFinished(AuditEvent auditEvent) {
        LOGGER.debug("fileFinished called for AuditEvent " + auditEvent);
    }

    @Override
    public void addError(AuditEvent auditEvent) {
        LOGGER.debug("addError called for AuditEvent " + auditEvent);
        errorMessages.add(auditEvent.getLocalizedMessage());
    }

    @Override
    public void addException(AuditEvent auditEvent, Throwable thrwbl) {
        LOGGER.debug("addException called for AuditEvent " + auditEvent);
        auditExceptions.add(thrwbl);
    }

    public List<Throwable> getAuditExceptions() {
        return auditExceptions;
    }

    public List<LocalizedMessage> getErrorMessages() {
        return errorMessages;
    }

    public boolean hasAuditExceptions() {
        return !auditExceptions.isEmpty();
    }
}
