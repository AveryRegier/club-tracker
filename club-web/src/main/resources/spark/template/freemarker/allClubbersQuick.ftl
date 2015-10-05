<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <LINK href="/main.css" rel="stylesheet" type="text/css">
    <title>All ${club.shortCode} Clubber's Upcoming Sections</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>

<form method="post">
    <fieldset class="inputGroup">
        <legend class="inputGroupLabel">All ${club.shortCode} Clubber's Upcoming Sections</legend>
        <div class="inputGroupFields">
            <div class="inputField">
                <label>Club Name:</label>
                <div class="staticField">${club.shortCode}</div>
            </div>
            <br clear="all">
            <div>
                <table>
                    <thead>
                    <th>Name</th>
                    <th>Upcoming Sections</th>
                    </thead>
                    <tbody>
                    <#list club.clubbers as clubber>
                        <tr>
                            <td><a href="/protected/clubbers/${clubber.id}/sections">${clubber.name.fullName}</a></td>
                            <td>
                                <#include "nextSectionsList.ftl">
                            </td>
                        </tr>
                    </#list>
                    </tbody>
                </table>
            </div>

        </div>
    </fieldset>
</form>
</body>
</html>