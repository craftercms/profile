var attributeTypes = [
    { name: 'TEXT', label: 'Text' },
    { name: 'LARGE_TEXT', label: 'Large Text'},
    { name: 'NUMBER', label: 'Number' },
    { name: 'BOOLEAN', label: 'Boolean' },
    { name: 'STRING_ARRAY', label: 'String Array' }
];

var attributeActions = [
    { name: 'READ_ATTRIBUTE', label: 'Read' },
    { name: 'WRITE_ATTRIBUTE', label: 'Write'},
    { name: 'REMOVE_ATTRIBUTE', label: 'Remove' }
];

var app = angular.module('CrafterAdminConsole', ['ngRoute']);

 function getObject(url, $http) {
     return $http.get(contextPath + url).then(function(result){
         return result.data;
     });
 }

function postObject(url, obj, $http) {
    return $http.post(contextPath + url, obj).then(function(result){
        return result.data;
    });
}

function allActionsAllowed(actions) {
    return actions.indexOf('*') > -1;
}

/**
 * Services
 */

app.factory('tenantService', function($http) {
    return {
        getTenantNames: function() {
            return getObject('/tenant/names', $http);
        },
        getAvailableRoles: function(tenantName) {
            return getObject('/tenant/available_roles?tenantName=' + tenantName, $http);
        },
        getTenant: function(tenantName) {
            return getObject('/tenant/' + tenantName, $http);
        },
        createTenant: function(tenant) {
            return postObject('/tenant/new', tenant, $http);
        },
        updateTenant: function(tenant) {
            return postObject('/tenant/update', tenant, $http);
        }
    }

});

app.factory('profileService', function($http) {
    return {
        getProfileList: function(tenantName) {
            return getObject('/profile/list?tenantName=' + tenantName, $http);
        },
        getProfile: function(id) {
            return getObject('/profile/' + id, $http);
        },
        createProfile: function(profile) {
            return postObject('/profile/new', profile, $http);
        },
        updateProfile: function(profile) {
            return postObject('/profile/update', profile, $http);
        }
    }
});

/**
 * Routing
 */

app.config(function($routeProvider) {
    $routeProvider.when('/', {
        controller: 'ProfileListController',
        templateUrl: contextPath + '/profile/list/view',
        resolve: {
            tenants: function(tenantService) {
                return tenantService.getTenantNames();
            }
        }
    });

    $routeProvider.when('/profile/list', {
        controller: 'ProfileListController',
        templateUrl: contextPath + '/profile/list/view',
        resolve: {
            tenants: function(tenantService) {
                return tenantService.getTenantNames();
            }
        }
    });

    $routeProvider.when('/profile/new', {
        controller: 'NewProfileController',
        templateUrl: contextPath + '/profile/new/view',
        resolve: {
            tenants: function(tenantService) {
                return tenantService.getTenantNames();
            },
            profile: function() {
                return {
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
            }
        }
    });

    $routeProvider.when('/profile/update/:id', {
        controller: 'UpdateProfileController',
        templateUrl: contextPath + '/profile/update/view',
        resolve: {
            profile: function($route, profileService) {
                return profileService.getProfile($route.current.params.id);
            }
        }
    });

    $routeProvider.when('/tenant/list', {
        controller: 'TenantListController',
        templateUrl: contextPath + '/tenant/list/view',
        resolve: {
            tenants: function(tenantService) {
                return tenantService.getTenantNames();
            }
        }
    });

    $routeProvider.when('/tenant/new', {
        controller: 'TenantController',
        templateUrl: contextPath + '/tenant/new/view',
        resolve: {
            tenant: function() {
                return {
                    name: null,
                    verifyNewProfiles: false,
                    availableRoles: [],
                    attributeDefinitions: []
                };
            },
            newTenant: function() {
                return true;
            }
        }
    });

    $routeProvider.when('/tenant/update/:name', {
        controller: 'TenantController',
        templateUrl: contextPath + '/tenant/update/view',
        resolve: {
            tenant: function($route, tenantService) {
                return tenantService.getTenant($route.current.params.name);
            },
            newTenant: function() {
                return false;
            }
        }
    });

    $routeProvider.otherwise({
        redirectTo: '/'
    });
});

/**
 * Controllers
 */

app.controller('ProfileListController', function($scope, tenants, profileService) {
    $scope.tenants = tenants;
    $scope.selectedTenant = $scope.tenants[0];

    $scope.getProfileList = function(tenantName) {
        profileService.getProfileList(tenantName).then(function(profiles) {
            $scope.profiles = profiles;
        });
    };

    $scope.getProfileList($scope.selectedTenant);
});

app.controller('NewProfileController', function($scope, $location, tenants, profile, tenantService, profileService) {
    $scope.tenants = tenants;
    $scope.profile = profile;
    $scope.profile.password = null;
    $scope.profile.confirmPassword = null;
    $scope.profile.tenant = $scope.tenants[0];

    $scope.getAvailableRoles = function(tenantName) {
        tenantService.getAvailableRoles(tenantName).then(function(availableRoles) {
            $scope.profile.roles = [];
            $scope.availableRoles = availableRoles;
        });
    };

    $scope.createProfile = function(profile) {
        delete profile.confirmPassword;

        profileService.createProfile(profile).then(function() {
            $location.path('#/');
        });
    };

    $scope.cancel = function() {
        $location.path('#/');
    };

    $scope.getAvailableRoles($scope.profile.tenant);
});

app.controller('UpdateProfileController', function($scope, $location, profile, tenantService, profileService) {
    $scope.profile = profile;
    $scope.profile.password = null;
    $scope.profile.confirmPassword = null;

    $scope.getAvailableRoles = function(tenantName) {
        tenantService.getAvailableRoles(tenantName).then(function(availableRoles) {
            $scope.availableRoles = availableRoles;
        });
    };

    $scope.updateProfile = function(profile) {
        delete profile.confirmPassword;

        profileService.updateProfile(profile).then(function() {
            $location.path('#/');
        });
    };

    $scope.cancel = function() {
        $location.path('#/');
    };

    $scope.getAvailableRoles($scope.profile.tenant);
});

app.controller('TenantListController', function($scope, tenants) {
    $scope.tenants = tenants;
});

app.controller('TenantController', function($scope, $location, tenant, newTenant, tenantService) {
    $scope.tenant = tenant;

    if (!newTenant) {
        // Tenant's names can't be changed after being created
        $('#name').attr('disabled', 'disabled');
    }

    $scope.attributeTypes = attributeTypes;
    $scope.attributeActions = attributeActions;

    $scope.getLabelForAttributeType = function(typeName) {
        for (var i = 0; i < $scope.attributeTypes.length; i++) {
            if ($scope.attributeTypes[i].name == typeName) {
                return $scope.attributeTypes[i].label;
            }
        }

        return null;
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

            $('#attribName').attr('disabled', 'disabled');
        } else {
            $scope.currentDefinition = {
                name: null,
                metadata: {
                    label: null,
                    type: $scope.attributeTypes[0].name
                },
                permissions: []
            };

            $('#attribName').removeAttr('disabled');
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

    $scope.saveTenant = function(tenant) {
        var promise;

        if (newTenant) {
            promise = tenantService.createTenant(tenant);
        } else {
            promise = tenantService.updateTenant(tenant);
        }

        promise.then(function() {
            $location.path('#/');
        });
    };

    $scope.cancel = function() {
        $location.path('#/');
    };
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