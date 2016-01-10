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

import jp.fieldnotes.hatunatu.dao.CommandContext;
import jp.fieldnotes.hatunatu.dao.Node;
import jp.fieldnotes.hatunatu.dao.util.OgnlUtil;

import java.lang.reflect.Array;
import java.util.List;

public class ParenBindVariableNode extends AbstractNode {

    private String expression;

    private Object parsedExpression;

    public ParenBindVariableNode(String expression) {
        this.expression = expression;
        this.parsedExpression = OgnlUtil.parseExpression(expression);
    }

    public String getExpression() {
        return expression;
    }

    /**
     * @see Node#accept(org.seasar.dao.QueryContext)
     */
    public void accept(CommandContext ctx) {
        Object var = OgnlUtil.getValue(parsedExpression, ctx);
        if (var instanceof List) {
            bindArray(ctx, ((List) var).toArray());
        } else if (var == null) {
            return;
        } else if (var.getClass().isArray()) {
            bindArray(ctx, var);
        } else {
            ctx.addSql("?", var, var.getClass());
        }

    }

    private void bindArray(CommandContext ctx, Object array) {
        int length = Array.getLength(array);
        if (length == 0) {
            return;
        }
        Class clazz = null;
        for (int i = 0; i < length; ++i) {
            Object o = Array.get(array, i);
            if (o != null) {
                clazz = o.getClass();
            }
        }
        ctx.addSql("(");
        ctx.addSql("?", Array.get(array, 0), clazz);
        for (int i = 1; i < length; ++i) {
            ctx.addSql(", ?", Array.get(array, i), clazz);
        }
        ctx.addSql(")");
    }
}