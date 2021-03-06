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
package jp.fieldnotes.hatunatu.api;

import java.util.Collection;

/**
 * @author higa
 * @author manhole
 */
public interface DtoMetaData {

    public String COLUMN_SUFFIX = "_COLUMN";

    public Class getBeanClass();

    Collection<PropertyType> getPropertyTypes();

    public PropertyType getPropertyType(int index);

    public PropertyType getPropertyType(String propertyName);

    public boolean hasPropertyType(String propertyName);
}