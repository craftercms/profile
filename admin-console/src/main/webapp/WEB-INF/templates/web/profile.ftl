<h1 class="page-header">Update Profile</h1>

<form role="form">
    <div class="form-group">
        <label for="username">Username</label>
        <input name="username" type="text" class="form-control" disabled="disabled" ng-model="profile.username"/>
    </div>

    <div class="form-group">
        <label for="tenant">Tenant</label>
        <input name="tenant" type="text" class="form-control" disabled="disabled" ng-model="profile.tenant"/>
    </div>

    <div class="form-group">
        <label for="email">Email</label>
        <input name="email" type="text" class="form-control" ng-model="profile.email"/>
    </div>

    <div class="form-group">
        <label for="password">Password</label>
        <input name="password" type="password" class="form-control" ng-model="profile.password"/>
    </div>

    <div class="form-group">
        <label for="confirmPassword">Confirm Password</label>
        <input name="confirmPassword" type="password" class="form-control" ng-model="profile.confirmPassword"/>
    </div>

    <div class="checkbox">
        <label>
            <input type="checkbox" ng-model="profile.enabled"/> Enabled
        </label>
    </div>

    <div class="checkbox">
        <label>
            <input type="checkbox" disabled="disabled" ng-model="profile.verified"/> Verified
        </label>
    </div>

    <div class="form-group">
        <label for="createdOn">Created On</label>
        <input name="createdOn" type="text" class="form-control" disabled="disabled" ng-model="profile.createdOn"/>
    </div>

    <div class="form-group">
        <label for="lastModified">Last Modified On</label>
        <input name="lastModified" type="text" class="form-control" disabled="disabled" ng-model="profile.lastModified"/>
    </div>

    <div class="form-group">
        <label>Roles</label>
        <br />
        <label class="checkbox-inline" ng-repeat="role in availableRoles">
            <input name="roles[]"
                   type="checkbox"
                   value="{{role}}"
                   ng-checked="profile.roles.indexOf(role) > -1"
                   ng-click="toggleRole(role)"/> {{role}}
        </label>
    </div>

    <button class="btn btn-default" type="button" ng-click="updateProfile(profile)">Accept</button>
    <button class="btn btn-default" type="button" ng-click="cancel()">Cancel</button>
</form>