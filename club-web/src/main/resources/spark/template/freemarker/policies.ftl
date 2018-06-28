<#include "formHeader.ftl">

<form method="post">
    <div class="inputForm">
    <fieldset class="inputGroup">
        <legend class="inputGroupLabel">Club Policies</legend>
        <div class="inputGroupFields">
            <div class="inputField">
                <label>Organization Name:</label>
                <div class="staticField">${club.program.shortCode}</div>
            </div>
            <div class="inputField">
                <label>Club:</label>
                <div class="staticField">${club.curriculum.name}</div>
            </div>
            <div class="inputField">
                <label for="noSectionAwards">No Section Awards</label>
                <input type="checkbox" name="policy" id="noSectionAwards" value="noSectionAwards" ${noSectionAwards}>
            </div>
            <div class="inputField">
                <label for="listenerGroupsByGender">Listener Groups by Gender</label>
                <input type="checkbox" name="policy" id="listenerGroupsByGender" value="listenerGroupsByGender" ${listenerGroupsByGender}>
            </div>
        </div>
    </fieldset>

    <#if club.curriculum.series?size != 0>
        <fieldset class="inputGroup">
            <legend class="inputGroupLabel">Customized Book List</legend>
            <div class="inputGroupFields">
                <div class="inputField">
                    <#list club.curriculum.ageGroups as ageGroup>
                        <label for="${ageGroup}-book">${ageGroup.displayName}:</label>
                        <select name="${ageGroup}-book" id="${ageGroup}-book">
                            <#list club.curriculum.allSeries as series>
                                <option value="${series.id}">${series.name}</option>
                            </#list>
                        </select>
                    </#list>
                </div>
            </div>
        </fieldset>
    </#if>

    <div class="actions">
        <button type='submit'>Submit</button>
    </div>
    </div>
</form>
<#include "footer.ftl">
