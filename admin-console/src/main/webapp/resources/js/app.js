var app = angular.module('CrafterAdminConsole', ['ngRoute']);

app.config(function($routeProvider) {
    $routeProvider.when('/', {
        controller: 'ProfileListController',
        templateUrl: contextPath + '/profile/list/view'
    });

    $routeProvider.when('/profile/list', {
        controller: 'ProfileListController',
        templateUrl: contextPath + '/profile/list/view'
    });

    $routeProvider.when('/profile/:id', {
        controller: 'ProfileController',
        templateUrl: contextPath + '/profile/view'
    });

    $routeProvider.when('/profile/new', {
        controller: 'NewProfileController',
        templateUrl: contextPath + '/profile/new/view'
    });

    $routeProvider.when('/tenant/list', {
        controller: 'TenantListController',
        templateUrl: contextPath + '/tenant/list/view'
    });

    $routeProvider.otherwise({
        redirectTo: '/'
    });
});

app.controller('ProfileListController', function($scope, $http) {
    $scope.fetchProfileList = function(tenantName) {
        $http.get(contextPath + '/profile/list?tenantName=' + tenantName).success(function(profiles){
            $scope.profiles = profiles;
        });
    };

    $scope.fetchTenantNames = function() {
        $http.get(contextPath + '/tenant/names').success(function(tenants){
            $scope.tenants = tenants;
            $scope.selectedTenant = tenants[0];

            $scope.fetchProfileList($scope.selectedTenant);
        });
    };

    $scope.fetchTenantNames();
});

app.controller('ProfileController', function($scope, $location, $routeParams, $http) {
    $scope.fetchAvailableRoles = function(tenantName) {
        $http.get(contextPath + '/tenant/available_roles?tenantName=' + tenantName).success(function(availableRoles){
            $scope.availableRoles = availableRoles;
        });
    };

    $scope.fetchProfile = function() {
        $http.get(contextPath + '/profile/' + $routeParams.id).success(function(profile){
            $scope.profile = profile;
            $scope.profile.password = null;
            $scope.profile.confirmPassword = null;

            $scope.fetchAvailableRoles($scope.profile.tenant);
        });
    };

    $scope.toggleRole = function(role) {
        var roles = $scope.profile.roles;
        var idx = roles.indexOf(role);

        if (idx > -1) {
            roles.splice(idx, 1);
        } else {
            roles.push(role);
        }
    };

    $scope.updateProfile = function(profile) {
        delete profile.confirmPassword;

        $http.post(contextPath + '/profile', profile).success(function(){
            $location.path('#/');
        });
    };

    $scope.cancel = function() {
        $location.path('#/');
    };

    $scope.fetchProfile();
});

app.controller('NewProfileController', function($scope, $http) {
});

app.controller('TenantListController', function($scope,  $http) {
    $scope.fetchTenantList = function() {
        $http.get(contextPath + '/tenant/list').success(function(tenants){
            $scope.tenants = tenants;
        });
    };

    $scope.fetchTenantList();
});

app.controller('TenantController', function($scope, $routeParams, $http) {
    $scope.fetchTenant = function() {
        $http.get(contextPath + '/tenant/' + $routeParams.id).success(function(tenant){
            $scope.tenant = tenant;
            $scope.selectedRole = tenant.availableRoles[0];
        });
    };

    $scope.fetchTenant();
});