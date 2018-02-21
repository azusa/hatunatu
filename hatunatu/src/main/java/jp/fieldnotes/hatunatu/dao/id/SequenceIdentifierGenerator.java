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
import jp.fieldnotes.hatunatu.util.convert.LongConversionUtil;

import javax.sql.DataSource;

public class SequenceIdentifierGenerator extends AbstractIdentifierGenerator {

    private String sequenceName;

    /**
     * {@inheritDoc}
     */
    public SequenceIdentifierGenerator(PropertyType propertyType, Dbms dbms) {
        super(propertyType, dbms);
    }

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

    @Override
    public void setIdentifier(Object bean, DataSource ds) throws Exception {
        setIdentifier(bean, getNextValue(ds));
    }

    @Override
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
    protected Object getNextValue(DataSource ds) throws Exception {
        return LongConversionUtil
                .toPrimitiveLong(getNewInitialValue(ds));
    }

    /**
     * 次の初期値を返します。
     * 
     * @param ds
     *            データソース
     * @return 初期値
     */
    protected Object getNewInitialValue(DataSource ds) throws Exception {
        return executeSql(ds, getDbms().getSequenceNextValString(sequenceName),
                null);
    }


}
