<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <LINK href="/main.css" rel="stylesheet" type="text/css">
    <title>Club Setup</title>
    <script type="text/javascript">
        <#include "forms.js">
    </script>
</head>
<body>

<form method="post">
    <div class="inputGroup">
        <div class="inputGroupLabel">Setup your club.</div>
        <div class="inputGroupFields">
            <div class="inputField">
                <label for="organizationName">Organization Name:</label>
                <input type="text" size="50" name="organizationName" id="organizationName" value="${program.shortName}"/>
            </div>
            <div class="inputField">
                <label>Program:</label>
                <div class="staticField">${program.curriculum.shortCode}</div>
            </div>
            <div class="inputField">
                <label>Clubs</label>
                <div class="staticField">

                    <UL>
                    <#list program.clubs as club>
                        <LI>${club.shortName}</LI>
                    </#list>
                    </UL>

                </div>
            </div>
            <div class="inputField">
                <label for="addClub">Add another:</label>
                <select name="addClub" id="addClub">
                    <#list program.curriculum.series as club>
                        <option value="${club.id}">${club.shortCode}</option>
                    </#list>
                </select>
            </div>
        </div>
    </div>
    <br/>
    <br/>
    <div class="actions">
	    <input type='submit'/>
    </div>
</form>
</body>
</html>