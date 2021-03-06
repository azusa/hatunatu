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
package jp.fieldnotes.hatunatu.util.xml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;

import jp.fieldnotes.hatunatu.util.exception.IORuntimeException;
import jp.fieldnotes.hatunatu.util.exception.SAXRuntimeException;
import jp.fieldnotes.hatunatu.util.misc.AssertionUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * {@link DocumentBuilder}用のユーティリティクラスです。
 * 
 * @author higa
 */
public abstract class DocumentBuilderUtil {

    /**
     * XMLを解析します。
     * 
     * @param builder
     *            {@link DocumentBuilder}。{@literal null}であってはいけません
     * @param is
     *            入力ストリーム。{@literal null}であってはいけません
     * @return {@link Document}
     */
    public static Document parse(final DocumentBuilder builder,
            final InputStream is) {
        AssertionUtil.assertArgumentNotNull("builder", builder);
        AssertionUtil.assertArgumentNotNull("is", is);

        try {
            return builder.parse(is);
        } catch (final SAXException e) {
            throw new SAXRuntimeException(e);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

}
