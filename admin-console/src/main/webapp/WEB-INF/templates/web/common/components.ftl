<#import "/spring.ftl" as spring />

<#macro head title>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${title}</title>

    <!-- Bootstrap -->
    <link href="<@spring.url '/resources/css/bootstrap.min.css'/>" rel="stylesheet">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>
</#macro>

<#macro navBar>
<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="<@spring.url '/'/>">Crafter Profile Admin Console</a>
        </div>
        <div class="navbar-collapse collapse">
            <ul class="nav navbar-nav navbar-right">
                <li>
                    <a href="<@spring.url '/crafter-security-logout'/>">Log Out</a>
                </li>
                <li>
                    <span class="navbar-text">
                        Signed in as: <a href="<@spring.url '/profile/${loggedInUser.id}'/>">${loggedInUser.username}</a>
                    </span>
                </li>
            </ul>
        </div>
    </div>
</div>
</#macro>

<#macro scripts>
<script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
<script src="<@spring.url '/resources/js/bootstrap.min.js'/>"></script>
</#macro>