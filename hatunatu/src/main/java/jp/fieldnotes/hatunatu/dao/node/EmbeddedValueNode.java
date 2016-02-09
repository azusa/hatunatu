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

import jp.fieldnotes.hatunatu.api.beans.BeanDesc;
import jp.fieldnotes.hatunatu.api.beans.PropertyDesc;
import jp.fieldnotes.hatunatu.dao.CommandContext;
import jp.fieldnotes.hatunatu.util.beans.factory.BeanDescFactory;
import jp.fieldnotes.hatunatu.util.lang.StringUtil;

public class EmbeddedValueNode extends AbstractNode {

    private String expression;

    private String baseName;

    private String propertyName;

    public EmbeddedValueNode(String expression) {
        this.expression = expression;
        String[] array = StringUtil.split(expression, ".");
        this.baseName = array[0];
        if (array.length > 1) {
            this.propertyName = array[1];
        }
    }

    public String getExpression() {
        return expression;
    }

    @Override
    public void accept(CommandContext ctx) {
        Object value = ctx.getArg(baseName);
        Class clazz = ctx.getArgType(baseName);
        if (propertyName != null) {
            BeanDesc beanDesc = BeanDescFactory.getBeanDesc(clazz);
            PropertyDesc pd = beanDesc.getPropertyDesc(propertyName);
            value = pd.getValue(value);
            clazz = pd.getPropertyType();
        }
        if (value != null) {
            ctx.addSql(value.toString());
        }
    }
}
