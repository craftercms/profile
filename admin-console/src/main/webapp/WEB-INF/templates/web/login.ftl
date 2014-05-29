<#import "spring.ftl" as spring />
<#import "common/components.ftl" as components />

<!DOCTYPE html>
<html lang="en">
<head>
    <@components.head "Login"/>
</head>

<body>
<div id="content">
    <div id="header">
        <a class="logo" href="<@spring.url '/'/>" title="Crafter Profile Admin Console"></a>

        <h1 class="mainTitle">Crafter Profile Admin Console</h1>
    </div>

    <form id="loginForm" action="<@spring.url '/crafter-security-login'/>" method="post"
          accept-charset="UTF-8">
        <div class="box pad mt40 style-inputs">
            <p>
                <label for="username">Username:</label>
                <input id="username" name="username" size="20" maxlength="50" type="text"/>
            </p>
            <p>
                <label for="password">Password:</label>
                <input id="password" name="password" size="20" maxlength="50" type="password"/>
            </p>
            <p>
                <label for=""></label>
                <button class="btn btn-info" type="submit" id="login" name="login">Login</button>
            </p>
            <p>
                <label for=""></label>
                <a target="_top" href="forgot-password" id="forgot-password">
                    Forgot your password?
                </a>
            </p>

        <#if RequestParameters.logout??>
            <p class="logout-success">You have been successfully logged out.</p>
        </#if>
        <#if RequestParameters.login_error??>
            <p class="login-error">Your credentials were not recognized.</p>
        </#if>
        <#if RequestParameters.login_permission_error??>
            <p class="login-error">You do not have permission to access this application.</p>
        </#if>
        </div>
    </form>
</div>

<@components.footer/>
</body>
</html>