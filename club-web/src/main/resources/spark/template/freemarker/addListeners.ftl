<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <LINK href="/main.css" rel="stylesheet" type="text/css">
    <title>Add ${club.shortCode} Workers</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
    <div class="menu"><a href="/protected/${club.program.id}/newWorker">Register New Worker</a></div>

    <#assign people=club.program.personManager.people>

    <div>
        <form action="/protected/club/${club.id}/listeners" method="POST">
        <table>
            <thead>
                <th>Name</th>
                <th>Club</th>
            </thead>
            <tbody>
            <#list people as person>
                <tr>
                    <td>${person.name.fullName}</td>
                    <td>
                    <#if !person.asClubber().isPresent()>
                        <#if !person.asListener().isPresent()>
                            <input type="checkbox" name="id" value="${person.id}">Make Listener
                        <#else>
                            ${person.asListener().get().getClub().get().shortCode}
                        </#if>
                    <#else>
                        <#if person.asClubber().get().getClub().isPresent()>
                            ${person.asClubber().get().getClub().get().shortCode}
                        </#if>
                    </#if>
                    </td>
                </tr>
            </#list>
            </tbody>
        </table>
        <input type="submit">
        </form>
    </div>
</body>
</html>