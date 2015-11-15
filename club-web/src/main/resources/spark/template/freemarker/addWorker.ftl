<#include "normalHeader.ftl">
    <div class="menu"><a href="/protected/${club.program.id}/newWorker">Register New Worker</a></div>

    <#assign people=club.program.personManager.people>

    <div>
        <ul>
            <#list people as person>
                <#if !person.asClubber().isPresent()>
                    <li><a href="/protected/club/${club.id}/workers/${person.id}">${person.name.fullName}</li>
                </#if>
            </#list>
        </ul>
    </div>
<#include "footer.ftl">
