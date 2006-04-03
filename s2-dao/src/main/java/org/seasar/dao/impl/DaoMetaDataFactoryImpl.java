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

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.seasar.dao.AnnotationReaderFactory;
import org.seasar.dao.DaoMetaData;
import org.seasar.dao.DaoMetaDataFactory;
import org.seasar.extension.jdbc.ResultSetFactory;
import org.seasar.extension.jdbc.StatementFactory;

/**
 * @author higa
 *  
 */
public class DaoMetaDataFactoryImpl implements DaoMetaDataFactory {

	protected Map daoMetaDataCache_ = new HashMap();

	protected DataSource dataSource_;
	
	protected StatementFactory statementFactory_;

	protected ResultSetFactory resultSetFactory_;
	
	protected AnnotationReaderFactory readerFactory_;

    private String encoding;

    private String[] daoSuffixes;

    private String[] insertPrefixes;

    private String[] deletePrefixes;

    private String[] updatePrefixes;

	public DaoMetaDataFactoryImpl(DataSource dataSource,
			StatementFactory statementFactory,
			ResultSetFactory resultSetFactory,
			AnnotationReaderFactory readerFactory) {
		
		dataSource_ = dataSource;
		statementFactory_ = statementFactory;
		resultSetFactory_ = resultSetFactory;
		readerFactory_ = readerFactory;
	}
    public void setSqlFileEncoding(String encoding){
        this.encoding = encoding;
    }
    public void setDaoSuffixes(String[] suffixes){
        this.daoSuffixes = suffixes;
    }
    public void setInsertPrefixes(String[] prefixes){
        this.insertPrefixes = prefixes;
    }
    public void setDeletePrefixes(String[] prefixes){
        this.deletePrefixes = prefixes;
    }
    public void setUpdatePrefixes(String[] prefixes){
        this.updatePrefixes = prefixes;
    }
    public synchronized DaoMetaData getDaoMetaData(Class daoClass) {
		String key = daoClass.getName();
		DaoMetaData dmd = (DaoMetaDataImpl) daoMetaDataCache_.get(key);
		if (dmd != null) {
			return dmd;
		}
		DaoMetaDataImpl dmdi = new DaoMetaDataImpl(daoClass, dataSource_,
				statementFactory_, resultSetFactory_,readerFactory_
                ,encoding,daoSuffixes,insertPrefixes,updatePrefixes,deletePrefixes);
		daoMetaDataCache_.put(key, dmdi);
		return dmdi;
	}

}
