<div class="form-group">
    <label>Roles</label>
    <div class="checkbox" ng-repeat="availableRole in availableRoles">
        <label>
            <input name="availableRoles[]"
                   type="checkbox"
                   value="{{availableRole}}"
                   ng-checked="selectedRoles.indexOf(availableRole) > -1"
                   ng-click="toggleRole(availableRole)"/> {{availableRole}}
        </label>
    </div>
</div>