<#import "spring.ftl" as spring />
<#import "crafter.ftl" as crafter />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Crafter Admin Console Role List</title>
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
                   href="item?username=${currentuser.userName}&tenantName=${currentuser.tenantName}">User: ${currentuser.userName!""}</a>
            </li>
        </ul>
        <h1 class="mainTitle">Manage System Roles</h1>
    </div>

    <form id="form-list" onsubmit="return onsubmitform();" accept-charset="UTF-8">
        <div class="top">
            <div class="pad">
                <nav>
                    <ul class="main-nav clearfix">
                        <li><a type="submit" href="javascript:onsubmitform('NewRole');"
                               onclick="javascript:onsubmitform('NewRole');" value="New Role" id="NewRole"
                               name="operation">New Role</a></li>
                        <!--li><a type="submit" href="javascript:onsubmitform('DeleteRole');" value="Delete Role" id="Delete" name="operation">Delete Role</a></li-->
                        <li><a type="submit" href="javascript:onsubmitform('GetTenants');" value="Get Tenants"
                               id="GetTenants" name="operation">Manage Tenants</a></li>
                    </ul>
                    </ul>
                </nav>
            </div>
        </div>
        <table id="mytable">
            <tr>
                <th scope="col">Role Name</th>
            </tr>
        <#list roleList as role>
            <tr>
                <td><a name="name" href="role_detail?roleName=${role.roleName}"
                       id="${role.roleName}">${role.roleName!""}</a></td>

            </tr>
        </#list>
        </table>
    </form>
</div>
<div class="footer" style="margin: 0 auto; width: 960px; padding: 10px 0pt;">
    &copy; 2007-2013 Crafter Software Corporation. All Rights Reserved.
</div>
</body>
</html> 