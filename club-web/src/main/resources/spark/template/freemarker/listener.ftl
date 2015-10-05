<div class="menu"><a href="/protected/club/${me.asListener().get().club.get().id}/clubbers">More Clubbers</a></div>

<div>
    <table>
        <thead>
        <th>Name</th>
        <th>Upcoming Sections</th>
        </thead>
        <tbody>
        <#list me.asListener().get().getQuickList() as clubber>
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