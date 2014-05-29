<#import "spring.ftl" as spring />
<#import "common/components.ftl" as components />

<!DOCTYPE html>
<html lang="en">
<head>
    <@components.head "Crafter Admin Console - Tenant List"/>
</head>
<body>
<div id="content">
    <@components.header "Manage Tenants"/>

    <form id="form-list" accept-charset="UTF-8">
        <div class="top">
            <div class="pad">
                <nav>
                    <ul class="main-nav clearfix">
                        <li>
                            <a id="newTenant" href="<@spring.url '/tenant/new'/>">
                                New Tenant
                            </a>
                        </li>
                        <li>
                            <a id="backToProfiles" href="<@spring.url '/'/>">
                                Back to Profiles
                            </a>
                        </li>
                    </ul>
                    <ul class="page-actions">
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
                <th scope="col">Tenant Name</th>
            </tr>
            <#list tenants as t>
            <tr>
                <td>
                    <a href="<@spring.url '/tenant/${t.name}'/>">${t.name}</a>
                </td>
            </tr>
            </#list>
        </table>
    </form>
</div>

<@components.footer/>
</body>
</html> 