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
        },
        {
            "_id" : "J7IrHlAJ2BJa2ncPntgIdBQ1w7mEMPiw9MbrV72WNKA=",
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
                        "application" : "adminconsole",
                        "allowedActions" : [ "*" ]
                    },
                    {
                        "application" : "craftersocial",
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
            "email" : "admin@craftersoftware.com",
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
            "email" : "john.doe@craftersoftware.com",
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