# local build
如果仅仅只是测试，也可以不进行编译，通过vue单独开启进程，不与原先web共用进程，详细流程如下：
1. 搭建安装node环境，如果已经安装，则跳过此步骤；
2. 执行`git pull`拉取最新代码；
3. 命令行进入hodor-ui目录，执行`npm install`，下载npm相关资源；
4. 进入hodor-ui/config/index.js，修改dev.proxyTable.target的值为后台服务端地址，host和port为自身进程的ip与端口号；
5. 在hodor-ui目录下执行命令：`npm run start`；
6. 启动ejob-console访问：`http://ip:port/hodor`，访问成功则说明集成成功；

# release build
如果需要将hodor-ui集成到已有的web中，详细流程如下：
1. 搭建安装node环境，如果已经安装，则跳过此步骤；
2. `git pull` 拉取最新代码；
3. 命令行进入hodor-ui目录，执行`npm install`，下载npm相关资源；
4. 要想集成进原先web，则需要打包编译成js文件，编译命令：`npm run build`；
5. 将dist文件夹中生成的static文件夹及index.html文件移动（复制）到原web工程的webapp目录下；
6. 启动hodor-admin访问：`http://ip:port/hodor`，访问成功则说明集成成功；
