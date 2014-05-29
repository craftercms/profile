<#import "spring.ftl" as spring/>
<#import "layouts/main-layout.ftl" as main/>

<@main.layout "Crafter Profile Admin Console - Profile List", "Profile List">
<form class="form-inline" role="form">
    <div class="form-group">
        <label for="tenant">Tenant:</label>
        <select id="tenant" name="tenant" class="form-control" style="margin-left: 10px; width: 200px;">
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
    </div>
</form>

<div id="profiles" class="table-responsive">
    <table class="table table-striped">
        <thead>
            <tr>
                <th>Username</th>
                <th>Enabled</th>
            </tr>
        </thead>
        <tbody>
            <#list profiles as p>
            <tr>
                <td>
                    <a href="<@spring.url '/profile/${p.id}'/>">${p.username}</a>
                </td>
                <td>
                    <#if p.enabled>Yes<#else>No</#if>
                </td>
            </tr>
            </#list>
        </tbody>
    </table>
</div>
</@main.layout>