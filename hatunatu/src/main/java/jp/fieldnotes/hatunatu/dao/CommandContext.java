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
package jp.fieldnotes.hatunatu.dao;

/**
 * @author higa
 * 
 */
public interface CommandContext {

    public Object getArg(String name);

    public Class getArgType(String name);

    public void addArg(String name, Object arg, Class argType);

    public String getSql();

    public Object[] getBindVariables();

    public Class[] getBindVariableTypes();

    public CommandContext addSql(String sql);

    public CommandContext addSql(String sql, Object bindVariable,
            Class bindVariableType);

    public CommandContext addSql(String sql, Object[] bindVariables,
            Class[] bindVariableTypes);

    public boolean isEnabled();

    public void setEnabled(boolean enabled);
}
