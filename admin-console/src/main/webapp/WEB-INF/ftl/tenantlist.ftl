<#import "spring.ftl" as spring />
<#import "crafter.ftl" as crafter />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Crafter Admin Console Tenant List</title>
    <link href="resources/image/favicon.ico" rel="Shortcut Icon">
    <link rel="stylesheet" href="resources/css/profile.css">
    <script src="resources/js/jquery-1.9.1.min.js"></script>
    <script language="javascript" src="resources/js/util.js"></script>

</head>
<body>

<div id="content">

    <div id="header">
        <a class="logo" href="index.jsp" title="Crafter Profile Admin Console"></a>
        <ul class="page-actions">
            <li><a type="submit" href="javascript:onsubmitform('Logout');" value="Logout" id="Logout" name="operation">Logout</a>
            </li>
            <li><a style="float:right" name="currentuser"
                   href="item?username=${currentuser.userName}">User: ${currentuser.userName!""}</a></li>
        </ul>
        <h1 class="mainTitle">Manage Tenants</h1>
    </div>

    <form id="form-list" onsubmit="return onsubmitform();" accept-charset="UTF-8">
        <div class="top">
            <div class="pad">
                <nav>
                    <ul class="main-nav clearfix">
                        <li><a type="submit" href="javascript:onsubmitform('NewTenant');" value="New Tenant"
                               id="NewTenant" name="operation">New Tenant</a></li>
                        <li><a type="submit" href="javascript:onsubmitform('Roles');" value="Roles" id="Roles"
                               name="operation">Manage System Roles</a></li>
                        <li><a type="submit" href="javascript:onsubmitform('Accounts');" value="Accounts" id="Accounts"
                               name="operation">Back to Profiles</a></li>
                    </ul>
                    <ul class="page-actions">
                        <li><@crafter.formInput "filter.tenantName", "filter", "style=width:120px", "text"/>
                            <a type="submit" href="javascript:onsubmitform('FilterTenant');" value="Search" id="Search"
                               name="operation">Search</a></li>
                        <li><a type="submit" href="javascript:onsubmitform('PreviousTenant');" value="Previous Tenant"
                               id="PreviousTenant" name="operation">&lt;&lt;</a></li>
                        <li><a type="submit" href="javascript:onsubmitform('NextTenant');" value="Next Tenant"
                               id="NextTenant" name="operation">&gt;&gt;</a></li>
                    </ul>
                </nav>
            </div>
        </div>
        <table id="mytable">
            <tr>
                <th scope="col">Tenant Name</th>
            </tr>
        <#list tenantList as t>

            <tr>
                <td><a name="${t.tenantName}" id="${t.tenantName}"
                       href="tenant?tenantName=${t.tenantName}">${t.tenantName!""}</a></td>
            </tr>

        </#list>
        </table>
    </form>
</div>
<div class="footer" style="margin: 0 auto; width: 960px; padding: 10px 0pt;">
    &copy; 2007-2013 Crafter SoftwareCorporation. All Rights Reserved.
</div>
</body>
</html>