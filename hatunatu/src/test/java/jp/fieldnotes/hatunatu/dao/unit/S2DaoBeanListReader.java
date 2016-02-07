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
package jp.fieldnotes.hatunatu.dao.unit;

import jp.fieldnotes.hatunatu.api.BeanMetaData;
import jp.fieldnotes.hatunatu.dao.BeanMetaDataFactory;

import java.util.List;

public class S2DaoBeanListReader extends S2DaoBeanReader {

    public S2DaoBeanListReader(List list,
                               BeanMetaDataFactory beanMetaDataFactory) throws Exception {
        final BeanMetaData beanMetaData = beanMetaDataFactory
                .createBeanMetaData(list.get(0).getClass());
        initialize(list, beanMetaData);
    }

    public S2DaoBeanListReader(List list, BeanMetaData beanMetaData) {
        initialize(list, beanMetaData);
    }

    private void initialize(List list, BeanMetaData beanMetaData) {
        setupColumns(beanMetaData);
        for (int i = 0; i < list.size(); ++i) {
            setupRow(beanMetaData, list.get(i));
        }
    }

}