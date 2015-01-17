<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <LINK href="/main.css" rel="stylesheet" type="text/css">
    <title>${club.shortName} Awards</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
    <fieldset class="inputGroup">
        <legend class="inputGroupLabel">${club.shortName} Awards</legend>
        <div>
            <table>
                <thead>
                <th>Name</th>
                <th>Accomplishment</th>
                <th>Award</th>
                </thead>
                <tbody>
                <#list club.awardsNotYetPresented as presentation>
                    <tr>
                        <td>${presentation.to().name.fullName}</td>
                        <td>${presentation.forAccomplishment().name}</td>
                        <td>
                            <#if !presentation.token().isPresent()>
                                ${presentation.token().get().name}
                            </#if>
                        </td>
                    </tr>
                </#list>
                </tbody>
            </table>
        </div>
    </fieldset>
</body>
</html>