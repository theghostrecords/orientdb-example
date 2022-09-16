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

    public void addEdge(OrientVertex customer, OrientVertex hotel) {
        customer.setProperty("fieldtest", "test");
        customer.addEdge("HasStayed", hotel, "HasStayed");
        customer.save();
    }

    public void commit() {
        if (!this.graph.isClosed()) {
            this.graph.commit();
            this.graph.shutdown();
        }
    }
}
