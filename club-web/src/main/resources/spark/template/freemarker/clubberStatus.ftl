
<div>
    <table>
        <thead>
            <th>Name</th>
            <th>Club</th>
        </thead>
        <tbody>
        <#list group.clubbers as clubber>
            <tr>
                <td>${clubber.name.fullName}</td>
                <#if clubber.club.isPresent()>
                    <td><a href="/protected/clubbers/${clubber.id}/sections">${clubber.club.get().name}</td>
                <#else>
                    <td></td>
                </#if>
                <#if clubber.club.isPresent() && clubber.family.isPresent()>
                    <td><a href="/protected/${clubber.club.get().program.id}/family/${clubber.family.get().id}">Registration</a></td>
                <#else>
                    <td></td>
                </#if>
            </tr>
        </#list>
        </tbody>
    </table>
</div>