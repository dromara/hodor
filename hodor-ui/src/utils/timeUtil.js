import dayjs, { Dayjs } from "dayjs";

// 获取本地时区
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

// 时间戳转换为时间
export const timestampToTime=(timestamp)=>{
    timestamp = timestamp ? timestamp : null;
    let date = new Date(timestamp);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
    let Y = date.getFullYear() + '-';
    let M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
    let D = (date.getDate() < 10 ? '0' + date.getDate() : date.getDate()) + ' ';
    let h = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':';
    let m = (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()) + ':';
    let s = date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds();
    return Y + M + D + h + m + s;
  }
