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
package jp.fieldnotes.hatunatu.dao.pager;

import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class NullPagingSqlRewriterTest {

    NullPagingSqlRewriter rewriter = new NullPagingSqlRewriter();

    @Test
    public void testRewrite() {
        String sql = "SELECT * FROM EMP WHERE ENAME = 'SCOTT'";
        QueryObject queryObject = new QueryObject();
        queryObject.setSql(sql);
        rewriter.rewrite(queryObject);
        assertThat(queryObject.getSql(), is(sql));
    }

}
