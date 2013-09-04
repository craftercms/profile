<#import "spring.ftl" as spring />
<#import "crafter.ftl" as crafter />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Crafter Admin Console Group List</title>
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
        <h1 class="mainTitle">Manage Group - Role Mapping > Tenant: ${tenant.tenantName}</h1>
    </div>

    <form id="form-list" onsubmit="return onsubmitform();" accept-charset="UTF-8">
        <div class="top">
            <div class="pad">
                <nav>
                    <ul class="main-nav clearfix">
                        <li><a type="submit" href="javascript:onsubmitform('NewGroup');"
                               onclick="javascript:onsubmitform('New');" value="New Group" id="New" name="operation">New
                            Group</a></li>
                        <li><a type="submit" href="javascript:onsubmitform('DeleteGroup');" value="Delete Profile"
                               id="Delete" name="operation">Delete Group</a></li>
                        <li><a type="submit" href="javascript:onsubmitform('GetTenants');" value="Get Tenants"
                               id="GetTenants" name="operation">Manage Tenants</a></li>
                    </ul>
                    </ul>
                </nav>
            </div>
        <@crafter.formInput "tenant.tenantName","tenantName", "style=width:270", "hidden"/>
        </div>
        <table id="mytable">
            <tr>
                <th scope="col"><input type=checkbox onclick="checkAll();" name="all" value="all" unchecked></th>
                <th scope="col">Group Name</th>
            </tr>
        <#list groupList as g>
            <tr>
                <td><input type=checkbox name="item" id="${g.id}" value="${g.id}" unchecked></td>
                <td><a name="name" href="group_update?id=${g.id}&tenantName=${g.tenantName}"
                       id="${g.name}">${g.name!""}</a></td>
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