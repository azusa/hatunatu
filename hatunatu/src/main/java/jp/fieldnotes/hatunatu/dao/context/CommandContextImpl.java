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
package jp.fieldnotes.hatunatu.dao.context;

import jp.fieldnotes.hatunatu.dao.CommandContext;
import jp.fieldnotes.hatunatu.util.collection.CaseInsensitiveMap;
import jp.fieldnotes.hatunatu.util.log.Logger;
import ognl.OgnlRuntime;

import java.util.ArrayList;
import java.util.List;

public class CommandContextImpl implements CommandContext {

    private static Logger logger = Logger.getLogger(CommandContextImpl.class);

    private CaseInsensitiveMap args = new CaseInsensitiveMap();

    private CaseInsensitiveMap argTypes = new CaseInsensitiveMap();

    private StringBuilder sqlBuf = new StringBuilder(100);

    private List bindVariables = new ArrayList();

    private List bindVariableTypes = new ArrayList();

    private boolean enabled = true;

    private CommandContext parent;

    static {
        OgnlRuntime.setPropertyAccessor(CommandContext.class,
                new CommandContextPropertyAccessor());
    }

    public CommandContextImpl() {
    }

    public CommandContextImpl(CommandContext parent) {
        this.parent = parent;
        enabled = false;
    }

    @Override
    public Object getArg(String name) {
        if (args.containsKey(name)) {
            return args.get(name);
        } else if (parent != null) {
            return parent.getArg(name);
        } else {
            if (args.size() == 1) {
                return args.getAt(0);
            }
            logger.log("WDAO0001", new Object[] { name });
            return null;
        }
    }

    @Override
    public Class getArgType(String name) {
        if (argTypes.containsKey(name)) {
            return (Class) argTypes.get(name);
        } else if (parent != null) {
            return parent.getArgType(name);
        } else {
            if (argTypes.size() == 1) {
                return (Class) argTypes.getAt(0);
            }
            logger.log("WDAO0001", new Object[] { name });
            return null;
        }
    }

    @Override
    public void addArg(String name, Object arg, Class argType) {
        args.put(name, arg);
        argTypes.put(name, argType);
    }

    @Override
    public String getSql() {
        return sqlBuf.toString();
    }

    @Override
    public Object[] getBindVariables() {
        return bindVariables.toArray(new Object[bindVariables.size()]);
    }

    @Override
    public Class[] getBindVariableTypes() {
        return (Class[]) bindVariableTypes.toArray(new Class[bindVariableTypes
                .size()]);
    }

    @Override
    public CommandContext addSql(String sql) {
        sqlBuf.append(sql);
        return this;
    }

    @Override
    public CommandContext addSql(String sql, Object bindVariable,
            Class bindVariableType) {

        sqlBuf.append(sql);
        bindVariables.add(bindVariable);
        bindVariableTypes.add(bindVariableType);
        return this;
    }

    public CommandContext addSql(String sql, Object[] bindVariables,
            Class[] bindVariableTypes) {

        sqlBuf.append(sql);
        for (int i = 0; i < bindVariables.length; ++i) {
            this.bindVariables.add(bindVariables[i]);
            this.bindVariableTypes.add(bindVariableTypes[i]);
        }
        return this;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}