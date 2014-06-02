<h1 class="page-header">Profile List</h1>

<form class="form-inline" role="form">
    <div class="form-group">
        <label for="tenant">Tenant:</label>
        <select name="tenant" class="form-control" style="margin-left: 10px; width: 175px;" ng-model="selectedTenant"
                ng-options="tenant for tenant in tenants" ng-change="fetchProfileList()"></select>
    </div>
</form>

<div class="table-responsive" style="margin-top: 20px;">
    <table class="table table-striped">
        <thead>
            <tr>
                <th>Username</th>
                <th>Email</th>
                <th>Enabled</th>
                <th>Roles</th>
            </tr>
        </thead>
        <tbody>
            <tr ng-repeat="profile in profiles">
                <td>
                    <a href="#/profile/update/{{profile.id}}">{{profile.username}}</a>
                </td>
                <td>
                    {{profile.email}}
                </td>
                <td>
                    {{profile.enabled ? 'Yes' : 'No'}}
                </td>
                <td>
                    {{profile.roles.join(', ')}}
                </td>
            </tr>
        </tbody>
    </table>
</div>