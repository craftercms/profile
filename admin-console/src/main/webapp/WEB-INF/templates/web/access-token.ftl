<h1 class="page-header">View Access Token</h1>

<form role="form" name="form" novalidate>
    <div class="form-group">
        <label for="id">ID</label>
        <input name="id" type="text" class="form-control" disabled="disabled" ng-model="accessToken.id"/>
    </div>

    <div class="form-group">
        <label for="application">Application</label>
        <input name="application" type="text" class="form-control" disabled="disabled"
               ng-model="accessToken.application"/>
    </div>

    <div class="checkbox">
        <label>
            <input type="checkbox" disabled="disabled" ng-model="accessToken.master"/> Master
        </label>
    </div>

    <div class="form-group">
        <label for="expiresOn">Expires On</label>
        <input name="expiresOn" type="text" class="form-control" disabled="disabled" ng-model="accessToken.expiresOn"/>
    </div>

    <div class="form-group">
        <div class="panel panel-default">
            <div class="panel-heading">
                <span class="form-panel-title">Tenant Permissions</span>
            </div>
            <div class="panel-body">
                <table class="table form-panel-table">
                    <thead>
                    <tr>
                        <th>Tenant</th>
                        <th class="col-centered" ng-repeat="action in tenantActions">
                            {{action.label}}
                        </th>
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
                                   disabled="disabled"/>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <button class="btn btn-default" type="button" ng-click="cancel()">Cancel</button>
</form>