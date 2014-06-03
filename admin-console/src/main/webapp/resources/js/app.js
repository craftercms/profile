var attributeTypes = [
    'text',
    'number',
    'boolean',
    'object'
];

var attributeActions = {
    'READ_ATTRIBUTE' : 'Read',
    'WRITE_ATTRIBUTE' : 'Write',
    'REMOVE_ATTRIBUTE' : 'Remove'
}

var app = angular.module('CrafterAdminConsole', ['ngRoute']);

function postProfile(url, profile, $http, $location) {
    delete profile.confirmPassword;

    $http.post(url, profile).success(function(){
        $location.path('#/');
    });
}

function cancel($location) {
    $location.path('#/');
}

function allActionsAllowed(actions) {
    return actions.indexOf('*') > -1;
}

/**
 * Routing
 */

app.config(function($routeProvider) {
    $routeProvider.when('/', {
        controller: 'ProfileListController',
        templateUrl: contextPath + '/profile/list/view'
    });

    $routeProvider.when('/profile/list', {
        controller: 'ProfileListController',
        templateUrl: contextPath + '/profile/list/view'
    });

    $routeProvider.when('/profile/update/:id', {
        controller: 'UpdateProfileController',
        templateUrl: contextPath + '/profile/update/view'
    });

    $routeProvider.when('/profile/new', {
        controller: 'NewProfileController',
        templateUrl: contextPath + '/profile/new/view'
    });

    $routeProvider.when('/tenant/list', {
        controller: 'TenantListController',
        templateUrl: contextPath + '/tenant/list/view'
    });

    $routeProvider.when('/tenant/update/:id', {
        controller: 'UpdateTenantController',
        templateUrl: contextPath + '/tenant/update/view'
    });

    $routeProvider.otherwise({
        redirectTo: '/'
    });
});

/**
 * Controllers
 */

app.controller('ProfileListController', function($scope, $http) {
    $scope.fetchTenantNames = function() {
        $http.get(contextPath + '/tenant/names').success(function(tenants){
            $scope.tenants = tenants;
            $scope.selectedTenant = tenants[0];

            $scope.fetchProfileList($scope.selectedTenant);
        });
    };

    $scope.fetchProfileList = function(tenantName) {
        $http.get(contextPath + '/profile/list?tenantName=' + tenantName).success(function(profiles){
            $scope.profiles = profiles;
        });
    };

    $scope.fetchTenantNames();
});

app.controller('UpdateProfileController', function($scope, $routeParams, $http, $location) {
    $scope.fetchProfile = function() {
        $http.get(contextPath + '/profile/' + $routeParams.id).success(function(profile){
            $scope.profile = profile;
            $scope.profile.password = null;
            $scope.profile.confirmPassword = null;

            $scope.fetchAvailableRoles($scope.profile.tenant);
        });
    };

    $scope.fetchAvailableRoles = function(tenantName) {
        $http.get(contextPath + '/tenant/available_roles?tenantName=' + tenantName).success(function(availableRoles){
            $scope.availableRoles = availableRoles;
        });
    };

    $scope.updateProfile = function(profile) {
        postProfile(contextPath + '/profile/update', profile, $http, $location);
    };

    $scope.cancel = function() {
        cancel($location);
    };

    $scope.fetchProfile();
});

app.controller('NewProfileController', function($scope, $http, $location) {
    $scope.profile = {
        id: null,
        username: null,
        password: null,
        confirmPassword: null,
        email: null,
        verified: false,
        enabled: false,
        createdOn: null,
        lastModified: null,
        tenant: null,
        roles: [],
        attributes: {}
    };

    $scope.fetchTenantNames = function() {
        $http.get(contextPath + '/tenant/names').success(function(tenants){
            $scope.tenants = tenants;
            $scope.profile.tenant = tenants[0];

            $scope.fetchAvailableRoles($scope.profile.tenant);
        });
    };

    $scope.fetchAvailableRoles = function(tenantName) {
        $http.get(contextPath + '/tenant/available_roles?tenantName=' + tenantName).success(function(availableRoles){
            $scope.profile.roles = [];
            $scope.availableRoles = availableRoles;
        });
    };

    $scope.createProfile = function(profile) {
        postProfile(contextPath + '/profile/new', profile, $http, $location);
    };

    $scope.cancel = function() {
        cancel($location);
    };

    $scope.fetchTenantNames();
});

app.controller('TenantListController', function($scope, $http) {
    $scope.fetchTenantNames = function() {
        $http.get(contextPath + '/tenant/names').success(function(tenants){
            $scope.tenants = tenants;
        });
    };

    $scope.fetchTenantNames();
});

app.controller('UpdateTenantController', function($scope, $routeParams, $http) {
    $scope.attributeTypes = attributeTypes;
    $scope.attributeActions = attributeActions;

    $scope.fetchTenant = function() {
        $http.get(contextPath + '/tenant/' + $routeParams.id).success(function(tenant){
            $scope.tenant = tenant;
        });
    };

    $scope.deleteRoleAt = function(index) {
        $scope.tenant.availableRoles.splice(index, 1);
    };

    $scope.addRole = function(role) {
        $scope.tenant.availableRoles.push(role);
    };

    $scope.showAttributeDefinitionModal = function(definition, index) {
        if (index > -1) {
            $scope.currentDefinition = angular.copy(definition);
        } else {
            $scope.currentDefinition = {
                'name': null,
                'metadata': {
                    'label': null,
                    'type': $scope.attributeTypes[0]
                },
                'permissions': []
            };
        }

        $scope.currentDefinitionIndex = index;
        $scope.application = null;

        $('#attributeDefinitionModal').modal('show');
    };

    $scope.saveAttributeDefinition = function(definition, index) {
        if (index > -1) {
            $scope.tenant.attributeDefinitions[index] = definition;
        } else {
            $scope.tenant.attributeDefinitions.push(definition);
        }

        $('#attributeDefinitionModal').modal('hide');
    };

    $scope.deleteAttributeDefinitionAt = function(index) {
        $scope.tenant.attributeDefinitions.splice(index, 1);
    };

    $scope.addPermission = function(definition, application) {
        var permission = {
            'application': application,
            'allowedActions': []
        };

        if (!definition.permissions) {
            definition.permissions = [];
        }

        definition.permissions.push(permission);
    };

    $scope.deletePermissionAt = function(definition, index) {
        definition.permissions.splice(index, 1);
    };

    $scope.hasAction = function(permission, action) {
        if (!permission.allowedActions) {
            return false;
        }
        if (allActionsAllowed(permission.allowedActions)) {
            return true;
        }
        if (permission.allowedActions.indexOf(action) > -1) {
            return true;
        }
    };

    $scope.toggleAction = function(permission, action) {
        if (permission.allowedActions === undefined || permission.allowedActions === null) {
            permission.allowedActions = [];
        }

        if (allActionsAllowed(permission.allowedActions)) {
            permission.allowedActions = [];

            for (var attributeAction in $scope.attributeActions) {
                if (attributeAction != action) {
                    permission.allowedActions.push(attributeAction);
                }
            }
        } else {
            var index = permission.allowedActions.indexOf(action);
            if (index > -1) {
                permission.allowedActions.splice(index, 1);
            } else {
                permission.allowedActions.push(action);
            }
        }
    };

    $scope.fetchTenant();
});

/**
 * Directives
 */

app.directive('roles', function() {
    return {
        restrict: 'E',
        scope: {
            selectedRoles: '=',
            availableRoles: '='
        },
        controller: function($scope) {
            $scope.toggleRole = function(role) {
                var index = $scope.selectedRoles.indexOf(role);
                if (index > -1) {
                    $scope.selectedRoles.splice(index, 1);
                } else {
                    $scope.selectedRoles.push(role);
                }
            };
        },
        templateUrl: contextPath + '/directives/roles',
        replace: true
    };
});