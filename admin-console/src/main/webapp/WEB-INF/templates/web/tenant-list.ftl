<h1 class="page-header">Tenant List</h1>

<div class="table-responsive">
    <table class="table table-striped">
        <thead>
            <tr>
                <th>Tenant Name</th>
                <th></th>
            </tr>
        </thead>
        <tbody>
            <tr ng-repeat="tenantName in tenantNames">
                <td>
                    <a href="#/tenant/update/{{tenantName}}">{{tenantName}}</a>
                </td>
                <td>
                    <a ng-click="showDeleteConfirmationDialog(tenantName, $index)">Delete</a>
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
                <p>Are you sure you want to delete tenant <strong>{{tenantToDelete.name}}</strong>, along with all
                    its profiles? You can't undo this action later.</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary"
                        ng-click="deleteTenant(tenantToDelete.name, tenantToDelete.index)">Ok</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
            </div>
        </div>
    </div>
</div>