package com.rackspacecloud.blueflood.elasticsearchqueryservice.service;

import com.rackspacecloud.blueflood.elasticsearchqueryservice.model.MetricsSearchResult;

import java.util.List;

public interface IElasticsearchService {
    List<MetricsSearchResult> fetch(String tenantId, String queryString) throws Exception;
}
