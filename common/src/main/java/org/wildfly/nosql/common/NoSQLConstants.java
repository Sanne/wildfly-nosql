/*
 * Copyright 2017 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.nosql.common;

/**
 * NoSQLConstants common shared NoSQL constants
 *
 * @author Scott Marlow
 */
public class NoSQLConstants {

    // Cassandra constants
    public static final String CASSANDRACDIEXTENSIONMODULE = "org.wildfly.extension.nosql.cassandra";
    public static final String CASSANDRACLUSTERCLASS = "com.datastax.driver.core.Cluster";
    public static final String CASSANDRACLUSTERBUILDERCLASS = "com.datastax.driver.core.Cluster$Builder";
    public static final String CASSANDRASESSIONCLASS = "com.datastax.driver.core.Session";
    public static final String CASSANDRACDIEXTENSIONCLASS = "org.wildfly.extension.nosql.cdi.CassandraExtension";

    // Neo4j constants
    public static final String NEO4JCDIEXTENSIONMODULE = "org.wildfly.extension.nosql.neo4j";
    public static final String NEO4JDRIVERCLASS = "org.neo4j.driver.v1.Driver";
    public static final String NEO4JGRAPHDATABASECLASS = "org.neo4j.driver.v1.GraphDatabase";
    public static final String NEO4JCDIEXTENSIONCLASS = "org.wildfly.extension.nosql.cdi.Neo4jExtension";
    public static final String NEO4JAUTHTOKENSCLASS = "org.neo4j.driver.v1.AuthTokens";
    public static final String NEO4JAUTHTOKENCLASS = "org.neo4j.driver.v1.AuthToken";

    // OrientDB constants
    public static final String ORIENTDBCDIEXTENSIONMODULE = "org.wildfly.extension.nosql.orientdb";
    public static final String ORIENTDBPARTIONEDDBPOOLCLASS = "com.orientechnologies.orient.core.db.OPartitionedDatabasePool";
    public static final String ORIENTDBDATABASERECORDTHREADLOCALCLASS = "com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal";
    public static final String ORIENTCDIEXTENSIONCLASS = "org.wildfly.extension.nosql.cdi.OrientExtension";

    // MongoDB constants
    public static String MONGOCLIENTCLASS = "com.mongodb.MongoClient";
    public static final String MONGOCLIENTOPTIONSCLASS = "com.mongodb.MongoClientOptions";
    public static final String MONGODATABASECLASS = "com.mongodb.client.MongoDatabase";
    public static final String MONGOBUILDERCLASS = "com.mongodb.MongoClientOptions$Builder";
    public static final String MONGOWRITECONCERNCLASS = "com.mongodb.WriteConcern";
    public static final String MONGOREADCONCERNCLASS = "com.mongodb.ReadConcern";
    public static final String MONGOREADCONCERNLEVELCLASS = "com.mongodb.ReadConcernLevel";
    public static final String MONGOSERVERADDRESSCLASS = "com.mongodb.ServerAddress";
    public static final String MONGOCREDENTIALCLASS = "com.mongodb.MongoCredential";
    public static final String MONGOCDIEXTENSIONMODULE = "org.wildfly.extension.nosql.mongodb";
    public static final String MONGOCDIEXTENSIONCLASS = "org.wildfly.extension.nosql.cdi.MongoExtension";
}
