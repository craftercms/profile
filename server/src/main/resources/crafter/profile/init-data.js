if (db.accesstoken.count() == 0) {
	db.accesstoken.insert([
        {
            "_id" : "e8f5170c-877b-416f-b70f-4b09772f8e2d",
            "application" : "adminconsole",
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
            "application" : "crafterengine",
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
            "application" : "craftersocial",
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
        "availableRoles" : [ "PROFILE_ADMIN", "SOCIAL_SUPERADMIN" ],
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
        "roles" : [ "PROFILE_ADMIN", "SOCIAL_SUPERADMIN" ]
    });
}