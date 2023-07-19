const userInfo = {
    "successful": true,
    "code": 200,
    "msg": "success",
    "data": {
        "id": 0,
        "username": "admin",
        "password": "admin",
        "email": "123456@email.com",
        "phone": "123456",
        "tenantId": 0,
        "createdAt": "",
        "updatedAt": ""
    }
}

export default [
    {
        url: "/hodor/admin/login/",
        method: "post",
        response: () => {
            return userInfo
        }
    },
]