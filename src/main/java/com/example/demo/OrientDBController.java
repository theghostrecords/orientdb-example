package com.example.demo;

import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("/")
public class OrientDBController {


    private final OrientDBFactory db;

    @Autowired
    public OrientDBController(OrientDBFactory db) {
        this.db = db;
    }

    @GetMapping("/customer")
    public String getVertex(@RequestParam("id") String id) {
        OrientVertex customer = null;
        try {
            TransactionWrapper tx = db.getTransaction();
            customer = tx.getVertexById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return customer != null ? customer.getRecord().toJSON() : "Not found";
    }

    @PutMapping("/customer")
    public String writeCustomer(@RequestParam("id") String id) {
        try {
            TransactionWrapper tx = db.getTransaction();
            OrientVertex customer = tx.getVertexById(id);
            OrientVertex hotel = tx.getVertexById("67:91");

            if (customer != null && hotel != null) {
                tx.addEdge(customer, hotel);
                tx.commit();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "ok";
    }
}
