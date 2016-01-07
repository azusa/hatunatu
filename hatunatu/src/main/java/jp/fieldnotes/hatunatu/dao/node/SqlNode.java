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
package jp.fieldnotes.hatunatu.dao.node;

import jp.fieldnotes.hatunatu.dao.Node;
import jp.fieldnotes.hatunatu.dao.CommandContext;

public class SqlNode extends AbstractNode {

    private String sql;

    public SqlNode(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    /**
     * @see Node#accept(org.seasar.dao.QueryContext)
     */
    public void accept(CommandContext ctx) {
        ctx.addSql(sql);
    }

}
