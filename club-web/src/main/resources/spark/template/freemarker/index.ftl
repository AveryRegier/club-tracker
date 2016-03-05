<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <LINK href="/login.css" rel="stylesheet" type="text/css">
    <title>Club Login</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
    <div id="main">
        <div class="alert alert-info">
        	<strong>Step 1&nbsp;:&nbsp;</strong>
        	Choose which provider you wish to use to login.
        </div>
        <div>
        <#list providers as provider>
	       	<div class="provider">
                <a href="socialauth?id=${provider.id}"><img src="${provider.image}" alt="${provider.name}" title="${provider.name}" border="0"/></a>
            </div>
        </#list>
	       	<div class="provider">
                <a href="/openid"><img src="http://openid.net/logo-graphics/openid-icon-100x100.png" alt="OpenID" title="OpenID" border="0"/></a>
            </div>
        </div>
    </div>
<#include "footer.ftl">
