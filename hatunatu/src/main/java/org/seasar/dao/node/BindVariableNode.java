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
package org.seasar.dao.node;

import org.seasar.dao.CommandContext;
import jp.fieldnotes.hatunatu.util.beans.BeanDesc;
import jp.fieldnotes.hatunatu.util.beans.PropertyDesc;
import jp.fieldnotes.hatunatu.util.beans.factory.BeanDescFactory;
import jp.fieldnotes.hatunatu.util.lang.StringUtil;

/**
 * @author higa
 * 
 */
public class BindVariableNode extends AbstractNode {

    private String expression;

    private String[] names;

    public BindVariableNode(String expression) {
        this.expression = expression;
        names = StringUtil.split(expression, ".");
        // baseName_ = array[0];
        // if (array.length > 1) {
        // propertyName_ = array[1];
        // }
    }

    public String getExpression() {
        return expression;
    }

    /**
     * @see org.seasar.dao.Node#accept(org.seasar.dao.QueryContext)
     */
    public void accept(CommandContext ctx) {
        Object value = ctx.getArg(names[0]);
        Class clazz = ctx.getArgType(names[0]);
        for (int pos = 1; pos < names.length; pos++) {
            BeanDesc beanDesc = BeanDescFactory.getBeanDesc(clazz);
            PropertyDesc pd = beanDesc.getPropertyDesc(names[pos]);
            if (value == null) {
                break;
            }
            value = pd.getValue(value);
            clazz = pd.getPropertyType();
        }
        ctx.addSql("?", value, clazz);
    }
}
