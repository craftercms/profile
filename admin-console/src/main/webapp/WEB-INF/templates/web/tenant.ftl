<h1 class="page-header">${pageHeader}</h1>

<form role="form" name="form" novalidate>
    <div class="alert alert-info">Fields with * are required</div>

    <div class="form-group" ng-class="{'has-error': form.name.$dirty && form.name.$invalid}">
        <label for="name">Name{{newTenant ? ' *' : ''}}</label>
        <input id="name" name="name" type="text" class="form-control" ng-model="tenant.name" ng-disabled="!newTenant"
               ng-required="newTenant"/>
        <span class="error-message" ng-show="form.name.$dirty && form.name.$error.required">
            Name is required
        </span>
    </div>

    <div class="checkbox">
        <label>
            <input type="checkbox" ng-model="tenant.verifyNewProfiles" /> Should profiles be verified when created?
        </label>
    </div>

    <div class="checkbox">
        <label>
            <input type="checkbox" ng-model="tenant.ssoEnabled" /> Single sign-on enabled
        </label>
    </div>

    <div class="checkbox">
      <label>
        <input type="checkbox" ng-model="tenant.cleanseAttributes" /> Cleanse attributes
      </label>
    </div>

    <div class="form-group">
        <editable-list name="Available Roles" items="tenant.availableRoles"
                       validation-callback="availableRolesValidationCallback(scope, item)"
                       delete-callback="showDeleteRoleConfirmationDialog(scope, item, index)"
                       undeletable-items="undeletableAvailableRoles"></editable-list>
    </div>

    <div class="form-group">
        <div class="panel panel-default">
            <div class="panel-heading">
                <span class="form-panel-title">Attribute Definitions</span>
            </div>
            <div class="panel-body">
                <button class="btn btn-default" type="button"
                        ng-click="showAttributeDefinitionModal(definition, -1)">New definition</button>
                <table class="table table-striped form-panel-table">
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>Label</th>
                            <th>Type</th>
                            <th>Display Order</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr ng-repeat="definition in tenant.attributeDefinitions">
                            <td>
                                <a ng-click="showAttributeDefinitionModal(definition, $index)">{{definition.name}}</a>
                            </td>
                            <td>
                                {{definition.metadata.label}}
                            </td>
                            <td>
                                {{getLabelForAttributeType(definition.metadata.type)}}
                            </td>
                            <td>
                                {{definition.metadata.displayOrder}}
                            </td>
                            <td>
                                <a ng-click="showDeleteAttribDefConfirmationDialog(definition, $index)">Delete</a>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <button class="btn btn-default" type="button" ng-disabled="form.$invalid" ng-click="saveTenant(tenant)">
        Accept
    </button>
    <button class="btn btn-default" type="button" ng-click="cancel()">Cancel</button>
</form>

<confirmation-dialog id="deleteRoleConfirmationDialog" title="Delete" message="deleteRoleConfirmationMsg"
                     confirmation-callback="deleteRole()"></confirmation-dialog>

<confirmation-dialog id="deleteAttribDefConfirmationDialog" title="Delete" message="deleteAttribDefConfirmationMsg"
                     confirmation-callback="deleteAttributeDefinition()"></confirmation-dialog>

<div id="attributeDefinitionModal" class="modal fade" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Attribute Definition</h4>
            </div>
            <div class="modal-body">
                <form role="form" name="definitionForm" novalidate>
                    <div class="form-group"
                         ng-class="{'has-error': definitionForm.name.$dirty && definitionForm.name.$invalid}">
                        <label for="name">Name{{newDefinition ? ' *' : ''}}</label>
                        <div ng-if="newDefinition">
                            <input name="name" type="text" class="form-control" ng-model="currentDefinition.name"
                                   attribute-not-repeated="tenant.attributeDefinitions" required="required"/>
                        </div>
                        <div ng-if="!newDefinition">
                            <input name="name" type="text" class="form-control" ng-model="currentDefinition.name"
                                   disabled="disabled"/>
                        </div>
                        <span class="error-message"
                              ng-show="definitionForm.name.$dirty && definitionForm.name.$error.required">
                            Name is required
                        </span>
                        <span class="error-message"
                              ng-show="definitionForm.name.$dirty && definitionForm.name.$error.attributeNotRepeated">
                            An attribute with that name already exists
                        </span>
                    </div>

                    <div class="form-group"
                         ng-class="{'has-error': definitionForm.label.$dirty && definitionForm.label.$invalid}">
                        <label for="label">Label *</label>
                        <input name="label" type="text" class="form-control"
                               ng-model="currentDefinition.metadata.label" required="required"/>
                        <span class="error-message"
                              ng-show="definitionForm.label.$dirty && definitionForm.label.$error.required">
                            Label is required
                        </span>
                    </div>

                    <div class="form-group">
                        <label for="type">Type</label>
                        <select name="type" class="form-control" ng-model="currentDefinition.metadata.type"
                                ng-options="type.name as type.label for type in attributeTypes">
                        </select>
                    </div>

                    <div ng-switch="currentDefinition.metadata.type">
                        <div class="form-group" ng-switch-when="TEXT">
                            <label for="defaultValue">Default Value</label>
                            <input name="defaultValue" type="text" class="form-control"
                                   ng-model="currentDefinition.defaultValue"/>
                        </div>
                        <div class="form-group" ng-switch-when="LARGE_TEXT">
                            <label for="defaultValue">Default Value</label>
                            <input name="defaultValue" type="text" class="form-control"
                                   ng-model="currentDefinition.defaultValue"/>
                        </div>
                        <div class="form-group" ng-switch-when="NUMBER">
                            <label for="defaultValue">Default Value</label>
                            <input name="defaultValue" type="number" class="form-control"
                                   ng-model="currentDefinition.defaultValue"/>
                        </div>
                        <div class="checkbox" ng-switch-when="BOOLEAN">
                            <label>
                                <input type="checkbox" ng-model="currentDefinition.defaultValue"/> Default Value
                            </label>
                        </div>
                    </div>

                    <div class="form-group"
                         ng-class="{'has-error': definitionForm.displayOrder.$dirty && definitionForm.displayOrder.$invalid}">
                        <label for="displayOrder">Display Order *</label>
                        <input name="displayOrder" type="number" class="form-control"
                               ng-model="currentDefinition.metadata.displayOrder" min="0" required="required"/>
                        <span class="error-message"
                              ng-show="definitionForm.displayOrder.$dirty && definitionForm.displayOrder.$error.required">
                            Display Order is required and must be a number
                        </span>
                        <span class="error-message"
                              ng-show="definitionForm.displayOrder.$dirty && definitionForm.displayOrder.$error.number">
                            Not a number
                        </span>
                        <span class="error-message"
                              ng-show="definitionForm.displayOrder.$dirty && definitionForm.displayOrder.$error.min">
                            Min value is 0
                        </span>
                    </div>

                    <div class="form-group">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                <span class="form-panel-title">Attribute Permissions</span>
                            </div>
                            <div class="panel-body">
                                <div class="input-group">
                                    <input type="text" class="form-control"
                                           placeholder="Enter application name (* for any)"
                                           ng-model="application"/>
                                    <span class="input-group-btn">
                                        <button class="btn btn-default" type="button"
                                                ng-click="addPermission()">Add</button>
                                    </span>
                                </div>
                                <table class="table form-panel-table">
                                    <thead>
                                        <tr>
                                            <th>Application</th>
                                            <th class="col-centered" ng-repeat="action in attributeActions">
                                                {{action.label}}
                                            </th>
                                            <th class="col-centered"></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr ng-repeat="permission in currentDefinition.permissions">
                                            <td>
                                                {{permission.application}}
                                            </td>
                                            <td class="col-centered" ng-repeat="action in attributeActions">
                                                <input type="checkbox"
                                                       value="{{action.name}}"
                                                       ng-checked="hasAction(permission, action.name)"
                                                       ng-click="toggleAction(permission, action.name, attributeActions)"/>
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
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary" ng-disabled="definitionForm.$invalid"
                        ng-click="saveAttributeDefinition()">Save changes</button>
            </div>
        </div>
    </div>
</div>