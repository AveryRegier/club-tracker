
<div>
    <form action="/protected/club/${club.shortName}/listeners" method="POST">
    <table>
        <thead>
            <th>Name</th>
            <th>Club</th>
        </thead>
        <tbody>
        <#list people as person>
            <tr>
                <td>${person.name.fullName}</td>
                <td>
                <#if !person.asClubber().isPresent()>
                    <#if !person.asListener().isPresent()>
                        <input type="checkbox" name="id" value="${person.id}">Make Listener
                    <#else>
                        ${person.asListener().get().getClub().get().shortName}
                    </#if>
                <#else>
                    <#if person.asClubber().get().getClub().isPresent()>
                        ${person.asClubber().get().getClub().get().shortName}
                    </#if>
                </#if>
                </td>
            </tr>
        </#list>
        </tbody>
    </table>
    <input type="submit">
    </form>
</div>