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
public interface SqlTokenizer {

    public int SQL = 1;

    public int COMMENT = 2;

    public int ELSE = 3;

    public int BIND_VARIABLE = 4;

    public int EOF = 99;

    public String getToken();

    public String getBefore();

    public String getAfter();

    public int getPosition();

    public int getTokenType();

    public int getNextTokenType();

    public int next();

    public String skipToken();

    public String skipWhitespace();
}
