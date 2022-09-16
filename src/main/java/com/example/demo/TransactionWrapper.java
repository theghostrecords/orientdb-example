package com.example.demo;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class TransactionWrapper {

    private final OrientGraph graph;

    public TransactionWrapper(OrientGraph tx) {
        this.graph = tx;
    }

    public OrientVertex getVertexById(String id) {
        return graph.getVertex(id);
    }

    public void addEdge(OrientVertex v, OrientVertex hotel) {
        v.addEdge("HasStayed", hotel);
    }

    public void commit() {
        this.graph.commit();
    }
}
