package com.rackspacecloud.blueflood.elasticsearchqueryservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rackspacecloud.blueflood.elasticsearchqueryservice.domain.ElasticsearchRestQueryModel;
import com.rackspacecloud.blueflood.elasticsearchqueryservice.model.MetricsSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ElasticsearchService implements IElasticsearchService {
    @Value("${ELASTICSEARCH_ROOT_URL}")
    private String elasticsearchRootUrl;

    private final int MAX_SIZE = 10000;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public List<MetricsSearchResult> fetch(String tenantId, String queryString) throws Exception {
        ElasticsearchRestQueryModel queryModel = new ElasticsearchRestQueryModel(tenantId, queryString);

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

        HttpEntity<String> request = new HttpEntity<>(payload);

        String url = String.format("%s/metric_metadata/_search?size=%d", elasticsearchRootUrl, MAX_SIZE);

        String response = restTemplate.exchange(url, HttpMethod.POST, request, String.class).getBody();

        return getSearchResults(response);
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
}
