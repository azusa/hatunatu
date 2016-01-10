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
package jp.fieldnotes.hatunatu.dao.id;

import jp.fieldnotes.hatunatu.api.IdentifierGenerator;
import jp.fieldnotes.hatunatu.api.PropertyType;
import jp.fieldnotes.hatunatu.api.beans.BeanDesc;
import jp.fieldnotes.hatunatu.api.beans.PropertyDesc;
import jp.fieldnotes.hatunatu.dao.Dbms;
import jp.fieldnotes.hatunatu.dao.exception.EmptyRuntimeException;
import jp.fieldnotes.hatunatu.util.beans.factory.BeanDescFactory;
import jp.fieldnotes.hatunatu.util.lang.ClassUtil;
import jp.fieldnotes.hatunatu.util.lang.ConstructorUtil;
import jp.fieldnotes.hatunatu.util.lang.StringUtil;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class IdentifierGeneratorFactory {

    private static Map generatorClasses = new HashMap();

    static {
        addIdentifierGeneratorClass("assigned",
                AssignedIdentifierGenerator.class);
        addIdentifierGeneratorClass("identity",
                IdentityIdentifierGenerator.class);
        addIdentifierGeneratorClass("sequence",
                SequenceIdentifierGenerator.class);
    }

    private IdentifierGeneratorFactory() {
    }

    public static void addIdentifierGeneratorClass(String name, Class clazz) {
        generatorClasses.put(name, clazz);
    }

    public static IdentifierGenerator createIdentifierGenerator(
            PropertyType propertyType, Dbms dbms) {

        return createIdentifierGenerator(propertyType, dbms, null);
    }

    public static IdentifierGenerator createIdentifierGenerator(
            PropertyType propertyType, Dbms dbms, String annotation) {
        if (propertyType == null) {
            throw new EmptyRuntimeException("propertyType");
        }
        if (dbms == null) {
            throw new EmptyRuntimeException("dbms");
        }
        if (annotation == null) {
            return new AssignedIdentifierGenerator(propertyType, dbms);
        }
        String[] array = StringUtil.split(annotation, "=, ");
        Class clazz = getGeneratorClass(array[0]);
        IdentifierGenerator generator = createIdentifierGenerator(clazz,
                propertyType, dbms);
        for (int i = 1; i < array.length; i += 2) {
            setProperty(generator, array[i].trim(), array[i + 1].trim());
        }
        return generator;
    }

    protected static Class getGeneratorClass(String name) {
        Class clazz = (Class) generatorClasses.get(name);
        if (clazz != null) {
            return clazz;
        }
        return ClassUtil.forName(name);
    }

    protected static IdentifierGenerator createIdentifierGenerator(Class clazz,
            PropertyType propertyType, Dbms dbms) {
        Constructor constructor = ClassUtil.getConstructor(clazz, new Class[] {
                PropertyType.class, Dbms.class });
        return (IdentifierGenerator) ConstructorUtil.newInstance(constructor,
                new Object[] { propertyType, dbms });
    }

    protected static void setProperty(IdentifierGenerator generator,
            String propertyName, String value) {
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(generator.getClass());
        PropertyDesc pd = beanDesc.getPropertyDesc(propertyName);
        pd.setValue(generator, value);
    }
}
