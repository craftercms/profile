<h1 class="page-header">New Profile</h1>

<form role="form" name="form" novalidate>
    <div class="alert alert-info">Fields with * are required</div>

    <div class="form-group" ng-class="{'has-error': form.username.$dirty && form.username.$invalid}">
        <label for="username">Username *</label>
        <input name="username" type="text" class="form-control" ng-model="profile.username" required="required"/>
        <span class="error-message" ng-show="form.username.$dirty && form.username.$error.required">
            Username is required
        </span>
    </div>

    <#if loggedInUser.roles?seq_contains("PROFILE_SUPERADMIN")>
    <div class="form-group">
        <label for="tenant">Tenant</label>
        <select name="tenant" class="form-control" ng-model="profile.tenant"
                ng-options="tenantName for tenantName in tenantNames" ng-change="getTenant(profile.tenant)">
        </select>
    </div>
    <#else>
    <div class="form-group">
        <label for="tenant">Tenant</label>
        <input name="tenant" type="text" class="form-control" disabled="disabled" ng-model="profile.tenant"/>
    </div>
    </#if>

    <div class="form-group" ng-class="{'has-error': form.email.$dirty && form.email.$invalid}">
        <label for="email">Email *</label>
        <input name="email" type="email" class="form-control" ng-model="profile.email" required="required"/>
        <span class="error-message" ng-show="form.email.$dirty && form.email.$error.required">
            Email is required
        </span>
        <span class="error-message" ng-show="form.email.$dirty && form.email.$error.email">
            This is not a valid email
        </span>
    </div>

    <div class="form-group" ng-class="{'has-error': form.password.$dirty && form.password.$invalid}">
        <label for="password">Password *</label>
        <input name="password" type="password" class="form-control" ng-model="profile.password" required="required"/>
        <span class="error-message" ng-show="form.password.$dirty && form.password.$error.required">
            Password is required
        </span>
    </div>

    <div class="form-group" ng-class="{'has-error': form.confirmPassword.$dirty && form.confirmPassword.$invalid}">
        <label for="confirmPassword">Confirm Password *</label>
        <input name="confirmPassword" type="password" class="form-control" ng-model="confirmPassword"
               equals="profile.password" required="required"/>
        <span class="error-message" ng-show="form.confirmPassword.$dirty && form.confirmPassword.$error.required">
            Confirm Password is required
        </span>
        <span class="error-message"
              ng-show="form.confirmPassword.$dirty && form.confirmPassword.$error.equals &&
              !form.confirmPassword.$error.required">
            Passwords don't match
        </span>
    </div>

    <div class="checkbox">
        <label>
            <input type="checkbox" ng-model="profile.enabled"/> Enabled
        </label>
    </div>

    <div class="form-group">
        <checkbox-list name="Roles" selected="profile.roles" options="tenant.availableRoles"
                       disabled-options="disabledRoles"></checkbox-list>
    </div>

    <attributes definitions="tenant.attributeDefinitions" attributes="profile.attributes"></attributes>

    <button class="btn btn-default" type="button" ng-disabled="form.$invalid" ng-click="createProfile(profile)">
        Accept
    </button>
    <button class="btn btn-default" type="button" ng-click="cancel()">Cancel</button>
</form>