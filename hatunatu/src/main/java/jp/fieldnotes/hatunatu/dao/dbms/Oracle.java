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
import jp.fieldnotes.hatunatu.dao.exception.SQLRuntimeException;
import jp.fieldnotes.hatunatu.dao.pager.OracleRownumPagingSqlRewriter;
import jp.fieldnotes.hatunatu.dao.util.DatabaseMetaDataUtil;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Oracle extends Standard {

    public Oracle() {
        this.rewriter = new OracleRownumPagingSqlRewriter();
    }

    @Override
    public String getSuffix() {
        return "_oracle";
    }

    @Override
    protected String createAutoSelectFromClause(BeanMetaData beanMetaData) {
        StringBuilder buf = new StringBuilder(100);
        buf.append("FROM ");
        String myTableName = beanMetaData.getTableName();
        buf.append(myTableName);
        StringBuilder whereBuf = new StringBuilder(100);
        for (int i = 0; i < beanMetaData.getRelationPropertyTypeSize(); ++i) {
            RelationPropertyType rpt = beanMetaData.getRelationPropertyType(i);
            BeanMetaData bmd = rpt.getBeanMetaData();
            buf.append(", ");
            buf.append(bmd.getTableName());
            buf.append(" ");
            String yourAliasName = rpt.getPropertyName();
            buf.append(yourAliasName);
            for (int j = 0; j < rpt.getKeySize(); ++j) {
                whereBuf.append(myTableName);
                whereBuf.append(".");
                whereBuf.append(rpt.getMyKey(j));
                whereBuf.append(" = ");
                whereBuf.append(yourAliasName);
                whereBuf.append(".");
                whereBuf.append(rpt.getYourKey(j));
                whereBuf.append("(+)");
                whereBuf.append(" AND ");
            }
        }
        if (whereBuf.length() > 0) {
            whereBuf.setLength(whereBuf.length() - 5);
            buf.append(" WHERE ");
            buf.append(whereBuf);
        }
        return buf.toString();
    }

    @Override
    public String getSequenceNextValString(String sequenceName) {
        return "select " + sequenceName + ".nextval from dual";
    }

    @Override
    public ResultSet getProcedures(final DatabaseMetaData databaseMetaData,
            final String procedureName) {
        final String[] names = DatabaseMetaDataUtil.convertIdentifier(
                databaseMetaData, procedureName).split("\\.");
        final int namesLength = names.length;
        ResultSet rs = null;
        try {
            if (namesLength == 1) {
                // カレントスキーマからプロシージャ/ファンクションを探索
                rs = databaseMetaData.getProcedures(null, "", names[0]);
                if (!rs.isBeforeFirst()) {
                    rs.close();
                    // 全スキーマからプロシージャ/ファンクションを探索
                    rs = databaseMetaData.getProcedures(null, null, names[0]);
                }
            } else if (namesLength == 2) {
                // 指定したスキーマからプロシージャ/ファンクションを探索
                rs = databaseMetaData.getProcedures(null, names[0], names[1]);
                if (!rs.isBeforeFirst()) {
                    rs.close();
                    // カレントスキーマからパッケージを探索
                    rs = databaseMetaData.getProcedures(names[0], "", names[1]);
                    if (!rs.isBeforeFirst()) {
                        rs.close();
                        // 全スキーマからパッケージを探索
                        rs = databaseMetaData.getProcedures(names[0], null,
                                names[1]);
                    }
                }
            } else if (namesLength == 3) {
                // 指定したスキーマからパッケージを探索
                rs = databaseMetaData.getProcedures(names[1], names[0],
                        names[2]);
            } else {
                throw new IllegalArgumentException();
            }
            return rs;
        } catch (final SQLException e) {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception ignore) {
                }
            }
            throw new SQLRuntimeException(e);
        }
    }

}
