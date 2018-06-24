<#include "normalHeader.ftl">

    <div class="menu"><a href=/protected/club/${club.id}/workers>Recruit ${club.shortCode} Workers</a></a></div>
    <div class="menu"><a href=/protected/club/${club.id}/policies>Edit ${club.shortCode} Policies</a></a></div>


    <fieldset class="inputGroup">
        <legend class="inputGroupLabel">View your club.</legend>
        <div class="inputGroupFields">
            <div class="inputField">
                <label>Organization Name:</label>
                <div class="staticField">${club.program.shortCode}</div>
            </div>
            <div class="inputField">
                <label>Program:</label>
                <div class="staticField">${club.curriculum.shortCode}</div>
            </div>
            <div class="inputField">
                <label>Listeners</label>
                <div class="staticField">
                    <UL>
                    <#list club.listeners as listener>
                        <LI>
                            <#if listener.club.isPresent() && listener.family.isPresent()>
                                 <a href="/protected/${listener.club.get().program.id}/family/${listener.family.get().id}">${listener.name.fullName}</a>
                            <#else>
                                ${listener.name.fullName}
                            </#if>
                        </LI>
                    </#list>
                    </UL>
                </div>
            </div>
            <div class="inputField">
                <label>Clubbers</label>
                <div>
                    <table>
                        <thead>
                        <th>Name</th>
                        <th>Today's Sections</th>
                        </thead>
                        <tbody>
                        <#list clubbers as entry>
                            <#assign clubber = entry.key>
                            <tr>
                                <td><a href="/protected/clubbers/${clubber.id}/sections">${clubber.name.fullName}</a></td>
                                <td>
                                    <#list entry.value as record>
                                        <#assign section=record.section>
                                        <a href="/protected/clubbers/${clubber.id}/sections/${section.id}">
                                            <#include "sectionName.ftl">
                                        </a>,
                                    </#list>
                                </td>
                            </tr>
                        </#list>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </fieldset>
<#include "footer.ftl">
