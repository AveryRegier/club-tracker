<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <LINK href="/main.css" rel="stylesheet" type="text/css">
    <title>${clubber.name.fullName} - ${book.name}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
    <#if previous.isPresent()><div class="menu"><a href=/protected/clubbers/${clubber.id}/books/${previous.get().id}>Previous</a></a></div></#if>
    <#if next.isPresent()><div class="menu"><a href=/protected/clubbers/${clubber.id}/books/${next.get().id}>Next</a></a></div></#if>

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
                                        ${record.section.shortCode}
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
            <br clear="all"></br>
            <div class="recordSheet">
                <div class="award">
                    <div class="sectionGroupLabel" style="display: table-cell">Award</div>
                    <div class="award-header">Earned</div>
                    <div class="award-header">Presented</div>
                    <div class="award-header">Token</div>
                </div>
                <#list awards as award>
                    <div style="display: table-row;">
                        <div class="sectionGroupLabel">${award.key.name}</div>
                        <#if award.value.isPresent()>
                            <#assign presentation = award.value.get()>
                            <div class="award-date">
                                ${presentation.earnedOn().month.value}/${presentation.earnedOn().dayOfMonth}
                            </div>
                            <div class="award-date">
                                <#if presentation.presentedAt()??>
                                    ${presentation.presentedAt().presentationDate().month.value}/${presentation.presentedAt().presentationDate().dayOfMonth}
                                </#if>
                            </div>
                            <div class="award-note">
                                <#if !presentation.token().isPresent()>
                                    ${presentation.token().get().name}
                                </#if>
                            </div>
                        <#else>
                            <div class="award-date"></div>
                            <div class="award-date"></div>
                            <div class="award-note"></div>
                        </#if>
                    </div>
                </#list>
            </div>
        </div>
    </fieldset>
</body>
</html>