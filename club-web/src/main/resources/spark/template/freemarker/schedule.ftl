<#include "preFormHeader.ftl">
    <#include "schedule.js">
<#include "postFormHeader.ftl">

<form method="post">
    <div class="inputForm">
    <fieldset class="inputGroup">
        <legend class="inputGroupLabel">Generate Schedule</legend>
        <div class="inputGroupFields">
            <div class="inputField">
                <label>Organization Name:</label>
                <div class="staticField">${program.shortCode}</div>
            </div>
        </div>
        <div class="inputGroupFields">
            <div class="inputField">
                <label for="startDate">First Week:</label>
                <input type="date" name="startDate" id="startDate" value="" onfocus="this.select()"/>
            </div>
        </div>
        <div class="inputGroupFields">
            <div class="inputField">
                <label for="endDate">Last Week:</label>
                <input type="date" name="endDate" id="endDate" value="" onfocus="this.select()"/>
            </div>
        </div>
    </fieldset>

    <fieldset class="inputGroup">
        <legend class="inputGroupLabel">Schedule</legend>
        <div id="schedule" class="inputGroupFields">
        </div>
    </fieldset>

    <div class="actions">
        <button type='submit'>Submit</button>
    </div>
    </div>
</form>
<#include "footer.ftl">
