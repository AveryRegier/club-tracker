<label for="${presentation.id}" class="list-item selectable" id="label.${presentation.id}">
    <span class="list-item-checkbox">
       <input type="checkbox" name="award" id="${presentation.id}" value="${presentation.id}"
              onchange="var d = document.getElementById('label.${presentation.id}');
                  d.className = this.checked ? 'list-item selected selectable' : 'list-item selectable';">
    </span>
    <div class="list-item-content">
        <div class="list-item-title">${presentation.to().name.fullName}</div>
        <div class="list-item-detail">
            ${presentation.forAccomplishment().book.name}
            ${presentation.forAccomplishment().name}</div>
        <#if !presentation.token().isPresent()>
            <div class="list-item-detail">${presentation.token().get().name}</div>
        </#if>
    </div>
</label>
