<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html>
<html>
<head>
  <title>OpenId sample</title>
</head>
<body>
	<div>
		<div>Login Success!</div>
		<div>
			<fieldset>
				<legend>Your OpenID</legend>
				<input type="text" name="openid_identifier" value="${identifier}" style="width: 50em"/>
			</fieldset>
		</div>
		<#--if fullname != null>
		<div id="sreg-result">
			<fieldset>
				<legend>Simple Registration</legend>
				<table>
					<tr>
						<th>Fullname:</th>
						<td>${fullname}</td>
					</tr>
				</table>
			</fieldset>
		</div>
		</#if>
		-->
		<div id="ax-result">
			<fieldset>
				<legend>Attribute Exchange</legend>
				<table>
				    <#list attributes?keys as key>
					<tr>
						<th>${key}:</th>
						<td>${attributes[key]}</td>
					</tr>
					</#list>
				</table>
			</fieldset>
		</div>
	</div>
</body>
</html>
