<#import "spring.ftl" as spring />
<#import "crafter.ftl" as crafter />
<!DOCTYPE html>
<html lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<title>Change Password</title>
	<link href="resources/image/favicon.ico" rel="Shortcut Icon">
	<link rel="stylesheet" href="resources/css/profile.css">
</head>

<body>
  <div id="content">
	  <div id="header">
	    <a class="logo" href="index.jsp" title="Crafter Profile Admin Console"></a> 
	    <h1 class="mainTitle">Crafter Profile Admin Console - Changing Password</h1>
	  </div>  
	  
	<form class="login-form" action="changing-password" method="post" accept-charset="UTF-8" id="loginForm">
	    <div class="box pad mt40 style-inputs">
			<p>
			<label for="newpassLabel">New Password:</label>
			<@crafter.formInput "changer.newpass", "newpass", "style=width:270 class='test'", "password"/>
			<@crafter.showErrors "error-msg", "mbs", ""/>
            </p>
			
			<p>
			<label for="confirmpassLabel">Confirm Password:</label>
			<@crafter.formInput "changer.confirmPass", "confirmPass", "style=width:270 class='test'", "password"/>
            <@crafter.showErrors "error-msg", "mbs", ""/>
			</p>
			<@crafter.formInput "changer.token", "token", "style=width:270", "hidden"/>
			<p>
			<label for=""></label>
			<button class="btn btn-info" type="submit" id="changing-password" name="changing-password">Change Password</button>
			</p>
			
		</div>
	</form>
			
  </div>	
  <div class="footer" style="margin: 0 auto; width: 960px; padding: 10px 0pt;">
	&copy; 2007-2013 Crafter Software Corporation. All Rights Reserved.
</div>
</body>
</html>