
<div>
    <table>
        <thead>
        <th>Name</th>
        <th>Upcoming Sections</th>
        </thead>
        <tbody>
        <#list me.asListener().get().getQuickList() as clubber>
            <tr>
                <td>${clubber.name.fullName}</td>
                <td>
                <#if clubber.nextSection.isPresent()>
                    <#assign defaultSection=clubber.nextSection.get()>
                    <#list clubber.nextSections(3) as record>
                        <#assign section=record.section>
                        <a href="/protected/clubbers/${clubber.id}/sections/${section.id}">
                            <#include "sectionName.ftl">
                        </a>,
                    </#list>
                    ...
                </#if>
                </td>
            </tr>
        </#list>
        </tbody>
    </table>
</div>