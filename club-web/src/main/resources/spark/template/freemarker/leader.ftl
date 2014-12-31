
Leader
<div class="menu"><a href="/protected/setup">Club Setup</a></div>
<#assign group=me.asClubLeader().get().club.get()>
<div class="menu"><a href="/protected/club/${group.shortName}">Recruit Listeners</a></div>

<#include "clubberStatus.ftl">
