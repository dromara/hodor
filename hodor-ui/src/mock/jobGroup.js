import Mock from "mockjs"

const preUrlPath = "/hodor/admin/group";

// mock任务分组列表
const generateGroupList = () => {
    const groupList = [];
    for (let i = 0; i < 10; i++) {
        const listItem = Mock.mock({
            'id': '@id',
            'groupName': '@name',
            "createUser": () => {
                const options = ["admin", "user"];
                const randomIndex = Mock.Random.integer(0, 1);
                return options[randomIndex];
            },
            'userId': '@id',
            'tenantId': '@email',
            "remark": "",
            "createdAt": "@datetime()",
            "updatedAt": "@datetime()",
        });
        groupList.push(listItem);
    }
    return groupList;
}
let groupList = generateGroupList();
// 获取分页List
const groupListPaging = ({ pageNo, pageSize }) => {
    return groupList.filter((item, index) => index < Number(pageSize) * Number(pageNo) && index >= Number(pageSize) * (Number(pageNo) - 1));
}
// 响应信息
const response = {
    queryGroupListPaging: ({ pageNo, pageSize }) => {
        pageNo = parseInt(pageNo);
        pageSize = parseInt(pageSize);
        return {
            successful: true,
            code: 200,
            msg: "",
            data: {
                total: groupList.length,
                pageSize,
                totalPage: Math.ceil(total / pageSize),
                currentPage: 1,
                pageNo,
                rows: groupListPaging({ pageNo, pageSize })
            },
        }
    },
    createGroup: (params) => {
        groupList.push(params);
        return {
            "successful": true,
            "code": 200,
            "msg": "成功",
            "data": params
        }
    },
    updateGroup: (params) => {
        const { id } = params;
        if (id) {
            groupList.some((item) => {
                if (item.id === id) {
                    item = {
                        ...item,
                        ...params
                    }
                }
            })
        }
        return {
            "successful": true,
            "code": 0,
            "msg": "",
            "data": params
        }
    },
    deleteGroup: (id) => {
        return {
            "successful": true,
            "code": 200,
            "msg": "",
            "data": groupList.filter((item) => item.id === id)
        }
    },
    queryGroupListById: (id) => {
        return {
            "successful": true,
            "code": 0,
            "msg": "",
            "data": groupList.filter((item)=>item.id===id)
        }
    }
}

export default [
    {
        url: `${preUrlPath}?pageNo=${pageNo}&pageSize=${pageSize}`,
        method: "get",
        response: ({ pageNo, pageSize }) => {
            return response.queryGroupListPaging({ pageNo, pageSize })
        }
    },
    {
        url: `${preUrlPath}`,
        method: "post",
        response: (params)=>{
            return response.createGroup(params)
        }
    },
    {
        url: `${preUrlPath}`,
        method: "put",
        response: (params)=>{
            return response.updateGroup(params)
        }
    },
    {
        url: `${preUrlPath}?id=${id}`,
        method: "delete",
        response: (id)=>{
            return response.deleteGroup(id)
        }
    },
    {
        url: `${preUrlPath}/${id}`,
        method: "get",
        response: (id)=>{
            return response.queryGroupListById(id)
        }
    },
]
