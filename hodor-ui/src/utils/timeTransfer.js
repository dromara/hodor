import dayjs, { Dayjs } from "dayjs";

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

export const timeTransfer = (time) => {
    const timezone = new Date().getTimezoneOffset() / 60;
    const utcDate=getLocalTime(time, timezone);
    return dayjs(utcDate).format('YYYY-MM-DD HH:mm:ss')
}
