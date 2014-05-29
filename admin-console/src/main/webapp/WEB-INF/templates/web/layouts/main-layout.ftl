<#import "/spring.ftl" as spring />
<#import "../common/components.ftl" as components />

<#macro layout title pageHeader stylesheets=[] scripts=[]>
<!DOCTYPE html>
<html lang="en">
    <head>
        <@components.head title/>
        <link href="<@spring.url '/resources/css/main.css'/>" rel="stylesheet">
        <#list stylesheets as stylesheet>
        <link href="${stylesheet}" rel="stylesheet">
        </#list>
    </head>

    <body>
        <@components.navBar/>

        <div class="container-fluid">
            <div class="row">
                <div class="col-sm-3 col-md-2 sidebar">
                    <ul class="nav nav-sidebar">
                        <li><a href="<@spring.url '/profile/all'/>">Show Profiles</a></li>
                        <li><a href="<@spring.url '/profile/new'/>">New Profile</a></li>
                    </ul>
                    <ul class="nav nav-sidebar">
                        <li><a href="<@spring.url '/tenant/all'/>">Show Tenants</a></li>
                        <li><a href="<@spring.url '/tenant/new'/>">New Tenant</a></li>
                    </ul>
                </div>
                <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
                    <h1 class="page-header">${pageHeader}</h1>

                    <#nested/>
                </div>
            </div>
        </div>

        <@components.scripts/>
        <#list scripts as script>
        <script src="${script}"></script>
        </#list>
    </body>
</html>
</#macro>