
<div>
    <table>
        <thead>
        <th>Name</th>
        <th>Next Section</th>
        </thead>
        <tbody>
        <#list me.asListener().get().getQuickList() as clubber>
            <tr>
                <td>${clubber.name.fullName}</td>
                <#if clubber.nextSection.isPresent()>
                    <#assign section=clubber.nextSection.get()>

                    <td><a href="/protected/clubbers/${clubber.id}/sections/${section.id}">
                        <#include "sectionName.ftl">
                    </a></td>
                <#else>
                    <td></td>
                </#if>

            </tr>
        </#list>
        </tbody>
    </table>
</div>