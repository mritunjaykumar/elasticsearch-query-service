package com.rackspacecloud.blueflood.elasticsearchqueryservice.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ElasticsearchRestQueryModel {
    @JsonProperty(value = "query")
    public QueryNode queryNode;

    public ElasticsearchRestQueryModel(String tenantId, String queryString){
        TenantIdTermNode tenantIdTermNode = new TenantIdTermNode(tenantId);
        MetricNameTermNode metricNameTermNode = new MetricNameTermNode(queryString);

        MustNode mustNode = new MustNode(tenantIdTermNode);
        ShouldNode shouldNode = new ShouldNode(metricNameTermNode);

        BoolNode boolNode = new BoolNode(mustNode, shouldNode);
        this.queryNode = new QueryNode(boolNode);
    }

    public class QueryNode {
        public QueryNode(BoolNode boolNode){
            this.boolNode = boolNode;
        }

        @JsonProperty("bool")
        public BoolNode boolNode;
    }

    public class BoolNode {
        public BoolNode(MustNode mustNode, ShouldNode shouldNode){
            this.mustNodes = new ArrayList<>();
            this.mustNodes.add(mustNode);
            this.shouldNodes = new ArrayList<>();
            this.shouldNodes.add(shouldNode);
            this.minimumShouldMatch = 1;
        }

        @JsonProperty(value = "must")
        public List<MustNode> mustNodes;

        @JsonProperty(value = "should")
        public List<ShouldNode> shouldNodes;

        @JsonProperty(value = "minimum_should_match")
        public int minimumShouldMatch;
    }

    public class MustNode {
        public MustNode(TermNode termNode){
            this.termNode = termNode;
        }

        @JsonProperty(value = "term")
        public TermNode termNode;
    }

    public class ShouldNode {
        public ShouldNode(TermNode termNode){
            this.termNode = termNode;
        }

        @JsonProperty(value = "term")
        public TermNode termNode;
    }

    public abstract class TermNode {

    }

    public class TenantIdTermNode extends TermNode {
        public TenantIdTermNode(String tenantId){ this.tenantId = tenantId; }

        @JsonProperty(value = "tenantId")
        public String tenantId;
    }

    public class MetricNameTermNode extends TermNode {
        public MetricNameTermNode(String metricName){ this.metricName = metricName; }

        @JsonProperty(value = "metric_name")
        public String metricName;
    }
}
