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
package jp.fieldnotes.hatunatu.dao.unit;

import jp.fieldnotes.hatunatu.api.BeanMetaData;
import jp.fieldnotes.hatunatu.dao.StatementFactory;
import jp.fieldnotes.hatunatu.dao.UpdateHandler;
import jp.fieldnotes.hatunatu.dao.dataset.DataReader;
import jp.fieldnotes.hatunatu.dao.dataset.DataSet;
import jp.fieldnotes.hatunatu.dao.dataset.impl.SqlWriter;
import jp.fieldnotes.hatunatu.dao.dataset.impl.XlsReader;
import jp.fieldnotes.hatunatu.dao.handler.BasicUpdateHandler;
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;
import jp.fieldnotes.hatunatu.util.io.ResourceUtil;
import junit.framework.Assert;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public abstract class S2DaoTestCase  {

    public void readXlsAllReplaceDb(String path) throws Exception {
        DataSet dataSet = readXls(path);
        for (int i = dataSet.getTableSize() - 1; i >= 0; --i) {
            deleteTable(dataSet.getTable(i).getTableName());
        }
        writeDb(dataSet);
    }

    public DataSet readXls(String path) {
        DataReader reader = new XlsReader( ResourceUtil.convertPath(path, getClass()), true);
        return reader.read();
    }

    public void writeDb(DataSet dataSet) throws Exception {
        SqlWriter writer = new SqlWriter(getDataSource());
        writer.write(dataSet);
    }


    public void deleteTable(String tableName) throws Exception {
        UpdateHandler handler = new BasicUpdateHandler(getDataSource(),
                StatementFactory.INSTANCE);
        QueryObject query = new QueryObject();
        query.setSql("DELETE FROM " + tableName);
        handler.execute(query);
    }

    public void assertDataSetEquals(String message, DataSet expected, Object actual) throws Exception {
        if (expected == null || actual == null) {
            Assert.assertEquals(message, expected, actual);
            return;
        }
        if (actual instanceof List) {
            List actualList = (List) actual;
            Assert.assertFalse(actualList.isEmpty());
            Object actualItem = actualList.get(0);
            if (actualItem instanceof Map) {
                assertMapListEquals(message, expected, actualList);
            } else {
                assertBeanListEquals(message, expected, actualList);
            }
        } else if (actual instanceof Object[]) {
            assertDataSetEquals(message, expected, Arrays.asList((Object[]) actual));
        } else {
            if (actual instanceof Map) {
                assertMapEquals(message, expected, (Map) actual);
            } else {
                assertBeanEquals(message, expected, actual);
            }
        }
    }
    public void assertDataSetEquals(String message, DataSet expected, DataSet actual) {
        message = message == null ? "" : message;
        assertThat(message, actual.getTableSize(), is(expected.getTableSize()));
        for (int i = 0; i < expected.getTableSize(); ++i) {
            junit.framework.TestCase.assertEquals(message, expected.getTable(i), actual.getTable(i));
        }
    }

    protected abstract DataSource getDataSource();

    protected void assertBeanEquals(final String message,
                                    final DataSet expected, final Object bean) throws Exception {

        final S2DaoBeanReader reader = new S2DaoBeanReader(bean,
                createBeanMetaData(bean.getClass()));
        assertDataSetEquals(message, expected, reader.read());
    }

    protected void assertBeanListEquals(final String message,
                                        final DataSet expected, final List list) throws Exception {

        final S2DaoBeanListReader reader = new S2DaoBeanListReader(list,
                createBeanMetaData(list.get(0).getClass()));
        assertDataSetEquals(message, expected, reader.read());
    }

    protected void assertMapListEquals(String message, DataSet expected,
                                       List list) throws Exception {

        MapListReader reader = new MapListReader(list);
        assertDataSetEquals(message, expected, reader.read());
    }

    protected void assertMapEquals(String message, DataSet expected, Map map) throws Exception {

        MapReader reader = new MapReader(map);
        assertDataSetEquals(message, expected, reader.read());
    }

    protected abstract BeanMetaData createBeanMetaData(final Class beanClass) throws SQLException;



}
