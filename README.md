# k-food-platform · 烹饪学习网站

一个「内容平台」形态的全栈项目：**后台录入课程与媒资，前台浏览、搜索、学习**。
三端分离 —— 门户静态站、创作者后台（管理端）、Spring Cloud 微服务后端。

> **一句话现状**：结构完整、代码读得懂，但**不能直接跑起来** —— 业务配置全在 Nacos、仓库里没有建库脚本、还依赖一整套中间件。
> 定位是**架构样本**（拿来看「一个大型 Web 项目长什么样」），不是可交付的产品。原因见 [为什么跑不起来](#为什么跑不起来)。

## 三端一览

| 部件 | 目录 | 技术 | 作用 | 现状 |
| --- | --- | --- | --- | --- |
| 门户站 | `k-food-user-web/` | 纯静态 HTML + jQuery + Bootstrap | 首页、登录、注册、微信登录、找回密码 | ✅ 起个静态服务就能看（卡片数据是写死的） |
| 管理后台 | `k-food-creator-web/` | Vue 2.6 + TypeScript + element-ui | 课程 / 媒资 / 订单 / 会员 / 工作记录管理 | ⚠️ 缺 `.env`，需 Node 16，**未验证** |
| 后端 | `k-food-java-server/` | Spring Cloud 微服务 | 13 个模块、28 个 Controller | ❌ 缺 Nacos 配置，**起不来** |

## 架构

```
浏览器
 ├─ k-food-user-web      静态页，直连资源
 └─ k-food-creator-web   Vue dev server :8080，/api 反代到网关
              │
        gateway :9999         统一入口 · JWT 校验 · 白名单
              │
  ┌────────┬──┴─────┬────────┬──────────┬────────┐
 auth    content   media   learning    orders   search / system
  └────────┴──┬─────┴────────┴──────────┴────────┘
              │
   MySQL · Redis · Elasticsearch 7.12 · MinIO · RabbitMQ · XXL-Job
              │
      Nacos（注册中心 + 配置中心）
```

## 后端模块

| 模块 | Java 文件 | 职责 |
| --- | ---: | --- |
| `kkbi-gateway` | 5 | 网关：统一入口、JWT 过滤、白名单 |
| `kkbi-auth` | 33 | 认证中心：Spring Security OAuth2、微信登录 |
| `kkbi-content` | 61 | 课程内容：课程基信息、教学计划、课程发布、分类、FreeMarker 静态化 |
| `kkbi-generator` | 49 | MyBatis-Plus 代码生成产物（mapper / model） |
| `kkbi-media` | 31 | 媒资：文件上传、大文件分块、MinIO 存储 |
| `kkbi-base` | 29 | 公共基础：统一异常、工具类、分页 |
| `kkbi-learning` | 29 | 学习中心：我的课程、学习进度 |
| `kkbi-orders` | 23 | 订单与支付（支付回调练习代码） |
| `kkbi-checkcode` | 13 | 验证码：短信 / 邮件 |
| `kkbi-search` | 13 | 课程搜索：ES 索引与查询 |
| `kkbi-message-sdk` | 11 | 消息通知 SDK（短信 / 邮件 / 内容审核） |
| `kkbi-system` | 9 | 系统配置、数据字典 |
| `kkbi-parent` | — | 父 POM，统一版本管理 |

合计 **306 个 Java 文件 / 28 个 Controller / 62 个接口映射**。

## 技术栈

| 层 | 选型 |
| --- | --- |
| 后端框架 | Spring Boot 2.3.7 · Spring Cloud Hoxton.SR9 · Spring Cloud Alibaba 2.2.6 · JDK 8 |
| 注册 / 配置 | Nacos（namespace `dev402`，group `kfood-project`） |
| 数据访问 | MyBatis-Plus 3.4.1 · Druid · MySQL 8 |
| 中间件 | Redis · Elasticsearch 7.12 · MinIO 8.4.3 · RabbitMQ · XXL-Job 2.3.1 |
| 工具 | Lombok · MapStruct · FastJSON · Swagger |
| 管理后台 | Vue 2.6 · TypeScript 3.5 · element-ui 2.11 · vuex-module-decorators · sass · qiniu-js |
| 门户 | HTML + jQuery + Bootstrap（无构建） |

## 为什么跑不起来

按「卡住顺序」排：

1. **配置全在 Nacos，仓库里一条都没有。**
   每个服务只有一份 `bootstrap.yml`，里面只写了 Nacos 地址和 `namespace / group`，真正的数据源、Redis、ES、MinIO、MQ 配置都在 Nacos 上。而且地址硬编码成课程环境的 `192.168.101.65:8848`。

2. **没有建库脚本。**
   全仓找不到一个 `.sql`。表结构只存在于 `kkbi-generator` 生成的实体类里 —— 能反推，但等于要自己重建整套库。

3. **中间件太多。**
   想全链路跑通，得同时起 Nacos、MySQL、Redis、ES、MinIO、RabbitMQ、XXL-Job 七个东西，没有任何 `docker-compose` 帮着编排。

4. **管理后台缺 `.env`。**
   `vue.config.js` 读 `VUE_APP_SERVER_API_URL` / `VUE_APP_CLIENT_MANAGE_PORT`，而根 `.gitignore` 把 `.env*` 全排掉了，所以仓库里从来没有这两个变量。另外 vue-cli 4 + Node 18+ 需要 `NODE_OPTIONS=--openssl-legacy-provider`。

5. **网关鉴权实际是开着的门。**
   `security-whitelist.properties` 第一行就是 `/**=临时全部打开`。

## 想真跑起来？分三档

| 档位 | 做什么 | 成本 | 结果 |
| --- | --- | --- | --- |
| **A. 只看门户** | `cd k-food-user-web && python3 -m http.server 8899` | 0 | 卡片区能看，数据写死；顶部导航和轮播不显示（原因见下） |
| **B. 跑通一个服务** | 挑 `kkbi-media`（依赖最少），把 Nacos 配置改成 `application.yml` 本地文件、Nacos 注册关掉，接本地 MySQL + MinIO | 半天~1 天 | 有**一个**真能跑的服务，够讲清楚「一个微服务怎么组起来」 |
| **C. 全链路** | 补 7 个中间件 + 反推建表 + 补齐 Nacos 全部配置 + 修网关白名单 | 1~2 周 | 真能跑，但性价比很低 |

> 想学东西选 **B**；只想留档就停在 **A** + 这份 README。

## 目录结构

```
k-food-platform/
├── k-food-user-web/             # 门户静态站（42MB，其中 img 24M + plugins 14M）
│   ├── index.html               # 首页
│   ├── sign.html                # 登录
│   ├── register.html            # 注册
│   ├── wxsign.html              # 微信登录
│   ├── findpassword.html        # 找回密码
│   └── course/  ucenter/  css/  js/  img/  plugins/
├── k-food-creator-web/          # 管理后台（Vue2 + TS）
│   ├── src/
│   │   ├── module-portal/       # 模块：门户（登录 / 首页 / 菜单）
│   │   ├── module-organization/ # 模块：机构端（课程/媒资/订单/会员/直播）
│   │   ├── module-personal/     # 模块：个人中心
│   │   ├── module-entering/     # 模块：入驻
│   │   ├── api/                 # 19 个接口封装文件
│   │   ├── store/               # vuex modules
│   │   └── router/index.ts      # 只有空壳，路由由各模块 addRoutes 注入
│   ├── api.http                 # 接口调试样例
│   └── vue.config.js            # devServer 端口 + /api 代理
└── k-food-java-server/          # 微服务后端
    ├── kkbi-parent/pom.xml      # 父 POM（modules 用 ../ 相对引用）
    ├── kkbi-*/                  # 12 个服务模块
    ├── api-test/                # .http 接口调试脚本
    └── md/                      # 踩坑记录（MINIO 异常）
```

## 前端值得一看的设计

管理后台不是「一个路由文件堆所有页面」，而是**按业务模块切分**：

```
src/module-organization/
├── index.ts      → 导出 { install(router) }，内部 router.addRoutes(...)
├── router/       → 本模块路由
├── pages/        → 本模块页面
└── components/   → 本模块组件
```

`main.ts` 里四个模块依次 `install(router)`，把一个空 router 拼装成完整路由表。加一个新业务域 = 加一个目录 + 一行 install，不用改别人的文件。

## License

MIT
