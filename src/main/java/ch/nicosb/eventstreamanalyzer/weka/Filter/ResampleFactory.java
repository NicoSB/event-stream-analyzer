package ch.nicosb.eventstreamanalyzer.weka.Filter;

import weka.filters.Filter;
import weka.filters.supervised.instance.Resample;

public class ResampleFactory implements FilterFactory {
    @Override
    public Filter createFilter() {
        Resample filter = new Resample();
        filter.setBiasToUniformClass(1.0);
        filter.setNoReplacement(false);
        filter.setSampleSizePercent(100);

        return filter;
    }
}
