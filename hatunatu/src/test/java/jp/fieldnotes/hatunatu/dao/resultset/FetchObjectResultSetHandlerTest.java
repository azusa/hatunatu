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
import jp.fieldnotes.hatunatu.dao.parser.SqlTokenizerImpl;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import jp.fieldnotes.hatunatu.dao.unit.S2DaoTestCase;
import jp.fieldnotes.hatunatu.dao.ResultSetHandler;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FetchObjectResultSetHandlerTest {

    @Rule
    public HatunatuTest test = new HatunatuTest(this);

    @Test
    public void testHandle() throws Exception {
        String sql = "select empno from emp";
        Connection con = test.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        final List<Integer> ret = new ArrayList<Integer>();
        ResultSetHandler handler = new FetchObjectResultSetHandler(
                Integer.class, new FetchHandler<Integer>() {
                    public boolean execute(Integer row) {
                        ret.add(row);
                        return true;
                    }
                });
        try {
            ResultSet rs = ps.executeQuery();
            try {
                handler.handle(rs);
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
        assertNotNull(ret);
        assertEquals(14, ret.size());
        assertEquals(Integer.class, ret.get(0).getClass());
    }

}
