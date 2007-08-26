/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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
package org.seasar.dao.impl;

import javax.sql.DataSource;

import org.seasar.dao.Dbms;
import org.seasar.dao.ProcedureMetaData;
import org.seasar.dao.ProcedureMetaDataFactory;
import org.seasar.dao.ProcedureParameterType;
import org.seasar.dao.dbms.DbmsManager;
import org.seasar.extension.jdbc.types.ValueTypes;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author taedium
 *
 */
public class ProcedureMetaDataFactoryImplTest extends S2TestCase {

    protected void setUp() throws Exception {
        super.setUp();
        include("j2ee-derby.dicon");
    }

    public void testCreateProcedureMetaDataTx() throws Exception {
        String name = "PROCEDURE_TEST_CCC2";
        DataSource ds = getDataSource();
        Dbms dbms = DbmsManager.getDbms(getDatabaseMetaData());
        ProcedureMetaDataFactory factory = new ProcedureMetaDataFactoryImpl(
                name, ds, dbms);
        ProcedureMetaData metaData = factory.createProcedureMetaData();

        ProcedureParameterType ppt = metaData.getParameterType("ccc");
        assertTrue(ppt.isOutType());
        assertEquals(ValueTypes.STRING, ppt.getValueType());

        ppt = metaData.getParameterType("ddd");
        assertTrue(ppt.isInType());
        assertEquals(ValueTypes.BIGDECIMAL, ppt.getValueType());

        ppt = metaData.getParameterType("eee");
        assertTrue(ppt.isOutType());
        assertEquals(ValueTypes.STRING, ppt.getValueType());
    }

}
