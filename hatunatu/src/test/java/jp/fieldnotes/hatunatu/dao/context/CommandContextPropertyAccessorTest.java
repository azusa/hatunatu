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
package jp.fieldnotes.hatunatu.dao.context;

import junit.framework.TestCase;
import ognl.Ognl;
import ognl.OgnlRuntime;

import jp.fieldnotes.hatunatu.dao.CommandContext;

/**
 * @author higa
 * 
 */
public class CommandContextPropertyAccessorTest extends TestCase {

    protected void tearDown() throws Exception {
        OgnlRuntime.setPropertyAccessor(CommandContext.class, null);
    }

    public void testGetProperty() throws Exception {
        CommandContext ctx = new CommandContextImpl();
        ctx.addArg("aaa", "111", String.class);
        OgnlRuntime.setPropertyAccessor(CommandContext.class,
                new CommandContextPropertyAccessor());
        assertEquals("1", "111", Ognl.getValue("aaa", ctx));
        String s = "ELSEhogeEND";
        System.out.println(s.substring(4, s.length() - 3));
    }
}