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

import jp.fieldnotes.hatunatu.dao.BeanAnnotationReader;
import jp.fieldnotes.hatunatu.api.DtoMetaData;
import jp.fieldnotes.hatunatu.dao.PropertyTypeFactory;
import jp.fieldnotes.hatunatu.api.PropertyType;
import jp.fieldnotes.hatunatu.util.collection.CaseInsensitiveMap;
import jp.fieldnotes.hatunatu.util.exception.PropertyNotFoundRuntimeException;

/**
 * @author higa
 * 
 */
public class DtoMetaDataImpl implements DtoMetaData {

    private Class beanClass;

    private CaseInsensitiveMap<PropertyType> propertyTypes = new CaseInsensitiveMap<>();

    protected BeanAnnotationReader beanAnnotationReader;

    protected PropertyTypeFactory propertyTypeFactory;

    public DtoMetaDataImpl() {
    }

    public void initialize() {
        setupPropertyType();
    }

    /**
     * @see DtoMetaData#getBeanClass()
     */
    public Class getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    /**
     * @see DtoMetaData#getPropertyTypeSize()
     */
    public int getPropertyTypeSize() {
        return propertyTypes.size();
    }

    /**
     * @see DtoMetaData#getPropertyType(int)
     */
    public PropertyType getPropertyType(int index) {
        return (PropertyType) propertyTypes.getAt(index);
    }

    /**
     * @see DtoMetaData#getPropertyType(java.lang.String)
     */
    public PropertyType getPropertyType(String propertyName)
            throws PropertyNotFoundRuntimeException {

        PropertyType propertyType =  propertyTypes
                .get(propertyName);
        if (propertyType == null) {
            throw new PropertyNotFoundRuntimeException(beanClass, propertyName);
        }
        return propertyType;
    }

    /**
     * @see DtoMetaData#hasPropertyType(java.lang.String)
     */
    public boolean hasPropertyType(String propertyName) {
        return propertyTypes.get(propertyName) != null;
    }

    protected void setupPropertyType() {
        PropertyType[] propertyTypes = propertyTypeFactory
                .createDtoPropertyTypes();
        for (int i = 0; i < propertyTypes.length; ++i) {
            PropertyType pt = propertyTypes[i];
            addPropertyType(pt);
        }
    }

    protected void addPropertyType(PropertyType propertyType) {
        propertyTypes.put(propertyType.getPropertyName(), propertyType);
    }

    public void setBeanAnnotationReader(
            BeanAnnotationReader beanAnnotationReader) {
        this.beanAnnotationReader = beanAnnotationReader;
    }

    public void setPropertyTypeFactory(PropertyTypeFactory propertyTypeFactory) {
        this.propertyTypeFactory = propertyTypeFactory;
    }

}
