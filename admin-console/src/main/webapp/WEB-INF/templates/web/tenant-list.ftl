<h1 class="page-header">Tenant List</h1>

<div class="table-responsive">
    <table class="table table-striped">
        <thead>
            <tr>
                <th>Tenant Name</th>
            </tr>
        </thead>
        <tbody>
            <tr ng-repeat="tenant in tenants">
                <td>
                    <a href="#/tenant/update/{{tenant}}">{{tenant}}</a>
                </td>
            </tr>
        </tbody>
    </table>
</div