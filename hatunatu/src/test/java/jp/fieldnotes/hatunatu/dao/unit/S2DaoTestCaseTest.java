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
import jp.fieldnotes.hatunatu.dao.BeanMetaDataFactory;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.Id;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.IdType;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.Relation;
import jp.fieldnotes.hatunatu.dao.impl.BeanMetaDataFactoryImpl;
import org.junit.Rule;
import org.junit.Test;
import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.extension.dataset.impl.DataSetImpl;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class S2DaoTestCaseTest extends S2DaoTestCase {

    @Rule
    public HatunatuTest test = new HatunatuTest(this);

    @Test
    public void testAssertBeanEquals() {
        DataSet expected = new DataSetImpl();
        DataTable table = expected.addTable("emp");
        table.addColumn("aaa");
        table.addColumn("bbb_0");
        DataRow row = table.addRow();
        row.setValue("aaa", "111");
        row.setValue("bbb_0", "222");
        Hoge bean = new Hoge();
        bean.setAaa("111");
        Foo foo = new Foo();
        foo.setBbb("222");
        bean.setFoo(foo);
        assertDataSetEquals("1", expected, bean);
    }

    @Test
    public void testAssertBeanListEquals() {
        DataSet expected = new DataSetImpl();
        DataTable table = expected.addTable("emp");
        table.addColumn("aaa");
        table.addColumn("bbb_0");
        DataRow row = table.addRow();
        row.setValue("aaa", "111");
        row.setValue("bbb_0", "222");
        Hoge bean = new Hoge();
        bean.setAaa("111");
        Foo foo = new Foo();
        foo.setBbb("222");
        bean.setFoo(foo);
        List list = new ArrayList();
        list.add(bean);
        assertDataSetEquals("1", expected, list);
    }

    @Override
    protected DataSource getDataSource() {
        return test.getDataSource();
    }

    @Override
    protected BeanMetaData createBeanMetaData(final Class beanClass) {
        return test.createBeanMetaData(beanClass);
    }

    public static class Hoge {

        private String aaa;

        private Foo foo;

        /**
         * @return Returns the aaa.
         */
        @Id(IdType.ASSIGNED)
        public String getAaa() {
            return aaa;
        }

        /**
         * @param aaa
         *            The aaa to set.
         */
        public void setAaa(String aaa) {
            this.aaa = aaa;
        }

        /**
         * @return Returns the foo.
         */
        public Foo getFoo() {
            return foo;
        }

        /**
         * @param foo
         *            The foo to set.
         */
        @Relation(relationNo = 0)
        public void setFoo(Foo foo) {
            this.foo = foo;
        }
    }

    public static class Foo {

        private String bbb;

        /**
         * @return Returns the bbb.
         */
        @Id(IdType.ASSIGNED)
        public String getBbb() {
            return bbb;
        }

        /**
         * @param bbb
         *            The bbb to set.
         */
        public void setBbb(String bbb) {
            this.bbb = bbb;
        }
    }
}