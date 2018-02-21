/*
 * Copyright 2004-2015 the Seasar Foundation and the Others.
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
package jp.fieldnotes.hatunatu.dao.unit;

import jp.fieldnotes.hatunatu.dao.dataset.*;
import jp.fieldnotes.hatunatu.dao.dataset.impl.DataSetImpl;
import jp.fieldnotes.hatunatu.dao.dataset.states.RowStates;
import jp.fieldnotes.hatunatu.dao.dataset.types.ColumnTypes;

import java.util.Iterator;
import java.util.Map;

/**
 * {@link Map}用の {@link DataReader}です。
 */
public class MapReader implements DataReader {

    private DataSet dataSet = new DataSetImpl();

    private DataTable table = dataSet.addTable("Map");

    /**
     * {@link MapReader}を作成します。
     */
    protected MapReader() {
    }

    /**
     * {@link MapReader}を作成します。
     *
     * @param map マップ
     */
    public MapReader(Map map) {
        setupColumns(map);
        setupRow(map);
    }

    /**
     * カラムをセットアップします。
     *
     * @param map マップ
     */
    protected void setupColumns(Map map) {
        for (Iterator i = map.keySet().iterator(); i.hasNext(); ) {
            String key = (String) i.next();
            table.addColumn(key);
        }
    }

    /**
     * 行をセットアップします。
     *
     * @param map マップ
     */
    protected void setupRow(Map map) {
        DataRow row = table.addRow();
        for (int i = 0; i < table.getColumnSize(); ++i) {
            DataColumn column = table.getColumn(i);
            Object value = map.get(column.getColumnName());
            if (value != null) {
                ColumnType ct = ColumnTypes.getColumnType(value.getClass());
                row.setValue(column.getColumnName(), ct.convert(value, null));
            }
        }
        row.setState(RowStates.UNCHANGED);
    }

    public DataSet read() {
        return dataSet;
    }

}