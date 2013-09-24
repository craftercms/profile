<#import "spring.ftl" as spring />
<#import "crafter.ftl" as crafter />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Verify Account</title>
    <link href="resources/image/favicon.ico" rel="Shortcut Icon">
    <link rel="stylesheet" href="resources/css/profile.css">
</head>

<body>
<div id="content">
    <div id="header">
        <a class="logo" href="index.jsp" title="Crafter Profile Admin Console"></a>

        <h1 class="mainTitle">Crafter Profile Admin Console</h1>
        <h2>Verify Account</h2>
    </div>

    <form class="login-form" action="crafter-verify-account" method="post" accept-charset="UTF-8"
          id="loginForm">
    <#setting url_escaping_charset='UTF-8'>
        <div class="box pad mt40 style-inputs">
        <#if RequestParameters.error??>
            <p class="general-error">
                <#if Session.verifyAccountException??>
                        
                        ${Session.verifyAccountException.message!""}
               </#if>
            </p>
        </#if>
            <p>
                Please confirm you want to verify your account

            </p>
			<br /><br />
            
        <#if RequestParameters["token"]??>
            <#assign token = RequestParameters["token"]>
            <input type="hidden" value="${token?url}" name="token" id="token"/>
        </#if>
            <p>
                <label for=""></label>
                <button class="btn btn-info" type="submit" id="verify-account" name="verify-account">Verify Account</button>
            </p>
        <#if RequestParameters.success??>
            <p class="logout-success">
                <br/>
                Your account has been verified successfully and it is currently active
            </p>
        </#if>

        </div>
    </form>

</div>
<div class="footer" style="margin: 0 auto; width: 960px; padding: 10px 0pt;">
    &copy; 2007-2013 Crafter Software Corporation. All Rights Reserved.
</div>
</body>
</html>