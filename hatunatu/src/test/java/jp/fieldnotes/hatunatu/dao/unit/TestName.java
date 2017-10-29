package jp.fieldnotes.hatunatu.dao.unit;

import java.util.UUID;

public class TestName {

    public String getDatabaseName() {
        String name =  "jdbc:hsqldb:mem:" + UUID.randomUUID().toString();
        System.err.println(name);
        return name;
    }
}
