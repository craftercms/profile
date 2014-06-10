<h1 class="page-header">Tenant List</h1>

<div class="table-responsive">
    <table class="table table-striped">
        <thead>
            <tr>
                <th>Tenant Name</th>
            </tr>
        </thead>
        <tbody>
            <tr ng-repeat="tenantName in tenantNames">
                <td>
                    <a href="#/tenant/update/{{tenantName}}">{{tenantName}}</a>
                </td>
            </tr>
        </tbody>
    </table>
</div