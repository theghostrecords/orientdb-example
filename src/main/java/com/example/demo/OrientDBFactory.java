package com.example.demo;

import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;

@Service
public class OrientDBFactory {

    protected static OrientGraphFactory factory;
    private final DatabaseConfig databaseConfig;


    @Autowired
    public OrientDBFactory(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    @PostConstruct
    public void connect() {
        ensureDatabaseIsCreated(databaseConfig.getName());

        if (null == factory) {
            factory = new OrientGraphFactory(databaseConfig.getUrl() + "/" + databaseConfig.getName(),
                    databaseConfig.getUsername(), databaseConfig.getPassword()).setupPool(3, 9);
        }
    }

    @PreDestroy
    public void disconnect() {
        if (null != factory) {
            factory.close();
        }
    }

    private void ensureDatabaseIsCreated(String databaseName) {
        OrientDB orientDB = new OrientDB(databaseConfig.getUrl(), databaseConfig.getUsername(), databaseConfig.getPassword(), OrientDBConfig.defaultConfig());
        if (!orientDB.exists(databaseName)) {
            throw new IllegalStateException();
        }
        orientDB.close();
    }

    public TransactionWrapper getTransaction() throws Exception {
        OrientGraph tx = null;
        for (int i = 0; i < 3 && null == tx; i++) {
            try {
                tx = factory.getTx();
                tx.getRawGraph().query(databaseConfig.getTestQuery(), new HashMap<>());
            } catch (Exception e) {
                if (tx != null) {
                    tx.shutdown();
                }
                tx = null;
            }
        }

        if (null == tx) {
            throw new Exception("Problems connecting to the db");
        }

        return new TransactionWrapper(tx);
    }

}
