/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
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

if (db.accesstoken.count() == 0) {
    db.accesstoken.insert([
        {
            "_id" : "e8f5170c-877b-416f-b70f-4b09772f8e2d",
            "application" : "profile-admin",
            "master": true,
            "tenantPermissions" : [
                {
                    "allowedActions" : [ "*" ],
                    "tenant" : "*"
                }
            ],
            "expiresOn" : new Date("Jan 1, 2024")
        },
        {
            "_id" : "b4d44030-d0af-11e3-9c1a-0800200c9a66",
            "application" : "engine",
            "master": false,
            "tenantPermissions" : [
                {
                    "allowedActions" : [ "READ_TENANT", "MANAGE_PROFILES", "MANAGE_TICKETS" ],
                    "tenant" : "*"
                }
            ],
            "expiresOn" : new Date("Jan 1, 2024")
        },
        {
            "_id" : "2ba3ac10-c43e-11e3-9c1a-0800200c9a66",
            "application" : "social",
            "master": false,
            "tenantPermissions" : [
                {
                    "allowedActions" : [ "READ_TENANT", "MANAGE_PROFILES", "MANAGE_TICKETS" ],
                    "tenant" : "*"
                }
            ],
            "expiresOn" : new Date("Jan 1, 2024")
        }
    ]);
}
if (db.tenant.count() == 0) {
    db.tenant.insert({
        "name" : "default",
        "verifyNewProfiles" : false,
        "availableRoles" : [ "PROFILE_SUPERADMIN", "PROFILE_TENANT_ADMIN", "PROFILE_ADMIN", "SOCIAL_SUPERADMIN" ],
        "attributeDefinitions" : [
            {
                "name" : "firstName",
                "metadata": {
                    "label": "First Name",
                    "type": "TEXT",
                    "displayOrder": 0
                },
                "permissions" : [
                    {
                        "application" : "*",
                        "allowedActions" : [ "*" ]
                    }
                ]
            },
            {
                "name" : "lastName",
                "metadata": {
                    "label": "Last Name",
                    "type": "TEXT",
                    "displayOrder": 1
                },
                "permissions" : [
                    {
                        "application" : "*",
                        "allowedActions" : [ "*" ]
                    }
                ]
            },
            {
                "name" : "displayName",
                "metadata": {
                    "label": "Display Name",
                    "type": "TEXT",
                    "displayOrder": 2
                },
                "permissions" : [
                    {
                        "application" : "*",
                        "allowedActions" : [ "*" ]
                    }
                ]
            },
            {
                "name" : "avatarLink",
                "metadata": {
                    "label": "Avatar Link",
                    "type": "TEXT",
                    "displayOrder": 3
                },
                "permissions" : [
                    {
                        "application" : "*",
                        "allowedActions" : [ "*" ]
                    }
                ]
            },
            {
                "name": "socialContexts",
                "metadata": {
                    "label": "Social Contexts",
                    "type": "COMPLEX",
                    "displayOrder": 4
                },
                "permissions": [
                    {
                        "application": "*",
                        "allowedActions": [ "*" ]
                    }
                ]
            },
            {
                "name": "connections",
                "metadata": {
                    "label": "Connections",
                    "type": "COMPLEX",
                    "displayOrder": 5
                },
                "permissions": [
                    {
                        "application": "*",
                        "allowedActions": [ "*" ]
                    }
                ]
            }
        ]
    });
}
if (db.profile.count() == 0) {
    db.profile.insert({
        "username" : "admin",
        "password" : "4rQ8a67wAk1GRwIqHix5kYw1MORa49o83Y7zXQhBqT0=|j4vsWtPbYjO3LfSiQcnGlw==",
        "email" : "admin@example.com",
        "verified" : false,
        "enabled" : true,
        "createdOn" : new Date(),
        "lastModified" : new Date(),
        "tenant" : "default",
        "roles": [ "PROFILE_SUPERADMIN", "SOCIAL_SUPERADMIN" ],
        "attributes": {
            "socialContexts": [
                {
                    "name": "Default",
                    "id": "f5b143c2-f1c0-4a10-b56e-f485f00d3fe9",
                    "roles": [ "SOCIAL_ADMIN", "SOCIAL_MODERATOR", "SOCIAL_USER" ]
                }
            ]
        }
    });
}
