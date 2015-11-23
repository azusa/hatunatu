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

public interface Employee7Dao {

    public Class BEAN = Employee.class;

    public static String getCount_hsql_SQL = "SELECT COUNT(*) FROM emp";

    public int getCount();

    public static String deleteEmployee_SQL = "DELETE FROM emp WHERE empno=?";

    public int deleteEmployee(int empno);

}
