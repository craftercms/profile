// Update roles
var defaultTenant = db.tenant.findOne({name: "default"});
if (defaultTenant) {
	var availableRoles = defaultTenant.availableRoles;
	if (availableRoles.length == 2 &&
		availableRoles.indexOf("PROFILE_ADMIN") >= 0 &&
		availableRoles.indexOf("SOCIAL_SUPERADMIN") >= 0) {
		db.tenant.update(
			{name: "default"}, 
			{$set: {
				availableRoles: ["PROFILE_SUPERADMIN", "PROFILE_TENANT_ADMIN", "PROFILE_ADMIN", "SOCIAL_SUPERADMIN"]
			}}
		);
		db.profile.update({roles: "PROFILE_ADMIN"}, {$addToSet: {roles: "PROFILE_SUPERADMIN"}}, {multi: true});
		db.profile.update({roles: "PROFILE_ADMIN"}, {$pull: {roles: "PROFILE_ADMIN"}}, {multi: true});
	}
}

// Update access tokens
if (db.accesstoken.find({_id: "e8f5170c-877b-416f-b70f-4b09772f8e2d"}).count() == 1) {
	db.accesstoken.remove({_id: "e8f5170c-877b-416f-b70f-4b09772f8e2d"});
	db.accesstoken.insert({
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
	});	
}
if (db.accesstoken.find({_id: "b4d44030-d0af-11e3-9c1a-0800200c9a66"}).count() == 1) {
	db.accesstoken.remove({_id: "b4d44030-d0af-11e3-9c1a-0800200c9a66"});
	db.accesstoken.insert({
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
	});
}
if (db.accesstoken.find({_id: "2ba3ac10-c43e-11e3-9c1a-0800200c9a66"}).count() == 1) {
	db.accesstoken.remove({_id: "2ba3ac10-c43e-11e3-9c1a-0800200c9a66"});
	db.accesstoken.insert({
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
	});
}



