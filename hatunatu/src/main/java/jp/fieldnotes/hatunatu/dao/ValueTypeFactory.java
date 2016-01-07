package jp.fieldnotes.hatunatu.dao;

import jp.fieldnotes.hatunatu.api.ValueType;

public interface ValueTypeFactory {


        public ValueType getValueTypeByClass(Class clazz);


}
