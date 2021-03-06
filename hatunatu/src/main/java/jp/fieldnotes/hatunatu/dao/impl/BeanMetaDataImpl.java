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
package jp.fieldnotes.hatunatu.dao.impl;

import jp.fieldnotes.hatunatu.api.BeanMetaData;
import jp.fieldnotes.hatunatu.api.IdentifierGenerator;
import jp.fieldnotes.hatunatu.api.PropertyType;
import jp.fieldnotes.hatunatu.api.RelationPropertyType;
import jp.fieldnotes.hatunatu.api.beans.BeanDesc;
import jp.fieldnotes.hatunatu.api.beans.PropertyDesc;
import jp.fieldnotes.hatunatu.dao.Dbms;
import jp.fieldnotes.hatunatu.dao.RelationPropertyTypeFactory;
import jp.fieldnotes.hatunatu.dao.TableNaming;
import jp.fieldnotes.hatunatu.dao.exception.ColumnNotFoundRuntimeException;
import jp.fieldnotes.hatunatu.dao.exception.NoPersistentPropertyTypeRuntimeException;
import jp.fieldnotes.hatunatu.dao.id.IdentifierGeneratorFactory;
import jp.fieldnotes.hatunatu.util.beans.factory.BeanDescFactory;
import jp.fieldnotes.hatunatu.util.collection.CaseInsensitiveMap;
import jp.fieldnotes.hatunatu.util.exception.PropertyNotFoundRuntimeException;
import jp.fieldnotes.hatunatu.util.lang.ClassUtil;

import java.util.*;
import java.util.stream.Collectors;

public class BeanMetaDataImpl extends DtoMetaDataImpl implements BeanMetaData {

    private String tableName;

    private Map propertyTypesByColumnName = new CaseInsensitiveMap();

    private List relationPropertyTypes = new ArrayList();

    private PropertyType[] primaryKeys;

    private String autoSelectList;

    private List identifierGenerators = new ArrayList();

    private Map identifierGeneratorsByPropertyName = new HashMap();

    private String versionNoPropertyName;

    private String timestampPropertyName;

    private Dbms dbms;

    private ModifiedPropertySupport modifiedPropertySupport;

    private TableNaming tableNaming;

    private RelationPropertyTypeFactory relationPropertyTypeFactory;

    private boolean relationToTable;

    public BeanMetaDataImpl() {
    }

    public void initialize() {
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(getBeanClass());
        setupTableName(beanDesc);
        setupProperty();
        setupPrimaryKey();
    }

    public void setDbms(Dbms dbms) {
        this.dbms = dbms;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public PropertyType getVersionNoPropertyType()
            throws PropertyNotFoundRuntimeException {

        return getPropertyType(getVersionNoPropertyName());
    }

    @Override
    public PropertyType getTimestampPropertyType()
            throws PropertyNotFoundRuntimeException {

        return getPropertyType(getTimestampPropertyName());
    }

    @Override
    public String getVersionNoPropertyName() {
        return versionNoPropertyName;
    }

    public void setVersionNoPropertyName(String versionNoPropertyName) {
        this.versionNoPropertyName = versionNoPropertyName;
    }

    @Override
    public String getTimestampPropertyName() {
        return timestampPropertyName;
    }

    public void setTimestampPropertyName(String timestampPropertyName) {
        this.timestampPropertyName = timestampPropertyName;
    }

    @Override
    public PropertyType getPropertyTypeByColumnName(String columnName)
            throws ColumnNotFoundRuntimeException {

        PropertyType propertyType = (PropertyType) propertyTypesByColumnName
                .get(columnName);
        if (propertyType == null) {
            throw new ColumnNotFoundRuntimeException(tableName, columnName);
        }
        return propertyType;
    }

    @Override
    public PropertyType getPropertyTypeByAliasName(String alias) {
        if (hasPropertyTypeByColumnName(alias)) {
            return getPropertyTypeByColumnName(alias);
        }
        int index = alias.lastIndexOf('_');
        if (index < 0) {
            throw new ColumnNotFoundRuntimeException(tableName, alias);
        }
        String columnName = alias.substring(0, index);
        String relnoStr = alias.substring(index + 1);
        int relno = -1;
        try {
            relno = Integer.parseInt(relnoStr);
        } catch (Throwable t) {
            throw new ColumnNotFoundRuntimeException(tableName, alias);
        }
        RelationPropertyType rpt = getRelationPropertyType(relno);
        if (!rpt.getBeanMetaData().hasPropertyTypeByColumnName(columnName)) {
            throw new ColumnNotFoundRuntimeException(tableName, alias);
        }
        return rpt.getBeanMetaData().getPropertyTypeByColumnName(columnName);
    }

    @Override
    public boolean hasPropertyTypeByColumnName(String columnName) {
        return propertyTypesByColumnName.get(columnName) != null;
    }

    @Override
    public boolean hasPropertyTypeByAliasName(String alias) {
        if (hasPropertyTypeByColumnName(alias)) {
            return true;
        }
        int index = alias.lastIndexOf('_');
        if (index < 0) {
            return false;
        }
        String columnName = alias.substring(0, index);
        String relnoStr = alias.substring(index + 1);
        int relno = -1;
        try {
            relno = Integer.parseInt(relnoStr);
        } catch (Throwable t) {
            return false;
        }
        if (relno >= getRelationPropertyTypeSize()) {
            return false;
        }
        RelationPropertyType rpt = getRelationPropertyType(relno);
        return rpt.getBeanMetaData().hasPropertyTypeByColumnName(columnName);
    }

    @Override
    public boolean hasVersionNoPropertyType() {
        return hasPropertyType(getVersionNoPropertyName());
    }

    @Override
    public boolean hasTimestampPropertyType() {
        return hasPropertyType(getTimestampPropertyName());
    }

    @Override
    public String convertFullColumnName(String alias) {
        if (hasPropertyTypeByColumnName(alias)) {
            return tableName + "." + alias;
        }
        int index = alias.lastIndexOf('_');
        if (index < 0) {
            throw new ColumnNotFoundRuntimeException(tableName, alias);
        }
        String columnName = alias.substring(0, index);
        String relnoStr = alias.substring(index + 1);
        int relno = -1;
        try {
            relno = Integer.parseInt(relnoStr);
        } catch (Throwable t) {
            throw new ColumnNotFoundRuntimeException(tableName, alias);
        }
        RelationPropertyType rpt = getRelationPropertyType(relno);
        if (!rpt.getBeanMetaData().hasPropertyTypeByColumnName(columnName)) {
            throw new ColumnNotFoundRuntimeException(tableName, alias);
        }
        return rpt.getPropertyName() + "." + columnName;
    }

    @Override
    public int getRelationPropertyTypeSize() {
        return relationPropertyTypes.size();
    }

    @Override
    public RelationPropertyType getRelationPropertyType(int index) {
        return (RelationPropertyType) relationPropertyTypes.get(index);
    }

    @Override
    public RelationPropertyType getRelationPropertyType(String propertyName)
            throws PropertyNotFoundRuntimeException {

        for (int i = 0; i < getRelationPropertyTypeSize(); i++) {
            RelationPropertyType rpt = (RelationPropertyType) relationPropertyTypes
                    .get(i);
            if (rpt != null
                    && rpt.getPropertyName().equalsIgnoreCase(propertyName)) {
                return rpt;
            }
        }
        throw new PropertyNotFoundRuntimeException(getBeanClass(), propertyName);
    }

    @Override
    public List<String> getPrimaryKeys() {
        return Arrays.stream(primaryKeys).map(propertyType -> propertyType.getColumnName()).collect(Collectors.toList());
    }

    protected void setupTableName(BeanDesc beanDesc) {
        String ta = beanAnnotationReader.getTableAnnotation();
        if (ta != null) {
            tableName = ta;
        } else {
            tableName = tableNaming.fromEntityNameToTableName(ClassUtil
                    .getShortClassName(beanDesc.getBeanClass().getName()));
        }
    }

    protected void setupProperty() {
        PropertyType[] propertyTypes = propertyTypeFactory
                .createBeanPropertyTypes(tableName);
        for (int i = 0; i < propertyTypes.length; i++) {
            PropertyType pt = propertyTypes[i];
            addPropertyType(pt);
            propertyTypesByColumnName.put(pt.getColumnName(), pt);
        }

        RelationPropertyType[] relationPropertyTypes = relationPropertyTypeFactory
                .createRelationPropertyTypes();
        for (int i = 0; i < relationPropertyTypes.length; i++) {
            RelationPropertyType rpt = relationPropertyTypes[i];
            addRelationPropertyType(rpt);
        }
    }

    protected void setupPrimaryKey() {
        List keys = new ArrayList();
        for (PropertyType pt : this.getPropertyTypes()) {
            if (pt.isPrimaryKey()) {
                keys.add(pt);
                setupIdentifierGenerator(pt);
            }
        }
        primaryKeys = (PropertyType[]) keys.toArray(new PropertyType[keys
                .size()]);
    }

    protected void setupIdentifierGenerator(PropertyType propertyType) {
        PropertyDesc pd = propertyType.getPropertyDesc();
        String propertyName = propertyType.getPropertyName();
        Identifier idType = beanAnnotationReader.getId(pd, dbms);
        IdentifierGenerator generator = IdentifierGeneratorFactory
                .createIdentifierGenerator(propertyType, dbms, idType);
        identifierGenerators.add(generator);
        identifierGeneratorsByPropertyName.put(propertyName, generator);
    }

    protected void addRelationPropertyType(RelationPropertyType rpt) {
        for (int i = relationPropertyTypes.size(); i <= rpt.getRelationNo(); ++i) {
            relationPropertyTypes.add(null);
        }
        relationPropertyTypes.set(rpt.getRelationNo(), rpt);
    }


    @Override
    public String getPrimaryKey(int index) {
        return primaryKeys[index].getColumnName();
    }

    @Override
    public int getIdentifierGeneratorSize() {
        return identifierGenerators.size();
    }

    @Override
    public IdentifierGenerator getIdentifierGenerator(int index) {
        return (IdentifierGenerator) identifierGenerators.get(index);
    }

    @Override
    public IdentifierGenerator getIdentifierGenerator(String propertyName) {
        return (IdentifierGenerator) identifierGeneratorsByPropertyName
                .get(propertyName);
    }

    /**
     * @see BeanMetaData#getAutoSelectList()
     */
    @Override
    public synchronized String getAutoSelectList() {
        if (autoSelectList != null) {
            return autoSelectList;
        }
        setupAutoSelectList();
        return autoSelectList;
    }

    protected void setupAutoSelectList() {
        StringBuilder buf = new StringBuilder(100);
        buf.append("SELECT ");
        boolean first = true;
        for (PropertyType pt : getPropertyTypes()) {
            if (pt.isPersistent()) {
                if (first) {
                    first = false;
                } else {
                    buf.append(", ");
                }
                buf.append(tableName);
                buf.append(".");
                buf.append(pt.getColumnName());
            }
        }
        for (int i = 0; i < getRelationPropertyTypeSize(); ++i) {
            RelationPropertyType rpt = getRelationPropertyType(i);
            BeanMetaData bmd = rpt.getBeanMetaData();
            for (PropertyType pt : bmd.getPropertyTypes()) {
                if (pt.isPersistent()) {
                    if (first) {
                        first = false;
                    } else {
                        buf.append(", ");
                    }
                    final String columnName = pt.getColumnName();
                    buf.append(rpt.getPropertyName());
                    buf.append(".");
                    buf.append(columnName);
                    buf.append(" AS ");
                    buf.append(columnName).append("_").append(
                            rpt.getRelationNo());
                }
            }
        }
        if (first) {
            throw new NoPersistentPropertyTypeRuntimeException(tableName);
        }
        autoSelectList = buf.toString();
    }

    public ModifiedPropertySupport getModifiedPropertySupport() {
        return modifiedPropertySupport;
    }

    public void setModifiedPropertySupport(
            final ModifiedPropertySupport propertyModifiedSupport) {
        this.modifiedPropertySupport = propertyModifiedSupport;
    }

    @Override
    public Set getModifiedPropertyNames(final Object bean) {
        return getModifiedPropertySupport().getModifiedPropertyNames(bean);
    }

    public TableNaming getTableNaming() {
        return tableNaming;
    }

    public void setTableNaming(TableNaming tableNaming) {
        this.tableNaming = tableNaming;
    }

    @Override
    public boolean hasRelationToTable(){
        return relationToTable;
    }

    /**
     * Set whether {@link BeanMetaData} has relation to table.
     *
     * @param relationToTable {@code true} when has relation to table
     */
    public void setRelationToTable(boolean relationToTable){
        this.relationToTable = relationToTable;
    }

    public void setRelationPropertyTypeFactory(
            RelationPropertyTypeFactory relationPropertyTypeFactory) {
        this.relationPropertyTypeFactory = relationPropertyTypeFactory;
    }


}
