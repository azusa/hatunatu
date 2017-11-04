package jp.fieldnotes.hatunatu.examples.util;

import jp.fieldnotes.hatunatu.examples.dao.EmployeeDaoClient;
import jp.fieldnotes.hatunatu.util.io.ResourceUtil;

import java.io.IOException;

public class TestName {

    public String getDatabaseName() throws IOException {
        return "jdbc:hsqldb:file:" + ResourceUtil.getBuildDir(EmployeeDaoClient.class).getCanonicalPath() + "/data-hsqldb/demo";
    }
}
