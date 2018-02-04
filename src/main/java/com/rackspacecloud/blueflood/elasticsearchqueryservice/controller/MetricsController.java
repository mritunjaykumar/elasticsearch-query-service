package com.rackspacecloud.blueflood.elasticsearchqueryservice.controller;

import com.rackspacecloud.blueflood.elasticsearchqueryservice.model.MetricsSearchResult;
import com.rackspacecloud.blueflood.elasticsearchqueryservice.service.ElasticsearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/v3")
@RestController
public class MetricsController {
    @Autowired
    ElasticsearchService elasticsearchService;

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsController.class);

    @RequestMapping(
            value = "{tenantId}/metrics/search",
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public List<MetricsSearchResult> fetch(
            @PathVariable(value = "tenantId") final String tenantId,
            @RequestParam(value = "query") final String queryString)
    {
        LOGGER.info("MetricsController: Received tenantId = '{}' and query= '{}'", tenantId, queryString);

        //TODO: validate queryString

        try {
            return elasticsearchService.fetch(tenantId, queryString);
        }
        catch(Exception ex){
            LOGGER.error(ex.getMessage());
            // TODO: Handle this in GlobalHandler?
        }

        return new ArrayList<>();
    }

    @RequestMapping(
            value = "tenantId}/events/getEvents",
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public List<MetricsSearchResult> fetchEvent(
            @PathVariable(value = "tenantId") final String tenantId,
            @RequestParam(value = "from") final long from,
            @RequestParam(value = "until") final long until){
        try {
            return elasticsearchService.fetchEvent(tenantId, from, until);
        }
        catch (Exception ex){
            return new ArrayList<>();
        }

    }
}

/*
v3/tenantId/metrics/search?query=rackspace.monitoring.entities.*.byte*
 */