
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
                    <td>${clubber.club.get().shortName}</td>
                <#else>
                    <td></td>
                </#if>

            </tr>
        </#list>
        </tbody>
    </table>
</div>