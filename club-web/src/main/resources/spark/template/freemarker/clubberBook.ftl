<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <LINK href="/main.css" rel="stylesheet" type="text/css">
    <title>${clubber.name.fullName} - ${book.name}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
    <fieldset class="inputGroup">
        <legend class="inputGroupLabel">Clubber Records</legend>
        <div class="inputGroupFields">
            <div class="inputField">
                <label>Name:</label>
                <div class="staticField">${clubber.name.fullName}</div>
            </div>
            <div class="inputField">
                <label>Handbook:</label>
                <div class="staticField">${book.name}</div>
            </div>
            <div class="inputField">
                <div class="recordSheet">

                    <#list sectionGroups as sectionGroup>
                        <div class="sectionGroup">
                            <div class="sectionGroupLabel">${sectionGroup.key.name}</div>
                            <#list sectionGroup.value as record>
                                <a class ="section" href="/protected/clubbers/${clubber.id}/sections/${record.section.id}">
                                <div>
                                    <label class="id">
                                        ${record.section.sequence()}
                                    </label>
                                    <label class="date">
                                    <#if record.signing.isPresent()>
                                        ${record.signing.get().date.month.value}/${record.signing.get().date.dayOfMonth}
                                    <#else>
                                        &nbsp;
                                    </#if>
                                    </label>
                                </div>
                                </a>
                            </#list>
                            <div class="pad">&nbsp;</div>
                        </div>
                    </#list>
                </div>
            </div>
        </div>
    </fieldset>
</body>
</html>