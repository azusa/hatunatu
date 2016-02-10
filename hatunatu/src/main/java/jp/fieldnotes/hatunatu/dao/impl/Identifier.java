package jp.fieldnotes.hatunatu.dao.impl;

import jp.fieldnotes.hatunatu.dao.annotation.tiger.IdType;

import java.util.Locale;

/**
 * Object of Identifier read by {@link jp.fieldnotes.hatunatu.dao.BeanAnnotationReader}.
 */
public class Identifier {

    private IdType idType;

    private String sequenceName;

    private String dbms;

    private int allocationSize;

    public IdType getIdType() {
        return idType;
    }

    public void setIdType(IdType idType) {
        this.idType = idType;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public String getDbms() {
        return dbms;
    }

    public void setDbms(String dbms) {
        this.dbms = dbms;
    }

    public int getAllocationSize() {
        return allocationSize;
    }

    public void setAllocationSize(int allocationSize) {
        this.allocationSize = allocationSize;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(idType.toString().toLowerCase(Locale.ENGLISH));
        if (sequenceName != null) {
            sb.append(", ");
            sb.append("sequenceName=");
            sb.append(sequenceName);
            sb.append(", ");
            sb.append("allocationSize=");
            sb.append(allocationSize);
        }
        return sb.toString();
    }
}
