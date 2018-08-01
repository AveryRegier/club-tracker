<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <LINK href="/login.css" rel="stylesheet" type="text/css">
    <title>Club Login</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
    <div id="main">
        <div class="alert alert-info">
        	<strong>Step 1&nbsp;:&nbsp;</strong>
        	Choose which provider you wish to use to login.
        </div>
        <div>
        <#list providers as provider>
	       	<div class="provider">
                <a href="socialauth?id=${provider.id}"><img src="${provider.image}" alt="${provider.name}" title="${provider.name}" border="0"/></a>
            </div>
        </#list>
	       	<div class="provider">
                <a href="/openid"><img src="http://openid.net/logo-graphics/openid-icon-100x100.png" alt="OpenID" title="OpenID" border="0"/></a>
            </div>
        </div>
    </div>
    <div>
        <h1>Club Tracker</h1>
        <p>Club Tracker is a mobile-friendly application that helps you keep track of your involvement in kids clubs.</p>

        <div>
            <div>
                <p><strong>Next Steps</strong></p>
                <p>Please select a login provider above to get started.</p>
                <p>We use Social Auth providers so that you do not have to maintain and remember yet another password.</p>
                <p>If you do not see a login provider you already use, please provide that feedback to the
                    <a href="mailto:avery.regier@gmail.com">site owner</a> about what login providers you
                    would like to use.</p>
                <p>Each login provider will ask you to approve of Club Tracker having access to your basic profile information.</p>
                <p>Club Tracker only uses information gathered from the login provider's profile to pre-fill
                    registration information for your convenience.</p>
                <#if program?exists>
                <p>We will ask you to register with the ${program.shortCode} ${program.name} program after login.</p>
                </#if>
                <p>All personally identifiable information about you or your family that Club Tracker keeps is
                    available for you to update on the registration page, and this page will continue to be
                    accessible to you via your login through the login provider you choose.</p>
                <p>Please see our <a href="/privacy.html">Privacy Policy</a> for how each field is used, and
                    if you have any questions, contact the <a href="mailto:avery.regier@gmail.com">site owner</a>.</p>
                <p>Return to <a href="/">Club Tracker</a> at any time to see your latest status.</p>
            </div>
            <#if program?exists>
            <div>
                <p><strong>Parents</strong></p>
                <p>Club Tracker helps you communicate with the leaders of the ${program.shortCode}
                    ${program.name}  program and the involvement of your family in it.
                </p>
                <p>You will be able to see the progress of your child and what awards they have received,
                    and see notes from their leaders.</p>
                <p>Our future plans involve you being able to send notes back and forth as well.</p>
            </div>

            <div>
                <p><strong>Listeners</strong></p>
                <p>Club Tracker has a quick list for your club group, where you can quickly find the next sections
                    each clubber should complete, and quickly sign them and leave a note for their parents.</p>
                <p>If you have a new clubber, you can find them from the club roster.</p>
                <p>You can see the all the clubber's records as well.</p>
            </div>
            <div>
                <p><strong>Clubbers</strong></p>
                <p>Club Tracker will help you keep track of your status in the ${program.shortCode}
                    ${program.name} program.</p>
                <p>See what next sections you should be preparing for the next club meeting.</p>
            </div>
            </#if>
            <div>
                <p><strong>Club Leadership: Commanders, Directors, Secretaries, and Pastors</strong></p>
                <p>Keep track of family registrations and clubber records.</p>
                <p>Awards are automatically calculated and collected for your nightly or yearly awards ceremonies.</p>
                <p>Customize your program's curriculum to use at your own pace or all together policies.</p>
                <p>Invite leaders and listeners to use the system too.</p>
                <p>As listeners sign off on clubber sections, records are automatically kept up to date.</p>
                <p>Make adjustments and corrections to records on the fly.</p>
                <p>Easily transfer clubbers from other clubs using the catch up features based on the awards you see
                    they've been given.</p>
            </div>

        </div>

        <div>
            <a href="/privacy.html">Privacy Policy</a>
        </div>
    </div>
<#include "footer.ftl">
