addEvent(window, 'load', function() {
  for (var i = 0; document.forms[0].elements[i]; i++) {
    var element = document.forms[0].elements[i];
    if(element.type == 'date') {
        addEvent(element, "change", function() {
            console.log(this.value); //e.g. 2015-11-13
            console.log(new Date(this.value)); //e.g. Fri Nov 13 2015 00:00:00 GMT+0000 (GMT Standard Time)
            buildSchedule(
                document.getElementById("startDate"),
                document.getElementById("endDate"),
                document.getElementById("schedule"));
        });
    }
  }
});

function buildSchedule(startDateElm, endDateElm, target) {
    var startDate = new Date(startDateElm.value);
    var endDate = new Date(endDateElm.value);

    if(startDate && endDate) {
        target.innerHTML = "";
        var current = startDate;
        var week = 1;
        while(dates.inRange(current, startDate, endDate)) {
            addRow(target, current, week++);
            current = nextWeek(current);
        }
    }
}

function nextWeek(date) {
    var temp = new Date(date);
    temp.setDate(date.getDate() + 7);
    return temp;
}

function addRow(target, date, num) {
    var div = document.createElement('div');

    div.className = 'row';

    div.innerHTML =
        '<div class="inputField">\
        <label for="week'+num+'">Week '+num+':</label>\
        <input id="week'+num+'" type="date" name="week'+num+'" value="'+dates.localDay(date)+'" />\
        <input type="button" value="X" onclick="removeRow(\''+target.id+'\', this.parentNode)">\
        </div>';

    target.appendChild(div);
}

function removeRow(target, input) {
    document.getElementById(target).removeChild(input.parentNode);
}

// Source: http://stackoverflow.com/questions/497790
var dates = {
    convert:function(d) {
        // Converts the date in d to a date-object. The input can be:
        //   a date object: returned without modification
        //  an array      : Interpreted as [year,month,day]. NOTE: month is 0-11.
        //   a number     : Interpreted as number of milliseconds
        //                  since 1 Jan 1970 (a timestamp)
        //   a string     : Any format supported by the javascript engine, like
        //                  "YYYY/MM/DD", "MM/DD/YYYY", "Jan 31 2009" etc.
        //  an object     : Interpreted as an object with year, month and date
        //                  attributes.  **NOTE** month is 0-11.
        return (
            d.constructor === Date ? d :
            d.constructor === Array ? new Date(d[0],d[1],d[2]) :
            d.constructor === Number ? new Date(d) :
            d.constructor === String ? new Date(d) :
            typeof d === "object" ? new Date(d.year,d.month,d.date) :
            NaN
        );
    },
    compare:function(a,b) {
        // Compare two dates (could be of any type supported by the convert
        // function above) and returns:
        //  -1 : if a < b
        //   0 : if a = b
        //   1 : if a > b
        // NaN : if a or b is an illegal date
        // NOTE: The code inside isFinite does an assignment (=).
        return (
            isFinite(a=this.convert(a).valueOf()) &&
            isFinite(b=this.convert(b).valueOf()) ?
            (a>b)-(a<b) :
            NaN
        );
    },
    inRange:function(d,start,end) {
        // Checks if date in d is between dates in start and end.
        // Returns a boolean or NaN:
        //    true  : if d is between start and end (inclusive)
        //    false : if d is before start or after end
        //    NaN   : if one or more of the dates is illegal.
        // NOTE: The code inside isFinite does an assignment (=).
       return (
            isFinite(d=this.convert(d).valueOf()) &&
            isFinite(start=this.convert(start).valueOf()) &&
            isFinite(end=this.convert(end).valueOf()) ?
            start <= d && d <= end :
            NaN
        );
    },
    // source: https://stackoverflow.com/questions/23593052/format-javascript-date-to-yyyy-mm-dd/45852520#45852520
    localDay:function(time) {
//        var minutesOffset = time.getTimezoneOffset()
//        var millisecondsOffset = minutesOffset*60*1000
//        var local = new Date(time - millisecondsOffset)
        return time.toISOString().substr(0, 10)
    }
}
