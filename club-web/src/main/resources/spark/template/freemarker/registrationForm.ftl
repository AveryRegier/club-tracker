<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Club Login</title>
    <style type="text/css">
        .style1{text-align: justify;}
    </style>
</head>
<body>
<div class="alert alert-info">
    <strong>Step 2&nbsp;:&nbsp;</strong>Shows pre-filled form with the information given by provider with other required fields.<br>
    Following information is retrieved. Please fill the missing fields.
</div>
<div align="center">

<form action='submitRegistration' method="post">
	<table border="1">
		<tr>
			<td>Email</td>
			<td><input type="text" size="25" name="email" id="email" value='${profile.email}'/></td>
		</tr>
		<tr>
			<td>Name</td>
			<td><input type="text" size="25" name="name" id="name" value='${profile.fullName}'/></td>
		</tr>
		<tr>
			<td>Date of Birth</td>
			<td><input type="text" size="25" name="dob" id="dob" value=""/></td>
		</tr>
		<#if profile.country??>
		<tr>
			<td>Country</td>
			<td><input type="text" size="25" name="country" id="country" value='${profile.country}'/></td>
		</tr>
        </#if>
		<#if profile.language??>
		<tr>
			<td>Language</td>
			<td><input type="text" size="25" name="language" id="language" value='${profile.language}'/></td>
		</tr>
        </#if>
		<tr>
			<td>Gender</td>
			<td><input type="text" size="25" name="gender" id="gender" value='${profile.gender}'/></td>
		</tr>
		<#if profile.location??>
		<tr>
			<td>Location</td>
			<td><input type="text" size="25" name="location" id="location" value='${profile.location}'/></td>
		</tr>
        </#if>
		<tr>
			<td>Profile Image</td>
			<td>
			    <#if profile.profileImageURL??>
					<img src='${profile.profileImageURL}' alt="No Image"/>
                </#if>
			</td>
		</tr>
		<tr>
			<td colspan="2" align="center">
				<input type="hidden" name="uniqueId" id="uniqueId" value='${profile.validatedId}'/>
				<input type="hidden" name="profileImageURL" id="profileImageURL" value='${profile.profileImageURL}'/>
				<input type="submit" value="Submit"/></td>
		</tr>
	</table>
</form>
</div>
</body>
</html>