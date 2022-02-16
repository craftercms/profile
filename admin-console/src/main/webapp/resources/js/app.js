/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Angular Module
 */
var app = angular.module('CrafterAdminConsole', ['ngRoute', 'ui.bootstrap']);

/**
 * Global variables
 */
var attributeTypes = [
    { name: 'TEXT', label: 'Text' },
    { name: 'LARGE_TEXT', label: 'Large Text'},
    { name: 'NUMBER', label: 'Number' },
    { name: 'BOOLEAN', label: 'Boolean' },
    { name: 'STRING_LIST', label: 'String List' },
    { name: 'COMPLEX', label: 'Complex' }
];

var attributeActions = [
    { name: 'READ_ATTRIBUTE', label: 'Read' },
    { name: 'WRITE_ATTRIBUTE', label: 'Write'},
    { name: 'REMOVE_ATTRIBUTE', label: 'Remove' }
];

var tenantActions = [
    { name: 'CREATE_TENANT', label: 'Create Tenant' },
    { name: 'READ_TENANT', label: 'Read Tenant' },
    { name: 'UPDATE_TENANT', label: 'Update Tenant' },
    { name: 'DELETE_TENANT', label: 'Delete Tenant' },
    { name: 'MANAGE_PROFILES', label: 'Manage Profiles' },
    { name: 'MANAGE_TICKETS', label: 'Manage Tickets' }
];

var paginationConfig = {
    size: 5,
    itemsPerPage: 10
};

var defaultAvailableRoles = ['PROFILE_TENANT_ADMIN', 'PROFILE_ADMIN'];

/**
 * Constants
 */
app.constant('paginationConfig', {
    maxSize: 10,
    itemsPerPage: 10,
    boundaryLinks: true,
    directionLinks: true,
    previousText: '‹',
    nextText: '›',
    firstText: '«',
    lastText: '»',
    rotate: true
});

/**
 * Global functions
 */
function isSuperadmin() {
    return hasRole('PROFILE_SUPERADMIN');
}

function isTenantAdmin() {
    return hasRole('PROFILE_TENANT_ADMIN');
}

function hasRole(role) {
    return currentRoles.indexOf(role) >= 0;
}

function getSuperiorRoles() {
    var superiorRoles = [];

    if (!isSuperadmin()) {
        superiorRoles.push('PROFILE_SUPERADMIN');

        if(!isTenantAdmin()) {
            superiorRoles.push('PROFILE_TENANT_ADMIN');
        }
    }

    return superiorRoles;
}

function isCurrentRoleNotInferior(profile) {
    var superiorRoles = getSuperiorRoles();

    for (var i = 0; i < profile.roles.length; i++) {
        if (superiorRoles.indexOf(profile.roles[i]) >= 0) {
            return false;
        }
    }

    return true;
}

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

function hasAllActionsWildcard(actions) {
    return actions.indexOf('*') > -1;
}

function hasAction(permission, action) {
    if (!permission.allowedActions) {
        return false;
    }
    if (hasAllActionsWildcard(permission.allowedActions)) {
        return true;
    }
    if (permission.allowedActions.indexOf(action) > -1) {
        return true;
    }
}

function toggleAction(permission, action, availableActions) {
    if (permission.allowedActions === undefined || permission.allowedActions === null) {
        permission.allowedActions = [];
    }

    if (hasAllActionsWildcard(permission.allowedActions)) {
        permission.allowedActions = [];

        for (var availableAction in availableActions) {
            if (availableAction != action) {
                permission.allowedActions.push(availableActions);
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
}

function getAllActions() {
    var actions = [];

    for (var i = 0; i < attributeActions.length; i++) {
        actions.push(attributeActions[i].name);
    }

    return actions;
}

function showGrowlMessage(type, message) {
    $.growl(message, {
        type: type,
        pause_on_mouseover: true,
        position: {
            from: 'top',
            align: 'center'
        },
        offset: 40
    });
}

function isLoggedIn() {
    $.getJSON(contextPath + "/crafter-security-current-auth", function (data, status, jxhl) {
        if (jxhl.status !== 200) {
            window.location = 'login'
        }
    });
}

function hideModalIfShown(modal) {
    if (modal.data('bs.modal') && modal.data('bs.modal').isShown) {
        modal.modal('hide');
    }
}

function setDefaultAttributeValues(attributes, attributeDefinitions) {
    for (var i = 0; i < attributeDefinitions.length; i++) {
        var definition = attributeDefinitions[i];
        if (definition.defaultValue != null && attributes[definition.name] == null) {
            attributes[definition.name] = definition.defaultValue;
        }
    }
}

/**
 * Filters
 */
app.filter('prettyStringify', function() {
    return function(input) {
        return angular.toJson(input, true);
    }
});

/**
 * Http Interceptors
 */
app.factory('httpErrorHandler', function ($q, $rootScope) {
    return {
        'response': function(response) {
            isLoggedIn();
            return response;
        },
        'responseError': function(rejection) {

            var message;

                if (rejection.status == 0) {
                    message = 'Unable to communicate with the server. Please try again later or contact IT support';
                } else {
                    message = 'Server responded with ' + rejection.status + ' error';
                    if (rejection.data.message) {
                        message += ': <strong>' + rejection.data.message + '</strong>';
                    }

                    message += '. If you need more information, please contact IT support';
                }

                $rootScope.$broadcast('httpError');

                showGrowlMessage('danger', message);
            return $q.reject(rejection);
        }
    };
});

app.config(['$httpProvider', function($httpProvider) {
    $httpProvider.interceptors.push('httpErrorHandler');
}]);

/**
 * Services
 */
app.factory('accessTokenService', function($http) {
    return {
        getAllAccessTokens: function() {
            return getObject('/access_token/all', $http);
        },
        getAccessToken: function(id) {
            return getObject('/access_token/' + id, $http);
        },
        createAccessToken: function(token) {
            return postObject('/access_token/create', token, $http);
        },
        deleteAccessToken: function(id) {
            return postObject('/access_token/' + id + '/delete', null, $http);
        }
    }
});

app.factory('tenantService', function($http) {
    return {
        getTenantNames: function() {
            return getObject('/tenant/names', $http);
        },
        getTenant: function(tenantName) {
            return getObject('/tenant/' + tenantName, $http);
        },
        createTenant: function(tenant) {
            return postObject('/tenant/create', tenant, $http);
        },
        updateTenant: function(tenant) {
            return postObject('/tenant/update', tenant, $http);
        },
        deleteTenant: function(tenantName) {
            return postObject('/tenant/' + tenantName + '/delete', null, $http);
        }
    }
});

app.factory('profileService', function($http) {
    return {
        getProfileCount: function(tenantName, query) {
            var url = '/profile/count?tenantName=' + tenantName;
            if (query != undefined && query != null) {
                url += '&query=' + query;
            }

            return getObject(url, $http);
        },
        getProfileList: function(tenantName, query, start, count) {
            var url ='/profile/list?tenantName=' + tenantName;
            if (query != undefined && query != null) {
                url += '&query=' + query;
            }
            if (start != undefined && start != null) {
                url += '&start=' + start;
            }
            if (count != undefined && count != null) {
                url += '&count=' + count;
            }

            return getObject(url, $http);
        },
        getProfile: function(id) {
            return getObject('/profile/' + id, $http);
        },
        createProfile: function(profile) {
            return postObject('/profile/create', profile, $http);
        },
        updateProfile: function(profile) {
            return postObject('/profile/update', profile, $http);
        },
        deleteProfile: function(id) {
            return postObject('/profile/' + id + '/delete', null, $http);
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
            tenantNames: function(tenantService) {
                return tenantService.getTenantNames();
            }
        }
    });

    $routeProvider.when('/profiles', {
        controller: 'ProfileListController',
        templateUrl: contextPath + '/profile/list/view',
        resolve: {
            tenantNames: function(tenantService) {
                return tenantService.getTenantNames();
            }
        }
    });

    $routeProvider.when('/new_profile', {
        controller: 'NewProfileController',
        templateUrl: contextPath + '/profile/new/view',
        resolve: {
            tenantNames: function(tenantService) {
                return tenantService.getTenantNames();
            },
            profile: function() {
                return {
                    id: null,
                    username: null,
                    password: null,
                    email: null,
                    verified: false,
                    enabled: true,
                    createdOn: null,
                    lastModified: null,
                    tenant: null,
                    roles: [],
                    attributes: {}
                };
            }
        }
    });

    $routeProvider.when('/profile/:id', {
        controller: 'ProfileController',
        templateUrl: contextPath + '/profile/view',
        resolve: {
            profile: function($route, profileService) {
                return profileService.getProfile($route.current.params.id);
            }
        }
    });

    $routeProvider.when('/tenants', {
        controller: 'TenantListController',
        templateUrl: contextPath + '/tenant/list/view',
        resolve: {
            tenantNames: function(tenantService) {
                return tenantService.getTenantNames();
            }
        }
    });

    $routeProvider.when('/new_tenant', {
        controller: 'TenantController',
        templateUrl: contextPath + '/tenant/new/view',
        resolve: {
            tenant: function() {
                return {
                    name: null,
                    verifyNewProfiles: false,
                    availableRoles: defaultAvailableRoles,
                    attributeDefinitions: []
                };
            },
            newTenant: function() {
                return true;
            }
        }
    });

    $routeProvider.when('/tenant/:name', {
        controller: 'TenantController',
        templateUrl: contextPath + '/tenant/view',
        resolve: {
            tenant: function($route, tenantService) {
                return tenantService.getTenant($route.current.params.name);
            },
            newTenant: function() {
                return false;
            }
        }
    });

    $routeProvider.when('/access_tokens', {
        controller: 'AccessTokenListController',
        templateUrl: contextPath + '/access_token/list/view',
        resolve: {
            accessTokens: function(accessTokenService) {
                return accessTokenService.getAllAccessTokens();
            }
        }
    });

    $routeProvider.when('/new_access_token', {
        controller: 'NewAccessTokenController',
        templateUrl: contextPath + '/access_token/new/view',
        resolve: {
            accessToken: function() {
                return {
                    application: null,
                    master: false,
                    tenantPermissions: [],
                    expiresOn: null
                };
            }
        }
    });

    $routeProvider.when('/access_token/:id', {
        controller: 'AccessTokenController',
        templateUrl: contextPath + '/access_token/view',
        resolve: {
            accessToken: function($route, accessTokenService) {
                return accessTokenService.getAccessToken($route.current.params.id);
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
app.controller('ProfileListController', function($scope, $location, tenantNames, profileService) {
    // Abort if tenantNames is null or empty. It means there was a server error
    if (!tenantNames) {
        return;
    }

    $scope.$on('httpError', function() {
        hideModalIfShown($('#deleteConfirmationDialog'));
    });

    $scope.tenantNames = tenantNames;
    $scope.selectedTenantName = currentTenantName;
    $scope.itemsPerPage = 10;

    $scope.isCurrentRoleNotInferior = function(profile) {
       return isCurrentRoleNotInferior(profile);
    };

    $scope.isValidUsername = function(text) {
        return /^\w+$/.test(text);
    };

    $scope.getCurrentPage = function(tenantName, searchText, currentPage, itemsPerPage) {
        var start = (currentPage - 1) * itemsPerPage;

        profileService.getProfileList(tenantName, searchText, start, itemsPerPage).then(function(profiles) {
            $scope.profiles = profiles;
        });
    };

    $scope.getProfiles = function(tenantName, searchText) {
        if ($scope.searchText == null || $scope.searchText == '' || $scope.isValidUsername($scope.searchText)) {
            profileService.getProfileCount(tenantName, searchText).then(function(count) {
                $scope.totalItems = count;
                $scope.currentPage = 1;

                $scope.getCurrentPage(tenantName, searchText, $scope.currentPage, $scope.itemsPerPage);
            });
        } else {
            showGrowlMessage('info', 'Search term must be a word with no spaces');
        }
    };

    $scope.resetSearchAndGetProfiles = function(tenantName) {
        $scope.searchText = "";

        $scope.getProfiles(tenantName, $scope.searchText);
    };

    $scope.showDeleteConfirmationDialog = function(profile, index) {
        $scope.profileToDelete = {};
        $scope.profileToDelete.id = profile.id;
        $scope.profileToDelete.index = index;
        $scope.deleteConfirmationDialogMsg = 'Are you sure you want to delete profile "' + profile.username + '"? ' +
            'You can\'t undo this action later.';

        $('#deleteConfirmationDialog').modal('show');
    };

    $scope.deleteProfile = function() {
        profileService.deleteProfile($scope.profileToDelete.id).then(function() {
            $scope.profiles.splice($scope.profileToDelete.index, 1);

            $('#deleteConfirmationDialog').modal('hide');
        });
    };

    $scope.resetSearchAndGetProfiles($scope.selectedTenantName);
});

app.controller('NewProfileController', function($scope, $location, tenantNames, profile, tenantService, profileService) {
    // Abort if tenantNames is null or empty. It means there was a server error
    if (!tenantNames) {
        return;
    }

    $scope.tenantNames = tenantNames;
    $scope.profile = profile;
    $scope.profile.tenant = currentTenantName;
    $scope.profile.password = "";
    $scope.confirmPassword = "";
    $scope.disabledRoles = getSuperiorRoles();

    $scope.getTenant = function(tenantName) {
        tenantService.getTenant(tenantName).then(function(tenant) {
            $scope.tenant = tenant;

            // Different tenant, Different roles and attributes
            $scope.profile.roles = [];
            $scope.profile.attributes = {};

            setDefaultAttributeValues($scope.profile.attributes, tenant.attributeDefinitions);
        });
    };

    $scope.createProfile = function() {
        profileService.createProfile($scope.profile).then(function() {
            $location.path('/');
        });
    };

    $scope.cancel = function() {
        $location.path('/');
    };

    $scope.getTenant($scope.profile.tenant);
});

app.controller('ProfileController', function($scope, $location, profile, tenantService, profileService) {
    // Abort if profile is null or empty. It means there was a server error
    if (!profile) {
        return;
    }

    $scope.profile = profile;
    $scope.profile.password = "";
    $scope.confirmPassword = "";
    $scope.disabledRoles = getSuperiorRoles();

    $scope.getTenant = function(tenantName) {
        tenantService.getTenant(tenantName).then(function(tenant) {
            $scope.tenant = tenant;
        });
    };

    $scope.updateProfile = function() {
        profileService.updateProfile($scope.profile).then(function() {
            $location.path('/');
        });
    };

    $scope.cancel = function() {
        $location.path('/');
    };

    $scope.getTenant($scope.profile.tenant);
});

app.controller('TenantListController', function($scope, tenantNames, tenantService) {
    // Abort if tenantNames is null or empty. It means there was a server error
    if (!tenantNames) {
        return;
    }

    $scope.tenantNames = tenantNames;

    $scope.$on('httpError', function() {
        hideModalIfShown($('#deleteConfirmationDialog'));
    });

    $scope.showDeleteConfirmationDialog = function(tenantName, index) {
        $scope.tenantToDelete = {};
        $scope.tenantToDelete.name = tenantName;
        $scope.tenantToDelete.index = index;
        $scope.deleteConfirmationDialogMsg = 'Are you sure you wan to delete tenant "' + tenantName + '"? All its ' +
            'profiles will be deleted too. You can\'t undo this action later.';

        $('#deleteConfirmationDialog').modal('show');
    };

    $scope.deleteTenant = function() {
        tenantService.deleteTenant($scope.tenantToDelete.name).then(function() {
            $scope.tenantNames.splice($scope.tenantToDelete.index, 1);

            $('#deleteConfirmationDialog').modal('hide');
        });
    };
});

app.controller('TenantController', function($scope, $location, tenant, newTenant, tenantService) {
    // Abort if tenant is null or empty. It means there was a server error
    if (!tenant) {
        return;
    }

    $scope.tenant = tenant;
    $scope.newTenant = newTenant;
    $scope.attributeTypes = attributeTypes;
    $scope.attributeActions = attributeActions;
    $scope.undeletableAvailableRoles = ['PROFILE_SUPERADMIN'];

    $scope.availableRolesValidationCallback = function(scope, item) {
        if (item == 'PROFILE_SUPERADMIN') {
            scope.errorMsg = 'PROFILE_SUPERADMIN is a system reserved role';

            return false;
        } else {
            return true;
        }
    };

    $scope.showDeleteRoleConfirmationDialog = function(scope, item, index) {
        $scope.roleToDelete = {};
        $scope.roleToDelete.name = item;
        $scope.roleToDelete.index = index;
        $scope.deleteRoleConfirmationMsg = 'Are you sure you wan to delete available role "' + item + '"? It ' +
            'will also be deleted from all profiles using it. You can\'t undo this action after accepting the ' +
            'changes.';

        $('#deleteRoleConfirmationDialog').modal('show');
    };

    $scope.deleteRole = function() {
        $scope.tenant.availableRoles.splice($scope.roleToDelete.index, 1);

        $('#deleteRoleConfirmationDialog').modal('hide');
    };

    $scope.showDeleteAttribDefConfirmationDialog = function(definition, index) {
        $scope.attribDefToDelete = {};
        $scope.attribDefToDelete.index = index;
        $scope.deleteAttribDefConfirmationMsg = 'Are you sure you wan to delete attribute definition "' +
            definition.name + '"? The attribute will also be deleted from all profiles using it. You can\'t undo ' +
            'this action after accepting the changes.';

        $('#deleteAttribDefConfirmationDialog').modal('show');
    };

    $scope.deleteAttributeDefinition = function() {
        $scope.tenant.attributeDefinitions.splice($scope.attribDefToDelete.index, 1);

        $('#deleteAttribDefConfirmationDialog').modal('hide')
    };

    $scope.getLabelForAttributeType = function(typeName) {
        for (var i = 0; i < $scope.attributeTypes.length; i++) {
            if ($scope.attributeTypes[i].name == typeName) {
                return $scope.attributeTypes[i].label;
            }
        }

        return null;
    };

    $scope.showAttributeDefinitionModal = function(definition, index) {
        if (index > -1) {
            $scope.currentDefinition = angular.copy(definition);

            $('#attribName').attr('disabled', 'disabled');
        } else {
            var allActions = [];
            for (var action in attributeActions) {
                allActions.push(action);
            }

            $scope.currentDefinition = {
                name: null,
                metadata: {
                    label: null,
                    type: $scope.attributeTypes[0].name
                },
                permissions: [
                    {
                        application: '*',
                        allowedActions: getAllActions()
                    }
                ]
            };

            $('#attribName').removeAttr('disabled');
        }

        $scope.currentDefinitionIndex = index;
        $scope.newDefinition = index < 0;
        $scope.application = null;

        $scope.definitionForm.$setPristine();

        $('#attributeDefinitionModal').modal('show');
    };

    $scope.saveAttributeDefinition = function() {
        if ($scope.currentDefinitionIndex > -1) {
            $scope.tenant.attributeDefinitions[$scope.currentDefinitionIndex] = $scope.currentDefinition;
        } else {
            $scope.tenant.attributeDefinitions.push($scope.currentDefinition);
        }

        $('#attributeDefinitionModal').modal('hide');
    };

    $scope.addPermission = function() {
        var permission = {
            'application': $scope.application,
            'allowedActions': []
        };

        if (!$scope.currentDefinition.permissions) {
            $scope.currentDefinition.permissions = [];
        }

        $scope.currentDefinition.permissions.push(permission);
    };

    $scope.deletePermissionAt = function(index) {
        $scope.currentDefinition.permissions.splice(index, 1);
    };

    $scope.hasAction = hasAction;
    $scope.toggleAction = toggleAction;

    $scope.saveTenant = function() {
        var promise;

        if (newTenant) {
            promise = tenantService.createTenant($scope.tenant);
        } else {
            promise = tenantService.updateTenant($scope.tenant);
        }

        promise.then(function() {
            $location.path('/');
        });
    };

    $scope.cancel = function() {
        $location.path('/');
    };
});

app.controller('AccessTokenListController', function($scope, accessTokens, accessTokenService) {
    // Abort if accessTokens is null or empty. It means there was a server error
    if (!accessTokens) {
        return;
    }

    $scope.accessTokens = accessTokens;

    $scope.$on('httpError', function() {
        hideModalIfShown($('#deleteConfirmationDialog'));
    });

    $scope.showDeleteConfirmationDialog = function(token, index) {
        $scope.tokenToDelete = {};
        $scope.tokenToDelete.id = token.id;
        $scope.tokenToDelete.index = index;
        $scope.deleteConfirmationDialogMsg = 'Are you sure you wan to delete access token "' + token.id + '". You ' +
        'can\'t undo this action later.';

        $('#deleteConfirmationDialog').modal('show');
    };

    $scope.deleteToken = function() {
        accessTokenService.deleteAccessToken($scope.tokenToDelete.id).then(function() {
            $scope.accessTokens.splice($scope.tokenToDelete.index, 1);

            $('#deleteConfirmationDialog').modal('hide');
        });
    };
});

app.controller('NewAccessTokenController', function($scope, $location, accessToken, accessTokenService) {
    $scope.dateOptions = {
        startingDay: 1
    };
    $scope.dateFormat = 'dd-MMMM-yyyy';
    $scope.serializedDateFormat = 'MM/DD/YYYY HH:mm:ss';
    $scope.minDate = new Date();
    $scope.accessToken = accessToken;
    $scope.accessToken.expiresOn = $scope.minDate;
    $scope.datePickerOpen = false;
    $scope.tenantActions = tenantActions;
    $scope.hasAction = hasAction;
    $scope.toggleAction = toggleAction;

    $scope.openDatePicker = function($event) {
        $event.preventDefault();
        $event.stopPropagation();

        $scope.datePickerOpen = true;
    };

    $scope.addPermission = function() {
        var permission = {};
            permission.tenant = $scope.tenant;
            permission.allowedActions = ['READ_TENANT', 'MANAGE_PROFILES', 'MANAGE_TICKETS'];

        $scope.accessToken.tenantPermissions.push(permission);
    };

    $scope.deletePermissionAt = function(index) {
        $scope.accessToken.tenantPermissions.splice(index, 1);
    };

    $scope.createAccessToken = function() {
        var expiresOn = moment($scope.accessToken.expiresOn);
            expiresOn.startOf('day');

        var token = angular.copy($scope.accessToken);
            token.expiresOn = moment(expiresOn).format($scope.serializedDateFormat);

        accessTokenService.createAccessToken(token).then(function() {
            $location.path('/');
        });
    };

    $scope.cancel = function() {
        $location.path('/');
    };
});

app.controller('AccessTokenController', function($scope, $location, accessToken) {
    // Abort if accessToken is null or empty. It means there was a server error
    if (!accessToken) {
        return;
    }

    $scope.accessToken = accessToken;
    $scope.tenantActions = tenantActions;
    $scope.hasAction = hasAction;

    $scope.cancel = function() {
        $location.path('/');
    };
});


/**
 * Directives
 */
app.directive('checkboxList', function() {
    return {
        restrict: 'E',
        scope: {
            name: '@',
            selected: '=',
            options: '=',
            disabledOptions: '='
        },
        controller: function($scope) {
            $scope.toggleOption = function(option) {
                var index = $scope.selected.indexOf(option);
                if (index > -1) {
                    $scope.selected.splice(index, 1);
                } else {
                    $scope.selected.push(option);
                }
            };
        },
        templateUrl: contextPath + '/directives/checkbox-list',
        replace: true
    };
});

app.directive('editableList', function() {
    return {
        restrict: 'E',
        scope: {
            name: '@',
            items: '=',
            validationCallback: '&',
            deleteCallback: '&',
            undeletableItems: '='
        },
        controller: function($scope) {
            if ($scope.items === undefined || $scope.items === null) {
                $scope.items = [];
            }

            $scope.addItem = function(item) {
                if (!$scope.validationCallback || $scope.validationCallback({scope: $scope, item: item})) {
                    $scope.addItemForm.itemToAdd.$setValidity('valid', true);

                    $scope.items.push(item);
                } else {
                    $scope.addItemForm.itemToAdd.$setValidity('valid', false);
                }
            };

            $scope.deleteItem = function(item, index) {
                if ($scope.deleteCallback) {
                    $scope.deleteCallback({scope: $scope, item: item, index: index});
                } else {
                    $scope.items.splice(index, 1);
                }
            };
        },
        link: function (scope, element, attrs) {
            if (!attrs.validationCallback) {
                scope.validationCallback = null;
            }
            if (!attrs.deleteCallback) {
                scope.deleteCallback = null;
            }
        },
        templateUrl: contextPath + '/directives/editable-list',
        replace: true
    };
});

app.directive('attributes', function() {
    return {
        restrict: 'E',
        scope: {
            definitions: '=',
            attributes: '='
        },
        controller: function($scope) {
            $scope.predicate = '+metadata.displayOrder';
        },
        templateUrl: contextPath + '/directives/attributes',
        replace: true
    };
});

app.directive('equals', function () {
    return {
        require: 'ngModel',
        restrict: 'A',
        scope: {
            equals: '='
        },
        link: function(scope, elem, attrs, ctrl) {
            scope.$watch(function() {
                return scope.equals == ctrl.$modelValue;
            }, function(currentValue) {
                ctrl.$setValidity('equals', currentValue);
            });
        }
    };
});

app.directive('attributeNotRepeated', function () {
    return {
        require: 'ngModel',
        restrict: 'A',
        scope: {
            attributeDefinitions: '=attributeNotRepeated'
        },
        link: function(scope, elem, attrs, ctrl) {
            scope.$watch(function() {
                for (var i = 0; i < scope.attributeDefinitions.length; i++) {
                    if (scope.attributeDefinitions[i].name == ctrl.$modelValue) {
                        return false;
                    }
                }

                return true;
            }, function(currentValue) {
                ctrl.$setValidity('attributeNotRepeated', currentValue);
            });
        }
    };
});

app.directive('confirmationDialog', function () {
    return {
        restrict: 'E',
        scope: {
            id: '@',
            title: '@',
            message: '=',
            confirmationCallback: '&'
        },
        link: function(scope, elem) {
            elem.attr('id', scope.id);
        },
        templateUrl: contextPath + '/directives/confirmation-dialog',
        replace: true
    };
});