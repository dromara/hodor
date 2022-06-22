export const getDateToMinute = (date) => {
    let dataStr = "";
    if (date) {
        var date = new Date(date);
        dataStr = date.getFullYear() + '-' + (date.getMonth() + 1) + '-'
            + date.getDate() + '  ' + date.getHours() + ':' + date.getMinutes();
    }
    return dataStr;
}

export const getDate = (date) => {
    let dataStr = "";
    if (date) {
        var date = new Date(date);
        dataStr = date.getFullYear() + '-' + (date.getMonth() + 1) + '-'
            + date.getDate();
    }
    return dataStr;
}
