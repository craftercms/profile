<#import "spring.ftl" as spring/>
<#import "layouts/main-layout.ftl" as main/>

<@main.layout "Crafter Profile Admin Console - Tenant List", "Tenant List">
<div id="profiles" class="table-responsive">
    <table class="table table-striped">
        <thead>
        <tr>
            <th>Tenant Name</th>
        </tr>
        </thead>
        <tbody>
            <#list tenants as t>
            <tr>
                <td>
                    <a href="<@spring.url '/tenant/${t.name}'/>">${t.name}</a>
                </td>
            </tr>
            </#list>
        </tbody>
    </table>
</div>
</@main.layout>