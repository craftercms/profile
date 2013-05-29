<#import "spring.ftl" as spring />
<#import "crafter.ftl" as crafter />
<!DOCTYPE html>
<html lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<title>Login</title>
	<link href="resources/image/favicon.ico" rel="Shortcut Icon">
	<link rel="stylesheet" href="resources/css/profile.css">
</head>

<body>
  <div id="content">
	  <div id="header">
	    <a class="logo" href="http://craftercms.org" title="Visit to Crafter CMS"></a> 
	    <h1 class="mainTitle">Profile Admin Console</h1>
	  </div>  
	  
	<form class="login-form" action="crafter-security-login" method="post" accept-charset="UTF-8" id="loginForm">
	    <div class="box pad mt40 style-inputs">
			<p>
			<label for="j_username">Username:</label>
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
			<#if RequestParameters.logout??>
		<p class="logout-success">You have been successfully logged out.</p>
	</#if>
    <#if RequestParameters.login_error??>
    	<p class="login-error">Your credentials were not recognized.</p>
    </#if>
    <#if RequestParameters.login_permission_error??>
    	<p class="login-error">You do not have permission to authenticate.</p>
    </#if>
		</div>
	</form>
			
  </div>	
  <div class="footer" style="margin: 0 auto; width: 960px; padding: 10px 0pt;">
	&copy; 2007-2013 Crafter Software Corporation. All Rights Reserved.
</div>
</body>
</html>