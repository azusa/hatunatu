package jp.fieldnotes.hatunatu.dao;

import jp.fieldnotes.hatunatu.api.ValueType;

/**
 * Created by azusa on 2015/12/04.
 */
public interface ValueTypeFactory {


        public ValueType getValueTypeByClass(Class clazz);


}
