package ch.nicosb.eventstreamanalyzer.weka.Filter;

import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class EmptyFilterFactory implements FilterFactory{

    @Override
    public Filter createFilter() {
        Remove remove = new Remove();
        remove.setAttributeIndicesArray(new int[]{});

        return remove;
    }
}
