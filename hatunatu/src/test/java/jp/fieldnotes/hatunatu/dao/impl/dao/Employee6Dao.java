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
package jp.fieldnotes.hatunatu.dao.impl.dao;

import jp.fieldnotes.hatunatu.dao.annotation.tiger.Query;
import jp.fieldnotes.hatunatu.dao.impl.bean.Employee;
import jp.fieldnotes.hatunatu.dao.impl.condition.EmployeeSearchCondition;

public interface Employee6Dao {

    /**
     * @param dto
     * @return
     */
    @Query("/*IF $dto.orderByString != null*/order by /*$dto.orderByString*/ENAME /*END*/")
    public Employee[] getEmployees(EmployeeSearchCondition dto);
}
