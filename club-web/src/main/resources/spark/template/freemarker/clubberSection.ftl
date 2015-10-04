<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <LINK href="/main.css" rel="stylesheet" type="text/css">
    <title>${clubber.name.fullName} Section <#include "sectionName.ftl"></title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script type="text/javascript">
        <#include "forms.js">
    </script>
</head>
<body>

<form method="post">
    <div class="inputForm">
    <fieldset class="inputGroup">
        <legend class="inputGroupLabel"><#if maySign>Sign </#if>Section <#include "sectionName.ftl"></legend>
        <div class="inputGroupFields">
            <div class="inputField">
                <label>Clubber Name:</label>
                <div class="staticField">${clubber.name.fullName}</div>
            </div>
            <div class="inputField">
                <label>Section:</label>
                <div class="staticField">${section.group.name}-${section.shortCode}</div>
            </div>
            <#if record.signing.isPresent()>
                <#assign signing=record.signing.get()>
                <div class="inputField">
                    <label>Signed:</label>
                    <div class="staticField signature">${signing.by().name.fullName}</div>
                </div>
                <div class="inputField">
                    <label>Date:</label>
                    <div class="staticField">${signing.date}</div>
                </div>
                <#if (signing.note)??>
                    <div class="inputField">
                        <label>Note:</label>
                        <div class="staticField">${signing.note}</div>
                    </div>
                </#if>
                <#if signing.completionAwards?size gt 0>
                    <div class="inputField">
                        <label>Awards</label>
                        <div class="staticField">

                            <UL>
                                <#list signing.completionAwards as award>
                                    <LI>${award.forAccomplishment().name}</LI>
                                </#list>
                            </UL>

                        </div>
                    </div>
                </#if>
            <#else>
                <#if maySign>
                    <div class="inputField">
                        <label for="note">Note:</label>
                        <textarea type="textarea" name="note" id="note"></textarea>
                    </div>
                <#else>
                    <div class="staticField">
                        <label for="note">Note:</label>
                        Not signed
                    </div>
                </#if>
            </#if>
        </div>
    </fieldset>
        <div class="actions">
            <#if maySign>
                <button type='submit' name="sign" value="true">Sign</button>
            </#if>
            <#if nextSection.isPresent()>
                <a href="/protected/clubbers/${clubber.id}/sections/${nextSection.get().id}" class="button">Next</a>
            </#if>
        </div>
    </div>
</form>
</body>
</html>