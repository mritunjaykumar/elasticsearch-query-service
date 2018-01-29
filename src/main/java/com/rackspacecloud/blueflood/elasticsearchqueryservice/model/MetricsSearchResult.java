package com.rackspacecloud.blueflood.elasticsearchqueryservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MetricsSearchResult {
    public MetricsSearchResult(String metricName, String unit){
        this.metricName = metricName;
        this.unit = unit;
    }

    @JsonProperty(value = "metric")
    public String metricName;

    @JsonProperty(value = "unit")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String unit;
}
