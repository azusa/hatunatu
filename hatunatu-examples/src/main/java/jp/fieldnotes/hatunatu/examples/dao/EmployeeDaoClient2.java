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

public class EmployeeDaoClient2 {

    private static final String PATH = "jp/fieldnotes/hatunatu/examples/dao/Examples.xml";

    public static void main(String[] args) {
        LaContainer container = LaContainerFactory.create(PATH);
        container.init();
        try {
            EmployeeDao dao = (EmployeeDao) container
                    .getComponent(EmployeeDao.class);
            EmployeeSearchCondition dto = new EmployeeSearchCondition();
            dto.setJob("CLERK");
            List employees = dao.getEmps(dto);
            for (int i = 0; i < employees.size(); ++i) {
                System.out.println(employees.get(i));
            }
        } finally {
            container.destroy();
        }

    }
}