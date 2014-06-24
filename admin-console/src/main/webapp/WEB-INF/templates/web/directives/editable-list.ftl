<div class="panel panel-default">
    <div class="panel-heading">
        <span class="form-panel-title">{{name}}</span>
    </div>
    <div class="panel-body">
        <div class="input-group">
            <input type="text" class="form-control" placeholder="Enter item to add" ng-model="itemToAdd"/>
            <span class="input-group-btn">
                <button class="btn btn-default" type="button" ng-click="addItem(itemToAdd)">Add</button>
            </span>
        </div>

        <table class="table table-striped form-panel-table">
            <tr ng-repeat="item in items">
                <td>
                    {{item}}
                </td>
                <td>
                    <a ng-click="deleteItemAt($index)">Delete</a>
                </td>
            </tr>
        </table>
    </div>
</div>