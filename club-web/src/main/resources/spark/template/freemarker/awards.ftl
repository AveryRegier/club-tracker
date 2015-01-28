<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <LINK href="/main.css" rel="stylesheet" type="text/css">
    <title>${club.shortName} Awards</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
    <form method="post">
        <header>${club.shortName} Awards</header>
        <div class="list">
            <header class="list-header">
                <div class="list-item-checkbox"></div>
                <nav class="list-item-content">
                    <button name="submit" type='submit' value="submit" class="primary">Mark Presented</button>
                </nav>
            </header>
            <#list club.awardsNotYetPresented as presentation>
                <label for="${presentation.id}" class="list-item selectable" id="label.${presentation.id}">
                    <span class="list-item-checkbox">
                       <input type="checkbox" name="award" id="${presentation.id}" value="${presentation.id}"
                              onchange="var d = document.getElementById('label.${presentation.id}');
                                  d.className = this.checked ? 'list-item selected selectable' : 'list-item selectable';">
                    </span>
                    <div class="list-item-content">
                        <div class="list-item-title">${presentation.to().name.fullName}</div>
                        <div class="list-item-detail">${presentation.forAccomplishment().name}</div>
                        <#if !presentation.token().isPresent()>
                            <div class="list-item-detail">${presentation.token().get().name}</div>
                        </#if>
                    </div>
                </label>
            </#list>
        </div>
    </form>
</body>
</html>