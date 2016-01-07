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
package jp.fieldnotes.hatunatu.dao.impl.dto;

import jp.fieldnotes.hatunatu.dao.annotation.tiger.Column;
import jp.fieldnotes.hatunatu.dao.impl.bean.Employee;

public class EmployeeDto2 extends Employee {

    private static final long serialVersionUID = 1L;

    private String departmentName;

    /**
     * @return Returns the dname.
     */
    @Column(value = "dname")
    public String getDepartmentName() {
        return departmentName;
    }

    /**
     * @param dname The dname to set.
     */
    public void setDepartmentName(String dname) {
        this.departmentName = dname;
    }

}