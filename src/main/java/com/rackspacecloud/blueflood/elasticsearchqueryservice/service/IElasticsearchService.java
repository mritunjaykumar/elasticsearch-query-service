package com.rackspacecloud.blueflood.elasticsearchqueryservice.service;

import com.rackspacecloud.blueflood.elasticsearchqueryservice.model.EventsSearchResult;
import com.rackspacecloud.blueflood.elasticsearchqueryservice.model.MetricsSearchResult;

import java.util.List;

public interface IElasticsearchService {
    List<MetricsSearchResult> fetchMetrics(String tenantId, String queryString) throws Exception;
    List<EventsSearchResult> fetchEvents(String tenantId, long from, long until) throws Exception;
}
