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
package org.seasar.dao.impl;

import org.seasar.dao.SqlCommand;
import org.seasar.dao.handler.ProcedureHandler;

public class StaticStoredProcedureCommand implements SqlCommand {

    private ProcedureHandler handler;

    public StaticStoredProcedureCommand(ProcedureHandler handler) {
        this.handler = handler;
    }

    public Object execute(Object[] args) {
        return handler.execute(args);
    }

}
