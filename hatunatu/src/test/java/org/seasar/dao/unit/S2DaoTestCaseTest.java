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
package org.seasar.dao.unit;

import org.seasar.dao.annotation.tiger.Id;
import org.seasar.dao.annotation.tiger.IdType;
import org.seasar.dao.annotation.tiger.Relation;
import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.extension.dataset.impl.DataSetImpl;

import java.util.ArrayList;
import java.util.List;

public class S2DaoTestCaseTest extends S2DaoTestCase {

    public S2DaoTestCaseTest(String arg0) {
        super(arg0);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(S2DaoTestCaseTest.class);
    }

    protected void setUp() {
        include("j2ee.dicon");
    }

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
        assertEquals("1", expected, bean);
    }

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
        assertEquals("1", expected, list);
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