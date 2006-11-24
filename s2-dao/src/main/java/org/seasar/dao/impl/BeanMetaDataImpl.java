/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
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

import java.lang.reflect.Field;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import org.seasar.dao.AnnotationReaderFactory;
import org.seasar.dao.BeanMetaData;
import org.seasar.dao.Dbms;
import org.seasar.dao.IdentifierGenerator;
import org.seasar.dao.ModifiedProperties;
import org.seasar.dao.NoPersistentPropertyTypeRuntimeException;
import org.seasar.dao.PropertyModifiedSupport;
import org.seasar.dao.RelationPropertyType;
import org.seasar.dao.id.IdentifierGeneratorFactory;
import org.seasar.extension.jdbc.ColumnNotFoundRuntimeException;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.util.DatabaseMetaDataUtil;
import org.seasar.framework.aop.javassist.AspectWeaver;
import org.seasar.framework.aop.javassist.EnhancedClassGenerator;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.PropertyNotFoundRuntimeException;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.beans.impl.PropertyDescImpl;
import org.seasar.framework.exception.CannotCompileRuntimeException;
import org.seasar.framework.exception.NoSuchFieldRuntimeException;
import org.seasar.framework.exception.NotFoundRuntimeException;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.CaseInsensitiveMap;
import org.seasar.framework.util.ClassLoaderUtil;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.FieldUtil;
import org.seasar.framework.util.StringUtil;

/**
 * @author higa
 * @author manhole
 */
public class BeanMetaDataImpl extends DtoMetaDataImpl implements BeanMetaData {

    private static Logger logger = Logger.getLogger(BeanMetaDataImpl.class);

    private static final String MODIFIED_PROPERTIES = "modifiedProperties";

    private static final String GET_MODIFIED_PROPERTIES = "getModifiedProperties";

    private String tableName;

    private Map propertyTypesByColumnName = new CaseInsensitiveMap();

    private List relationPropertyTypes = new ArrayList();

    private PropertyType[] primaryKeys = new PropertyType[0];

    private String autoSelectList;

    private boolean relation;

    private IdentifierGenerator identifierGenerator;

    private String versionNoPropertyName = "versionNo";

    private String timestampPropertyName = "timestamp";

    private AnnotationReaderFactory annotationReaderFactory;

    private Dbms dbms;

    private DatabaseMetaData databaseMetaData;

    private Class originalBeanClass;

    public BeanMetaDataImpl() {
    }

    /**
     * @deprecated
     */
    public BeanMetaDataImpl(Class beanClass, DatabaseMetaData dbMetaData,
            Dbms dbms) {
        this(beanClass, dbMetaData, dbms, new FieldAnnotationReaderFactory(),
                false);
    }

    /**
     * @deprecated
     */
    public BeanMetaDataImpl(Class beanClass, DatabaseMetaData dbMetaData,
            Dbms dbms, AnnotationReaderFactory annotationReaderFactory) {
        this(beanClass, dbMetaData, dbms, annotationReaderFactory, false);
    }

    /**
     * @deprecated
     */
    public BeanMetaDataImpl(Class beanClass, DatabaseMetaData dbMetaData,
            Dbms dbms, AnnotationReaderFactory annotationReaderFactory,
            boolean relation) {
        setBeanClass(beanClass);
        setAnnotationReaderFactory(annotationReaderFactory);
        setRelation(relation);
        setValueTypeFactory(new ValueTypeFactoryImpl());
        initialize();
    }

    protected AnnotationReaderFactory getAnnotationReaderFactory() {
        return annotationReaderFactory;
    }

    public void setAnnotationReaderFactory(
            AnnotationReaderFactory annotationReaderFactory) {
        this.annotationReaderFactory = annotationReaderFactory;
    }

    public void initialize() {
        final boolean isEnhancedClass = isEnhancedClass(getBeanClass());
        if (isEnhancedClass) {
            // enhance前のクラスがBEANアノテーションで指定されたクラス
            setBeanClass(getBeanClass().getSuperclass());
        }
        originalBeanClass = getBeanClass();
        beanAnnotationReader = getAnnotationReaderFactory()
                .createBeanAnnotationReader(getBeanClass());
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(getBeanClass());
        setupTableName(beanDesc);
        setupVersionNoPropertyName(beanDesc);
        setupTimestampPropertyName(beanDesc);
        setupProperty(beanDesc, databaseMetaData, dbms);
        setupDatabaseMetaData(beanDesc, databaseMetaData, dbms);
        setupPropertiesByColumnName();
        if (!isEnhancedClass) {
            enhanceBeanClass();
        }
    }

    public void setDbms(Dbms dbms) {
        this.dbms = dbms;
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getTableName()
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getVersionNoPropertyType()
     */
    public PropertyType getVersionNoPropertyType()
            throws PropertyNotFoundRuntimeException {

        return getPropertyType(versionNoPropertyName);
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getTimestampPropertyType()
     */
    public PropertyType getTimestampPropertyType()
            throws PropertyNotFoundRuntimeException {

        return getPropertyType(timestampPropertyName);
    }

    public String getVersionNoPropertyName() {
        return versionNoPropertyName;
    }

    public String getTimestampPropertyName() {
        return timestampPropertyName;
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getPropertyTypeByColumnName(java.lang.String)
     */
    public PropertyType getPropertyTypeByColumnName(String columnName)
            throws ColumnNotFoundRuntimeException {

        PropertyType propertyType = (PropertyType) propertyTypesByColumnName
                .get(columnName);
        if (propertyType == null) {
            throw new ColumnNotFoundRuntimeException(tableName, columnName);
        }
        return propertyType;
    }

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

    /**
     * @see org.seasar.dao.BeanMetaData#hasPropertyTypeByColumnName(java.lang.String)
     */
    public boolean hasPropertyTypeByColumnName(String columnName) {
        return propertyTypesByColumnName.get(columnName) != null;
    }

    /**
     * @see org.seasar.dao.BeanMetaData#hasPropertyTypeByAliasName(java.lang.String)
     */
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

    /**
     * @see org.seasar.dao.BeanMetaData#hasVersionNoPropertyType()
     */
    public boolean hasVersionNoPropertyType() {
        return hasPropertyType(versionNoPropertyName);
    }

    /**
     * @see org.seasar.dao.BeanMetaData#hasTimestampPropertyType()
     */
    public boolean hasTimestampPropertyType() {
        return hasPropertyType(timestampPropertyName);
    }

    /**
     * @see org.seasar.dao.BeanMetaData#convertFullColumnName(java.lang.String)
     */
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

    /**
     * @see org.seasar.dao.BeanMetaData#getRelationPropertyTypeSize()
     */
    public int getRelationPropertyTypeSize() {
        return relationPropertyTypes.size();
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getRelationPropertyType(int)
     */
    public RelationPropertyType getRelationPropertyType(int index) {
        return (RelationPropertyType) relationPropertyTypes.get(index);
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getRelationPropertyType(java.lang.String)
     */
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

    protected void setupTableName(BeanDesc beanDesc) {
        String ta = beanAnnotationReader.getTableAnnotation();
        if (ta != null) {
            tableName = ta;
        } else {
            tableName = ClassUtil.getShortClassName(originalBeanClass);
        }
    }

    protected void setupVersionNoPropertyName(BeanDesc beanDesc) {
        String vna = beanAnnotationReader.getVersionNoProteryNameAnnotation();
        if (vna != null) {
            versionNoPropertyName = vna;
        }
    }

    protected void setupTimestampPropertyName(BeanDesc beanDesc) {
        String tsa = beanAnnotationReader.getTimestampPropertyName();
        if (tsa != null) {
            timestampPropertyName = tsa;
        }
    }

    protected void setupProperty(BeanDesc beanDesc,
            DatabaseMetaData dbMetaData, Dbms dbms) {

        for (int i = 0; i < beanDesc.getPropertyDescSize(); ++i) {
            PropertyDesc pd = beanDesc.getPropertyDesc(i);
            if (MODIFIED_PROPERTIES.equals(pd.getPropertyName())) {
                continue;
            }
            PropertyType pt = null;
            if (beanAnnotationReader.hasRelationNo(pd)) {
                if (!relation) {
                    RelationPropertyType rpt = createRelationPropertyType(
                            beanDesc, pd, dbMetaData, dbms);
                    addRelationPropertyType(rpt);
                }
            } else {
                pt = createPropertyType(beanDesc, pd);
                addPropertyType(pt);
            }
            if (identifierGenerator == null) {
                String idAnnotation = beanAnnotationReader.getId(pd);
                if (idAnnotation != null) {
                    identifierGenerator = IdentifierGeneratorFactory
                            .createIdentifierGenerator(pd.getPropertyName(),
                                    dbms, idAnnotation);
                    primaryKeys = new PropertyType[] { pt };
                    pt.setPrimaryKey(true);
                }
            }
        }
    }

    protected void setupDatabaseMetaData(BeanDesc beanDesc,
            DatabaseMetaData dbMetaData, Dbms dbms) {
        setupPropertyPersistentAndColumnName(beanDesc, dbMetaData);
        setupPrimaryKey(dbMetaData, dbms);
    }

    protected void setupPrimaryKey(DatabaseMetaData dbMetaData, Dbms dbms) {
        if (identifierGenerator == null) {
            List pkeyList = new ArrayList();
            Set primaryKeySet = DatabaseMetaDataUtil.getPrimaryKeySet(
                    dbMetaData, tableName);
            for (int i = 0; i < getPropertyTypeSize(); ++i) {
                PropertyType pt = getPropertyType(i);
                if (primaryKeySet.contains(pt.getColumnName())) {
                    pt.setPrimaryKey(true);
                    pkeyList.add(pt);
                } else {
                    pt.setPrimaryKey(false);
                }
            }
            primaryKeys = (PropertyType[]) pkeyList
                    .toArray(new PropertyType[pkeyList.size()]);
            identifierGenerator = IdentifierGeneratorFactory
                    .createIdentifierGenerator(null, dbms);
        }
    }

    protected void setupPropertyPersistentAndColumnName(BeanDesc beanDesc,
            DatabaseMetaData dbMetaData) {

        Set columnSet = DatabaseMetaDataUtil
                .getColumnMap(dbMetaData, tableName).keySet();
        if (columnSet.isEmpty()) {
            logger.log("WDAO0002", new Object[] { tableName });
        }
        for (Iterator i = columnSet.iterator(); i.hasNext();) {

            String columnName = (String) i.next();
            String columnName2 = StringUtil.replace(columnName, "_", "");
            for (int j = 0; j < getPropertyTypeSize(); ++j) {
                PropertyType pt = getPropertyType(j);
                if (pt.getColumnName().equalsIgnoreCase(columnName2)) {
                    final PropertyDesc pd = pt.getPropertyDesc();
                    if (beanAnnotationReader.getColumnAnnotation(pd) == null) {
                        pt.setColumnName(columnName);
                    }
                    break;
                }
            }
        }
        String[] props = beanAnnotationReader.getNoPersisteneProps();
        if (props != null) {
            for (int i = 0; i < props.length; ++i) {
                PropertyType pt = getPropertyType(props[i].trim());
                pt.setPersistent(false);
            }
        }
        for (int i = 0; i < getPropertyTypeSize(); ++i) {
            PropertyType pt = getPropertyType(i);
            if (!columnSet.contains(pt.getColumnName())) {
                pt.setPersistent(false);
            }
        }
    }

    protected void setupPropertiesByColumnName() {
        for (int i = 0; i < getPropertyTypeSize(); ++i) {
            PropertyType pt = getPropertyType(i);
            propertyTypesByColumnName.put(pt.getColumnName(), pt);
        }
    }

    protected RelationPropertyType createRelationPropertyType(
            BeanDesc beanDesc, PropertyDesc propertyDesc,
            DatabaseMetaData dbMetaData, Dbms dbms) {

        String[] myKeys = new String[0];
        String[] yourKeys = new String[0];
        int relno = beanAnnotationReader.getRelationNo(propertyDesc);
        String relkeys = beanAnnotationReader.getRelationKey(propertyDesc);
        if (relkeys != null) {
            StringTokenizer st = new StringTokenizer(relkeys, " \t\n\r\f,");
            List myKeyList = new ArrayList();
            List yourKeyList = new ArrayList();
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                int index = token.indexOf(':');
                if (index > 0) {
                    myKeyList.add(token.substring(0, index));
                    yourKeyList.add(token.substring(index + 1));
                } else {
                    myKeyList.add(token);
                    yourKeyList.add(token);
                }
            }
            myKeys = (String[]) myKeyList.toArray(new String[myKeyList.size()]);
            yourKeys = (String[]) yourKeyList.toArray(new String[yourKeyList
                    .size()]);
        }
        final Class beanClass = propertyDesc.getPropertyType();
        final BeanMetaDataImpl beanMetaData = new BeanMetaDataImpl();
        beanMetaData.setBeanClass(beanClass);
        beanMetaData.setDatabaseMetaData(dbMetaData);
        beanMetaData.setDbms(dbms);
        beanMetaData.setAnnotationReaderFactory(getAnnotationReaderFactory());
        beanMetaData.setValueTypeFactory(getValueTypeFactory());
        beanMetaData.setRelation(true);
        beanMetaData.initialize();
        final PropertyDescImpl enhancedPd = new PropertyDescImpl(propertyDesc
                .getPropertyName(), beanMetaData.getBeanClass(), propertyDesc
                .getReadMethod(), propertyDesc.getWriteMethod(), beanDesc);
        final RelationPropertyType rpt = new RelationPropertyTypeImpl(
                enhancedPd, relno, myKeys, yourKeys, beanMetaData);
        return rpt;
    }

    protected void addRelationPropertyType(RelationPropertyType rpt) {
        for (int i = relationPropertyTypes.size(); i <= rpt.getRelationNo(); ++i) {
            relationPropertyTypes.add(null);
        }
        relationPropertyTypes.set(rpt.getRelationNo(), rpt);
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getPrimaryKeySize()
     */
    public int getPrimaryKeySize() {
        return primaryKeys.length;
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getPrimaryKey(int)
     */
    public String getPrimaryKey(int index) {
        return primaryKeys[index].getColumnName();
    }

    public IdentifierGenerator getIdentifierGenerator() {
        return identifierGenerator;
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getAutoSelectList()
     */
    public synchronized String getAutoSelectList() {
        if (autoSelectList != null) {
            return autoSelectList;
        }
        setupAutoSelectList();
        return autoSelectList;
    }

    protected void setupAutoSelectList() {
        StringBuffer buf = new StringBuffer(100);
        buf.append("SELECT ");
        boolean first = true;
        for (int i = 0; i < getPropertyTypeSize(); ++i) {
            PropertyType pt = getPropertyType(i);
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
            for (int j = 0; j < bmd.getPropertyTypeSize(); ++j) {
                PropertyType pt = bmd.getPropertyType(j);
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
            throw new NoPersistentPropertyTypeRuntimeException();
        }
        autoSelectList = buf.toString();
    }

    /**
     * @see org.seasar.dao.BeanMetaData#isRelation()
     */
    public boolean isRelation() {
        return relation;
    }

    public void setRelation(boolean relation) {
        this.relation = relation;
    }

    public void setDatabaseMetaData(DatabaseMetaData databaseMetaData) {
        this.databaseMetaData = databaseMetaData;
    }

    private void enhanceBeanClass() {
        final Class targetClass = getBeanClass();
        final BeanAspectWeaver aspectWeaver = new BeanAspectWeaver(targetClass);
        final Class generateBeanClass = aspectWeaver.generateBeanClass();
        setBeanClass(generateBeanClass);
    }

    private boolean isEnhancedClass(final Class targetClass) {
        return StringUtil.contains(ClassUtil.getSimpleClassName(targetClass),
                AspectWeaver.SUFFIX_ENHANCED_CLASS);
    }

    /**
     * Entityに{@link PropertyModifiedSupport}をimplementsさせるエンハンサ。
     *
     */
    private static class BeanAspectWeaver extends AspectWeaver {

        private static final String modifiedPropertiesfieldName = "modifiedProperties_";

        public BeanAspectWeaver(final Class targetClass) {
            super(targetClass, null);
        }

        public Class generateBeanClass() {
            try {
                final CtClass enhancedCtClass = getEnhancedCtClass();
                combineField(enhancedCtClass);
                combineInterface(enhancedCtClass);
                combineProperties(enhancedCtClass);
                final Class beanClass = enhancedClassGenerator
                        .toClass(ClassLoaderUtil.getClassLoader(targetClass));
                return beanClass;
            } catch (final CannotCompileException e) {
                throw new CannotCompileRuntimeException(e);
            } catch (final NotFoundException e) {
                throw new NotFoundRuntimeException(e);
            }
        }

        /**
         * {@link PropertyModifiedSupport}をEntityにimplementsさせ、
         * {@link ModifiedProperties}を返却するメソッドを実装する。
         */
        private void combineInterface(final CtClass enhancedCtClass)
                throws NotFoundException, CannotCompileException {
            enhancedCtClass.addInterface(classPool
                    .get(PropertyModifiedSupport.class.getName()));
            final String s = "public " + ModifiedProperties.class.getName()
                    + " " + GET_MODIFIED_PROPERTIES + "() {" + "  return "
                    + modifiedPropertiesfieldName + "; }";
            final CtMethod m = CtNewMethod.make(s, enhancedCtClass);
            enhancedCtClass.addMethod(m);
        }

        /**
         * setterが呼ばれたことを記録するインスタンス変数をEntityに実装する。
         */
        private void combineField(final CtClass enhancedCtClass)
                throws CannotCompileException {
            final String s = "private " + ModifiedProperties.class.getName()
                    + " " + modifiedPropertiesfieldName + " = new "
                    + ModifiedPropertiesImpl.class.getName() + "();";
            final CtField modifiedPropertiesField = CtField.make(s,
                    enhancedCtClass);
            enhancedCtClass.addField(modifiedPropertiesField);
        }

        /**
         * setterを拡張し、
         * (1)スーパークラスの同メソッドを呼び、
         * (2)modifiedPropertiesフィールドへsetterが呼ばれたことを記録します。
         */
        private void combineProperties(final CtClass enhancedCtClass)
                throws CannotCompileException {
            final BeanDesc beanDesc = BeanDescFactory.getBeanDesc(targetClass);
            final int propertyDescSize = beanDesc.getPropertyDescSize();
            for (int i = 0; i < propertyDescSize; i++) {
                final PropertyDesc pd = beanDesc.getPropertyDesc(i);
                if (!pd.hasWriteMethod() || !pd.hasReadMethod()) {
                    continue;
                }

                final String setterName = pd.getWriteMethod().getName();
                final String propertyClassName = ClassUtil
                        .getSimpleClassName(pd.getPropertyType());
                final String propertyName = pd.getPropertyName();
                final String s = "public void " + setterName + "("
                        + propertyClassName + " " + propertyName + ")"
                        + " { super." + setterName + "(" + propertyName + "); "
                        + modifiedPropertiesfieldName + ".addPropertyName(\""
                        + propertyName + "\"); }";
                final CtMethod m = CtNewMethod.make(s, enhancedCtClass);
                enhancedCtClass.addMethod(m);
            }
        }

        private CtClass getEnhancedCtClass() {
            final String enhancedClassFieldName = "enhancedClass";
            try {
                final Field field = EnhancedClassGenerator.class
                        .getDeclaredField(enhancedClassFieldName);
                field.setAccessible(true);
                final CtClass enhancedCtClass = (CtClass) FieldUtil.get(field,
                        enhancedClassGenerator);
                return enhancedCtClass;
            } catch (final NoSuchFieldException e) {
                throw new NoSuchFieldRuntimeException(
                        EnhancedClassGenerator.class, enhancedClassFieldName, e);
            }
        }

    }

}
