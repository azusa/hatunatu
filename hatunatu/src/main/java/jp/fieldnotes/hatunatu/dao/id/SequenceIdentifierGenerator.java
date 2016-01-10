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
package jp.fieldnotes.hatunatu.dao.id;

import jp.fieldnotes.hatunatu.api.PropertyType;
import jp.fieldnotes.hatunatu.dao.Dbms;
import jp.fieldnotes.hatunatu.dao.util.SelectableDataSourceProxyUtil;
import jp.fieldnotes.hatunatu.util.convert.LongConversionUtil;

import javax.sql.DataSource;
import java.util.HashMap;

public class SequenceIdentifierGenerator extends AbstractIdentifierGenerator {

    private String sequenceName;

    private long allocationSize = 0;

    private HashMap idContextMap = new HashMap();

    /**
     * @param propertyType
     *            プロパティの型
     * @param dbms
     *            DBMS
     */
    public SequenceIdentifierGenerator(PropertyType propertyType, Dbms dbms) {
        super(propertyType, dbms);
    }

    /**
     * シーケンス名を返します。
     * 
     * @return シーケンス名
     */
    public String getSequenceName() {
        return sequenceName;
    }

    /**
     * シーケンス名を設定します。
     * 
     * @param sequenceName
     *            シーケンス名
     */
    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    /**
     * 割り当てサイズを返します。
     * 
     * @return 割り当てサイズ
     */
    public long getAllocationSize() {
        return allocationSize;
    }

    /**
     * 割り当てサイズを設定します。
     * 
     * @param allocationSize
     *            割り当てサイズ
     */
    public void setAllocationSize(long allocationSize) {
        this.allocationSize = allocationSize;
    }

    public void setIdentifier(Object bean, DataSource ds) {
        setIdentifier(bean, getNextValue(ds));
    }

    public boolean isSelfGenerate() {
        return getDbms().isSelfGenerate();
    }

    /**
     * 次の識別子の値を返します。
     * 
     * @param ds
     *            データソース
     * @return 識別子の値
     */
    protected Object getNextValue(DataSource ds) {
        if (allocationSize > 0) {
            long value = getIdContext(ds).getNextValue(ds);
            return new Long(value);
        }
        return getNewInitialValue(ds);
    }

    /**
     * 次の初期値を返します。
     * 
     * @param ds
     *            データソース
     * @return 初期値
     */
    protected Object getNewInitialValue(DataSource ds) {
        return executeSql(ds, getDbms().getSequenceNextValString(sequenceName),
                null);
    }

    /**
     * IDコンテキストを返します。
     * 
     * @param ds
     *            データソース
     * @return IDコンテキスト
     */
    protected IdContext getIdContext(DataSource ds) {
        synchronized (idContextMap) {
            String dsName = SelectableDataSourceProxyUtil
                    .getSelectableDataSourceName(ds);
            IdContext context = (IdContext) idContextMap.get(dsName);
            if (context == null) {
                context = new IdContext();
                idContextMap.put(dsName, context);
            }
            return context;
        }
    }

    /**
     * 自動生成される識別子のコンテキスト情報を保持するクラスです。
     * 
     */
    public class IdContext {

        /** 初期値 */
        protected long initialValue;

        /** 割り当て済みの値 */
        protected long allocated = Long.MAX_VALUE;

        /**
         * 自動生成された識別子の値を返します。
         * 
         * @param ds
         *            データソース
         * @return 自動生成された識別子の値
         */
        public synchronized long getNextValue(DataSource ds) {
            if (allocated < allocationSize) {
                return initialValue + allocated++;
            }
            initialValue = LongConversionUtil
                    .toPrimitiveLong(getNewInitialValue(ds));
            allocated = 1;
            return initialValue;
        }

    }
}
