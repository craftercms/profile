<#import "spring.ftl" as spring />
<#import "common/crafter.ftl" as crafter />
<#import "common/components.ftl" as components />
<#import "layouts/main-layout.ftl" as main/>

<@main.layout "Crafter Profile Admin Console - Update Profile", "Update Profile">
<div ng-controller="TenantController">
    <form role="form">
        <div class="form-group">
            <label for="name">Name</label>
            <input name="name" type="text" class="form-control" ng-model="tenant.name" />
        </div>

        <div class="checkbox">
            <label>
                <input type="checkbox" ng-model="tenant.verifyNewProfiles" /> Enabled
            </label>
        </div>

        <div class="form-group">
            <label for="availableRoles">Available Roles</label>
            <select name="availableRoles" class="form-control" size="4" ng-model="selectedRole"
                    ng-options="role for role in tenant.availableRoles"></select>
        </div>
    </form>
</div>
</@main.layout>