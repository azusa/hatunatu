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
package jp.fieldnotes.hatunatu.dao.dbms;

import jp.fieldnotes.hatunatu.dao.Dbms;
import jp.fieldnotes.hatunatu.dao.handler.BasicSelectHandler;
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;
import jp.fieldnotes.hatunatu.dao.resultset.ObjectResultSetHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.SingletonLaContainer;
import org.lastaflute.di.core.factory.LaContainerFactory;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

public class H2Test  {

    @Before
    public void before(){
        LaContainer container = LaContainerFactory.create("app-h2.xml");
        SingletonLaContainerFactory.setContainer(container);
    }


    @Test
    public void testSequence() throws Exception {
        // ## Arrange ##
        final DataSource ds = SingletonLaContainer.getComponent(DataSource.class);
        final Connection con = ds.getConnection();
        final Statement stmt = con.createStatement();
        stmt.executeUpdate("DROP SEQUENCE IF EXISTS H2TEST_SEQ");
        stmt
                .executeUpdate("CREATE SEQUENCE H2TEST_SEQ START WITH 7650 INCREMENT BY 1");
        stmt.close();
        final Dbms dbms = DbmsManager.getDbms(ds);
        System.err.println(dbms.getClass());
        assertEquals(true, dbms instanceof H2);

        // ## Act ##
        // ## Assert ##
        final String sequenceNextValString = dbms
                .getSequenceNextValString("H2TEST_SEQ");
        QueryObject queryObject = new QueryObject();
        queryObject.setSql(sequenceNextValString);

        final BasicSelectHandler nextvalHandler = new BasicSelectHandler(
                ds,
                new ObjectResultSetHandler(Number.class));
        {
            final Number nextval = (Number) nextvalHandler.execute(queryObject);
            assertEquals(7650, nextval.intValue());
        }
        {
            final Number nextval = (Number) nextvalHandler.execute(queryObject);
            assertEquals(7651, nextval.intValue());
        }
        {
            final Number nextval = (Number) nextvalHandler.execute(queryObject);
            assertEquals(7652, nextval.intValue());
        }

        final String identitySelectString = dbms.getIdentitySelectString();
        final BasicSelectHandler identityHandler = new BasicSelectHandler(
                ds,
                new ObjectResultSetHandler(Number.class));
        QueryObject queryObject2 = new QueryObject();
        queryObject2.setSql(identitySelectString);
        {
            final Number currval = (Number) identityHandler.execute(queryObject2);
            assertEquals(7652, currval.intValue());
        }
        {
            final Number currval = (Number) identityHandler.execute(queryObject2);
            assertEquals(7652, currval.intValue());
        }
        {
            nextvalHandler.execute(queryObject);
            final Number currval = (Number) identityHandler.execute(queryObject2);
            assertEquals(7653, currval.intValue());
        }

    }

    @After
    public void after(){
        SingletonLaContainerFactory.destroy();
    }

}
