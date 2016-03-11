// Update roles for the default tenant
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

// Set the admin console token as the master
if (db.accesstoken.findOne({_id : "e8f5170c-877b-416f-b70f-4b09772f8e2d"})) {
	db.accesstoken.update({_id: "e8f5170c-877b-416f-b70f-4b09772f8e2d"}, {$set: {master: true}});
}