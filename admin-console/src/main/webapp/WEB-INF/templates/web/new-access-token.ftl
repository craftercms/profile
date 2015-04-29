<h1 class="page-header">New Access Token</h1>

<form role="form" name="form" novalidate>
    <div class="alert alert-info">Fields with * are required</div>

    <div class="form-group" ng-class="{'has-error': form.application.$dirty && form.application.$invalid}">
        <label for="application">Application *</label>
        <input name="application" type="text" class="form-control" ng-model="accessToken.application"
               required="required"/>
        <span class="error-message" ng-show="form.application.$dirty && form.application.$error.required">
            Application is required
        </span>
    </div>

    <div class="checkbox">
        <label>
            <input type="checkbox" ng-model="accessToken.master"/> Master
        </label>
    </div>

    <div class="form-group" ng-class="{'has-error': form.expiresOn.$dirty && form.expiresOn.$invalid}">
        <label for="expiresOn">Expires On *</label>
        <div class="input-group">
            <input name="expiresOn" type="text" class="form-control" ng-model="accessToken.expiresOn"
                   required="required" datepicker-options="dateOptions" datepicker-popup="{{dateFormat}}"
                   min-date="minDate" is-open="datePickerOpen"/>
            <span class="input-group-btn">
                <button type="button" class="btn btn-default" ng-click="openDatePicker($event)">
                    <i class="glyphicon glyphicon-calendar"></i>
                </button>
            </span>
        </div>
        <span class="error-message" ng-show="form.expiresOn.$dirty && form.expiresOn.$error.required">
            Expires On is required
        </span>
    </div>

    <div class="form-group">
        <div class="panel panel-default">
            <div class="panel-heading">
                <span class="form-panel-title">Tenant Permissions</span>
            </div>
            <div class="panel-body">
                <div class="input-group">
                    <input type="text" class="form-control" placeholder="Enter tenant name (* for any)"
                           ng-model="tenant"/>
                    <span class="input-group-btn">
                        <button class="btn btn-default" type="button"
                                ng-click="addPermission()">Add</button>
                    </span>
                </div>
                <table class="table form-panel-table">
                    <thead>
                    <tr>
                        <th>Tenant</th>
                        <th class="col-centered" ng-repeat="action in tenantActions">
                            {{action.label}}
                        </th>
                        <th class="col-centered"></th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr ng-repeat="permission in accessToken.tenantPermissions">
                        <td>
                            {{permission.tenant}}
                        </td>
                        <td class="col-centered" ng-repeat="action in tenantActions">
                            <input type="checkbox"
                                   value="{{action.name}}"
                                   ng-checked="hasAction(permission, action.name)"
                                   ng-click="toggleAction(permission, action.name, tenantActions)"/>
                        </td>
                        <td class="col-centered">
                            <a ng-click="deletePermissionAt($index)">Delete</a>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <button class="btn btn-default" type="button" ng-disabled="form.$invalid" ng-click="createAccessToken()">
        Accept
    </button>
    <button class="btn btn-default" type="button" ng-click="cancel()">Cancel</button>
</form>