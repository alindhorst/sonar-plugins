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
import net.sourceforge.pmd.SourceType;

/**
 * @author alindhorst
 */
public class PerFilePMDAuditRunner implements Runnable {

    private final RuleSet ruleSet;
    private final RuleContext ruleContext;
    private final PMDResultProvider resultProvider;
    private Exception exception;

    public PerFilePMDAuditRunner(RuleSet ruleSet, File file) {
        RuleSet checked = null;
        if (ruleSet == null) {
            try {
                checked = new RuleSetFactory().createSingleRuleSet("rulesets/basic.xml");
            } catch (RuleSetNotFoundException ex) {
                exception = ex;
            }
        } else {
            checked = ruleSet;
        }
        this.ruleSet = checked;
        ruleContext = new RuleContext();
        resultProvider = new PMDResultProvider();
        ruleContext.setSourceCodeFile(file);
        ruleContext.setSourceCodeFilename(file.getAbsolutePath());
        //TODO: make this configurable
        ruleContext.setSourceType(SourceType.JAVA_16);
        ruleContext.getReport().addListener(resultProvider);
    }

    @Override
    public void run() {
        if (hasAuditProblem()) {
            return;
        }
        PMD pmd = new PMD();
        try {
            pmd.processFile(new FileReader(ruleContext.getSourceCodeFile()), ruleSet, ruleContext);
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