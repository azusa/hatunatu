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
package jp.fieldnotes.hatunatu.examples.dao;

import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.factory.LaContainerFactory;

import java.util.List;

public class EmployeeDaoClient {

    private static final String PATH = "jp/fieldnotes/hatunatu/examples/dao/Examples.xml";

    public static void main(String[] args) {
        LaContainer container = LaContainerFactory.create(PATH);
        container.init();
        try {
            EmployeeDao dao = (EmployeeDao) container
                    .getComponent(EmployeeDao.class);
            List employees = dao.getAllEmployees();
            for (int i = 0; i < employees.size(); ++i) {
                System.out.println(employees.get(i));
            }

            Employee employee = dao.getEmployee(7788);
            System.out.println(employee);

            int count = dao.getCount();
            System.out.println("count:" + count);

            dao.getEmployeeByJobDeptno(null, null);
            dao.getEmployeeByJobDeptno("CLERK", null);
            dao.getEmployeeByJobDeptno(null, new Integer(20));
            dao.getEmployeeByJobDeptno("CLERK", new Integer(20));
            dao.getEmployeeByDeptno(new Integer(20));
            dao.getEmployeeByDeptno(null);

            System.out.println("updatedRows:" + dao.update(employee));

            int[] numbers = dao.getAllEmployeeNumbers();
            for (int i = 0; i < numbers.length; i++) {
                System.out.println(numbers[i]);
            }
        } finally {
            container.destroy();
        }

    }
}