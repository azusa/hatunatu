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
package jp.fieldnotes.hatunatu.util.beans;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

import jp.fieldnotes.hatunatu.api.beans.FieldDesc;
import jp.fieldnotes.hatunatu.api.beans.MethodDesc;
import jp.fieldnotes.hatunatu.api.beans.PropertyDesc;
import jp.fieldnotes.hatunatu.util.beans.factory.BeanDescFactory;

/**
 * JavaBeansのメタデータを扱うためのインターフェースです。
 * <p>
 * {@link jp.fieldnotes.hatunatu.api.beans.BeanDesc}のインスタンスは{@link BeanDescFactory}から取得します。
 * <p>
 * 
 * <pre>
 * BeanDesc beanDesc = BeanDescFactory.getBeanDesc(Foo.class);
 * </pre>
 * <p>
 * 取得した{@link jp.fieldnotes.hatunatu.api.beans.BeanDesc}から，対象となるJavaBeansのプロパティやフィールド、コンストラクタ、メソッドのメタデータを取得できます。
 * </p>
 * 
 * <pre>
 * for (PropertyDesc propertyDesc : beanDesc.getPropertyDescs()) {
 *     propertyDesc.getValue(foo); // Foo のプロパティの値を取得
 * }
 * 
 * for (FieldDesc fieldDesc : beanDesc.getFieldDescs()) {
 *     fieldDesc.getFileldValue(foo); // Foo のフィールドの値を取得
 * }
 * 
 * for (ConstructorDesc constructorDesc : beanDesc.getConstructorDescs()) {
 *     constructorDesc.newInstance(...); // Foo のインスタンスを生成
 * }
 * 
 * for (String methodName : beanDesc.getMethodNames()) {
 *     for (MethodDesc methodDesc : beanDesc.getMethodDescs(methodName)) {
 *         methodDesc.invoke(foo, ...); // Foo のメソッドを起動
 *     }
 * }
 * </pre>
 * 
 * @author higa
 * @see BeanDescFactory
 */
public interface BeanDesc {

    /**
     * Beanのクラスを返します。
     * 
     * @param <T>
     *            Beanのクラス
     * @return Beanのクラス
     */
    <T> Class<T> getBeanClass();

    /**
     * 型変数から型引数へのマップを返します。
     * 
     * @return 型変数から型引数へのマップ
     */
    Map<TypeVariable<?>, Type> getTypeVariables();

    /**
     * {@link jp.fieldnotes.hatunatu.api.beans.PropertyDesc}を持っているかどうかを返します。
     * 
     * @param propertyName
     *            プロパティ名。{@literal null}や空文字列であってはいけません
     * @return {@link jp.fieldnotes.hatunatu.api.beans.PropertyDesc}を持っているかどうか
     */
    boolean hasPropertyDesc(String propertyName);

    /**
     * {@link jp.fieldnotes.hatunatu.api.beans.PropertyDesc}を返します。
     * 
     * @param propertyName
     *            プロパティ名。{@literal null}や空文字列であってはいけません
     * @return {@link jp.fieldnotes.hatunatu.api.beans.PropertyDesc}
     */
    jp.fieldnotes.hatunatu.api.beans.PropertyDesc getPropertyDesc(String propertyName);

    /**
     * {@link jp.fieldnotes.hatunatu.api.beans.PropertyDesc}を返します。
     * 
     * @param index
     *            {@link jp.fieldnotes.hatunatu.api.beans.PropertyDesc}のインデックス
     * @return {@link jp.fieldnotes.hatunatu.api.beans.PropertyDesc}
     */
    jp.fieldnotes.hatunatu.api.beans.PropertyDesc getPropertyDesc(int index);

    /**
     * {@link jp.fieldnotes.hatunatu.api.beans.PropertyDesc}の数を返します。
     * 
     * @return {@link jp.fieldnotes.hatunatu.api.beans.PropertyDesc}の数
     */
    int getPropertyDescSize();

    /**
     * {@link jp.fieldnotes.hatunatu.api.beans.PropertyDesc}の{@link Iterable}を返します。
     * 
     * @return {@link jp.fieldnotes.hatunatu.api.beans.PropertyDesc}の{@link Iterable}
     */
    Iterable<PropertyDesc> getPropertyDescs();

    /**
     * {@link jp.fieldnotes.hatunatu.api.beans.FieldDesc}を持っているかどうかを返します。
     * 
     * @param fieldName
     *            フィールド名。{@literal null}や空文字列であってはいけません
     * @return {@link jp.fieldnotes.hatunatu.api.beans.FieldDesc}を持っているかどうか
     */
    boolean hasFieldDesc(String fieldName);

    /**
     * {@link jp.fieldnotes.hatunatu.api.beans.FieldDesc}を返します。
     * 
     * @param fieldName
     *            フィールド名。{@literal null}や空文字列であってはいけません
     * @return {@link jp.fieldnotes.hatunatu.api.beans.FieldDesc}
     */
    jp.fieldnotes.hatunatu.api.beans.FieldDesc getFieldDesc(String fieldName);

    /**
     * {@link jp.fieldnotes.hatunatu.api.beans.FieldDesc}を返します。
     * 
     * @param index
     *            {@link jp.fieldnotes.hatunatu.api.beans.FieldDesc}のインデックス
     * @return {@link jp.fieldnotes.hatunatu.api.beans.FieldDesc}
     */
    jp.fieldnotes.hatunatu.api.beans.FieldDesc getFieldDesc(int index);

    /**
     * {@link jp.fieldnotes.hatunatu.api.beans.FieldDesc}の数を返します。
     * 
     * @return {@link jp.fieldnotes.hatunatu.api.beans.FieldDesc}の数
     */
    int getFieldDescSize();

    /**
     * {@link jp.fieldnotes.hatunatu.api.beans.FieldDesc}の{@link Iterable}を返します。
     * 
     * @return {@link jp.fieldnotes.hatunatu.api.beans.FieldDesc}の{@link Iterable}
     */
    Iterable<FieldDesc> getFieldDescs();

    /**
     * 新しいインスタンスを作成します。
     * 
     * @param <T>
     *            Beanクラスの型
     * @param args
     *            コンストラクタに渡す引数の並び
     * @return 新しいインスタンス
     */
    <T> T newInstance(Object... args);

    /**
     * 引数の型に応じた{@link jp.fieldnotes.hatunatu.api.beans.ConstructorDesc}を返します。
     * 
     * @param paramTypes
     *            コンストラクタに渡す引数型の並び
     * @return 引数の型に応じた{@link ConstructorDesc}
     */
    jp.fieldnotes.hatunatu.api.beans.ConstructorDesc getConstructorDesc(Class<?>... paramTypes);

    /**
     * 引数に適合する{@link jp.fieldnotes.hatunatu.api.beans.ConstructorDesc}を返します。
     * 
     * @param args
     *            コンストラクタに渡す引数の並び
     * @return 引数に適合する{@link Constructor}
     */
    jp.fieldnotes.hatunatu.api.beans.ConstructorDesc getSuitableConstructorDesc(Object... args);

    /**
     * {@link jp.fieldnotes.hatunatu.api.beans.ConstructorDesc}を返します。
     * 
     * @param index
     *            {@link jp.fieldnotes.hatunatu.api.beans.ConstructorDesc}のインデックス
     * @return {@link jp.fieldnotes.hatunatu.api.beans.ConstructorDesc}
     */
    jp.fieldnotes.hatunatu.api.beans.ConstructorDesc getConstructorDesc(int index);

    /**
     * {@link jp.fieldnotes.hatunatu.api.beans.ConstructorDesc}の数を返します。
     * 
     * @return {@link jp.fieldnotes.hatunatu.api.beans.ConstructorDesc}の数
     */
    int getConstructorDescSize();

    /**
     * {@link jp.fieldnotes.hatunatu.api.beans.ConstructorDesc}の{@link Iterable}を返します。
     * 
     * @return {@link jp.fieldnotes.hatunatu.api.beans.ConstructorDesc}の{@link Iterable}
     */
    Iterable<jp.fieldnotes.hatunatu.api.beans.ConstructorDesc> getConstructorDescs();

    /**
     * 引数の型に応じた{@link jp.fieldnotes.hatunatu.api.beans.MethodDesc}を返します。
     * 
     * @param methodName
     *            メソッド名。{@literal null}や空文字列であってはいけません
     * @param paramTypes
     *            メソッドの引数型の並び
     * @return 引数の型に応じた{@link MethodDesc} メソッド
     */
    jp.fieldnotes.hatunatu.api.beans.MethodDesc getMethodDesc(String methodName, Class<?>... paramTypes);

    /**
     * 引数の型に応じた{@link jp.fieldnotes.hatunatu.api.beans.MethodDesc}を返します。見つからない場合は、{@literal null}を返します。
     * 
     * @param methodName
     *            メソッド名。{@literal null}や空文字列であってはいけません
     * @param paramTypes
     *            メソッドの引数型の並び
     * @return 引数の型に応じた{@link MethodDesc}
     */
    jp.fieldnotes.hatunatu.api.beans.MethodDesc getMethodDescNoException(String methodName,
                                                                         Class<?>... paramTypes);

    /**
     * 引数に適合する{@link jp.fieldnotes.hatunatu.api.beans.MethodDesc}を返します。
     * 
     * @param methodName
     *            メソッド名。{@literal null}や空文字列であってはいけません
     * @param args
     *            メソッドの引数の並び
     * @return 引数に適合する{@link MethodDesc} メソッド
     */
    jp.fieldnotes.hatunatu.api.beans.MethodDesc getSuitableMethodDesc(String methodName, Object... args);

    /**
     * {@link jp.fieldnotes.hatunatu.api.beans.MethodDesc}があるかどうか返します。
     * 
     * @param methodName
     *            メソッド名。{@literal null}や空文字列であってはいけません
     * @return {@link jp.fieldnotes.hatunatu.api.beans.MethodDesc}があるかどうか
     */
    boolean hasMethodDesc(String methodName);

    /**
     * {@link jp.fieldnotes.hatunatu.api.beans.MethodDesc}の配列を返します。
     * 
     * @param methodName
     *            メソッド名。{@literal null}や空文字列であってはいけません
     * @return {@link jp.fieldnotes.hatunatu.api.beans.MethodDesc}の配列
     */
    MethodDesc[] getMethodDescs(String methodName);

    /**
     * メソッド名の配列を返します。
     * 
     * @return メソッド名の配列
     */
    String[] getMethodNames();

}
