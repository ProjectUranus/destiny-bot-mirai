# Destiny Bot - Mirai 版

![](https://socialify.git.ci/ProjectUranus/destiny-bot-mirai/image?description=1&descriptionEditable=A%20enhanced%20QQ%20bot&font=Inter&language=1&name=1&owner=1&pattern=Floating%20Cogs&stargazers=1&theme=Light)

LG 自用机器人。

## 结构

项目采用 Jigsaw 并使用 ServiceLoader 加载模块和插件。

`net.origind.destinybot.api` 模块使用最少的依赖，负责日志，插件抽象，命令解析等基础代码。

`net.origind.destinybot.features` 模块实现机器人的绝大部分功能，这些功能不需要依赖 QQ 或 Mirai 本身。

`net.origind.destinybot.core` 模块实现与 Mirai 的交互，账号登陆，守护进程，也包括机器人的管理命令与帮助命令。

## 功能

- [X] 命运2相关功能
  - [ ] Perk 查询
  - [X] 用户信息查询
  - [X] 传说故事查询
  - [X] 用户信息搜索
- [X] Minecraft 相关功能
  - [X] 服务器 Ping
  - [X] https://howoldisminecraft1710.today/ (发送/1710)
- [X] 哔哩哔哩相关功能
  - [X] 下饭主播
  - [ ] 查成分
- [X] Apex Legends 相关功能
  - [X] 开盒
  - [X] 地图轮换
- [X] GitHub 相关功能
  - [X] 查询最近 Commit
- [X] Injdk 功能
- [ ] Instatus 功能
  - [ ] 快速增加警告信息
- [X] 管理功能
  - [X] 更改配置
  - [X] reload
  
## 使用

`.\gradlew distZip` 打包所有需要的文件。解压后复制 `config-example.toml` 为 `config.toml` 并进行必要的配置修改，之后执行 `bin/destinybot` 即可。

## 协议

[GPLv3](LICENSE)