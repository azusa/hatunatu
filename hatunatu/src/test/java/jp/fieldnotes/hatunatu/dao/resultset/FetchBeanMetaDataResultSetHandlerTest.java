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
package jp.fieldnotes.hatunatu.dao.resultset;

import jp.fieldnotes.hatunatu.api.BeanMetaData;
import jp.fieldnotes.hatunatu.api.DaoMetaData;
import jp.fieldnotes.hatunatu.dao.FetchHandler;
import jp.fieldnotes.hatunatu.dao.RelationRowCreator;
import jp.fieldnotes.hatunatu.dao.RowCreator;
import jp.fieldnotes.hatunatu.dao.command.SelectDynamicCommand;
import jp.fieldnotes.hatunatu.dao.impl.AnnotationReaderFactoryImpl;
import jp.fieldnotes.hatunatu.dao.impl.RelationRowCreatorImpl;
import jp.fieldnotes.hatunatu.dao.impl.RowCreatorImpl;
import jp.fieldnotes.hatunatu.dao.parser.SqlTokenizerImpl;
import jp.fieldnotes.hatunatu.dao.impl.bean.Employee;
import jp.fieldnotes.hatunatu.dao.impl.condition.EmployeeSearchCondition;
import jp.fieldnotes.hatunatu.dao.impl.dao.EmployeeDao;
import jp.fieldnotes.hatunatu.api.pager.PagerContext;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import jp.fieldnotes.hatunatu.dao.unit.S2DaoTestCase;
import jp.fieldnotes.hatunatu.dao.ResultSetHandler;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class FetchBeanMetaDataResultSetHandlerTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this);

    private BeanMetaData beanMetaData;

    @Test
    public void testHandle() throws Exception {
        String sql = "select * from emp";
        Connection con = test.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        final List<Employee> ret = new ArrayList<Employee>();
        ResultSetHandler handler = new FetchBeanMetaDataResultSetHandler(
                beanMetaData, createRowCreator(), createRelationRowCreator(),
                new FetchHandler<Employee>() {
                    public boolean execute(Employee bean) {
                        ret.add(bean);
                        return true;
                    }
                });
        try {
            ResultSet rs = ps.executeQuery();
            try {
                handler.handle(rs);
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
        assertNotNull("1", ret);
        for (int i = 0; i < ret.size(); ++i) {
            Employee emp = (Employee) ret.get(i);
            System.out.println(emp.getEmpno() + "," + emp.getEname());
        }
    }

    @Test
    public void testHandle2() {
        try {
            PagerContext.start();
            DaoMetaData dmd = test.createDaoMetaData(EmployeeDao.class);
            assertNotNull("1", dmd);
            SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                    .getSqlCommand(test.getSingleDaoMethod(EmployeeDao.class, "fetchEmployeesBySearchCondition"));
            assertNotNull("2", cmd);
            System.out.println(cmd.toString());
            final List<Employee> ret = new ArrayList<Employee>();
            FetchHandler<Employee> handler = new FetchHandler<Employee>() {
                public boolean execute(Employee emp) {
                    if (!emp.getDepartment().getDname().equals("RESEARCH")) {
                        fail();
                    }
                    ret.add(emp);
                    return true;
                }
            };
            EmployeeSearchCondition dto = new EmployeeSearchCondition();
            dto.setDname("RESEARCH");
            Object[] args = new Object[]{dto, handler};
            PagerContext.getContext().pushArgs(args);
            Object count = cmd.execute(args);
            assertNotNull("3", count);
            assertEquals("4", Integer.valueOf(5), count);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            PagerContext.getContext().popArgs();
            PagerContext.end();
        }
    }



    @Before
    public void setUpAfterBindFields() throws Throwable {
        beanMetaData = test.createBeanMetaData(Employee.class);
    }

    protected RowCreator createRowCreator() {// [DAO-118] (2007/08/25)
        return new RowCreatorImpl();
    }

    protected RelationRowCreator createRelationRowCreator() {
        return new RelationRowCreatorImpl();
    }

}
