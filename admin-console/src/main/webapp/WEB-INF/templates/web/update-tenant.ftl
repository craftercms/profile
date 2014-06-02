<h1 class="page-header">Update Tenant</h1>

<form role="form">
    <div class="form-group">
        <label for="name">Name</label>
        <input name="name" type="text" class="form-control" disabled="disabled" ng-model="tenant.name" />
    </div>

    <div class="checkbox">
        <label>
            <input type="checkbox" ng-model="tenant.verifyNewProfiles" /> Should profiles be verified when created?
        </label>
    </div>

    <div class="form-group">
        <div class="panel panel-default">
            <div class="panel-heading">
                <span class="form-panel-title">Available Roles</span>
            </div>
            <div class="panel-body">
                <div class="input-group">
                    <input type="text" class="form-control" ng-model="roleToAdd">
                    <span class="input-group-btn">
                        <button class="btn btn-default" type="button" ng-click="addRole(roleToAdd)">Add</button>
                    </span>
                </div>
                <table class="table table-striped form-panel-table">
                    <tr ng-repeat="role in tenant.availableRoles">
                        <td>
                            {{role}}
                        </td>
                        <td>
                            <a ng-click="deleteRoleAt($index)">Delete</a>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>

    <div class="form-group">
        <div class="panel panel-default">
            <div class="panel-heading">
                <span class="form-panel-title">Attribute Definitions</span>
            </div>
            <div class="panel-body">
                <button class="btn btn-default" type="button">New definition</button>
                <table class="table table-striped form-panel-table">
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>Label</th>
                            <th>Type</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr ng-repeat="definition in tenant.attributeDefinitions">
                            <td>
                                <a ng-click="showAttributeDefModal(definition)">{{definition.name}}</a>
                            </td>
                            <td>
                                {{definition.metadata.label}}
                            </td>
                            <td>
                                {{definition.metadata.type}}
                            </td>
                            <td>
                                <a>Delete</a>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</form>

<div id="attributeDefModal" class="modal fade" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Attribute Definition</h4>
            </div>
            <div class="modal-body">
                <form role="form">
                    <div class="form-group">
                        <label for="attributeDefName">Name</label>
                        <input name="attributeDefName" type="text" class="form-control"
                               ng-model="selectedDefinition.name" />
                    </div>

                    <div class="form-group">
                        <label for="attributeDefLabel">Label</label>
                        <input name="attributeDefLabel" type="text" class="form-control"
                               ng-model="selectedDefinition.metadata.label" />
                    </div>

                    <div class="form-group">
                        <label for="attributeDefType">Type</label>
                        <input name="attributeDefType" type="text" class="form-control"
                               ng-model="selectedDefinition.metadata.type" />
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary">Save changes</button>
            </div>
        </div>
    </div><
</div>