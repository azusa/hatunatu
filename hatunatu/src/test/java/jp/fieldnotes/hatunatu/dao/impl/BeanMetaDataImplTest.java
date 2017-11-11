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
package jp.fieldnotes.hatunatu.dao.impl;

import jp.fieldnotes.hatunatu.api.BeanMetaData;
import jp.fieldnotes.hatunatu.api.PropertyType;
import jp.fieldnotes.hatunatu.api.RelationPropertyType;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.*;
import jp.fieldnotes.hatunatu.dao.impl.bean.Department;
import jp.fieldnotes.hatunatu.dao.impl.bean.Employee;
import jp.fieldnotes.hatunatu.dao.impl.bean.Employee4;
import jp.fieldnotes.hatunatu.dao.impl.bean.IdentityTable;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Timestamp;

import static org.junit.Assert.*;

public class BeanMetaDataImplTest {
    
    @Rule
    public HatunatuTest test = new HatunatuTest(this);


    @Test
    public void testSetup() throws Exception {
        BeanMetaData bmd = test.createBeanMetaData(MyBean.class);
        assertEquals("1", "MyBean", bmd.getTableName());
        assertEquals("2", 3, bmd.getPropertyTypes().size());
        PropertyType aaa = bmd.getPropertyType("aaa");
        assertEquals("3", "aaa", aaa.getColumnName());
        PropertyType bbb = bmd.getPropertyType("bbb");
        assertEquals("4", "myBbb", bbb.getColumnName());
        assertEquals("5", 1, bmd.getRelationPropertyTypeSize());
        RelationPropertyType rpt = bmd.getRelationPropertyType(0);
        assertEquals("6", 1, rpt.getKeySize());
        assertEquals("7", "ddd", rpt.getMyKey(0));
        assertEquals("8", "id", rpt.getYourKey(0));
        assertNotNull("9", bmd.getIdentifierGenerator(0));
        assertEquals("10", 1, bmd.getPrimaryKeys().size());
        assertEquals("11", "aaa", bmd.getPrimaryKey(0));
    }

    @Test
    public void testSetupDatabaseMetaData() throws Exception {
        BeanMetaData bmd = test.createBeanMetaData(Employee.class);
        PropertyType empno = bmd.getPropertyType("empno");
        assertEquals("1", true, empno.isPrimaryKey());
        assertEquals("2", true, empno.isPersistent());
        PropertyType ename = bmd.getPropertyType("ename");
        assertEquals("3", false, ename.isPrimaryKey());
        PropertyType dummy = bmd.getPropertyType("dummy");
        assertEquals("4", false, dummy.isPersistent());
    }

    @Test
    public void testSetupAutoSelectList() throws Exception {
        BeanMetaData bmd = test.createBeanMetaData(Department.class);
        BeanMetaData bmd2 = test.createBeanMetaData(Employee.class);
        String sql = bmd.getAutoSelectList();
        String sql2 = bmd2.getAutoSelectList();
        System.out.println(sql);
        System.out.println(sql2);

        assertTrue("1", sql2.indexOf("EMP.deptno") > 0);
        assertTrue("2", sql2.indexOf("department.deptno AS deptno_0") > 0);
        assertTrue("3", sql2.indexOf("dummy_0") < 0);
    }

    @Test
    public void testConvertFullColumnName() throws Exception {
        BeanMetaData bmd = test.createBeanMetaData(Employee.class);
        assertEquals("1", "EMP.empno", bmd.convertFullColumnName("empno"));
        assertEquals("2", "department.dname", bmd
                .convertFullColumnName("dname_0"));
    }

    @Test
    public void testHasPropertyTypeByAliasName() throws Exception {
        BeanMetaData bmd = test.createBeanMetaData(Employee.class);
        assertEquals("1", true, bmd.hasPropertyTypeByAliasName("empno"));
        assertEquals("2", true, bmd.hasPropertyTypeByAliasName("dname_0"));
        assertEquals("3", false, bmd.hasPropertyTypeByAliasName("xxx"));
        assertEquals("4", false, bmd.hasPropertyTypeByAliasName("xxx_10"));
        assertEquals("5", false, bmd.hasPropertyTypeByAliasName("xxx_0"));
    }

    @Test
    public void testGetPropertyTypeByAliasName() throws Exception {
        BeanMetaData bmd = test.createBeanMetaData(Employee.class);
        assertNotNull("1", bmd.getPropertyTypeByAliasName("empno"));
        assertNotNull("2", bmd.getPropertyTypeByAliasName("dname_0"));
    }

    @Test
    public void testSelfReference() throws Exception {
        BeanMetaData bmd = test.createBeanMetaData(Employee4.class);
        RelationPropertyType rpt = bmd.getRelationPropertyType("parent");
        assertEquals("1", true, Employee4.class.isAssignableFrom(
                rpt.getBeanMetaData().getBeanClass()));
    }

    @Test
    public void testNoPersistentPropsEmpty() throws Exception {
        BeanMetaData bmd = test.createBeanMetaData(Ddd.class);
        PropertyType pt = bmd.getPropertyType("name");
        assertEquals("1", false, pt.isPersistent());
    }

    @Test
    public void testNoPersistentPropsDefined() throws Exception {
        BeanMetaData bmd = test.createBeanMetaData(Eee.class);
        PropertyType pt = bmd.getPropertyType("name");
        assertEquals("1", false, pt.isPersistent());
    }

    @Test
    public void testPrimaryKeyForIdentifier() throws Exception {
        BeanMetaData bmd = test.createBeanMetaData(IdentityTable.class);
        assertEquals("1", "id", bmd.getPrimaryKey(0));
    }

    @Test
    public void testGetVersionNoPropertyName() throws Exception {
        BeanMetaData bmd = test.createBeanMetaData(Fff.class);
        assertEquals("1", "version", bmd.getVersionNoPropertyName());
    }

    @Test
    public void testGetTimestampPropertyName() throws Exception {
        BeanMetaData bmd = test.createBeanMetaData(Fff.class);
        assertEquals("1", "updated", bmd.getTimestampPropertyName());
    }

    @Test
    public void testGetPrimaryKeyWithoutIdAnnotation() throws Exception {
        runTestEmployee(test.createBeanMetaData(Employee3A.class));
    }

    @Test
    public void testGetPrimaryKeyWithIdAnnotation() throws Exception {
        runTestEmployee(test.createBeanMetaData(Employee3B.class));
    }

    @Test
    public void testConvertClassName() throws Exception {
        BeanMetaDataFactoryImpl factory = (BeanMetaDataFactoryImpl) test.getBeanMetaDataFactory();
        factory.setTableNaming(new DecamelizeTableNaming());
        BeanMetaData metaData = factory.createBeanMetaData(NoPkTable.class);
        assertEquals("NO_PK_TABLE", metaData.getTableName());
    }

    @Test
    public void testMultiIdentities() throws Exception {
        BeanMetaData bmd = test.createBeanMetaData(Ggg.class);
        assertEquals(2, bmd.getIdentifierGeneratorSize());
        assertNotNull(bmd.getIdentifierGenerator("id"));
        assertNotNull(bmd.getIdentifierGenerator("id2"));
    }

    private void runTestEmployee(BeanMetaData bmd) {
        assertEquals(3, bmd.getPropertyTypes().size());
        {
            PropertyType pt = bmd.getPropertyType("employeeId");
            assertEquals("employeeId", pt.getPropertyName());
            assertEquals("employee_id", pt.getColumnName());
        }
        {
            PropertyType pt = bmd.getPropertyType("employeeName");
            assertEquals("employeeName", pt.getPropertyName());
            assertEquals("employee_name", pt.getColumnName());
        }
        {
            PropertyType pt = bmd.getPropertyType("departmentId");
            assertEquals("departmentId", pt.getPropertyName());
            assertEquals("department_id", pt.getColumnName());
        }
        assertEquals(1, bmd.getPrimaryKeys().size());
        assertEquals("employee_id", bmd.getPrimaryKey(0));
    }

    @Bean(table="EMP3")
    public static class Employee3A {

        private static final long serialVersionUID = 1L;

        private Integer employeeId;

        private String employeeName;

        private Integer departmentId;

        public Integer getDepartmentId() {
            return departmentId;
        }

        public void setDepartmentId(Integer departmentId) {
            this.departmentId = departmentId;
        }

        public Integer getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(Integer employeeId) {
            this.employeeId = employeeId;
        }

        public String getEmployeeName() {
            return employeeName;
        }

        public void setEmployeeName(String employeeName) {
            this.employeeName = employeeName;
        }

    }

    public static class Employee3B extends Employee3A {
        @Id(value= IdType.ASSIGNED)
        @Override
        public Integer getEmployeeId() {
            return super.getEmployeeId();
        }


    }

    @Bean(table = "MyBean")
    public static class MyBean {
        private Integer aaa;

        private String bbb;

        private Ccc ccc;

        private Integer ddd;

        @Id(IdType.ASSIGNED)
        public Integer getAaa() {
            return aaa;
        }

        public void setAaa(Integer aaa) {
            this.aaa = aaa;
        }

        @Column(value = "myBbb")
        public String getBbb() {
            return bbb;
        }

        public void setBbb(String bbb) {
            this.bbb = bbb;
        }

        @Relation(relationNo = 0, relationKey = "ddd:id")
        public Ccc getCcc() {
            return ccc;
        }

        public void setCcc(Ccc ccc) {
            this.ccc = ccc;
        }

        public Integer getDdd() {
            return ddd;
        }

        public void setDdd(Integer ddd) {
            this.ddd = ddd;
        }
    }

    public static class Ccc {
        private Integer id;

        @Id(value = IdType.ASSIGNED)
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    @Bean(noPersistentProperty = {""})
    public static class Ddd extends Ccc {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Bean(noPersistentProperty = {"name"})
    public static class Eee extends Ccc {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Bean(versionNoProperty = "version", timeStampProperty = "updated")
    public static class Fff {

        private int version;

        private Integer id;

        private Timestamp updated;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public Timestamp getUpdated() {
            return updated;
        }

        public void setUpdated(Timestamp updated) {
            this.updated = updated;
        }
    }

    public static class Ggg {

        private Integer id;

        private Integer id2;

        @Id(value = IdType.ASSIGNED)
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @Id(value = IdType.SEQUENCE, sequenceName = "id2")
        public Integer getId2() {
            return id2;
        }

        public void setId2(Integer id2) {
            this.id2 = id2;
        }

    }

}
