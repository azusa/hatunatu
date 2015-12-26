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
package jp.fieldnotes.hatunatu.dao.command;

import java.io.Serializable;
import java.util.List;

import jp.fieldnotes.hatunatu.api.DaoMetaData;
import jp.fieldnotes.hatunatu.dao.RelationRowCreator;
import jp.fieldnotes.hatunatu.dao.RowCreator;
import jp.fieldnotes.hatunatu.api.SqlCommand;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.Bean;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.S2Dao;
import jp.fieldnotes.hatunatu.dao.impl.RelationRowCreatorImpl;
import jp.fieldnotes.hatunatu.dao.impl.RowCreatorImpl;
import jp.fieldnotes.hatunatu.dao.parser.SqlTokenizerImpl;
import jp.fieldnotes.hatunatu.dao.impl.bean.Employee;
import jp.fieldnotes.hatunatu.dao.pager.NullPagingSqlRewriter;
import jp.fieldnotes.hatunatu.dao.resultset.BeanMetaDataResultSetHandler;
import jp.fieldnotes.hatunatu.dao.unit.S2DaoTestCase;
import jp.fieldnotes.hatunatu.dao.impl.BasicResultSetFactory;
import jp.fieldnotes.hatunatu.dao.impl.BasicStatementFactory;

public class SelectDynamicCommandTest extends S2DaoTestCase {

    public void testExecute() throws Exception {
        SelectDynamicCommand cmd = new SelectDynamicCommand(getDataSource(),
                BasicStatementFactory.INSTANCE,
                new BeanMetaDataResultSetHandler(
                        createBeanMetaData(Employee.class), createRowCreator(),
                        createRelationRowCreator()),
                BasicResultSetFactory.INSTANCE, new NullPagingSqlRewriter());
        cmd.setSql("SELECT * FROM emp WHERE empno = /*empno*/1234");
        Employee emp = (Employee) cmd
                .execute(new Object[] { new Integer(7788) });
        assertNotNull("1", emp);
    }

    protected RowCreator createRowCreator() {// [DAO-118] (2007/08/25)
        return new RowCreatorImpl();
    }

    protected RelationRowCreator createRelationRowCreator() {
        return new RelationRowCreatorImpl();
    }

    public void testSelectDynamic() throws Exception {
        DaoMetaData dmd = createDaoMetaData(DynamicDao.class);
        SqlCommand cmd = dmd.getSqlCommand(getSingleDaoMethod(DynamicDao.class, "getEmployeesBySearchCondition"));
        assertTrue(cmd instanceof SelectDynamicCommand);
        Employee cond = new Employee();
        cond.setJob("CLERK");
        cond.setEmpno(7369);
        cond.setDeptno(20);
        List result = (List) cmd.execute(new Object[] { cond });
        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof Employee);

        cmd = dmd.getSqlCommand(getSingleDaoMethod(DynamicDao.class, "getEmployeeBySearchCondition"));
        assertTrue(cmd instanceof SelectDynamicCommand);
        Object obj = cmd.execute(new Object[] { cond });
        assertTrue(obj instanceof Employee);
    }

    @S2Dao(bean=Employee.class)
    public interface DynamicDao {

        List getEmployeesBySearchCondition(Serializable dto);

        Object getEmployeeBySearchCondition(Serializable dto);

        int update(Object dto);

        List getEmployeeByDto(String s);
    }

    public void testSelectByDtoTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(Emp3Dao.class);
        SqlCommand cmd = dmd.getSqlCommand(getSingleDaoMethod(Emp3Dao.class,"insert"));
        Object[] param = new Object[1];
        for (int i = 0; i < 3; i++) {
            Emp3 e = new Emp3();
            e.employeeId = new Integer(i);
            e.departmentId = new Integer((i + 1) * 100);
            e.employeeName = "NAME" + String.valueOf(i);
            param[0] = e;
            cmd.execute(param);
        }

        cmd = dmd.getSqlCommand(getSingleDaoMethod(Emp3Dao.class,"selectByDto"));
        assertTrue(cmd instanceof SelectDynamicCommand);
        Emp3Dto dto = new Emp3Dto();
        dto.employeeName = "NAME1";
        List l = (List) cmd.execute(new Object[] { dto });
        assertEquals(1, l.size());

        cmd = dmd.getSqlCommand(getSingleDaoMethod(Emp3Dao.class, "selectByDto2"));
        assertTrue(cmd instanceof SelectDynamicCommand);
        Emp3ExDto ex = new Emp3ExDto();
        ex.department_Id = new Integer(200);
        l = (List) cmd.execute(new Object[] { ex });
        assertEquals(1, l.size());
    }

    public static class Emp3Dto {
        private String employeeName;

        public String getEmployeeName() {
            return employeeName;
        }

        public void setEmployeeName(String employeeName) {
            this.employeeName = employeeName;
        }

    }

    public static class Emp3ExDto {
        private Integer department_Id;

        public Integer getDepartment_Id() {
            return department_Id;
        }

        public void setDepartment_Id(Integer department_Id) {
            this.department_Id = department_Id;
        }
    }

    @Bean(table="EMP3")
    public static class Emp3 {
        public static String TABLE = "EMP3";

        private Integer employeeId;

        private String employeeName;

        private Integer departmentId;

        public Integer getDepartmentId() {
            return departmentId;
        }

        public void setDepartmentId(Integer departmentId) {
            this.departmentId = departmentId;
        }

        public String getEmployeeName() {
            return employeeName;
        }

        public void setEmployeeName(String employeeName) {
            this.employeeName = employeeName;
        }

        public Integer getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(Integer employeeId) {
            this.employeeId = employeeId;
        }
    }

    @S2Dao( bean = Emp3.class)
    public interface Emp3Dao {

        List selectByDto(Emp3Dto dto);

        List selectByDto2(Emp3ExDto dto);

        List select(Emp3 dto);

        void insert(Emp3 emp3);
    }

    public void setUp() {
        include("j2ee.dicon");
    }


}