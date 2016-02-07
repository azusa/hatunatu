/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.fieldnotes.hatunatu.dao.resultset;

import jp.fieldnotes.hatunatu.dao.FetchHandler;
import jp.fieldnotes.hatunatu.dao.ResultSetHandler;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FetchMapResultSetHandlerTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this);


    @Test
    public void testHandle() throws Exception {
        String sql = "select employee_id, employee_name from emp4 where employee_id = 7369";
        Connection con = test.getConnection();

        final List<Map> list = new ArrayList<Map>();
        ResultSetHandler handler = new FetchMapResultSetHandler(
                new FetchHandler<Map>() {
                    public boolean execute(Map row) {
                        list.add(row);
                        return true;
                    }
                });
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            handler.handle(rs, test.getQueryObject());
        }
        assertNotNull(list);
        assertEquals(1, list.size());
        Map row = list.get(0);
        assertEquals(Integer.valueOf(7369), row.get("employeeId"));
        assertEquals("SMITH", row.get("employeeName"));
    }
}
