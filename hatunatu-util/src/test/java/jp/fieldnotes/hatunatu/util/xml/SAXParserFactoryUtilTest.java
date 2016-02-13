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

import jp.fieldnotes.hatunatu.util.io.ResourceUtil;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * {@link SAXParserFactoryUtil}のテストです。
 * 
 * @author koichik
 */
public class SAXParserFactoryUtilTest {

    boolean included;

    /**
     * {@link SAXParserFactoryUtil#setXIncludeAware}のテストです。
     * 
     * @throws Exception
     */
    @Test
    public void testSetXIncludeAware() throws Exception {
        SAXParserFactory spf = SAXParserFactoryUtil.newInstance();
        SAXParserFactoryUtil.setXIncludeAware(spf, true);
        spf.setNamespaceAware(true);
        SAXParser parser = SAXParserFactoryUtil.newSAXParser(spf);

        InputSource is =
            new InputSource(
                ResourceUtil
                    .getResourceAsStream("jp/fieldnotes/hatunatu/util/xml/include.xml"));
        is.setSystemId("include.xml");
        parser.parse(is, new DefaultHandler() {

            @Override
            public void startElement(String uri, String localName,
                    String qName, Attributes attributes) throws SAXException {
                if ("bar".equals(qName)) {
                    included = true;
                }
            }

            @Override
            public InputSource resolveEntity(String publicId, String systemId)
                    throws IOException, SAXException {
                InputSource is =
                    new InputSource(
                        ResourceUtil
                            .getResourceAsStream("jp/fieldnotes/hatunatu/util/xml/included.xml"));
                is.setSystemId("included.xml");
                return is;
            }

        });
        assertTrue(included);
    }
}
