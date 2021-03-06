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
package jp.fieldnotes.hatunatu.util.lang;

import java.util.Iterator;

import jp.fieldnotes.hatunatu.util.exception.SNoSuchElementException;
import jp.fieldnotes.hatunatu.util.exception.SUnsupportedOperationException;

import static jp.fieldnotes.hatunatu.util.misc.AssertionUtil.*;

/**
 * クラスローダの階層を親クラスローダに向かって反復する{@link Iterator}です。
 * <p>
 * 次のように使います．
 * </p>
 * 
 * <pre>
 * import static org.seasar.util.lang.ClassLoaderIterator.*;
 * 
 * ClassLoader classLoader = ...;
 * for (ClassLoader loader : iterable(classLoader)) {
 *     ...
 * }
 * </pre>
 * 
 * @author koichik
 */
public class ClassLoaderIterator implements Iterator<ClassLoader> {

    /** クラスローダ */
    protected ClassLoader classLoader;

    /**
     * for each構文で使用するために{@link ClassLoaderIterator}をラップした{@link Iterable}を返します。
     * 
     * @param classLoader
     *            クラスローダ。{@literal null}であってはいけません
     * @return {@link ClassLoaderIterator}をラップした{@link Iterable}
     */
    public static Iterable<ClassLoader> iterable(final ClassLoader classLoader) {
        return new Iterable<ClassLoader>() {
            @Override
            public Iterator<ClassLoader> iterator() {
                return new ClassLoaderIterator(classLoader);
            }
        };
    }

    /**
     * インスタンスを構築します。
     * 
     * @param classLoader
     *            クラスローダ。{@literal null}であってはいけません
     */
    public ClassLoaderIterator(final ClassLoader classLoader) {
        assertArgumentNotNull("classLoader", classLoader);
        this.classLoader = classLoader;
    }

    @Override
    public boolean hasNext() {
        return classLoader != null;
    }

    @Override
    public ClassLoader next() {
        if (!hasNext()) {
            throw new SNoSuchElementException();
        }
        final ClassLoader result = classLoader;
        classLoader = classLoader.getParent();
        return result;
    }

    @Override
    public void remove() {
        throw new SUnsupportedOperationException("remove");
    }

}
