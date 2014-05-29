<#import "spring.ftl" as spring />
<#import "common/components.ftl" as components />

<!DOCTYPE html>
<html lang="en">
<head>
    <@components.head "Crafter Admin Console - Profile List"/>
</head>
<body>
<div id="content">
    <@components.header "Manage Profiles"/>

    <form id="form-list" accept-charset="UTF-8">
        <div class="top">
            <div class="pad">
                <nav>
                    <ul class="main-nav clearfix">
                        <li>
                            <a id="newProfile" type="submit" href="<@spring.url '/profile/new'/>">
                                New Profile
                            </a>
                        </li>
                        <li>
                            <a id="manageTenants" type="submit" href="<@spring.url '/tenant/all'/>">
                                Manage Tenants
                            </a>
                        </li>
                    </ul>
                    <ul class="page-actions">
                        <li>
                            <label id="tenantLabel" for="tenant" style="width: 50px;">Tenant:</label>
                            <select style="width:150px;" id="tenant" name="tenant">
                            <#list tenants as t>
                                <#if currentTenant == t.name>
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
                <th scope="col">Username</th>
                <th scope="col">Enabled</th>
            </tr>
        <#list profiles as p>
            <tr>
                <td>
                    <a name="username" href="<@spring.url '/profile/${p.id}'/>">
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

<@components.footer/>
</body>
</html> 