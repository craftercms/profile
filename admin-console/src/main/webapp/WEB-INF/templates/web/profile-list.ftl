<h1 class="page-header">Profile List</h1>

<form class="form-inline" role="form">
    <#if loggedInUser.roles?seq_contains("PROFILE_SUPERADMIN")>
    <div class="form-group">
        <label for="tenant">Tenant:</label>
        <select name="tenant" class="form-control"
                ng-model="selectedTenantName" ng-options="tenantName for tenantName in tenantNames"
                ng-change="resetSearchAndGetProfiles(selectedTenantName)">
        </select>
    </div>
    </#if>
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
                    <div ng-if="isCurrentRoleNotInferior(profile)">
                        <a href="#/profile/update/{{profile.id}}">{{profile.id}}</a>
                    </div>
                    <div ng-if="!isCurrentRoleNotInferior(profile)">
                        {{profile.id}}
                    </div>
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
                    <div ng-if="isCurrentRoleNotInferior(profile)">
                        <a ng-click="showDeleteConfirmationDialog(profile, $index)">Delete</a>
                    </div>
                </td>
            </tr>
        </tbody>
    </table>
</div>

<confirmation-dialog id="deleteConfirmationDialog" title="Delete" message="deleteConfirmationDialogMsg"
                     confirmation-callback="deleteProfile()"></confirmation-dialog>