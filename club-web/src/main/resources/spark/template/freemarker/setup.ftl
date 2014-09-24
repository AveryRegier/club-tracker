<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Club Setup</title>
    <style type="text/css">
        .style1{text-align: justify;}
    </style>
</head>
<body>
<div class="alert alert-info">
    <strong>Setup your club.
</div>
<div align="center">

<form method="post">
	<table border="1">
		<tr>
			<td>Organization Name:</td>
			<td><input type="text" size="50" name="organizationName" id="organizationName"/></td>
		</tr>
		<tr>
			<td>Program:</td>
			<td>
			<select name="program" id="program"'>
			<#list programs as program>
                <option value="${program}">${program}</option>
            </#list>
			</select>
			</td>
		</tr>
		<tr>
			<td>My role in this program is:</td>
			<td>
			<select name="role" id="role"'>
			<#list roles as role>
                <option value="${role}"/>${role}</option>
            </#list>
			</select>
			</td>
		</tr>
	</table>
	<input type='submit'>
</form>
</div>
</body>
</html>