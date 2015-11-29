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

import org.seasar.dao.annotation.tiger.*;
import org.seasar.dao.impl.bean.Department;
import org.seasar.dao.impl.bean.Employee;
import org.seasar.dao.impl.bean.Employee4;
import org.seasar.dao.impl.bean.IdentityTable;

import java.sql.Timestamp;

/**
 * @author higa
 * 
 */
public class DefaultBeanMetaDataImplTest extends BeanMetaDataImplTest {

    public void setUp() {
        include("j2ee.dicon");
    }

    protected Class getBeanClass(String className) {
        if (className.equals("MyBean")) {
            return MyBean.class;
        } else if (className.equals("Employee")) {
            return Employee.class;
        } else if (className.equals("Department")) {
            return Department.class;
        } else if (className.equals("Employee4")) {
            return Employee4.class;
        } else if (className.equals("Ddd")) {
            return Ddd.class;
        } else if (className.equals("Eee")) {
            return Eee.class;
        } else if (className.equals("Fff")) {
            return Fff.class;
        } else if (className.equals("Ggg")) {
            return Ggg.class;
        } else if (className.equals("IdentityTable")) {
            return IdentityTable.class;
        }
        return null;
    }

    @Bean(table = "MyBean")
    public static class MyBean {
        private Integer aaa;

        private String bbb;

        private Ccc ccc;

        private Integer ddd;

        @Id(IdType.ASSIGNED)
        public Integer getAaa() {
            return aaa;
        }

        public void setAaa(Integer aaa) {
            this.aaa = aaa;
        }

        @Column(value = "myBbb")
        public String getBbb() {
            return bbb;
        }

        public void setBbb(String bbb) {
            this.bbb = bbb;
        }

        @Relation(relationNo = 0, relationKey = "ddd:id")
        public Ccc getCcc() {
            return ccc;
        }

        public void setCcc(Ccc ccc) {
            this.ccc = ccc;
        }

        public Integer getDdd() {
            return ddd;
        }

        public void setDdd(Integer ddd) {
            this.ddd = ddd;
        }
    }

    public static class Ccc {
        private Integer id;

        @Id(value = IdType.ASSIGNED)
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    @Bean(noPersistentProperty = {""})
    public static class Ddd extends Ccc {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Bean(noPersistentProperty = {"name"})
    public static class Eee extends Ccc {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Bean(versionNoProperty = "version", timeStampProperty = "updated")
    public static class Fff {

        private int version;

        private Integer id;

        private Timestamp updated;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public Timestamp getUpdated() {
            return updated;
        }

        public void setUpdated(Timestamp updated) {
            this.updated = updated;
        }
    }

    public static class Ggg {

        private Integer id;

        private Integer id2;

        @Id(value = IdType.ASSIGNED)
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @Id(value = IdType.SEQUENCE, sequenceName = "id2")
        public Integer getId2() {
            return id2;
        }

        public void setId2(Integer id2) {
            this.id2 = id2;
        }

    }
}