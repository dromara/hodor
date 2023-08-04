function getLocalTime(time, timezone) {
    if (typeof timezone !== "number") {
        return new Date(time);
    }
    var d = new Date(time);
    var len = d.getTime();
    var offset = d.getTimezoneOffset() * 60000;
    var utcTime = len + offset;
    return new Date(utcTime + 3600000 * timezone);
}

export const timeTransfer = (createdAt) => {
    const timezone = new Date().getTimezoneOffset() / 60;
    const utcDate=getLocalTime(createdAt, timezone);
    var options = { year: 'numeric', month: 'long', day: 'numeric', hour: 'numeric', minute: 'numeric', second: 'numeric' };
    var formatterEn = new Intl.DateTimeFormat('en-US', options);
    return formatterEn.format(utcDate);
}
