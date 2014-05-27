<#import "spring.ftl" as spring />

<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Crafter Admin Console Profile List</title>
    <link href="resources/image/favicon.ico" rel="Shortcut Icon">
    <link rel="stylesheet" href="resources/css/profile.css">
</head>
<body>
<div id="content">
    <div id="header">
        <a class="logo" href="index.jsp" title="Crafter Profile Admin Console"></a>
        <ul class="page-actions">
            <li><a type="submit" href="crafter-security-logout" id="logout">Logout</a>
            </li>
            <li>
                <a style="float:right" id="loggedInUser"
                   href="profile?userId=${loggedInUser.id}&tenant=${loggedInUser.tenant}">
                    User: ${loggedInUser.username!""}
                </a>
            </li>
        </ul>
        <h1 class="mainTitle">Manage Profiles</h1>
    </div>

    <form id="form-list" accept-charset="UTF-8">
        <div class="top">
            <div class="pad">
                <nav>
                    <ul class="main-nav clearfix">
                        <li><a id="newProfile" type="submit" href="profile/new">New Profile</a></li>
                        <li><a id="manageTenants" type="submit" href="tenant/all">Manage Tenants</a></li>
                    </ul>
                    <ul class="page-actions">
                        <li>
                            <label id="tenantLabel" for="selectedTenantName" style="width: 50px;">Tenant:</label>
                            <select style="width:150px;" id="tenant" name="tenant">
                            <#list tenants as t>
                                <#if (tenant == t.name) >
                                    <#assign selected = true>
                                <#else>
                                    <#assign selected = false>
                                </#if>
                                <option value="${t.name}"<#if selected>selected="selected"</#if>>
                                ${t.name}
                                </option>
                            </#list>
                            </select>
                        </li>
                        <#--li>
                            <@spring.formInput "", "style=width:120px", "text"/>
                            <a type="submit" id="search">Search</a>
                        </li-->
                        <li><a type="submit" id="previous">&lt;&lt;</a></li>
                        <li><a type="submit" id="next">&gt;&gt;</a></li>
                    </ul>
                </nav>
            </div>
        </div>
        <table id="mytable">
            <tr>
                <th scope="col">User Name</th>
                <th scope="col">Enabled</th>
            </tr>
        <#list profiles as p>
            <tr>
                <td>
                    <a name="username" href="profile?userId=${p.id}&tenant=${p.tenant}">
                    ${p.username!""}
                    </a>
                </td>
                <td>
                    <#if p.enabled>
                        Yes
                    <#else>
                        No
                    </#if>
                </td>
            </tr>
        </#list>
        </table>
    </form>
</div>

<#include "common/footer.ftl"/>
</body>
</html> 