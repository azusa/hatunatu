package jp.fieldnotes.hatunatu.api;

/**
 * Created by azusa on 2015/12/05.
 */
public interface DaoMetaDataFactory {
    DaoMetaData getDaoMetaData(Class targetClass);
}
