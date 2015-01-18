<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <LINK href="/main.css" rel="stylesheet" type="text/css">
    <title>${club.shortName} Awards</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
    <form method="post">
    <fieldset class="inputGroup">
        <legend class="inputGroupLabel">${club.shortName} Awards</legend>
        <div>
            <table>
                <thead>
                    <th></th>
                    <th>Name</th>
                    <th>Accomplishment</th>
                    <th>Award</th>
                </thead>
                <tbody>
                <#list club.awardsNotYetPresented as presentation>
                    <tr class="selectable" id="tr.${presentation.id}">
                        <td><input type="checkbox" name="award" id="${presentation.id}" value="${presentation.id}"
                                onchange="var d = document.getElementById('tr.${presentation.id}');
                                          d.className = this.checked ? 'selected selectable' : 'selectable';"></td>
                        <td>
                            <label for="${presentation.id}">${presentation.to().name.fullName}</label></td>
                        <td><label for="${presentation.id}">${presentation.forAccomplishment().name}</label></td>
                        <td>
                            <label for="${presentation.id}">
                            <#if !presentation.token().isPresent()>
                                ${presentation.token().get().name}
                            <#else>&nbsp;
                            </#if>
                            </label>
                        </td>
                    </tr>
                </#list>
                </tbody>
            </table>
            <div class="actions">
                <button name="submit" type='submit' value="submit">Mark Presented</button>
            </div>
        </div>
    </fieldset>
    </form>
</body>
</html>