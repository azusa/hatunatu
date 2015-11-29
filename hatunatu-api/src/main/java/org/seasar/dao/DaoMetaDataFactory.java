package org.seasar.dao;

/**
 * Created by azusa on 2015/12/05.
 */
public interface DaoMetaDataFactory {
    DaoMetaData getDaoMetaData(Class targetClass);
}
