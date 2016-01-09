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

import jp.fieldnotes.hatunatu.api.DtoMetaData;
import jp.fieldnotes.hatunatu.dao.DtoMetaDataFactory;
import jp.fieldnotes.hatunatu.dao.impl.dto.EmployeeDto;
import jp.fieldnotes.hatunatu.dao.impl.dto.EmployeeDto2;
import jp.fieldnotes.hatunatu.api.PropertyType;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Rule;
import org.junit.Test;
import org.seasar.extension.unit.S2TestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class DtoMetaDataFactoryImplTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this,"dao.dicon");

    private DtoMetaDataFactory factory;

    @Test
    public void testGetDtoMetaData() {
        DtoMetaData dmd = factory.getDtoMetaData(EmployeeDto.class);
        assertNotNull(dmd);
        assertSame(dmd, factory.getDtoMetaData(EmployeeDto.class));
    }

    @Test
    public void testColumnAnnotationForDto() {
        DtoMetaData dmd = factory.getDtoMetaData(EmployeeDto2.class);
        PropertyType pt = dmd.getPropertyType("departmentName");
        assertEquals("dname", pt.getColumnName());
    }
}