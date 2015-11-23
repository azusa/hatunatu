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
package org.seasar.dao.impl;

/**
 * @author higa
 * 
 */
public final class RelationKey {

    private Object[] values;

    private int hashCode;

    public RelationKey(Object[] values) {
        this.values = values;
        for (int i = 0; i < values.length; ++i) {
            hashCode += values[i].hashCode();
        }
    }

    public Object[] getValues() {
        return values;
    }

    public int hashCode() {
        return hashCode;
    }

    public boolean equals(Object o) {
        if (!(o instanceof RelationKey)) {
            return false;
        }
        Object[] otherValues = ((RelationKey) o).values;
        if (values.length != otherValues.length) {
            return false;
        }
        for (int i = 0; i < values.length; ++i) {
            if (!values[i].equals(otherValues[i])) {
                return false;
            }
        }
        return true;
    }
}
