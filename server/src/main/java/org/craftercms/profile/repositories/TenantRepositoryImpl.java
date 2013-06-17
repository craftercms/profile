/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.profile.repositories;

import org.craftercms.profile.constants.ProfileConstants;

import org.craftercms.profile.domain.Attribute;
import org.craftercms.profile.domain.Tenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TenantRepositoryImpl implements TenantRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;

    @Override
    public List<Tenant> getTenantRange(String sortBy, String sortOrder, int start, int end) {
        Query query = new Query();

        if (sortBy != null) {
        	String sortingBy = sortBy;
            if ( ! Arrays.asList(ProfileConstants.TENANT_ORDER_BY_FIELDS).contains(sortBy) ) {
            	sortingBy = ProfileConstants.TENANT_NAME;
            }

            if (sortOrder != null) {
                query.sort().on(sortingBy, sortOrder.equalsIgnoreCase(ProfileConstants.SORT_ORDER_DESC) ? Order.DESCENDING : Order.ASCENDING);
            } else {
                query.sort().on(sortingBy, Order.ASCENDING);
            }
        }

        query.skip(start);
        query.limit(end > start ? (end - start + 1) : 0);

        return mongoTemplate.find(query, Tenant.class);
    }

    @Override
	public Tenant getTenantByName(String tenantName) {
		Query query = new Query();
		query.addCriteria(Criteria.where(ProfileConstants.TENANT_NAME).is(tenantName));
		return mongoTemplate.findOne(query, Tenant.class);
	}

    @Override
    public void setAttribute(String tenantName, Attribute attribute) {
        Query query = new Query();
        query.addCriteria(Criteria.where(ProfileConstants.TENANT_NAME).is(tenantName));
        Update update = new Update();
        Tenant t = getTenantByName(tenantName);
        if(t!= null){
            Iterator<Attribute> i = t.getSchema().getAttributes().iterator();
            boolean added = false;
            int pos = 0;
            while (i.hasNext()){
                if(i.next().getName().equals(attribute.getName())){
                    i.remove();
                    t.getSchema().getAttributes().add(pos, attribute);
                    added = true;
                    break;
                }
                pos++;
            }
            if(!added){
                t.getSchema().getAttributes().add(attribute);
            }
        }

        update = update.set(ProfileConstants.SCHEMA, t.getSchema());
        mongoTemplate.updateFirst(query, update, Tenant.class);
    }

    @Override
    public void deleteAttribute(String tenantName, String attributeName) {
        Query query = new Query();
        query.addCriteria(Criteria.where(ProfileConstants.TENANT_NAME).is(tenantName));
        Update update = new Update();
        Tenant t = getTenantByName(tenantName);
        if(t!= null){
            Iterator<Attribute> i = t.getSchema().getAttributes().iterator();
            while (i.hasNext()){
                if(i.next().getName().equals(attributeName)){
                    i.remove();
                    break;
                }
            }
        }
        update = update.set(ProfileConstants.SCHEMA, t.getSchema());
        mongoTemplate.updateFirst(query, update, Tenant.class);
    }
}
