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



