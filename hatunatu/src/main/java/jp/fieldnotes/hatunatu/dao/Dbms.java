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
package jp.fieldnotes.hatunatu.dao;

import jp.fieldnotes.hatunatu.api.BeanMetaData;
import jp.fieldnotes.hatunatu.dao.pager.PagingSqlRewriter;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

public interface Dbms extends PagingSqlRewriter {

    String getAutoSelectSql(BeanMetaData beanMetaData);

    String getSuffix();

    String getIdentitySelectString();

    String getSequenceNextValString(String sequenceName);

    boolean isSelfGenerate();

    String getBaseSql(Statement st);

    ResultSet getProcedures(DatabaseMetaData databaseMetaData,
            String procedureName);

}
