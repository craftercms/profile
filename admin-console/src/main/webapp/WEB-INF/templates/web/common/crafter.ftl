<#ftl strip_whitespace=true>

<#import "/spring.ftl" as spring />

<#--
 * showErrors
 *
 * Show validation errors for the currently bound field, with optional style attributes. For each class or style
 * parameter, if the value passed contains a colon (:) then a 'style=' attribute will be used, else a 'class='
 * attribute will be used.
 *
 * @param classOrStyle          the class or style of the <ul> element
 * @param errorClassOrStyle     the class or style of each <li> element, except the last
 * @param lastErrorClassOrStyle the class or style of the last <li> element
-->
<#macro showErrors classOrStyle="" errorClassOrStyle="" lastErrorClassOrStyle=errorClassOrStyle>
    <#if spring.status.errorMessages?size != 0>
    <ul <@renderClassOrStyle classOrStyle/>>
        <#list spring.status.errorMessages as error>
            <#if error_has_next>
                <li <@renderClassOrStyle errorClassOrStyle/>>${error?html}</li>
            <#else>
                <li <@renderClassOrStyle lastErrorClassOrStyle/>>${error?html}</li>
            </#if>
        </#list>
    </ul>
    </#if>
</#macro>

<#--
 * renderClassOrStyle
 *
 * Render the parameter value as a 'style=' attribute if it contains a colon (:), else rendder as a 'class='
 * attribute.
 *
 * @param classOrStyle the class or style
-->
<#macro renderClassOrStyle classOrStyle>
    <#if classOrStyle != "">
        <#if classOrStyle?index_of(":") == -1>class="${classOrStyle}"<#else>style="${classOrStyle}"</#if>
    </#if>
</#macro>