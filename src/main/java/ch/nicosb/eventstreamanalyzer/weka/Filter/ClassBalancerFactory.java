package ch.nicosb.eventstreamanalyzer.weka.Filter;

import weka.filters.Filter;
import weka.filters.supervised.instance.ClassBalancer;

public class ClassBalancerFactory implements FilterFactory {
    @Override
    public Filter createFilter() {
        ClassBalancer classBalancer = new ClassBalancer();
        classBalancer.setNumIntervals(10);
        return classBalancer;
    }
}
