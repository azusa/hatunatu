package org.seasar.dao;

import org.seasar.extension.jdbc.ValueType;
import org.seasar.extension.jdbc.types.ValueTypes;

/**
 * Created by azusa on 2015/12/04.
 */
public interface ValueTypeFactory {


        public ValueType getValueTypeByClass(Class clazz);


}
