if (db.accesstoken.count() == 0) {
    db.accesstoken.insert([
        {
            "_id" : "2dvkf1Ss1thCHa1e0gD3MH8tdsDct+GpVrSt1ZNwC14=",
            "application" : "adminconsole",
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
            "_id" : "BUYn7GFRN6yFi1G+Px4kYU0lsd4mBxSm81KsIrSkBGY=",
            "application" : "crafterengine",
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
            "_id" : "eB1jPJpw1UM2474xeTEePWCwpR7VlPRgAlh4HasMCio=",
            "application" : "craftersocial",
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
        "email" : "admin@craftersoftware.com",
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