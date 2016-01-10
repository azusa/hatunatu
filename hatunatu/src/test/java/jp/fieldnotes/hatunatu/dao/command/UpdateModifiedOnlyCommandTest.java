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

import jp.fieldnotes.hatunatu.dao.annotation.tiger.Arguments;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.Bean;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Rule;
import org.junit.Test;
import org.seasar.framework.util.ClassUtil;

import static org.junit.Assert.assertEquals;

public class UpdateModifiedOnlyCommandTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this, ClassUtil.getSimpleClassName(
            UpdateModifiedOnlyCommandTest.class).replace('.', '/')
            + ".dicon");

    /*
     * TODO testing...
     * 
     * - "ModifiedOnly"サフィックスが変更された場合にも動くこと
     * 
     */

    private EmpByReflectionDao empByReflectionDao;


    /*
     * InterfaceではなくReflectionでModifiedPropertiesを取得する方法のテスト。
     * 'new EmpByReflection()'したInstanceに更新したい値をSetして更新する方法を試す。
     * (更新前に一度Selectしないと排他制御は動作しないので、ここではTimestampプロパティを含めない)
     * 
     * また、既にModifiedPropertiesプロパティを持つEntityはエンハンスされないこと。
     */
    @Test
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
    }

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
            StringBuilder buf = new StringBuilder();
            buf.append(empno).append(", ");
            buf.append(ename).append(", ");
            buf.append(job).append(", ");
            buf.append(sal).append(", ");
            buf.append(comm).append(", ");
            return buf.toString();
        }

    }

}
