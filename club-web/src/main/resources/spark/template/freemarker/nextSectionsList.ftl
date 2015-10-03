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
