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
                    <a href="#/tenant/{{tenantName}}">{{tenantName}}</a>
                </td>
                <td>
                    <a ng-click="showDeleteConfirmationDialog(tenantName, $index)">Delete</a>
                </td>
            </tr>
        </tbody>
    </table>
</div>

<confirmation-dialog id="deleteConfirmationDialog" title="Delete" message="deleteConfirmationDialogMsg"
                     confirmation-callback="deleteTenant()"></confirmation-dialog>