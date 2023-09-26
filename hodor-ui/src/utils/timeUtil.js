import dayjs, {Dayjs} from "dayjs";

// 获取本地时区
function getLocalTime(time) {
    let d = new Date(time);
    let len = d.getTime();
    // 获取当前时区偏移量
    let offset = d.getTimezoneOffset() * 60000;
    let utcTime = len + (0 - offset);
    return new Date(utcTime);
}

export const timeTransfer = (time) => {
    const utcDate = getLocalTime(time);
    return dayjs(utcDate).format('YYYY-MM-DD HH:mm:ss')
}

// 时间戳转换为时间
export const timestampToTime = (timestamp) => {
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

export function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}
