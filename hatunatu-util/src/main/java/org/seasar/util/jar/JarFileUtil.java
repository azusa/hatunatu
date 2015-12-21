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
package org.seasar.util.jar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.seasar.util.exception.IORuntimeException;
import org.seasar.util.io.FileUtil;
import org.seasar.util.log.Logger;
import org.seasar.util.net.JarURLConnectionUtil;
import org.seasar.util.net.URLUtil;

import static org.seasar.util.log.Logger.*;
import static org.seasar.util.misc.AssertionUtil.*;

/**
 * {@link java.util.jar.JarFile}を扱うユーティリティクラスです。
 * 
 * @author higa
 */
public abstract class JarFileUtil {

    private static final Logger logger = Logger.getLogger(JarFileUtil.class);

    /**
     * 指定されたJarファイルを読み取るための<code>JarFile</code>を作成して返します。
     * 
     * @param file
     *            ファイルパス。{@literal null}であってはいけません
     * @return 指定されたJarファイルを読み取るための<code>JarFile</code>
     */
    public static JarFile create(final String file) {
        assertArgumentNotNull("file", file);

        try {
            return new JarFile(file);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 指定されたJarファイルを読み取るための<code>JarFile</code>を作成して返します。
     * 
     * @param file
     *            ファイル。{@literal null}であってはいけません
     * @return 指定されたJarファイルを読み取るための<code>JarFile</code>
     */
    public static JarFile create(final File file) {
        assertArgumentNotNull("file", file);

        try {
            return new JarFile(file);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 指定されたJarファイルエントリの内容を読み込むための入力ストリームを返します。
     * 
     * @param file
     *            Jarファイル。{@literal null}であってはいけません
     * @param entry
     *            Jarファイルエントリ。{@literal null}であってはいけません
     * @return 指定されたJarファイルエントリの内容を読み込むための入力ストリーム
     */
    public static InputStream getInputStream(final JarFile file,
            final ZipEntry entry) {
        assertArgumentNotNull("file", file);
        assertArgumentNotNull("entry", entry);

        try {
            return file.getInputStream(entry);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * URLで指定されたJarファイルを読み取るための<code>JarFile</code>を作成して返します。
     * 
     * @param jarUrl
     *            Jarファイルを示すURL。{@literal null}であってはいけません
     * @return 指定されたJarファイルを読み取るための<code>JarFile</code>
     */
    public static JarFile toJarFile(final URL jarUrl) {
        assertArgumentNotNull("jarUrl", jarUrl);

        final URLConnection con = URLUtil.openConnection(jarUrl);
        if (con instanceof JarURLConnection) {
            return JarURLConnectionUtil.getJarFile((JarURLConnection) con);
        }
        return create(new File(toJarFilePath(jarUrl)));
    }

    /**
     * URLで指定されたJarファイルのパスを返します。
     * 
     * @param jarUrl
     *            Jarファイルを示すURL。{@literal null}であってはいけません
     * @return URLで指定されたJarファイルのパス
     */
    public static String toJarFilePath(final URL jarUrl) {
        assertArgumentNotNull("jarUrl", jarUrl);

        final URL nestedUrl = URLUtil.create(jarUrl.getPath());
        final String nestedUrlPath = nestedUrl.getPath();
        final int pos = nestedUrlPath.lastIndexOf('!');
        final String jarFilePath = nestedUrlPath.substring(0, pos);
        final File jarFile = new File(URLUtil.decode(jarFilePath, "UTF8"));
        return FileUtil.getCanonicalPath(jarFile);
    }

    /**
     * Jarファイルをクローズします。
     * <p>
     * {@link JarFile#close()}が例外をスローした場合はログにエラーメッセージを出力します。 例外は再スローされません。
     * </p>
     * 
     * @param jarFile
     *            Jarファイル。{@literal null}であってはいけません
     */
    public static void close(final JarFile jarFile) {
        assertArgumentNotNull("jarFile", jarFile);

        try {
            jarFile.close();
        } catch (final IOException e) {
            logger.log(format("EUTL0017", e.getMessage()), e);
        }
    }

}
