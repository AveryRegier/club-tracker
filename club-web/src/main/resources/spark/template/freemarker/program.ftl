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
			<td><input type="text" size="50" name="organizationName" id="organizationName" value="${program.shortName}"/></td>
		</tr>
		<tr>
			<td>Program:</td>
			<td>${program.curriculum.shortCode}</td>
		</tr>
		<tr>
			<td>Clubs</td>
			<td>
			<UL>
			<#list program.clubs as club>
                <LI>${club.shortName}</LI>
            </#list>
            </UL>
            Add another:
            <select name="addClub">
            <#list program.curriculum.series as club>
                <option value="${club.id}">${club.shortCode}</option>
            </#list>
            </select>
			</td>
		</tr>
	</table>
	<input type='submit'/>
</form>
</div>
</body>
</html>