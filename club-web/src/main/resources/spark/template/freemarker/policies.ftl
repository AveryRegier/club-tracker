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
                <label>Program:</label>
                <div class="staticField">${club.curriculum.shortCode}</div>
            </div>
            <div class="inputField">
                <label>No Section Awards</label>
                <input type="checkbox" name="policy" id="noSectionAwards" value="noSectionAwards" ${noSectionAwards}>
            </div>
        </div>
    </fieldset>
    <div class="actions">
        <button type='submit'>Submit</button>
    </div>
    </div>
</form>
<#include "footer.ftl">
