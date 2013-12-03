package de.alexanderlindhorst.sonarpmdprocessor;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import net.sourceforge.pmd.IRuleViolation;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;

/**
 * @author alindhorst
 */
public class PerFilePMDAuditRunner implements Runnable {

    private final RuleSet ruleSet;
    private final File file;
    private final RuleContext ruleContext;
    private final PMDResultProvider resultProvider;
    private Exception exception;

    public PerFilePMDAuditRunner(RuleSet ruleSet, File file) {
        this.ruleSet = ruleSet;
        if (ruleSet == null) {
            try {
                ruleSet = new RuleSetFactory().createSingleRuleSet("basic.xml");
            } catch (RuleSetNotFoundException ex) {
                exception = ex;
            }
        }
        this.file = file;
        ruleContext = new RuleContext();
        resultProvider = new PMDResultProvider();
        ruleContext.getReport().addListener(resultProvider);
    }

    @Override
    public void run() {
        if (hasAuditProblem()) {
            return;
        }
        PMD pmd = new PMD();
        try {
            pmd.processFile(new FileReader(file), ruleSet, ruleContext);
        } catch (Exception ex) {
            exception = ex;
        }
    }

    public boolean hasAuditProblem() {
        return exception != null;
    }

    public Exception getAuditProblem() {
        return exception;
    }

    public List<IRuleViolation> getViolations() {
        return resultProvider.getViolations();
    }
}
