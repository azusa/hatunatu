/*
 * Copyright 2004-2012 the Seasar Foundation and the Others.
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
package jp.fieldnotes.hatunatu.util.beans.converter;

import java.util.Date;

import jp.fieldnotes.hatunatu.util.lang.StringUtil;
import jp.fieldnotes.hatunatu.api.beans.Converter;
import jp.fieldnotes.hatunatu.util.convert.StringConversionUtil;
import jp.fieldnotes.hatunatu.util.convert.TimestampConversionUtil;

import static jp.fieldnotes.hatunatu.util.misc.AssertionUtil.*;

/**
 * 日時用のコンバータです。
 * 
 * @author higa
 */
public class TimestampConverter implements Converter {

    /**
     * 日時のパターンです。
     */
    protected String pattern;

    /**
     * インスタンスを構築します。
     * 
     * @param pattern
     *            日時のパターン
     */
    public TimestampConverter(final String pattern) {
        assertArgumentNotEmpty("pattern", pattern);
        this.pattern = pattern;
    }

    @Override
    public Object getAsObject(final String value) {
        if (StringUtil.isEmpty(value)) {
            return null;
        }
        return TimestampConversionUtil.toSqlTimestamp(value, pattern);
    }

    @Override
    public String getAsString(final Object value) {
        if (value == null) {
            return null;
        }
        return StringConversionUtil.toString((Date) value, pattern);
    }

    @Override
    public boolean isTarget(final Class<?> clazz) {
        assertArgumentNotNull("clazz", clazz);
        return clazz == java.sql.Timestamp.class;
    }

}
