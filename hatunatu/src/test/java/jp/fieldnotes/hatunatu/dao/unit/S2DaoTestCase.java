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
import junit.framework.Assert;
import org.seasar.extension.dataset.DataReader;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.impl.SqlWriter;
import org.seasar.extension.dataset.impl.XlsReader;
import org.seasar.extension.jdbc.impl.BasicUpdateHandler;
import org.seasar.extension.unit.MapListReader;
import org.seasar.extension.unit.MapReader;
import org.seasar.framework.util.ResourceUtil;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public abstract class S2DaoTestCase  {

    public void readXlsAllReplaceDb(String path) {
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

    public void writeDb(DataSet dataSet) {
        SqlWriter writer = new SqlWriter(getDataSource());
        writer.write(dataSet);
    }



    public void deleteTable(String tableName) {
        org.seasar.extension.jdbc.UpdateHandler handler = new BasicUpdateHandler(getDataSource(),
                "DELETE FROM " + tableName);
        handler.execute(null);
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
        junit.framework.TestCase.assertEquals(message, expected.getTableSize(), actual.getTableSize());
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
                                       List list) {

        MapListReader reader = new MapListReader(list);
        assertDataSetEquals(message, expected, reader.read());
    }

    protected void assertMapEquals(String message, DataSet expected, Map map) {

        MapReader reader = new MapReader(map);
        assertDataSetEquals(message, expected, reader.read());
    }

    protected abstract BeanMetaData createBeanMetaData(final Class beanClass) throws SQLException;



}
