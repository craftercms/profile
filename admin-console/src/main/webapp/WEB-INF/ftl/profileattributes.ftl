<#import "spring.ftl" as spring />
<#import "crafter.ftl" as crafter />
<#list attributeList as attribute>
    <#assign index=attributeList?seq_index_of(attribute)>
<p>
    <#assign attributePath = "account.attributes['"+ attribute.name + "']">
    <label id="${attribute.name+"Label"}" for="attribute.name" class='schemaAttribute'>${attribute.label}:</label>
    <@crafter.formInput attributePath, "attribute.name","style=width:270 class='schemaAttribute' ", "text" />
</p>
</#list>
<p>
    <label id="roleLabel" for="role">Roles:</label>
<@spring.bind "account.roles"/>
<#assign selectedRoles = spring.status.value?default(" ")>
    <select style="width:270px;" multiple="multiple" id="${spring.status.expression}"
            name="${spring.status.expression}">
    <#list account.roleOption?keys as value>
        <#if selectedRoles?contains(value) >
            <#assign isSelected = true>
        <#else>
            <#assign isSelected = false>
        </#if>
        <option value="${value?html}"<#if isSelected> selected="selected"</#if>>
        ${account.roleOption[value]?html}
        </option>
    </#list>
    </select>
<@crafter.showErrors "error-msg", "mbs", ""/>
</p>
