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

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;

import org.seasar.dao.NotFoundModifiedPropertiesRuntimeException;
import org.seasar.dao.SqlCommand;
import org.seasar.dao.annotation.tiger.*;
import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.framework.util.ClassUtil;

/**
 * @author manhole
 * @author jflute
 */
public class UpdateModifiedOnlyCommandTest extends S2DaoTestCase {

    /*
     * TODO testing...
     * 
     * - Entityがinterface ModifiedPropertyプロパティを持っている場合(余計なエンハンスをしないこと)
     * - "ModifiedOnly"サフィックスが変更された場合にも動くこと
     * 
     */

    private EmpDao empDao;

    private Emp2Dao emp2Dao;

    private EmpByReflectionDao empByReflectionDao;

    protected void setUp() throws Exception {
        super.setUp();
        include(ClassUtil.getSimpleClassName(
                UpdateModifiedOnlyCommandTest.class).replace('.', '/')
                + ".dicon");
    }

    /*
     * 更新されたプロパティとtimestampだけをUPDATE文に含むこと。
     */
    public void testCreateModifiedPropertiesTx() throws Exception {
        // ## Arrange ##
        final DaoMetaDataImpl dmd = createDaoMetaData(EmpDao.class);

        final SqlCommand findById = dmd.getSqlCommand("findById");
        final Emp emp = (Emp) findById.execute(new Object[] { new Long(7499) });
        System.out.println(emp);
        assertEquals(7499, emp.getEmpno());
        assertEquals(true, getBeanEnhancer().isEnhancedClass(emp.getClass()));

        /*
         * ここで更新した2カラムと、必ず追加されるtimestampの、
         * あわせて3カラムがUPDATE文に含まれるべき。
         */
        emp.setEname("hoge");
        emp.setJob("hoge2");

        // ## Act ##
        final UpdateModifiedOnlyCommand updateModifiedOnly = (UpdateModifiedOnlyCommand) dmd
                .getSqlCommand("updateModifiedOnly");
        final PropertyType[] propertyTypes = updateModifiedOnly
                .createUpdatePropertyTypes(dmd.getBeanMetaData(), emp,
                        updateModifiedOnly.getPropertyNames());

        // ## Assert ##
        final HashSet set = new HashSet();
        for (int i = 0; i < propertyTypes.length; i++) {
            final PropertyType type = propertyTypes[i];
            set.add(type.getPropertyName());
            System.out.println(type.getPropertyName() + ", "
                    + type.getColumnName());
        }
        assertEquals(3, set.size());
        assertEquals(true, set.contains("ename"));
        assertEquals(true, set.contains("job"));
        assertEquals(true, set.contains("timestamp"));
        final Object result = updateModifiedOnly.execute(new Object[] { emp });
        final Integer count = (Integer) result;
        assertEquals(1, count.intValue());
    }

    /*
     * setterが呼ばれても値が変更されたプロパティ(とtimestamp)だけをUPDATE文に含むこと。
     */
    public void testCreateModifiedPropertiesForChangedTx() throws Exception {
        // ## Arrange ##
        final DaoMetaDataImpl dmd = createDaoMetaData(EmpDao.class);

        final SqlCommand findById = dmd.getSqlCommand("findById");
        final Emp emp = (Emp) findById.execute(new Object[] { new Long(7499) });
        System.out.println(emp);

        /*
         * ここで2カラムをのsetterを呼んでいるが、enameは更新されていないので、
         * jobと必ず追加されるtimestampの、
         * あわせて2カラムがUPDATE文に含まれるべき。
         */
        assertEquals("ALLEN", emp.getEname());
        emp.setEname("ALLEN");
        emp.setJob("hoge2");

        // ## Act ##
        final UpdateModifiedOnlyCommand updateModifiedOnly = (UpdateModifiedOnlyCommand) dmd
                .getSqlCommand("updateModifiedOnly");
        final PropertyType[] propertyTypes = updateModifiedOnly
                .createUpdatePropertyTypes(dmd.getBeanMetaData(), emp,
                        updateModifiedOnly.getPropertyNames());

        // ## Assert ##
        final HashSet set = new HashSet();
        for (int i = 0; i < propertyTypes.length; i++) {
            final PropertyType type = propertyTypes[i];
            set.add(type.getPropertyName());
            System.out.println(type.getPropertyName() + ", "
                    + type.getColumnName());
        }
        assertEquals(2, set.size());
        assertEquals(true, set.contains("job"));
        assertEquals(true, set.contains("timestamp"));
        final Object result = updateModifiedOnly.execute(new Object[] { emp });
        final Integer count = (Integer) result;
        assertEquals(1, count.intValue());
    }

    /*
     * 1つも変更されていない場合でも、timestampプロパティを持つ場合は
     * timestampだけが更新される。
     */
    public void testCreateModifiedPropertiesNoChangedTx() throws Exception {
        // ## Arrange ##
        final DaoMetaDataImpl dmd = createDaoMetaData(EmpDao.class);

        final SqlCommand findById = dmd.getSqlCommand("findById");
        final Emp emp = (Emp) findById.execute(new Object[] { new Long(7499) });
        System.out.println(emp);

        // ## Act ##
        final UpdateModifiedOnlyCommand updateModifiedOnly = (UpdateModifiedOnlyCommand) dmd
                .getSqlCommand("updateModifiedOnly");
        final PropertyType[] propertyTypes = updateModifiedOnly
                .createUpdatePropertyTypes(dmd.getBeanMetaData(), emp,
                        updateModifiedOnly.getPropertyNames());

        // ## Assert ##
        final HashSet set = new HashSet();
        for (int i = 0; i < propertyTypes.length; i++) {
            final PropertyType type = propertyTypes[i];
            set.add(type.getPropertyName());
            System.out.println(type.getPropertyName() + ", "
                    + type.getColumnName());
        }
        assertEquals(1, set.size());
        assertEquals(true, set.contains("timestamp"));
        final Object result = updateModifiedOnly.execute(new Object[] { emp });
        final Integer count = (Integer) result;
        assertEquals(1, count.intValue());
    }

    /*
     * プロパティが更新されずtimestampやversionNoも持たない場合は、
     * UPDATE文が発行されないこと。
     */
    public void testNoUpdateTx() throws Exception {
        // ## Arrange ##
        final DaoMetaDataImpl dmd = createDaoMetaData(Emp3Dao.class);

        final SqlCommand findById = dmd.getSqlCommand("findById");
        final Emp3 emp = (Emp3) findById
                .execute(new Object[] { new Long(7499) });
        System.out.println(emp);

        // ## Act ##
        final UpdateModifiedOnlyCommand updateModifiedOnly = (UpdateModifiedOnlyCommand) dmd
                .getSqlCommand("updateModifiedOnly");
        final PropertyType[] propertyTypes = updateModifiedOnly
                .createUpdatePropertyTypes(dmd.getBeanMetaData(), emp,
                        updateModifiedOnly.getPropertyNames());

        // ## Assert ##
        final HashSet set = new HashSet();
        for (int i = 0; i < propertyTypes.length; i++) {
            final PropertyType type = propertyTypes[i];
            set.add(type.getPropertyName());
            System.out.println(type.getPropertyName() + ", "
                    + type.getColumnName());
        }
        assertEquals(0, set.size());
        final Object result = updateModifiedOnly.execute(new Object[] { emp });
        final Integer count = (Integer) result;
        assertEquals(0, count.intValue());
    }

    /**
     * 関連先のEntityでも、更新されたプロパティとtimestampだけをUPDATE文に含むこと。
     * (RelationPropertyTypeの先もエンハンスされていること)
     */
    public void testRelationCreateModifiedPropertiesTx() throws Exception {
        // ## Arrange ##
        final Emp2 emp = emp2Dao.findById(7499);
        System.out.println(emp);
        assertEquals(7499, emp.getEmpno());
        assertEquals(true, getBeanEnhancer().isEnhancedClass(emp.getClass()));

        final Dept dept = emp.getDept();
        assertNotNull(dept);
        assertEquals(true, getBeanEnhancer().isEnhancedClass(dept.getClass()));

        // TODO enhanceされたクラスであること、をassertする
        System.out.println(dept);
        System.out.println(dept.getClass());

        // ここで更新した1カラムがUPDATE文に含まれるべき。
        dept.setDname("FOO");

        // ## Act ##
        final DaoMetaDataImpl dmd = createDaoMetaData(DeptDao.class);
        final UpdateModifiedOnlyCommand updateModifiedOnly = (UpdateModifiedOnlyCommand) dmd
                .getSqlCommand("updateModifiedOnly");
        final PropertyType[] propertyTypes = updateModifiedOnly
                .createUpdatePropertyTypes(dmd.getBeanMetaData(), dept,
                        updateModifiedOnly.getPropertyNames());

        // ## Assert ##
        final HashSet set = new HashSet();
        for (int i = 0; i < propertyTypes.length; i++) {
            final PropertyType type = propertyTypes[i];
            set.add(type.getPropertyName());
            System.out.println(type.getPropertyName() + ", "
                    + type.getColumnName());
        }
        assertEquals(1, set.size());
        assertEquals(true, set.contains("dname"));
        updateModifiedOnly.execute(new Object[] { dept });

        final Emp2 emp2 = emp2Dao.findById(7499);
        assertEquals("FOO", emp2.getDept().getDname());
    }

    /**
     * 関連先のEntityでも、更新されたプロパティとtimestampだけをUPDATE文に含むこと。
     * (RelationPropertyTypeの先もエンハンスされていること)
     */
    public void testListRelationCreateModifiedPropertiesTx() throws Exception {
        // ## Arrange ##
        final List emps = emp2Dao.findByJob("MANAGER");
        System.out.println(emps);
        final Emp2 emp1 = ((Emp2) emps.get(0));
        assertEquals(7566, emp1.getEmpno());
        assertEquals(true, getBeanEnhancer().isEnhancedClass(emp1.getClass()));

        final Dept dept = emp1.getDept();
        assertNotNull(dept);
        assertEquals("RESEARCH", dept.getDname());
        assertEquals(true, getBeanEnhancer().isEnhancedClass(dept.getClass()));

        // ここで更新した1カラムがUPDATE文に含まれるべき。
        dept.setDname("baar");

        // ## Act ##
        final DaoMetaDataImpl dmd = createDaoMetaData(DeptDao.class);
        final UpdateModifiedOnlyCommand updateModifiedOnly = (UpdateModifiedOnlyCommand) dmd
                .getSqlCommand("updateModifiedOnly");
        final PropertyType[] propertyTypes = updateModifiedOnly
                .createUpdatePropertyTypes(dmd.getBeanMetaData(), dept,
                        updateModifiedOnly.getPropertyNames());

        // ## Assert ##
        final HashSet set = new HashSet();
        for (int i = 0; i < propertyTypes.length; i++) {
            final PropertyType type = propertyTypes[i];
            set.add(type.getPropertyName());
            System.out.println(type.getPropertyName() + ", "
                    + type.getColumnName());
        }
        assertEquals(1, set.size());
        assertEquals(true, set.contains("dname"));
        updateModifiedOnly.execute(new Object[] { dept });

        final Emp2 emp2 = emp2Dao.findById(7566);
        assertEquals("baar", emp2.getDept().getDname());
    }

    /*
     * BeanにModifiedPropertiesが何も定義されていない場合のテスト。
     * ModifiedOnlyのメソッドの引数に、Interfaceを実装してない、かつ、
     * Reflection用のModifiedPropertiesも定義していないBeanを指定した場合は、
     * 例外が発生すること。
     */
    public void testUpdateModifiedOnlyWithNotSupportBeanTx() throws Exception {
        final int targetEmpno = 7499;
        final Emp emp = new Emp();
        emp.setEmpno(targetEmpno);
        emp.setEname("UpdateModifiedOnlyWithNotSupportBean");
        try {
            empDao.updateModifiedOnly(emp);
            fail("If the bean doesn't have modified properties, this invoking should throw exception: "
                    + emp);
        } catch (final NotFoundModifiedPropertiesRuntimeException e) {
            // OK
            assertEquals(emp.getClass().getName(), e.getBeanClassName());
            System.out.println(e.getMessage());
        }
    }

    /*
     * InterfaceではなくReflectionでModifiedPropertiesを取得する方法のテスト。
     * 'new EmpByReflection()'したInstanceに更新したい値をSetして更新する方法を試す。
     * (更新前に一度Selectしないと排他制御は動作しないので、ここではTimestampプロパティを含めない)
     * 
     * また、既にModifiedPropertiesプロパティを持つEntityはエンハンスされないこと。
     */
    public void testModifiedPropertiesByReflectionTx() throws Exception {
        // ## Arrange ##
        final int targetEmpno = 7499;
        final EmpByReflection expectedEmp = empByReflectionDao
                .findById(targetEmpno);
        final EmpByReflection emp = new EmpByReflection();
        emp.setEmpno(targetEmpno);
        emp.setEname("Modified");

        // ## Act ##
        final int updatedCount = empByReflectionDao.updateModifiedOnly(emp);
        assertEquals(1, updatedCount);

        // ## Assert ##
        // SetしたColumnの値だけが更新されて、残りは以前の値と同じであること。
        final EmpByReflection actualEmp = empByReflectionDao
                .findById(targetEmpno);
        assertEquals(expectedEmp.getEmpno(), actualEmp.getEmpno());
        assertEquals("Modified", actualEmp.getEname());
        assertEquals(expectedEmp.getJob(), actualEmp.getJob());
        assertEquals(expectedEmp.getComm(), actualEmp.getComm());
        assertEquals(expectedEmp.getSal(), actualEmp.getSal());
        assertEquals(false, getBeanEnhancer().isEnhancedClass(
                actualEmp.getClass()));
    }

    @S2Dao( bean=Emp.class)
    public static interface EmpDao {

        @Arguments({"empno"})
        Emp findById(long empno);

        int updateModifiedOnly(Emp emp);

    }

    @S2Dao(bean=Emp2.class)
    public static interface Emp2Dao {

        @Arguments({"empno"})
        Emp2 findById(long empno);

        @Arguments({"job"})
        List findByJob(String job);

        int updateModifiedOnly(Emp2 emp);

    }

    @S2Dao(bean=Emp3.class)
    public static interface Emp3Dao {

        @Arguments({"empno"})
        Emp3 findById(long empno);

        @Arguments("job")
        List findByJob(String job);

        int updateModifiedOnly(Emp3 emp);

    }

    @S2Dao(bean=Dept.class)
    public static interface DeptDao {

        Class BEAN = Dept.class;

        public String findById_ARGS = "deptno";

        Dept findById(long deptno);

        int updateModifiedOnly(Dept emp);

    }

    @Bean(table = "EMP")
    public static class Emp {

        private long empno;

        private String ename;

        private String job;

        private Float sal;

        private Float comm;

        @Column(value = "tstamp")
        private Timestamp timestamp;

        public long getEmpno() {
            return this.empno;
        }

        public void setEmpno(long empno) {
            this.empno = empno;
        }

        public String getEname() {
            return this.ename;
        }

        public void setEname(String ename) {
            this.ename = ename;
        }

        public String getJob() {
            return this.job;
        }

        public void setJob(String job) {
            this.job = job;
        }

        public Float getSal() {
            return this.sal;
        }

        public void setSal(Float sal) {
            this.sal = sal;
        }

        public Float getComm() {
            return this.comm;
        }

        public void setComm(Float comm) {
            this.comm = comm;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Timestamp timestamp) {
            this.timestamp = timestamp;
        }

        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append(empno).append(", ");
            buf.append(ename).append(", ");
            buf.append(job).append(", ");
            buf.append(sal).append(", ");
            buf.append(comm).append(", ");
            buf.append(timestamp);
            return buf.toString();
        }

    }

    @Bean( table = "EMP")
    public static class Emp2 {

        private long empno;

        private String ename;

        private String job;

        private Float sal;

        private Float comm;

        private Timestamp timestamp;

        private Dept dept;

        public long getEmpno() {
            return this.empno;
        }

        public void setEmpno(long empno) {
            this.empno = empno;
        }

        public String getEname() {
            return this.ename;
        }

        public void setEname(String ename) {
            this.ename = ename;
        }

        public String getJob() {
            return this.job;
        }

        public void setJob(String job) {
            this.job = job;
        }

        public Float getSal() {
            return this.sal;
        }

        public void setSal(Float sal) {
            this.sal = sal;
        }

        public Float getComm() {
            return this.comm;
        }

        public void setComm(Float comm) {
            this.comm = comm;
        }

        @Column(value = "tstamp")
        public Timestamp getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Timestamp timestamp) {
            this.timestamp = timestamp;
        }

        @Relation(relationNo = 0)
        public Dept getDept() {
            return dept;
        }

        public void setDept(Dept dept) {
            this.dept = dept;
        }

        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append(empno).append(", ");
            buf.append(ename).append(", ");
            buf.append(job).append(", ");
            buf.append(sal).append(", ");
            buf.append(comm).append(", ");
            buf.append(timestamp);
            return buf.toString();
        }
    }

    @Bean(table = "EMP")
    public static class Emp3 {

        private long empno;

        private String ename;

        private String job;

        private Float sal;

        private Float comm;

        public long getEmpno() {
            return this.empno;
        }

        public void setEmpno(long empno) {
            this.empno = empno;
        }

        public String getEname() {
            return this.ename;
        }

        public void setEname(String ename) {
            this.ename = ename;
        }

        public String getJob() {
            return this.job;
        }

        public void setJob(String job) {
            this.job = job;
        }

        public Float getSal() {
            return this.sal;
        }

        public void setSal(Float sal) {
            this.sal = sal;
        }

        public Float getComm() {
            return this.comm;
        }

        public void setComm(Float comm) {
            this.comm = comm;
        }

        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append(empno).append(", ");
            buf.append(ename).append(", ");
            buf.append(job).append(", ");
            buf.append(sal).append(", ");
            buf.append(comm);
            return buf.toString();
        }

    }

    @Bean(table = "DEPT")
    public static class Dept {

        private long deptno;

        private String dname;

        private String loc;

        public long getDeptno() {
            return this.deptno;
        }

        public void setDeptno(long deptno) {
            this.deptno = deptno;
        }

        public String getDname() {
            return this.dname;
        }

        public void setDname(String dname) {
            this.dname = dname;
        }

        public String getLoc() {
            return loc;
        }

        public void setLoc(String loc) {
            this.loc = loc;
        }

        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append(deptno).append(", ");
            buf.append(dname).append(", ");
            buf.append(loc);
            return buf.toString();
        }

    }

    @S2Dao( bean= EmpByReflection.class)
    public static interface EmpByReflectionDao {

        @Arguments({"empno"})
        EmpByReflection findById(long empno);

        int updateModifiedOnly(EmpByReflection emp);

    }

    /**
     * PropertyModifiedSupportではなくReflectionでModifiedPropertiesを
     * 取得するテストのためのEntity。
     * 
     * 排他制御を含めないように、timestampプロパティは定義していない。
     * 
     * @author jflute
     */
    @Bean(table = "EMP")
    public static class EmpByReflection {

        private long empno;

        private String ename;

        private String job;

        private Float sal;

        private Float comm;

        private java.util.Set _modifiedPropertySet = new java.util.HashSet();

        public long getEmpno() {
            return this.empno;
        }

        public void setEmpno(long empno) {
            _modifiedPropertySet.add("empno");
            this.empno = empno;
        }

        public String getEname() {
            return this.ename;
        }

        public void setEname(String ename) {
            _modifiedPropertySet.add("ename");
            this.ename = ename;
        }

        public String getJob() {
            return this.job;
        }

        public void setJob(String job) {
            _modifiedPropertySet.add("job");
            this.job = job;
        }

        public Float getSal() {
            return this.sal;
        }

        public void setSal(Float sal) {
            _modifiedPropertySet.add("sal");
            this.sal = sal;
        }

        public Float getComm() {
            return this.comm;
        }

        public void setComm(Float comm) {
            _modifiedPropertySet.add("comm");
            this.comm = comm;
        }

        public java.util.Set getModifiedPropertyNames() {
            return _modifiedPropertySet;
        }

        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append(empno).append(", ");
            buf.append(ename).append(", ");
            buf.append(job).append(", ");
            buf.append(sal).append(", ");
            buf.append(comm).append(", ");
            return buf.toString();
        }

    }

}
