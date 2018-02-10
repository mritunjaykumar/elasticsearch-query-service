package com.rackspacecloud.blueflood.elasticsearchqueryservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rackspacecloud.blueflood.elasticsearchqueryservice.domain.EventsQueryModel;
import com.rackspacecloud.blueflood.elasticsearchqueryservice.domain.MetricsQueryModel;
import com.rackspacecloud.blueflood.elasticsearchqueryservice.model.EventsSearchResult;
import com.rackspacecloud.blueflood.elasticsearchqueryservice.model.MetricsSearchResult;
import com.rackspacecloud.blueflood.elasticsearchqueryservice.utils.GlobPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ElasticsearchService implements IElasticsearchService {
    @Value("${elasticsearch.url}")
    private String elasticsearchRootUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchService.class);

    private final int MAX_SIZE = 10000;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public List<MetricsSearchResult> fetchMetrics(String tenantId, String queryString) throws Exception {
        String logMethodName = "ElasticsearchService.fetchMetrics";
        LOGGER.info("{}: received tenantId = '{}' and query= '{}'", logMethodName, tenantId, queryString);

        String payload = getMetricsQueryPayload(tenantId, queryString);

        HttpEntity<String> request = new HttpEntity<>(payload);
        String url = String.format("%s/metric_metadata/_search?size=%d", elasticsearchRootUrl, MAX_SIZE);

        LOGGER.debug("{}: url = '{}' and payload = '{}'", logMethodName, url, payload);
        String response = restTemplate.exchange(url, HttpMethod.POST, request, String.class).getBody();
        LOGGER.debug("{}: elasticsearch response = '{}'", logMethodName, response);

        return getSearchResults(response);
    }

    private String getMetricsQueryPayload(String tenantId, String queryString) throws Exception {
        GlobPattern pattern = new GlobPattern(queryString);

        MetricsQueryModel queryModel;

        if(pattern.hasWildcard()){
            String compiledString = pattern.compiled().toString();
            queryModel = new MetricsQueryModel(tenantId, compiledString, true);
        }
        else{
            queryModel = new MetricsQueryModel(tenantId, queryString, false);
        }

        ObjectMapper mapper = new ObjectMapper();

        String payload = "";
        try {
            payload = mapper.writeValueAsString(queryModel);
        }
        catch (JsonProcessingException ex){
            System.out.println(ex.getMessage());
        }

        if(StringUtils.isEmpty(payload)) {
            throw new Exception("Payload to ES is blank.");
        }
        return payload;
    }

    private List<MetricsSearchResult> getSearchResults(String response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);

        Iterator<JsonNode> iter = root.get("hits").get("hits").elements();

        List<MetricsSearchResult> searchResults = new ArrayList<>();
        while(iter.hasNext()){
            JsonNode source = iter.next().get("_source");
            String metricName = source.get("metric_name").asText();
            String unit = null;
            if(source.has("unit"))
                unit = source.get("unit").asText();

            MetricsSearchResult result = new MetricsSearchResult(metricName, unit);
            searchResults.add(result);
        }
        return searchResults;
    }

    @Override
    public List<EventsSearchResult> fetchEvents(String tenantId, long from, long until) throws Exception {
        String logMethodName = "ElasticsearchService.fetchEvents";
        LOGGER.info("{}: received tenantId = '{}' and from = '{}' until = '{}'", logMethodName, tenantId, from, until);

        String payload = getEventsQueryPayload(tenantId, from, until);

        HttpEntity<String> request = new HttpEntity<>(payload);
        String url = String.format("%s/events/_search?size=%d", elasticsearchRootUrl, MAX_SIZE);

        LOGGER.debug("{}: url = '{}' and payload = '{}'", logMethodName, url, payload);
        String response = restTemplate.exchange(url, HttpMethod.POST, request, String.class).getBody();
        LOGGER.debug("{}: elasticsearch response = '{}'", logMethodName, response);

        return getEventsSearchResults(response);
    }

    private List<EventsSearchResult> getEventsSearchResults(String response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);

        Iterator<JsonNode> iter = root.get("hits").get("hits").elements();

        List<EventsSearchResult> searchResults = new ArrayList<>();
        while(iter.hasNext()){
            JsonNode source = iter.next().get("_source");
            String what = source.get("what").asText();
            String when = source.get("when").asText();
            String tenantId = source.get("tenantId").asText();

            String tags = null;
            if(source.has("tags")) tags = source.get("tags").asText();

            String data = null;
            if(source.has("data")) data = source.get("data").asText();

            EventsSearchResult result = new EventsSearchResult(what, when, tags, tenantId, data);
            searchResults.add(result);
        }
        return searchResults;
    }

    private String getEventsQueryPayload(String tenantId, long from, long until) throws Exception {
        EventsQueryModel queryModel = new EventsQueryModel(tenantId, from, until);

        ObjectMapper mapper = new ObjectMapper();

        String payload = "";
        try {
            payload = mapper.writeValueAsString(queryModel);
        }
        catch (JsonProcessingException ex){
            System.out.println(ex.getMessage());
        }

        if(StringUtils.isEmpty(payload)) {
            throw new Exception("Payload to ES is blank.");
        }
        return payload;
    }
}
