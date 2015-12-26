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
package jp.fieldnotes.hatunatu.dao.handler;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import jp.fieldnotes.hatunatu.dao.Dbms;
import jp.fieldnotes.hatunatu.dao.StatementFactory;
import jp.fieldnotes.hatunatu.api.ValueType;
import jp.fieldnotes.hatunatu.dao.impl.BasicStatementFactory;
import jp.fieldnotes.hatunatu.dao.types.ValueTypes;
import jp.fieldnotes.hatunatu.dao.util.ConnectionUtil;
import jp.fieldnotes.hatunatu.dao.util.DataSourceUtil;
import jp.fieldnotes.hatunatu.dao.util.DatabaseMetaDataUtil;
import jp.fieldnotes.hatunatu.dao.exception.EmptyRuntimeException;
import jp.fieldnotes.hatunatu.dao.exception.SQLRuntimeException;
import jp.fieldnotes.hatunatu.util.exception.SRuntimeException;
import jp.fieldnotes.hatunatu.util.sql.ResultSetUtil;

/**
 * @author higa
 * 
 */
public abstract class AbstractBasicProcedureHandler implements ProcedureHandler {

    protected boolean initialised = false;

    protected DataSource dataSource;

    protected String procedureName;

    protected String sql;

    protected Integer[] columnInOutTypes;

    protected Integer[] columnTypes;

    protected String[] columnNames;

    protected StatementFactory statementFactory = BasicStatementFactory.INSTANCE;

    protected Dbms dbms;

    protected int inParameterSize;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public StatementFactory getStatementFactory() {
        return statementFactory;
    }

    public void setStatementFactory(StatementFactory statementFactory) {
        this.statementFactory = statementFactory;
    }

    protected Connection getConnection() {
        if (dataSource == null) {
            throw new EmptyRuntimeException("dataSource");
        }
        return DataSourceUtil.getConnection(dataSource);
    }

    protected CallableStatement prepareCallableStatement(Connection connection) {
        if (sql == null) {
            throw new EmptyRuntimeException("sql");
        }
        return statementFactory.createCallableStatement(connection, sql);
    }

    public Object execute(Object[] args) throws SQLRuntimeException {
        Connection connection = getConnection();
        try {
            return execute(connection, args);
        } finally {
            ConnectionUtil.close(connection);
        }
    }

    protected int initTypes() {
        StringBuffer buff = new StringBuffer();
        buff.append("{ call ");
        buff.append(getProcedureName());
        buff.append("(");
        List columnNames = new ArrayList();
        List dataType = new ArrayList();
        List inOutTypes = new ArrayList();
        ResultSet rs = null;
        int outparameterNum = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            DatabaseMetaData dmd = ConnectionUtil.getMetaData(connection);

            final ProcedureMetaData pmd = getProcedureMetaData(getDataSource(),
                    getProcedureName());

            rs = dmd.getProcedureColumns(pmd.getProcedureCat(), pmd
                    .getProcedureSchem(), pmd.getProcedureName(), null);
            boolean commaRequired = false;
            while (rs.next()) {
                columnNames.add(rs.getObject(4));
                int columnType = rs.getInt(5);
                if (isInputColum(columnType)) {
                    inParameterSize++;
                }
                inOutTypes.add(new Integer(columnType));
                dataType.add(new Integer(rs.getInt(6)));
                if (columnType == DatabaseMetaData.procedureColumnIn) {
                    if (commaRequired) {
                        buff.append(",");
                    }
                    buff.append("?");
                    commaRequired = true;
                } else if (columnType == DatabaseMetaData.procedureColumnResult) {
                    // ignore
                } else if (columnType == DatabaseMetaData.procedureColumnReturn) {
                    buff.setLength(0);
                    buff.append("{? = call ");
                    buff.append(getProcedureName());
                    buff.append("(");
                } else if (columnType == DatabaseMetaData.procedureColumnOut
                        || columnType == DatabaseMetaData.procedureColumnInOut) {
                    if (commaRequired) {
                        buff.append(",");
                    }
                    buff.append("?");
                    commaRequired = true;
                    outparameterNum++;
                } else {
                    throw new SRuntimeException("EDAO0010",
                            new Object[] { getProcedureName() });
                }
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        } finally {
            ResultSetUtil.close(rs);
            ConnectionUtil.close(connection);
        }
        buff.append(")}");
        this.sql = buff.toString();
        this.columnNames = (String[]) columnNames
                .toArray(new String[columnNames.size()]);
        this.columnTypes = (Integer[]) dataType.toArray(new Integer[dataType
                .size()]);
        this.columnInOutTypes = (Integer[]) inOutTypes
                .toArray(new Integer[inOutTypes.size()]);
        return outparameterNum;
    }

    public abstract void initialize();

    protected abstract Object execute(Connection connection, Object[] args);

    protected void bindArgs(CallableStatement ps, Object[] args)
            throws SQLException {
        if (args == null) {
            return;
        }
        int argPos = 0;
        for (int i = 0; i < columnTypes.length; i++) {
            if (isOutputColum(columnInOutTypes[i].intValue())) {
                ps.registerOutParameter(i + 1, columnTypes[i].intValue());
            }
            if (isInputColum(columnInOutTypes[i].intValue())) {
                ps.setObject(i + 1, args[argPos++], columnTypes[i].intValue());
            }
        }
    }

    protected boolean isInputColum(int columnInOutType) {
        return columnInOutType == DatabaseMetaData.procedureColumnIn
                || columnInOutType == DatabaseMetaData.procedureColumnInOut;
    }

    protected boolean isOutputColum(int columnInOutType) {
        return columnInOutType == DatabaseMetaData.procedureColumnReturn
                || columnInOutType == DatabaseMetaData.procedureColumnOut
                || columnInOutType == DatabaseMetaData.procedureColumnInOut;
    }

    protected String getCompleteSql(Object[] args) {
        if (args == null || args.length == 0) {
            return sql;
        }
        StringBuffer buf = new StringBuffer(200);
        int pos = 0;
        int pos2 = 0;
        int index = 0;
        while (true) {
            pos = sql.indexOf('?', pos2);
            if (pos > 0) {
                buf.append(sql.substring(pos2, pos));
                buf.append(getBindVariableText(args[index++]));
                pos2 = pos + 1;
            } else {
                buf.append(sql.substring(pos2));
                break;
            }
        }
        return buf.toString();
    }

    protected String getBindVariableText(Object bindVariable) {
        if (bindVariable instanceof String) {
            return "'" + bindVariable + "'";
        } else if (bindVariable instanceof Number) {
            return bindVariable.toString();
        } else if (bindVariable instanceof Timestamp) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
            return "'" + sdf.format((java.util.Date) bindVariable) + "'";
        } else if (bindVariable instanceof java.util.Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return "'" + sdf.format((java.util.Date) bindVariable) + "'";
        } else if (bindVariable instanceof Boolean) {
            return bindVariable.toString();
        } else if (bindVariable == null) {
            return "null";
        } else {
            return "'" + bindVariable.toString() + "'";
        }
    }

    protected ValueType getValueType(Class clazz) {
        return ValueTypes.getValueType(clazz);
    }

    ProcedureMetaData getProcedureMetaData(DataSource dataSource,
            String procedureName) {
        final Connection con = DataSourceUtil.getConnection(dataSource);
        ResultSet rs = null;
        try {
            final DatabaseMetaData dmd = ConnectionUtil.getMetaData(con);
            rs = getDbms().getProcedures(dmd,
                    DatabaseMetaDataUtil.convertIdentifier(dmd, procedureName));
            if (rs == null || !rs.next()) {
                rs.close();
                rs = getDbms().getProcedures(dmd, procedureName);
                if (rs == null || !rs.next()) {
                    throw new SRuntimeException("EDAO0012",
                            new Object[] { procedureName });
                }
            }
            final ProcedureMetaData procedureMetaData = new ProcedureMetaData();
            procedureMetaData.setProcedureCat(rs.getString(1));
            procedureMetaData.setProcedureSchem(rs.getString(2));
            procedureMetaData.setProcedureName(rs.getString(3));
            procedureMetaData.setProcedureType(rs.getShort(8));
            if (rs.next()) {
                throw new SRuntimeException("EDAO0013",
                        new Object[] { procedureName });
            }
            return procedureMetaData;
        } catch (final SQLException e) {
            throw new SQLRuntimeException(e);
        } finally {
            try {
                ResultSetUtil.close(rs);
            } finally {
                ConnectionUtil.close(con);
            }
        }
    }

    public Dbms getDbms() {
        return dbms;
    }

    public void setDbms(final Dbms dbms) {
        this.dbms = dbms;
    }

    static class ProcedureMetaData {

        private String procedureCat;

        private String procedureSchem;

        private String procedureName;

        private short procedureType;

        public String getProcedureCat() {
            return procedureCat;
        }

        public void setProcedureCat(String procedureCat) {
            this.procedureCat = procedureCat;
        }

        public String getProcedureName() {
            return procedureName;
        }

        public void setProcedureName(String procedureName) {
            this.procedureName = procedureName;
        }

        public String getProcedureSchem() {
            return procedureSchem;
        }

        public void setProcedureSchem(String procedureSchem) {
            this.procedureSchem = procedureSchem;
        }

        public short getProcedureType() {
            return procedureType;
        }

        public void setProcedureType(short procedureType) {
            this.procedureType = procedureType;
        }

    }

}
