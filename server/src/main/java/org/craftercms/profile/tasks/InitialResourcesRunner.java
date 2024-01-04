/*
 * Copyright (C) 2007-2024 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.profile.tasks;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.*;

/**
 * Run initial JSON data insert task at startup if flagged
 * Run initial collection fields update at startup if flagged
 */
public class InitialResourcesRunner implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(InitialResourcesRunner.class);
    private static final String MONGO_COLLECTION_ACCESSTOKEN = "accesstoken";
    private static final String MONGO_COLLECTION_TENANT = "tenant";
    private static final String MONGO_COLLECTION_PROFILE = "profile";
    private static final String MONGO_FIELD_EXPIRES_ON = "expiresOn";
    private static final String MONGO_FIELD_CREATED_ON = "createdOn";
    private static final String MONGO_FIELD_LAST_MODIFIED = "lastModified";

    private MongoClient mongo;
    private String dbName;
    private List<Resource> resourcesPaths;
    private boolean runOnInit;

    public InitialResourcesRunner() {
        this.runOnInit = true;
    }

    @Required
    public void setMongo(MongoClient mongo) {
        this.mongo = mongo;
    }

    @Required
    public void setResourcesPaths(List<Resource> resourcesPaths) {
        this.resourcesPaths = resourcesPaths;
    }

    @Required
    public void setRunOnInit(boolean runOnInit) {
        this.runOnInit = runOnInit;
    }

    @Required
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.debug("Running initial data insert and update?", runOnInit);
        if (runOnInit) {
            logger.debug("Run initial data insert and update using Java Mongo Client");
            runInitialDataWithMongoClient();
        }
    }

    /**
     * Run initial JSON data with Java Mongo Client
     * Run collection update with Java Mongo Client
     * @throws IOException
     */
    private void runInitialDataWithMongoClient() throws IOException {
        for (Resource resource : resourcesPaths) {
            runInitialJsonResource(resource);
        }

        runInitialUpgrade();
    }

    /**
     * Run initial JSON resource insert
     * @param jsonResource JSON Resource object
     * @throws IOException
     */
    private void runInitialJsonResource(Resource jsonResource) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        JsonNode jsonNode = objectMapper.readTree(jsonResource.getFile());
        for (Iterator<String> it = jsonNode.fieldNames(); it.hasNext(); ) {
            String key = it.next();
            MongoDatabase database = getDB();
            MongoCollection<Document> collection = database.getCollection(key);

            if (collection.count() == 0) {
                ArrayNode arrayNode = (ArrayNode) jsonNode.get(key);

                List<Document> documents = new ArrayList<>();
                for (JsonNode node : arrayNode) {
                    Document doc = Document.parse(node.toString());
                    if (key.equalsIgnoreCase(MONGO_COLLECTION_ACCESSTOKEN) || key.equalsIgnoreCase(MONGO_COLLECTION_TENANT)) {
                        doc.append(MONGO_FIELD_EXPIRES_ON, getDefaultExpiresOn());
                    } else if (key.equalsIgnoreCase(MONGO_COLLECTION_PROFILE)) {
                        Date current = new Date();
                        doc.append(MONGO_FIELD_CREATED_ON, current);
                        doc.append(MONGO_FIELD_LAST_MODIFIED, current);
                    }
                    documents.add(doc);
                }

                collection.insertMany(documents);
            }
        }
    }

    /**
     * Run initial upgrade collections records
     */
    private void runInitialUpgrade() {
        MongoDatabase database = getDB();
        updateDefaultTenant(database);
        updateDefaultAccessToken(database);
    }

    /**
     * Update default tenant collection fields
     * @param database {@link MongoDatabase} object
     */
    private void updateDefaultTenant(MongoDatabase database) {
        MongoCollection<Document> tenantCollection = database.getCollection(MONGO_COLLECTION_TENANT);
        MongoCollection<Document> profileCollection = database.getCollection(MONGO_COLLECTION_PROFILE);

        Document defaultTenant = tenantCollection.find(Filters.eq("name", "default")).first();
        if (defaultTenant != null) {
            List<String> availableRoles = (List<String>) defaultTenant.get("availableRoles");
            if (availableRoles.size() == 2 &&
                    availableRoles.contains("PROFILE_ADMIN") &&
                    availableRoles.contains("SOCIAL_SUPERADMIN")) {
                tenantCollection.updateOne(
                        Filters.eq("name", "default"),
                        Updates.set("availableRoles", Arrays.asList("PROFILE_SUPERADMIN", "PROFILE_TENANT_ADMIN", "PROFILE_ADMIN", "SOCIAL_SUPERADMIN"))
                );
                profileCollection.updateMany(
                        Filters.eq("roles", "PROFILE_ADMIN"),
                        Updates.addToSet("roles", "PROFILE_SUPERADMIN")
                );
                profileCollection.updateMany(
                        Filters.eq("roles", "PROFILE_ADMIN"),
                        Updates.pull("roles", "PROFILE_ADMIN")
                );
            }
        }
    }

    /**
     * Update default access token fields
     * @param database {@link MongoDatabase} object
     */
    private void updateDefaultAccessToken(MongoDatabase database) {
        MongoCollection<Document> collection = database.getCollection(MONGO_COLLECTION_ACCESSTOKEN);

        Document doc1 = collection.find(Filters.eq("_id", "e8f5170c-877b-416f-b70f-4b09772f8e2d")).first();
        if (doc1 != null) {
            collection.updateOne(Filters.eq("_id", "e8f5170c-877b-416f-b70f-4b09772f8e2d"),
                    Updates.combine(Updates.set("master", true), Updates.set("application", "profile-admin")));
        }

        Document doc2 = collection.find(Filters.eq("_id", "b4d44030-d0af-11e3-9c1a-0800200c9a66")).first();
        if (doc2 != null) {
            collection.updateOne(Filters.eq("_id", "b4d44030-d0af-11e3-9c1a-0800200c9a66"),
                    Updates.set("application", "engine"));
        }

        Document doc3 = collection.find(Filters.eq("_id", "2ba3ac10-c43e-11e3-9c1a-0800200c9a66")).first();
        if (doc3 != null) {
            collection.updateOne(Filters.eq("_id", "2ba3ac10-c43e-11e3-9c1a-0800200c9a66"),
                    Updates.set("application", "social"));
        }
    }

    /**
     * Get Mongo Database instance
     * @return MongoDatabase instance
     */
    private MongoDatabase getDB() {
        MongoDatabase db = mongo.getDatabase(dbName);
        logger.debug("Getting DB {}",dbName);
        return db;
    }

    /**
     * Get the default expires date from current date plus 10 years
     * @return expires date
     */
    private Date getDefaultExpiresOn() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 10);
        return calendar.getTime();
    }
}