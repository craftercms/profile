<div class="panel panel-default">
    <div class="panel-heading">
        <span class="form-panel-title">{{name}}</span>
    </div>
    <div class="panel-body">
        <form role="form" name="addItemForm" novalidate>
            <div class="form-group" ng-class="{'has-error': addItemForm.itemToAdd.$invalid}">
                <div class="input-group">
                    <input name="itemToAdd" type="text" class="form-control" placeholder="Enter item to add"
                           ng-model="itemToAdd"/>
                    <span class="input-group-btn">
                        <button class="btn btn-default" type="button" ng-click="addItem(itemToAdd)">Add</button>
                    </span>
                </div>
                <span class="error-message" ng-show="addItemForm.itemToAdd.$error.valid">
                    {{errorMsg}}
                </span>
            </div>
        </form>

        <table class="table table-striped form-panel-table">
            <tr ng-repeat="item in items">
                <td>
                    {{item}}
                </td>
                <td>
                    <div ng-if="!undeletableItems || undeletableItems.indexOf(item) < 0">
                        <a ng-click="deleteItemAt($index)">Delete</a>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</div>