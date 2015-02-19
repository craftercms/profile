<#import "/spring.ftl" as spring />

<#macro head title>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>${title}</title>

<!-- Favicon -->
<link rel="shortcut icon" href="<@spring.url '/resources/image/favicon.ico'/>">

<!-- Bootstrap -->
<link href="<@spring.url '/resources/css/bootstrap.min.css'/>" rel="stylesheet">

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
<script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
<script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->
</#macro>

<#macro navBar>
<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#/">Crafter Profile Admin Console</a>
        </div>
        <div class="navbar-collapse collapse">
            <ul class="nav navbar-nav navbar-right">
                <li>
                    <a href="<@spring.url '/crafter-security-logout'/>">Log Out</a>
                </li>
                <li>
                    <span class="navbar-text">
                        Signed in to
                        <#if loggedInUser.roles?seq_contains("PROFILE_ADMIN")>
                            ${loggedInUser.tenant}
                        <#else>
                            <a href="#/tenant/update/${loggedInUser.tenant}">${loggedInUser.tenant}</a>
                        </#if>
                        as: <a href="#/profile/update/${loggedInUser.id}">${loggedInUser.username}</a>
                    </span>
                </li>
            </ul>
        </div>
    </div>
</div>
</#macro>

<#macro navSidebar>
<div class="col-sm-3 col-md-2 sidebar">
    <ul class="nav nav-sidebar">
        <li><a href="#/profile/list">List Profiles</a></li>
        <li><a href="#/profile/new">New Profile</a></li>
    </ul>
    <ul class="nav nav-sidebar">
        <#if loggedInUser.roles?seq_contains("PROFILE_SUPERADMIN")>
            <li><a href="#/tenant/list">List Tenants</a></li>
            <li><a href="#/tenant/new">New Tenant</a></li>
        <#elseif loggedInUser.roles?seq_contains("PROFILE_TENANT_ADMIN")>
            <li><a href="#/tenant/update/${loggedInUser.tenant}">Edit Tenant</a></li>
        </#if>
    </ul>
</div>
</#macro>

<#macro scripts>
<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.16/angular.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.16/angular-route.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
<script src="<@spring.url '/resources/js/bootstrap.min.js'/>"></script>
<script src="<@spring.url '/resources/js/ui-bootstrap-tpls-0.11.0.min.js'/>"></script>
<script src="<@spring.url '/resources/js/bootstrap-growl.min.js'/>"></script>
<script src="<@spring.url '/resources/js/jquery.cookie.js'/>"></script>
<script src="<@spring.url '/resources/js/app.js'/>"></script>
<script type="text/javascript">
    var contextPath = "${requestContext.contextPath}";
    var currentTenantName = "${loggedInUser.tenant}";
    var superadmin = <#if loggedInUser.roles?seq_contains("PROFILE_SUPERADMIN")>true<#else>false</#if>;
</script>
</#macro>