if (db.accesstoken.count() == 0) {
	db.accesstoken.save({
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
    db.accesstoken.save({
        "_id" : "9161fb80-c329-11e3-9c1a-0800200c9a66",
        "application" : "adminconsole",
        "tenantPermissions" : [
            {
                "allowedActions" : [ "*" ],
                "tenant" : "*"
            }
        ],
        "expiresOn" : new Date("Jan 1, 2013")
    });
    db.accesstoken.save({
        "_id" : "f9929b40-c358-11e3-9c1a-0800200c9a66",
        "application" : "adminconsole",
        "tenantPermissions" : [
            {
                "allowedActions" : [ ],
                "tenant" : "*"
            }
        ],
        "expiresOn" : new Date("Jan 1, 2024")
    });
    db.accesstoken.save({
        "_id" : "2ba3ac10-c43e-11e3-9c1a-0800200c9a66",
        "application" : "craftersocial",
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
	db.tenant.save({
		"name" : "default",
		"verifyNewProfiles" : false,
		"roles" : [ "PROFILE_ADMIN", "SOCIAL_USER", "SOCIAL_MODERATOR", "SOCIAL_AUTHOR", "SOCIAL_ADMIN" ],
        "attributeDefinitions" : [
            {
                "name" : "firstName",
                "label" : "First Name",
                "order" : 0,
                "type" : "java.lang.String",
                "constraint" : "",
                "required" : false,
                "owner" : "adminconsole",
                "permissions" : [
                    {
                        "application" : "*",
                        "actions" : "*"
                    }
                ]
            },
            {
                "name" : "lastName",
                "label" : "Last Name",
                "order" : 0,
                "type" : "java.lang.String",
                "constraint" : "",
                "required" : false,
                "owner" : "adminconsole",
                "permissions" : [
                    {
                        "application" : "*",
                        "actions" : "*"
                    }
                ]
            }
        ]
	});
}
if (db.profile.count() == 0) {
    db.profile.save({
        "username" : "admin",
        "password" : "4rQ8a67wAk1GRwIqHix5kYw1MORa49o83Y7zXQhBqT0=|j4vsWtPbYjO3LfSiQcnGlw==",
        "email" : "admin@craftersoftware.com",
        "verified" : false,
        "enabled" : true,
        "created" : new Date(),
        "modified" : new Date(),
        "tenant" : "default",
        "roles" : [ "PROFILE_ADMIN", "SOCIAL_ADMIN" ],
        "attributes" : [ ]
    });
}