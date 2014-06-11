<h1 class="page-header">Update Profile</h1>

<form role="form" name="form" novalidate>
    <div class="alert alert-info">Fields with * are required</div>

    <div class="form-group">
        <label for="username">Username</label>
        <input name="username" type="text" class="form-control" disabled="disabled" ng-model="profile.username"/>
    </div>

    <div class="form-group">
        <label for="tenant">Tenant</label>
        <input name="tenant" type="text" class="form-control" disabled="disabled" ng-model="profile.tenant"/>
    </div>

    <div class="form-group" ng-class="{'has-error': form.email.$invalid}">
        <label for="email">Email *</label>
        <input name="email" type="email" class="form-control" ng-model="profile.email" required/>
        <span class="error-message" ng-show="form.email.$error.required">Email is required</span>
        <span class="error-message" ng-show="form.email.$error.email">Not a valid email</span>
    </div>

    <div class="form-group">
        <label for="password">Password</label>
        <input name="password" type="password" class="form-control" ng-model="profile.password"/>
    </div>

    <div class="form-group" ng-class="{'has-error': form.confirmPassword.$invalid}">
        <label for="confirmPassword">Confirm Password</label>
        <input name="confirmPassword" type="password" class="form-control" ng-model="profile.confirmPassword"
               equals="profile.password"/>
        <span class="error-message" ng-show="form.confirmPassword.$error.equals">
            Passwords don't match
        </span>
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
        <checkbox-list name="Roles" selected="profile.roles" options="tenant.availableRoles"></checkbox-list>
    </div>

    <attributes definitions="tenant.attributeDefinitions" attributes="profile.attributes"></attributes>

    <button class="btn btn-default" type="button" ng-disabled="form.$invalid" ng-click="updateProfile(profile)">
        Accept
    </button>
    <button class="btn btn-default" type="button" ng-click="cancel()">Cancel</button>
</form>