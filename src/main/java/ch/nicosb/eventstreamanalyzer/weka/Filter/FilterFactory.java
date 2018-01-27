package ch.nicosb.eventstreamanalyzer.weka.Filter;

import weka.filters.Filter;

public interface FilterFactory {
    Filter createFilter();
}
