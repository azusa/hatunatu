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
package jp.fieldnotes.hatunatu.dao.command;

import jp.fieldnotes.hatunatu.api.BeanMetaData;
import jp.fieldnotes.hatunatu.api.PropertyType;
import jp.fieldnotes.hatunatu.dao.StatementFactory;
import jp.fieldnotes.hatunatu.dao.handler.UpdateAutoHandler;
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;
import jp.fieldnotes.hatunatu.util.log.Logger;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UpdateModifiedOnlyCommand extends UpdateAutoDynamicCommand {

    private static Logger logger = Logger
            .getLogger(UpdateModifiedOnlyCommand.class);

    private static final Integer NO_UPDATE = new Integer(0);

    public UpdateModifiedOnlyCommand(final DataSource dataSource,
            final StatementFactory statementFactory) {
        super(dataSource, statementFactory);
    }

    @Override
    protected Object doExecute(final Object[] args) throws Exception {
        final Object bean = args[0];
        final BeanMetaData bmd = getBeanMetaData();
        final PropertyType[] propertyTypes = createUpdatePropertyTypes(bmd,
                bean, getPropertyNames());
        if (propertyTypes.length == 0) {
            if (logger.isDebugEnabled()) {
                final String s = createNoUpdateLogMessage(bean, bmd);
                logger.debug(s);
            }
            return NO_UPDATE;
        }

        final UpdateAutoHandler handler = new UpdateAutoHandler(
                getDataSource(), getStatementFactory(), bmd, propertyTypes,
                isCheckSingleRowUpdate());
        QueryObject queryObject = new QueryObject();
        queryObject.setSql(createUpdateSql(bmd, propertyTypes));
        queryObject.setMethodArguments(args);
        final int i = handler.execute(queryObject);
        return new Integer(i);
    }

    protected String createNoUpdateLogMessage(final Object bean,
            final BeanMetaData bmd) {
        final StringBuilder sb = new StringBuilder();
        sb.append("skip UPDATE: table=");
        sb.append(bmd.getTableName());
        final int size = bmd.getPrimaryKeySize();
        for (int i = 0; i < size; i++) {
            if (i == 0) {
                sb.append(", key{");
            } else {
                sb.append(", ");
            }
            final String keyName = bmd.getPrimaryKey(i);
            sb.append(keyName);
            sb.append("=");
            sb.append(bmd.getPropertyTypeByColumnName(keyName)
                    .getPropertyDesc().getValue(bean).toString());
            if (i == size - 1) {
                sb.append("}");
            }
        }
        final String s = new String(sb);
        return s;
    }

    @Override
    protected PropertyType[] createUpdatePropertyTypes(final BeanMetaData bmd,
            final Object bean, final String[] propertyNames) {

        final Set modifiedPropertyNames = getBeanMetaData()
                .getModifiedPropertyNames(bean);
        final List types = new ArrayList();
        final String timestampPropertyName = bmd.getTimestampPropertyName();
        final String versionNoPropertyName = bmd.getVersionNoPropertyName();
        for (int i = 0; i < propertyNames.length; ++i) {
            final PropertyType pt = bmd.getPropertyType(propertyNames[i]);
            if (pt.isPrimaryKey() == false) {
                final String propertyName = pt.getPropertyName();
                if (propertyName.equalsIgnoreCase(timestampPropertyName)
                        || propertyName.equalsIgnoreCase(versionNoPropertyName)
                        || modifiedPropertyNames.contains(propertyName)) {
                    types.add(pt);
                }
            }
        }
        final PropertyType[] propertyTypes = (PropertyType[]) types
                .toArray(new PropertyType[types.size()]);
        return propertyTypes;
    }

}
