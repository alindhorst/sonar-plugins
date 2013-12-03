
package de.alexanderlindhorst.sonarpmdprocessor;

import net.sourceforge.pmd.IRuleViolation;
import net.sourceforge.pmd.ReportListener;
import net.sourceforge.pmd.stat.Metric;

/**
 * @author alindhorst
 */
public class PMDResultProvider implements ReportListener{

    @Override
    public void ruleViolationAdded(IRuleViolation ruleViolation) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void metricAdded(Metric metric) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
