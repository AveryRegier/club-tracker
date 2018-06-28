<#include "normalHeader.ftl">

    <fieldset class="inputGroup">
        <legend class="inputGroupLabel">All ${club.name} Clubber's Upcoming Sections</legend>
        <div class="inputGroupFields">
            <div class="inputField">
                <label>Club Name:</label>
                <div class="staticField">${club.name}</div>
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
<#include "footer.ftl">
