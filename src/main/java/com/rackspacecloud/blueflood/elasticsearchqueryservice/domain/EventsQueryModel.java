package com.rackspacecloud.blueflood.elasticsearchqueryservice.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class EventsQueryModel {
    @JsonProperty(value = "query")
    public QueryNode queryNode;

    public EventsQueryModel(String tenantId, long from, long until){
        TenantIdTermNode tenantIdTermNode = new TenantIdTermNode(tenantId);
        TermMustNode termMustNode = new TermMustNode(tenantIdTermNode);

        RangeNode rangeNode = new RangeNode(from, until);
        RangeMustNode rangeMustNode = new RangeMustNode(rangeNode);


        BoolNode boolNode = new BoolNode();
        boolNode.mustNodes.add(termMustNode);
        boolNode.mustNodes.add(rangeMustNode);
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
        public BoolNode(){
            this.mustNodes = new ArrayList<>();
        }

        @JsonProperty(value = "must")
        public List<MustNode> mustNodes;
    }

    public abstract class MustNode { }

    public class TermMustNode extends MustNode {
        public TermMustNode(TenantIdTermNode termNode){
            this.termNode = termNode;
        }

        @JsonProperty(value = "term")
        public TenantIdTermNode termNode;
    }

    public class RangeMustNode extends MustNode {
        public RangeMustNode(RangeNode rangeNode){
            this.rangeNode = rangeNode;
        }

        @JsonProperty(value = "range")
        public RangeNode rangeNode;
    }

    public class TenantIdTermNode {
        public TenantIdTermNode(String tenantId){ this.tenantId = tenantId; }

        @JsonProperty(value = "tenantId")
        public String tenantId;
    }

    public class RangeNode {
        public RangeNode(long from, long until){ this.whenNode = new WhenNode(from, until); }

        @JsonProperty(value = "when")
        public WhenNode whenNode;
    }

    public class WhenNode {
        public WhenNode(long from, long until){
            this.from = from;
            this.until = until;
        }

        @JsonProperty(value = "from")
        public long from;

        @JsonProperty(value = "to")
        public long until;
    }
}
