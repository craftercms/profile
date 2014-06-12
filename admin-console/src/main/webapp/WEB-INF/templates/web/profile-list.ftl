<h1 class="page-header">Profile List</h1>

<form class="form-inline" role="form">
    <div class="form-group">
        <label for="tenant">Tenant:</label>
        <select name="tenant" class="form-control" style="margin-left: 10px; width: 175px;"
                ng-model="selectedTenantName" ng-options="tenantName for tenantName in tenantNames"
                ng-change="initPaginationAndGetProfileList(selectedTenantName)">
        </select>
    </div>
    <div class="form-group" style="float: right;">
        <ul class="pagination" style="margin: 0px;">
            <li ng-class="{'disabled': pagination.current == 0}">
                <a ng-click="prevPage()">&laquo;</a>
            </li>
            <li ng-repeat="p in pagination.displayed" ng-class="{'active': pagination.current == p}">
                <a ng-click="currentPage(p)">{{p + 1}}</a>
            </li>
            <li ng-class="{'disabled': pagination.current == (pagination.total - 1)}">
                <a ng-click="nextPage()">&raquo;</a>
            </li>
        </ul>
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