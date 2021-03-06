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
package jp.fieldnotes.hatunatu.util.log;

import jp.fieldnotes.hatunatu.util.exception.SIllegalArgumentException;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static jp.fieldnotes.hatunatu.util.log.Logger.*;

/**
 * @author higa
 * 
 */
public class LoggerTest {

    /**
     * @see org.junit.rules.ExpectedException
     */
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private Logger logger = Logger.getLogger(getClass());

    /**
     * @throws Exception
     */
    @Test
    public void testGetLogger() throws Exception {
        assertThat(
            Logger.getLogger(getClass()),
            is(sameInstance(Logger.getLogger(getClass()))));
    }

    /**
     * @throws Exception
     */
    @Test
    public void testDebug() throws Exception {
        logger.debug("debug");
    }

    /**
     * @throws Exception
     */
    @Test
    public void testInfo() throws Exception {
        logger.info("info");
    }

    /**
     * @throws Exception
     */
    @Test
    public void testWarn() throws Exception {
        logger.warn("warn");
    }

    /**
     * @throws Exception
     */
    @Test
    public void testError() throws Exception {
        logger.error("error");
    }

    /**
     * @throws Exception
     */
    @Test
    public void testFatal() throws Exception {
        logger.fatal("fatal");
    }

    /**
     * @throws Exception
     */
    @Test
    public void testLog() throws Exception {
        logger.log("ILOGTEST0001");
    }

    /**
     * @throws Exception
     */
    @Test
    public void testLogWithArgs() throws Exception {
        logger.log("ILOGTEST0002", "x", "y");
    }

    /**
     * @throws Exception
     */
    @Test
    public void testLogWithException() throws Exception {
        logger.log(format("ILOGTEST0001"), new Exception());
    }

    /**
     * @throws Exception
     */
    @Test
    public void testLogWithArgsAndException() throws Exception {
        logger.log(format("ILOGTEST0002", "1", "2"), new Exception());
    }

    /**
     * @throws Exception
     */
    @Test
    public void testPerformance() throws Exception {
        final int num = 100;
        long start = System.currentTimeMillis();
        for (int i = 0; i < num; i++) {
            System.out.println("test" + i);
        }
        long sysout = System.currentTimeMillis() - start;
        start = System.currentTimeMillis();
        for (int i = 0; i < num; i++) {
            logger.fatal("test" + i);
        }
        long logger = System.currentTimeMillis() - start;
        System.out.println("System.out:" + sysout);
        System.out.println("logger:" + logger);
    }

    /**
     * Test method for
     * {@link Logger#format(String, Object...)} .
     */
    @SuppressWarnings("static-access")
    @Test
    @Ignore
    public void testFormat() {
        exception.expect(SIllegalArgumentException.class);
        exception
            .expectMessage(is("[EUTL0009]引数[AUTL0009]が不正です。理由はmessageCode : AUTL0009"));
        logger.format("AUTL0009", "hoge");
    }

}
