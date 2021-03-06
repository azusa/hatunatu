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
import jp.fieldnotes.hatunatu.dao.exception.IllegalBoolExpressionRuntimeException;
import jp.fieldnotes.hatunatu.dao.util.OgnlUtil;

public class IfNode extends ContainerNode {

    private String expression;

    private Object parsedExpression;

    private ElseNode elseNode;

    public IfNode(String expression) {
        this.expression = expression;
        this.parsedExpression = OgnlUtil.parseExpression(expression);
    }

    public String getExpression() {
        return expression;
    }

    public ElseNode getElseNode() {
        return elseNode;
    }

    public void setElseNode(ElseNode elseNode) {
        this.elseNode = elseNode;
    }

    @Override
    public void accept(CommandContext ctx) {
        Object result = OgnlUtil.getValue(parsedExpression, ctx);
        if (result instanceof Boolean) {
            if (((Boolean) result).booleanValue()) {
                super.accept(ctx);
                ctx.setEnabled(true);
            } else if (elseNode != null) {
                elseNode.accept(ctx);
                ctx.setEnabled(true);
            }
        } else {
            throw new IllegalBoolExpressionRuntimeException(expression);
        }
    }

}
