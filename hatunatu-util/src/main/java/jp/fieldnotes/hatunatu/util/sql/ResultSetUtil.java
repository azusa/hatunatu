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
package jp.fieldnotes.hatunatu.util.sql;

import jp.fieldnotes.hatunatu.util.exception.SQLRuntimeException;
import jp.fieldnotes.hatunatu.util.log.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

import static jp.fieldnotes.hatunatu.util.misc.AssertionUtil.assertArgumentNotNull;

/**
 * {@link ResultSet}のためのユーティリティクラスです。
 * 
 * @author higa
 */
public abstract class ResultSetUtil {

    private static final Logger logger = Logger.getLogger(ResultSetUtil.class);


    /**
     * 結果セットを次に進めます。
     * 
     * @param resultSet
     *            結果セット。{@literal null}であってはいけません
     * @return 次に進めたかどうか
     */
    public static boolean next(final ResultSet resultSet) {
        assertArgumentNotNull("resultSet", resultSet);

        try {
            return resultSet.next();
        } catch (final SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }

    /**
     * カーソルを指定した位置まで進めます。
     * 
     * @param resultSet
     *            結果セット。{@literal null}であってはいけません
     * @param index
     *            位置
     * @return 指定した位置まで進めたかどうか
     * @throws SQLRuntimeException
     *             SQL例外が起こった場合。
     */
    public static boolean absolute(final ResultSet resultSet, final int index)
            throws SQLRuntimeException {
        assertArgumentNotNull("resultSet", resultSet);

        try {
            return resultSet.absolute(index);
        } catch (final SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }

}
