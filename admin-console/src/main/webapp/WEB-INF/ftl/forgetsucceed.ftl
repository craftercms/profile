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
	  
	<div class="box pad mt40 style-inputs">
	<br />
	<p>
	<h3>An email was sent to ${profile.email!""}</h2>
	<br />
	Please follow the instructions included in that email. 
	</p>
	</div>		
  </div>	
  <div class="footer" style="margin: 0 auto; width: 960px; padding: 10px 0pt;">
	&copy; 2007-2013 Crafter Software Corporation. All Rights Reserved.
</div>
</body>
</html>