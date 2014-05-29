<#ftl strip_whitespace=true>

<#import "/spring.ftl" as spring />

<#macro head title>
<title>${title}</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<link href="<@spring.url '/resources/image/favicon.ico'/>" rel="Shortcut Icon">
<link rel="stylesheet" href="<@spring.url '/resources/css/profile.css'/>">
<script src="http://code.jquery.com/jquery-2.1.1.min.js"></script>
</#macro>

<#macro header mainTitle>
<div id="header">
    <a class="logo" href="<@spring.url '/'/>" title="Crafter Profile Admin Console"></a>
    <ul class="page-actions">
        <li><a type="submit" href="<@spring.url '/crafter-security-login'/>" id="logout">Logout</a>
        </li>
        <li>
            <a style="float:right" id="loggedInUser" href="<@spring.url '/profile/${loggedInUser.id}'/>">
                User: ${loggedInUser.username!""}
            </a>
        </li>
    </ul>
    <h1 class="mainTitle">${mainTitle}</h1>
</div>
</#macro>

<#macro footer>
<div class="footer" style="margin: 0 auto; width: 960px; padding: 10px 0pt;">
    &copy; 2007-2014 Crafter Software Corporation. All Rights Reserved.
</div>
</#macro>