package de.alexanderlindhorst.sonarpmdprocessor;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.sourceforge.pmd.IRuleViolation;
import net.sourceforge.pmd.ReportListener;
import net.sourceforge.pmd.stat.Metric;

/**
 * @author alindhorst
 */
class PMDResultProvider implements ReportListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PMDResultProvider.class);
    private List<IRuleViolation> violations = new ArrayList<IRuleViolation>();

    @Override
    public void ruleViolationAdded(IRuleViolation ruleViolation) {
        LOGGER.debug("Violation added: {}", ruleViolation);
        violations.add(ruleViolation);
    }

    @Override
    public void metricAdded(Metric metric) {
        LOGGER.debug("Metric added: {}", metric);
    }

    List<IRuleViolation> getViolations() {
        return violations;
    }
}
