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
package jp.fieldnotes.hatunatu.util.io;

import jp.fieldnotes.hatunatu.util.exception.ResourceNotFoundRuntimeException;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import static org.junit.Assert.*;

/**
 * @author higa
 * 
 */
public class ResourceUtilTest {

    @Test
    public void testGetResourcePath() throws Exception {
        assertEquals(
            "1",
            "aaa/bbb.xml",
            ResourceUtil.getResourcePath("aaa/bbb.xml", "xml"));
        assertEquals(
            "2",
            "aaa/bbb.xml",
            ResourceUtil.getResourcePath("aaa.bbb", "xml"));
        assertEquals(
            "3",
            "jp/fieldnotes/hatunatu/util/io/ResourceUtilTest.class",
            ResourceUtil.getResourcePath(getClass()));
    }

    @Test
    public void testGetResource() throws Exception {
        assertNotNull(ResourceUtil.getResource(
            "java/lang/String.class",
            "class"));
        assertNotNull(ResourceUtil.getResource("jp/fieldnotes"));
        try {
            ResourceUtil.getResource("hoge", "xml");
            fail("2");
        } catch (ResourceNotFoundRuntimeException e) {
            System.out.println(e);
            assertEquals("3", "hoge.xml", e.getPath());
        }
        System.out.println(ResourceUtil.getResource("."));
    }

    @Test
    public void testGetResourceAsStreamNoException() throws Exception {
        assertNotNull(ResourceUtil.getResourceAsStreamNoException(
            "java/lang/String.class",
            "class"));
        assertNull(ResourceUtil.getResourceAsStreamNoException(
            "java/lang/String2.class",
            "class"));
    }

    @Test
    public void testGetBuildDir() throws Exception {
        File file = ResourceUtil.getBuildDir(getClass());
        System.out.println(file);
        File file2 = ResourceUtil.getBuildDir("jp/fieldnotes/hatunatu/util/io");
        assertEquals(file, file2);
        File junitJar = ResourceUtil.getBuildDir(TestCase.class);
        assertTrue(junitJar.exists());
        URL url = junitJar.toURI().toURL();
        URLClassLoader loader = new URLClassLoader(new URL[] { url });
        loader.loadClass(TestCase.class.getName());
    }

    @Test
    public void testIsExist() throws Exception {
        assertEquals("1", true, ResourceUtil.isExist("UTLMessages.properties"));
        assertEquals("2", false, ResourceUtil.isExist("hoge"));
    }

    @Test
    public void testGetExtension() throws Exception {
        assertEquals("1", "xml", ResourceUtil.getExtension("aaa/bbb.xml"));
        assertEquals("2", null, ResourceUtil.getExtension("aaa"));
    }

    @Test
    public void testRemoteExtension() throws Exception {
        assertEquals(
            "1",
            "aaa/bbb",
            ResourceUtil.removeExtension("aaa/bbb.xml"));
        assertEquals("2", "aaa/bbb", ResourceUtil.removeExtension("aaa/bbb"));
    }

    @Test
    public void testToExternalForm() throws Exception {
        URL url = new File("/Program File").toURI().toURL();
        assertEquals(
            "file:" + getRoot() + "Program File",
            ResourceUtil.toExternalForm(url));
    }

    @Test
    public void testGetFileName() throws Exception {
        URL url = new File("/Program File").toURI().toURL();
        assertEquals(getRoot() + "Program File", ResourceUtil.getFileName(url));
        url = ResourceUtil.getResource("java/lang/String.class");
        assertNull(ResourceUtil.getFile(url));
    }

    private String getRoot() throws IOException {
        String root = new File("/").getCanonicalPath().replace('\\', '/');
        if (root.startsWith("/")) {
            return root;
        }
        return "/" + root;
    }

}
