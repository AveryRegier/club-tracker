<#include "formHeader.ftl">

<form method="get" action="/protected/my">
    <fieldset class="inputGroup">
        <legend class="inputGroupLabel">Invite family members to use Club Tracker.</legend>
            <div class="explanation">
                Just click each name of each person you want to use the system, and send them an email
                invitation.
            </div>
        <div class="inputField">
            <label>People</label>
            <div class="staticField">
                <UL>
                    <#list people as person>
                    <LI><a href="/protected/person/${person.id}/invite">${person.name.fullName}</a></LI>
                    </#list>
                </UL>
            </div>
        </div>
        <div class="actions">
            <button name="submit" type='submit' value="submit">Done</button>
        </div>
    </fieldset>

</form>
<#include "footer.ftl">
