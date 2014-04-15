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
		"expiresOn" : ISODate("2024-01-01T06:00:00Z")
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
        "expiresOn" : ISODate("2013-01-01T06:00:00Z")
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
        "expiresOn" : ISODate("2024-01-01T06:00:00Z")
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
        "expiresOn" : ISODate("2024-01-01T06:00:00Z")
    });
}
if (db.tenant.count() == 0) {
	db.tenant.save({
		"name" : "default",
		"verifyNewProfiles" : false,
		"roles" : [ "ADMIN" ],
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