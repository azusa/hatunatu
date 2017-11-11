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
package jp.fieldnotes.hatunatu.dao.dbms;

import jp.fieldnotes.hatunatu.api.BeanMetaData;
import jp.fieldnotes.hatunatu.api.RelationPropertyType;
import jp.fieldnotes.hatunatu.dao.Dbms;
import jp.fieldnotes.hatunatu.util.exception.SQLRuntimeException;
import jp.fieldnotes.hatunatu.util.exception.SRuntimeException;
import jp.fieldnotes.hatunatu.util.misc.Disposable;
import jp.fieldnotes.hatunatu.util.misc.DisposableUtil;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Standard implements Dbms, Disposable {

    private static final Pattern baseSqlPattern = Pattern.compile(
            "^.*?(select)", Pattern.CASE_INSENSITIVE);

    final Map<String, String> autoSelectFromClauseCache = new HashMap<>();

    boolean initialized;

    @Override
    public String getSuffix() {
        return "";
    }

    @Override
    public String getAutoSelectSql(BeanMetaData beanMetaData) {
        if (!initialized) {
            DisposableUtil.add(this);
            initialized = true;
        }
        StringBuilder buf = new StringBuilder(100);
        buf.append(beanMetaData.getAutoSelectList());
        buf.append(" ");
        String beanName = beanMetaData.getBeanClass().getName();
        synchronized (autoSelectFromClauseCache) {
            String fromClause = autoSelectFromClauseCache
                    .get(beanName);
            if (fromClause == null) {
                fromClause = createAutoSelectFromClause(beanMetaData);
                autoSelectFromClauseCache.put(beanName, fromClause);
            }
            buf.append(fromClause);
        }
        return buf.toString();
    }

    protected String createAutoSelectFromClause(BeanMetaData beanMetaData) {
        StringBuilder buf = new StringBuilder(100);
        buf.append("FROM ");
        String myTableName = beanMetaData.getTableName();
        buf.append(myTableName);
        for (int i = 0; i < beanMetaData.getRelationPropertyTypeSize(); ++i) {
            RelationPropertyType rpt = beanMetaData.getRelationPropertyType(i);
            BeanMetaData bmd = rpt.getBeanMetaData();
            buf.append(" LEFT OUTER JOIN ");
            buf.append(bmd.getTableName());
            buf.append(" ");
            String yourAliasName = rpt.getPropertyName();
            buf.append(yourAliasName);
            buf.append(" ON ");
            for (int j = 0; j < rpt.getKeySize(); ++j) {
                buf.append(myTableName);
                buf.append(".");
                buf.append(rpt.getMyKey(j));
                buf.append(" = ");
                buf.append(yourAliasName);
                buf.append(".");
                buf.append(rpt.getYourKey(j));
                buf.append(" AND ");
            }
            buf.setLength(buf.length() - 5);

        }
        return buf.toString();
    }

    @Override
    public String getIdentitySelectString() {
        throw new SRuntimeException("EDAO0022", new String[] { ("Identity") });
    }

    @Override
    public String getSequenceNextValString(String sequenceName) {
        throw new SRuntimeException("EDAO0022", new String[] { ("Sequence") });
    }

    @Override
    public boolean isSelfGenerate() {
        return true;
    }

    @Override
    public String getBaseSql(Statement st) {
        String sql = st.toString();
        Matcher matcher = baseSqlPattern.matcher(sql);
        if (matcher.find()) {
            return matcher.replaceFirst(matcher.group(1));
        } else {
            return sql;
        }
    }

    @Override
    public synchronized void dispose() {
        autoSelectFromClauseCache.clear();
        initialized = false;
    }

    @Override
    public ResultSet getProcedures(final DatabaseMetaData databaseMetaData,
            final String procedureName) {
        final String[] names = procedureName.split("\\.");
        final int namesLength = names.length;
        try {
            ResultSet rs = null;
            if (namesLength == 1) {
                rs = databaseMetaData.getProcedures(null, null, names[0]);
            } else if (namesLength == 2) {
                rs = databaseMetaData.getProcedures(null, names[0], names[1]);
            } else if (namesLength == 3) {
                rs = databaseMetaData.getProcedures(names[0], names[1],
                        names[2]);
            }
            return rs;
        } catch (final SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

}
