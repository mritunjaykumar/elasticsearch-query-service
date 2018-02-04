package com.rackspacecloud.blueflood.elasticsearchqueryservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EventsSearchResult {
    public EventsSearchResult(String what, String when, String tags, String tenantId, String data){
        this.what = what;
        this.when = when;
        this.tags = tags;
        this.tenantId = tenantId;
        this.data = data;
    }

    @JsonProperty(value = "what")
    public String what;

    @JsonProperty(value = "tenantId")
    public String tenantId;

    @JsonProperty(value = "when")
    public String when;

    @JsonProperty(value = "tags")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String tags;

    @JsonProperty(value = "data")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String data;
}
