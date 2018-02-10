package com.rackspacecloud.blueflood.elasticsearchqueryservice.controller;

import com.rackspacecloud.blueflood.elasticsearchqueryservice.model.EventsSearchResult;
import com.rackspacecloud.blueflood.elasticsearchqueryservice.model.MetricsSearchResult;
import com.rackspacecloud.blueflood.elasticsearchqueryservice.service.ElasticsearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequestMapping("/v3")
@RestController
public class MetricsController {
    @Autowired
    ElasticsearchService elasticsearchService;

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsController.class);

    @RequestMapping(
            value = "{tenantId}/metrics/search",
            method = RequestMethod.GET
    )
    public List<MetricsSearchResult> fetch(
            @PathVariable(value = "tenantId") final String tenantId,
            @RequestParam(value = "query") final String queryString)
    {
        //TODO: Get tracking ID from the record to stitch the tracing
        String passedTrackingId = "";
        String currentTrackingId = "";

        currentTrackingId = StringUtils.isEmpty(passedTrackingId)
                ? UUID.randomUUID().toString() : String.format("%s|%s", passedTrackingId, currentTrackingId);

        String requestVars = String.format("tenantId:[%s], query:[%s]", tenantId, queryString);

        LOGGER.info("TrackingId:{}, START: Processing", currentTrackingId);
        LOGGER.debug("TrackingId:{}, Request variables:{}", currentTrackingId, requestVars);

        //TODO: validate queryString

        List<MetricsSearchResult> result = new ArrayList<>();
        try {
            result = elasticsearchService.fetchMetrics(tenantId, queryString);
        }
        catch(Exception ex){
            LOGGER.error("TrackingId:{}, Exception message:{}", currentTrackingId, ex.getMessage());
            // TODO: Handle this in GlobalHandler?
        }

        LOGGER.info("TrackingId:{}, FINISH: Processing", currentTrackingId);
        return result;
    }

    @RequestMapping(
            value = "{tenantId}/events/getEvents",
            method = RequestMethod.GET
    )
    public List<EventsSearchResult> fetchEvents(
            @PathVariable(value = "tenantId") final String tenantId,
            @RequestParam(value = "from") final long from,
            @RequestParam(value = "until") final long until){
        try {
            return elasticsearchService.fetchEvents(tenantId, from, until);
        }
        catch (Exception ex){
            LOGGER.error(ex.getMessage());
            // TODO: Handle this in GlobalHandler?
        }
        return new ArrayList<>();
    }
}

/*
v3/tenantId/metrics/search?query=rackspace.monitoring.entities.*.byte*
 */