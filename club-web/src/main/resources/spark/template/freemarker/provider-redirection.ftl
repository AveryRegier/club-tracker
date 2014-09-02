<!DOCTYPE html>
<html>
<head>
    <title>OpenID Provider Redirection</title>
</head>
  <body onload="document.forms['openid-provider-redirection'].submit();">
	    <form name="openid-provider-redirection" action="${message.OPEndpoint}" method="post">
	        <#list message.parameterMap?keys as prop>
	        	<input type="hidden" name="${prop}" value="${message.parameterMap[prop]}"/>
            </#list>
	        <button type="submit">Continue</button>
	    </form>
	</body>
</html>