package ch.nicosb.eventstreamanalyzer.weka.Filter;

import weka.filters.Filter;
import weka.filters.supervised.instance.SMOTE;

public class SMOTEFactory implements FilterFactory{
    @Override
    public Filter createFilter() {
        SMOTE smote = new SMOTE();
        smote.setPercentage(300);

        return smote;
    }
}
