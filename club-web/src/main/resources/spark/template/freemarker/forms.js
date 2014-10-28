addEvent(window, 'load', function() {
  for (var i = 0; document.forms[0].elements[i].type == 'hidden'; i++);
  document.forms[0].elements[i].focus();
});

function addEvent(obj, evType, fn){
  if (obj.addEventListener){
     obj.addEventListener(evType, fn, true);
     return true;
  } else if (obj.attachEvent){
     var r = obj.attachEvent("on"+evType, fn);
     return r;
  } else {
     return false;
  }
}


function getLabelForId(id) {
    var label, labels = document.getElementsByTagName('label');
    for (var i = 0; (label = labels[i]); i++) {
        if (label.htmlFor == id) {
            return label;
        }
    }
    return false;
}
function checkEmail() {
    var email = document.getElementById('email');
    var label = getLabelForId('email');
    if (email.value.indexOf('@') == -1 || email == '') { // Naive check for non empty string with @ sign
        label.className = 'problem';
    } else {
        label.className = 'completed';
    }
}
function checkPhone() {
    var phone = document.getElementById('phone');
    var label = getLabelForId('phone');
    var digits = phone.value.replace(/[^0-9]/ig, '');
    if (!digits) {
        label.className = '';
        phone.value = '';
        return;
    }
    if (digits.length == 10) {
        phone.value = '(' + digits.substring(0, 3) + ') ' +
            digits.substring(3, 6) + '-' +
            digits.substring(6, 10);
        label.className = 'completed';
    } else {
        label.className = 'problem';
        phone.value = digits;
    }
}
function checkRequired(id) {
    var formfield = document.getElementById(id);
    var label = getLabelForId(id);
    if (formfield.value.length == 0) {
        label.className = 'problem';
    } else {
        label.className = 'completed';
    }
}

addEvent(window, 'load', function() {
    var input;
    var inputs = document.getElementsByTagName('input');
    for (var i = 0; (input = inputs[i]); i++) {
        addEvent(input, 'focus', oninputfocus);
        addEvent(input, 'blur', oninputblur);
    }
    var textareas = document.getElementsByTagName('textarea');
    for (var i = 0; (textarea = textareas[i]); i++) {
        addEvent(textarea, 'focus', oninputfocus);
        addEvent(textarea, 'blur', oninputblur);
    }
});
function oninputfocus(e) {
    /* Cookie-cutter code to find the source of the event */
    if (typeof e == 'undefined') {
        var e = window.event;
    }
    var source;
    if (typeof e.target != 'undefined') {
        source = e.target;
    } else if (typeof e.srcElement != 'undefined') {
        source = e.srcElement;
    } else {
        return;
    }
    /* End cookie-cutter code */
    source.style.border='2px solid #000';
}
function oninputblur(e) {
    /* Cookie-cutter code to find the source of the event */
    if (typeof e == 'undefined') {
        var e = window.event;
    }
    var source;
    if (typeof e.target != 'undefined') {
        source = e.target;
    } else if (typeof e.srcElement != 'undefined') {
        source = e.srcElement;
    } else {
        return;
    }
    /* End cookie-cutter code */
    source.style.border='2px solid #ccc';
}
function addEvent(obj, evType, fn){
    if (obj.addEventListener){
        obj.addEventListener(evType, fn, true);
        return true;
    } else if (obj.attachEvent){
        var r = obj.attachEvent("on"+evType, fn);
        return r;
    } else {
        return false;
    }
}
