<h1 class="page-header">Profile List</h1>

<form class="form-inline" role="form">
    <div class="form-group">
        <label for="tenant">Tenant:</label>
        <select name="tenant" class="form-control"
                ng-model="selectedTenantName" ng-options="tenantName for tenantName in tenantNames"
                ng-change="resetSearchAndGetProfiles(selectedTenantName)">
        </select>
    </div>
    <div class="form-group">
        <input type="text" class="form-control search-box" placeholder="Search by username" ng-model="searchText"/>
        <button class="btn btn-default" type="button" ng-disabled="searchText == null || searchText == ''"
                ng-click="getProfiles(selectedTenantName, searchText)">Search</button>
        <button class="btn btn-default" type="button"
                ng-click="resetSearchAndGetProfiles(selectedTenantName)">Reset</button>
    </div>
    <div class="form-group pull-right">
        <pagination total-items="totalItems" items-per-page="itemsPerPage" class="no-margin" ng-model="currentPage"
                    ng-change="getCurrentPage(selectedTenantName, searchText, currentPage, itemsPerPage)">
        </pagination>
    </div>
</form>

<div class="table-responsive" style="margin-top: 20px;">
    <table class="table table-striped">
        <thead>
            <tr>
                <th>ID</th>
                <th>Username</th>
                <th>Email</th>
                <th>Enabled</th>
                <th>Roles</th>
                <th class="col-centered"></th>
            </tr>
        </thead>
        <tbody>
            <tr ng-repeat="profile in profiles">
                <td>
                    <a href="#/profile/update/{{profile.id}}">{{profile.id}}</a>
                </td>
                <td>
                    {{profile.username}}
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
                <td>
                    <a ng-click="showDeleteConfirmationDialog(profile, $index)">Delete</a>
                </td>
            </tr>
        </tbody>
    </table>
</div>

<div id="deleteConfirmationDialog" class="modal fade" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Delete</h4>
            </div>
            <div class="modal-body">
                <p>Are you sure you want to delete profile <strong>{{profileToDelete.username}}</strong>?
                    You can't undo this action later.</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary"
                        ng-click="deleteProfile(profileToDelete.id, profileToDelete.index)">Ok</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
            </div>
        </div>
    </div>
</div>