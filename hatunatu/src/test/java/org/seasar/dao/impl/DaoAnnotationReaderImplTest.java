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
package org.seasar.dao.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import org.seasar.dao.AnnotationReaderFactory;
import org.seasar.dao.DaoAnnotationReader;
import org.seasar.dao.DaoMetaDataFactory;
import org.seasar.dao.NullBean;
import org.seasar.dao.annotation.tiger.*;
import org.seasar.dao.impl.bean.Employee;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;

/**
 * @author higa
 * 
 */
public class DaoAnnotationReaderImplTest extends TestCase {
    protected AnnotationReaderFactory readerFactory;

    protected Class aaaClazz;

    protected DaoAnnotationReader annotationReader;


    protected Class clazz;

    protected Class daoClazz;

    public void setUp() {
        readerFactory = new AnnotationReaderFactoryImpl();
        clazz = AbstractAaaDaoImpl2.class;
        BeanDesc daoDesc = BeanDescFactory.getBeanDesc(clazz);
        annotationReader = new DaoAnnotationReaderImpl(daoDesc);
        aaaClazz = Aaa.class;
        daoClazz = AaaDao.class;
    }

    protected Class getDaoClass(String className) {
        if (className.equals("AnnotationTestDaoImpl")) {
            return AnnotationTestDaoImpl.class;
        } else if (className.equals("DummyDao")) {
            return DummyDao.class;
        }
        throw new RuntimeException("unkown dao class " + className);
    }

    public void testGetElementTypeOfList() throws Exception {
        Method method = Aaa2Dao.class.getMethod("findAll", new Class[0]);
        Type type = method.getGenericReturnType();
        Type ret = DaoAnnotationReaderImpl.getElementTypeOfList(type);
        assertEquals(Aaa.class, ret);
    }

    public void testBasic() throws Exception {
        assertEquals(aaaClazz, annotationReader.getBeanClass());

        String query = annotationReader.getQuery(daoClazz.getMethod(
                "getAaaById2", new Class[] { int.class }));
        assertEquals("A > B", query);
    }



    public void testGetBean() {
        BeanDesc beanDesc1 = BeanDescFactory
                .getBeanDesc(getDaoClass("AnnotationTestDaoImpl"));
        DaoAnnotationReader reader1 = readerFactory
                .createDaoAnnotationReader(beanDesc1);
        assertEquals(Employee.class, reader1.getBeanClass());
    }

    public void testGetBeanClass() throws Exception {
        Method method = Aaa2Dao.class.getMethod("findAll", new Class[0]);
        assertEquals(Aaa.class, annotationReader.getBeanClass(method));
    }

    public void testGetBeanClassGenerics() throws Exception {
        Method method = AaaDao.class.getMethod("findAll2", new Class[0]);
        Class<?> clazz = annotationReader.getBeanClass(method);
        assertEquals(Map.class, clazz);
    }

    public void testGetBeanClassGenerics_simpleType() throws Exception {
        Method method = AaaDao.class.getMethod("findAll3", new Class[0]);
        Class<?> clazz = annotationReader.getBeanClass(method);
        assertEquals(Integer.class, clazz);
    }

    public void testGetBeanClass_noAnnotation() throws Exception {
        BeanDesc daoDesc = BeanDescFactory.getBeanDesc(Employee.class);
        DaoAnnotationReader reader = new DaoAnnotationReaderImpl(daoDesc);
        Class clazz = reader.getBeanClass();
        assertEquals(NullBean.class, clazz);
    }

    public void testGetNullBean() {
        BeanDesc beanDesc = BeanDescFactory
                .getBeanDesc(getDaoClass("DummyDao"));
        DaoAnnotationReader reader = readerFactory
                .createDaoAnnotationReader(beanDesc);
        assertEquals(NullBean.class, reader.getBeanClass());
    }

    public void testGetArgNames() throws Exception {
        BeanDesc beanDesc = BeanDescFactory
                .getBeanDesc(getDaoClass("AnnotationTestDaoImpl"));
        DaoAnnotationReader reader = readerFactory
                .createDaoAnnotationReader(beanDesc);
        assertEquals("1", Employee.class, reader.getBeanClass());
        Method method = beanDesc.getMethods("withArgumentAnnotaion")[0];
        String[] names = reader.getArgNames(method);
        assertEquals("2", 2, names.length);
        assertEquals("2", "arg1", names[0]);
        assertEquals("2", "arg2", names[1]);
        // getArgNames return 0 length array if args annotation is not
        // specified.
        Method method2 = beanDesc.getMethods("withNoAnnotaion")[0];
        String[] names2 = reader.getArgNames(method2);
        assertEquals("3", 0, names2.length);
        // annotationReader must read subclass annotation
        Method method3 = beanDesc.getMethods("subclassMethod")[0];
        String[] names3 = reader.getArgNames(method3);
        assertEquals("3", 1, names3.length);
    }

    public void testGetQuery() throws Exception {
        BeanDesc beanDesc = BeanDescFactory
                .getBeanDesc(getDaoClass("AnnotationTestDaoImpl"));
        DaoAnnotationReader reader = readerFactory
                .createDaoAnnotationReader(beanDesc);
        Method method1 = beanDesc.getMethods("withQueryAnnotaion")[0];
        String queryq = reader.getQuery(method1);
        assertEquals("1", "arg1 = /*arg1*/'dummy'", queryq);
        // return null if QUERY annotation not found
        Method method2 = beanDesc.getMethods("withNoAnnotaion")[0];
        String query2 = reader.getQuery(method2);
        assertNull("1", query2);
        // annotationReader must read subclass annotation
        Method method3 = beanDesc.getMethods("subclassMethod")[0];
        String[] names3 = reader.getArgNames(method3);
        assertEquals("3", 1, names3.length);

    }

    public void testGetPersistentProps() throws Exception {
        BeanDesc beanDesc = BeanDescFactory
                .getBeanDesc(getDaoClass("AnnotationTestDaoImpl"));
        DaoAnnotationReader reader = readerFactory
                .createDaoAnnotationReader(beanDesc);
        Method method1 = beanDesc.getMethods("withPersistentProps")[0];
        String[] props1 = reader.getPersistentProps(method1);
        assertEquals("1", 2, props1.length);
        assertEquals("1", "prop1", props1[0]);
        assertEquals("1", "prop2", props1[1]);
        // return null if QUERY annotation not found
        Method method2 = beanDesc.getMethods("withNoAnnotaion")[0];
        String[] props2 = reader.getPersistentProps(method2);
        assertNull("2", props2);
        // annotationReader must read subclass annotation
        Method method3 = beanDesc.getMethods("subclassMethod")[0];
        String[] props3 = reader.getPersistentProps(method3);
        assertEquals("1", 2, props3.length);
        assertEquals("1", "prop1", props3[0]);
        assertEquals("1", "prop2", props3[1]);

    }

    public void testGetNoPersistentProps() throws Exception {
        BeanDesc beanDesc = BeanDescFactory
                .getBeanDesc(getDaoClass("AnnotationTestDaoImpl"));
        DaoAnnotationReader reader = readerFactory
                .createDaoAnnotationReader(beanDesc);
        Method method1 = beanDesc.getMethods("withNoPersistentProps")[0];
        String[] props1 = reader.getNoPersistentProps(method1);
        assertEquals("1", 2, props1.length);
        assertEquals("1", "prop1", props1[0]);
        assertEquals("1", "prop2", props1[1]);
        // return null if QUERY annotation not found
        Method method2 = beanDesc.getMethods("withNoAnnotaion")[0];
        String[] props2 = reader.getNoPersistentProps(method2);
        assertNull("2", props2);
        // annotationReader must read subclass annotation
        Method method3 = beanDesc.getMethods("subclassMethod2")[0];
        String[] props3 = reader.getNoPersistentProps(method3);
        assertEquals("1", 2, props3.length);
        assertEquals("1", "prop1", props3[0]);
        assertEquals("1", "prop2", props3[1]);
    }

    public void testGetSql() throws Exception {
        BeanDesc beanDesc = BeanDescFactory
                .getBeanDesc(getDaoClass("AnnotationTestDaoImpl"));
        DaoAnnotationReader reader = readerFactory
                .createDaoAnnotationReader(beanDesc);
        Method method1 = beanDesc.getMethods("subclassMethod2")[0];
        String sql = reader.getSQL(method1, "mysql");
        assertEquals("1", "SELECT * FROM emp", sql);
    }

    @S2Dao(bean=Employee.class)
    public static interface AnnotationTestDao {

        @Arguments({"arg1", "arg2"})
        public Employee withArgumentAnnotaion(int arg1, String arg2);

        @Query("arg1 = /*arg1*/'dummy'")
        public Employee withQueryAnnotaion(int arg1);

        @PersistentProperty({"prop1", "prop2"})
        public Employee withPersistentProps(int arg1);

        @NoPersistentProperty({"prop1","prop2"})
        public Employee withNoPersistentProps(int arg1);

        public String withSQLAnnotaion_mysql_SQL = "SELECT * FROM emp1";

        public String withSQLAnnotaion_SQL = "SELECT * FROM emp2";

        @Sqls( {@Sql(value = "SELECT * FROM emp1", dbms = "mysql"),
                @Sql(value = "SELECT * FROM emp2")})
        public Employee withSQLAnnotaion();

        public Employee withNoAnnotaion(int arg1);
    }

    public static interface DummyDao {

    }

    public static abstract class AnnotationTestDaoImpl extends AbstractDao
            implements AnnotationTestDao {

        public AnnotationTestDaoImpl(DaoMetaDataFactory factory) {
            super(factory);
        }

        public Employee[] getEmployeesByDeptno(int deptno) {
            return (Employee[]) getEntityManager().findArray("deptno = ?",
                    new Integer(deptno));
        }


        @Arguments({"arg1"})
        @Query( "arg1 = /*arg1*/'dummy'")
        @PersistentProperty({"prop1", "prop2"})
        public abstract Employee subclassMethod(String arg1);


        @NoPersistentProperty({"prop1", "prop2"})
        @Sql("SELECT * FROM emp")
        public abstract Employee subclassMethod2(String arg1);

    }

    @S2Dao(bean = Aaa.class)
    @CheckSingleRowUpdate(false)
    public static interface AaaDao {

        @Arguments( { "aaa1", "aaa2" })
        public Aaa getAaaById1(int id);

        @Query("A > B")
        public Aaa getAaaById2(int id);

        @Sql("SELECT * FROM AAA")
        public Aaa getAaaById3(int id);

        @Sql("SELECT * FROM AAA")
        public List<Aaa> findAll();

        public List<Map<String, String>> findAll2();

        public List<Integer> findAll3();

        public Aaa[] findArray();

        public int[] findSimpleTypeArray();

        public Aaa find(int id);

        @NoPersistentProperty("abc")
        public Aaa createAaa1(Aaa aaa);

        @PersistentProperty("def")
        public Aaa createAaa2(Aaa aaa);

        @CheckSingleRowUpdate(false)
        public int createAaa3(Aaa aaa);

        @Sqls( { @Sql(value = "SELECT * FROM BBB", dbms = "oracle"),
                @Sql("SELECT * FROM DDD") })
        public Aaa selectB(int id);

        @Sql(value = "SELECT * FROM CCC", dbms = "oracle")
        public Aaa selectC(int id);

        @SqlFile
        public Aaa findUsingSqlFile(int id);

        @SqlFile("org/seasar/dao/impl/sqlfile/testFile.sql")
        public Aaa findUsingSqlFile2(int id);

        @ProcedureCall("hoge")
        public void execute();

    }

    public static interface Aaa2Dao extends AaaDao {
    }

    public static class Aaa {
    }
    public static abstract class AbstractAaaDaoImpl extends AbstractDao
            implements Aaa2Dao {

        public AbstractAaaDaoImpl(DaoMetaDataFactory daoMetaDataFactory) {
            super(daoMetaDataFactory);
        }

    }

    // [DAO-135] AOPによるエンハンスされたクラスの代わり
    public static abstract class AbstractAaaDaoImpl2 extends AbstractAaaDaoImpl {

        public AbstractAaaDaoImpl2(DaoMetaDataFactory daoMetaDataFactory) {
            super(daoMetaDataFactory);
        }

    }
}
