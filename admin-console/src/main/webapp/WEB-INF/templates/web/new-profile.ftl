<h1 class="page-header">New Profile</h1>

<form role="form">
    <div class="form-group">
        <label for="username">Username</label>
        <input name="username" type="text" class="form-control" ng-model="profile.username"/>
    </div>

    <div class="form-group">
        <label for="tenant">Tenant</label>
        <select name="tenant" class="form-control" ng-model="profile.tenant"
                ng-options="tenant for tenant in tenants" ng-change="getAvailableRoles(profile.tenant)"></select>
    </div>

    <div class="form-group">
        <label for="email">Email</label>
        <input name="email" type="email" class="form-control" ng-model="profile.email"/>
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

    <roles selected-roles="profile.roles" available-roles="availableRoles"></roles>

    <button class="btn btn-default" type="button" ng-click="createProfile(profile)">Accept</button>
    <button class="btn btn-default" type="button" ng-click="cancel()">Cancel</button>
</form>