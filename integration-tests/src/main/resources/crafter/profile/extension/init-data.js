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
        },
        {
            "_id" : "f91cdaf0-e5c6-11e3-ac10-0800200c9a66",
            "application" : "randomapp",
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
		"availableRoles" : [ "PROFILE_SUPERADMIN", "SOCIAL_USER", "SOCIAL_MODERATOR", "SOCIAL_AUTHOR", "SOCIAL_ADMIN" ],
        "attributeDefinitions" : [
            {
                "name" : "firstName",
                "permissions" : [
                    {
                        "application" : "*",
                        "allowedActions" : [ "*" ]
                    }
                ]
            },
            {
                "name" : "lastName",
                "permissions" : [
                    {
                        "application" : "*",
                        "allowedActions" : [ "*" ]
                    }
                ]
            },
            {
                "name" : "subscriptions",
                "permissions" : [
                    {
                        "application" : "profile-admin",
                        "allowedActions" : [ "*" ]
                    },
                    {
                        "application" : "social",
                        "allowedActions" : [ "*" ]
                    }
                ]
            }
        ]
	});
}
if (db.profile.count() == 0) {
    db.profile.insert([
        {
            "username" : "admin",
            "password" : "4rQ8a67wAk1GRwIqHix5kYw1MORa49o83Y7zXQhBqT0=|j4vsWtPbYjO3LfSiQcnGlw==",
            "email" : "admin@example.com",
            "verified" : false,
            "enabled" : true,
            "createdOn" : new Date(),
            "lastModified" : new Date(),
            "tenant" : "default",
            "roles" : [ "PROFILE_SUPERADMIN", "SOCIAL_ADMIN" ],
            "attributes" : { }
        },
        {
            "username" : "jdoe",
            "password" : "s1TISAqZA3jctSTy7Pz9sT/828eo3/PVfu5oyIBiyMM=|LyIyByTrB7RHJ0uePTL05w==",
            "email" : "john.doe@example.com",
            "verified" : false,
            "enabled" : false,
            "createdOn" : new Date(),
            "lastModified" : new Date(),
            "tenant" : "default",
            "roles" : [ "SOCIAL_ADMIN" ],
            "attributes" : {
                "firstName" : "John",
                "lastName" : "Doe",
                "subscriptions" : {
                    "frequency" : "instant",
                    "autoWatch" : true,
                    "targets" : [ "news" ]
                }
            }
        }
    ]);
}
