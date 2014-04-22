if (db.accesstoken.count() == 0) {
	db.accesstoken.insert({
		"_id" : "e8f5170c-877b-416f-b70f-4b09772f8e2d",
		"application" : "adminconsole",
		"tenantPermissions" : [
			{
                "allowedActions" : [ "*" ],
				"tenant" : "*"
			}
		],
		"expiresOn" : new Date("Jan 1, 2024")
	});
}
if (db.tenant.count() == 0) {
    db.tenant.insert({
        "name" : "default",
        "verifyNewProfiles" : false,
        "roles" : [ "PROFILE_ADMIN", "SOCIAL_USER", "SOCIAL_MODERATOR", "SOCIAL_AUTHOR", "SOCIAL_ADMIN" ],
        "attributeDefinitions" : [
            {
                "name" : "firstName",
                "owner" : "adminconsole",
                "permissions" : [
                    {
                        "application" : "*",
                        "allowedActions" : [ "*" ]
                    }
                ]
            },
            {
                "name" : "lastName",
                "owner" : "adminconsole",
                "permissions" : [
                    {
                        "application" : "*",
                        "allowedActions" : [ "*" ]
                    }
                ]
            },
            {
                "name" : "subscriptions",
                "owner" : "craftersocial",
                "permissions" : [
                    {
                        "application" : "craftersocial",
                        "allowedActions" : [ "*" ]
                    },
                    {
                        "application" : "crafterengine",
                        "allowedActions" : [ "read" ]
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
        "created" : new Date(),
        "modified" : new Date(),
        "tenant" : "default",
        "roles" : [ "PROFILE_ADMIN", "SOCIAL_ADMIN" ],
        "attributes" : { }
    });
}