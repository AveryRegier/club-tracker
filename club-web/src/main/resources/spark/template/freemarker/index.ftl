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
        </div>
    </div>

    <br/>
    <br/>

	<fieldset>
		<legend for="openid_identifier">Login with OpenID</legend>
		<form action="consumer" method="post">

            <div class="inputField">
                <label for="openid_identifier"><img src="http://wiki.openid.net/f/openid-16x16.gif"/></label>
				<input type="text" name="openid_identifier" id="openid_identifier" style="width: calc(100% - 30px);"/>
			</div>
			<div class="action">
				<button type="submit" name="login">Login</button>
			</div>
		</form>
	</fieldset>
<#include "footer.ftl">
