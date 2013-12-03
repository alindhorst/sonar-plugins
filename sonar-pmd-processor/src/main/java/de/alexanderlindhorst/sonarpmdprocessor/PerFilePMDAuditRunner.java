package de.alexanderlindhorst.sonarpmdprocessor;

import java.io.File;
import java.io.FileReader;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;

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
        this.file = file;
        ruleContext = new RuleContext();
        resultProvider = new PMDResultProvider();
        ruleContext.getReport().addListener(resultProvider);
    }

    @Override
    public void run() {
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

    public Object getViolations() {
        return resultProvider.getViolations();
    }
}
