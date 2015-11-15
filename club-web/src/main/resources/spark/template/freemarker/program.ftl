<#include "formHeader.ftl">

<form method="post">
    <div class="inputForm">
    <fieldset class="inputGroup">
        <legend class="inputGroupLabel">Setup your club.</legend>
        <div class="inputGroupFields">
            <div class="inputField">
                <label for="organizationName">Organization Name:</label>
                <input type="text" size="50" name="organizationName" id="organizationName" value="${program.shortCode}"/>
            </div>
            <div class="inputField">
                <label>Program:</label>
                <div class="staticField">${program.curriculum.shortCode}</div>
            </div>
            <div class="inputField">
                <label>Clubs</label>
                <div class="staticField">

                    <UL>
                    <#list program.clubs as club>
                        <LI>${club.shortCode}</LI>
                    </#list>
                    </UL>

                </div>
            </div>
            <div class="inputField">
                <label for="addClub">Add another:</label>
                <select name="addClub" id="addClub">
                    <#list program.curriculum.series as club>
                        <option value="${club.id}">${club.shortCode}</option>
                    </#list>
                </select>
            </div>
        </div>
    </fieldset>
    <div class="actions">
        <button type='submit'>Submit</button>
    </div>
    </div>
</form>
<#include "footer.ftl">
