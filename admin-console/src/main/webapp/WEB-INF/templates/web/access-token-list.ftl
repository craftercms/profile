<h1 class="page-header">Access Token List</h1>

<div class="table-responsive">
    <table class="table table-striped">
        <thead>
            <tr>
                <th>ID</th>
                <th>Application</th>
                <th>Master</th>
                <th>Expires On</th>
                <th></th>
            </tr>
        </thead>
        <tbody>
            <tr ng-repeat="token in accessTokens">
                <td>
                    <a href="#/access_token/{{token.id}}">{{token.id}}</a>
                </td>
                <td>{{token.application}}</td>
                <td>{{token.master ? 'Yes' : 'No'}}</td>
                <td>{{token.expiresOn}}</td>
                <td>
                    <a ng-click="showDeleteConfirmationDialog(token, $index)">Delete</a>
                </td>
            </tr>
        </tbody>
    </table>
</div>

<confirmation-dialog id="deleteConfirmationDialog" title="Delete" message="deleteConfirmationDialogMsg"
                     confirmation-callback="deleteToken()"></confirmation-dialog>