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
package jp.fieldnotes.hatunatu.dao.annotation.tiger;

import java.lang.annotation.*;

/**
 * プロシージャの呼び出しを示します。
 * <p>
 * このアノテーションが指定されたメソッドは、引数の数が0または1でなければいけません。
 * </p>
 * <p>
 * 引数の数が1のとき、その引数の型はDTOでなけばいけません。 DTOのフィールドに{@link ProcedureParameter}を指定することでプロシージャのパラメータを示すことができます。
 * </p>
 * 
 * @see
 * @author taedium
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ProcedureCall {
    /**
     * プロシージャ名
     */
    String value();
}