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
import jp.fieldnotes.hatunatu.dao.Dbms;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.IdType;
import jp.fieldnotes.hatunatu.dao.exception.EmptyRuntimeException;
import jp.fieldnotes.hatunatu.dao.impl.Identifier;
import jp.fieldnotes.hatunatu.util.beans.util.BeanUtil;
import jp.fieldnotes.hatunatu.util.lang.ClassUtil;
import jp.fieldnotes.hatunatu.util.lang.ConstructorUtil;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory of {@link IdentifierGenerator}.
 */
public class IdentifierGeneratorFactory {

    private static Map<IdType, Class<? extends IdentifierGenerator>> generatorClasses = new HashMap<>();

    static {
        addIdentifierGeneratorClass(IdType.ASSIGNED,
                AssignedIdentifierGenerator.class);
        addIdentifierGeneratorClass(IdType.IDENTITY,
                IdentityIdentifierGenerator.class);
        addIdentifierGeneratorClass(IdType.SEQUENCE,
                SequenceIdentifierGenerator.class);
    }

    private IdentifierGeneratorFactory() {
    }

    public static void addIdentifierGeneratorClass(IdType name, Class clazz) {
        generatorClasses.put(name, clazz);
    }

    public static IdentifierGenerator createIdentifierGenerator(
            PropertyType propertyType, Dbms dbms, Identifier annotation) {
        if (propertyType == null) {
            throw new EmptyRuntimeException("propertyType");
        }
        if (dbms == null) {
            throw new EmptyRuntimeException("dbms");
        }
        if (annotation == null) {
            return new AssignedIdentifierGenerator(propertyType, dbms);
        }
        Class clazz = getGeneratorClass(annotation.getIdType());
        IdentifierGenerator generator = createIdentifierGenerator(clazz,
                propertyType, dbms);
        BeanUtil.copyBeanToBean(annotation, generator);
        return generator;
    }

    protected static Class getGeneratorClass(IdType name) {
        return generatorClasses.get(name);
    }

    protected static IdentifierGenerator createIdentifierGenerator(Class clazz,
            PropertyType propertyType, Dbms dbms) {
        Constructor constructor = ClassUtil.getConstructor(clazz, new Class[] {
                PropertyType.class, Dbms.class });
        return (IdentifierGenerator) ConstructorUtil.newInstance(constructor,
                new Object[] { propertyType, dbms });
    }
}
