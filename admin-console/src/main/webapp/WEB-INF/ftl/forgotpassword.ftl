<#import "spring.ftl" as spring />
<#import "crafter.ftl" as crafter />
<!DOCTYPE html>
<html lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<title>Forgot Password</title>
	<link href="resources/image/favicon.ico" rel="Shortcut Icon">
	<link rel="stylesheet" href="resources/css/profile.css">
</head>

<body>
  <div id="content">
	  <div id="header">
	    <a class="logo" href="index.jsp" title="Crafter Profile Admin Console"></a> 
	    <h1 class="mainTitle">Crafter Profile Admin Console - Forgot Password</h1>
	  </div>  
	  
	<form class="login-form" action="forgeting-password" method="post" accept-charset="UTF-8" id="forgeting-password">
	    <div class="box pad mt40 style-inputs">
			<p>
			<label for="tenantNameLabel">Tenant Name:</label>
			<@crafter.formInput "forgoter.tenantName", "tenantName", "style=width:270 class='test'", "text"/>
			<@crafter.showErrors "error-msg", "mbs", ""/>
            </p>
            
            <p>
			<label for="usernameLabel">Username:</label>
			<@crafter.formInput "forgoter.username", "username", "style=width:270 class='test'", "text"/>
            <@crafter.showErrors "error-msg", "mbs", ""/>
			</p>
			<p>
			<label for=""></label>
			<button class="btn btn-info" type="submit" id="forgeting-password" name="forgeting-password">Continue</button>
			</p>
			
		</div>
	</form>
			
  </div>	
  <div class="footer" style="margin: 0 auto; width: 960px; padding: 10px 0pt;">
	&copy; 2007-2013 Crafter Software Corporation. All Rights Reserved.
</div>
</body>
</html>