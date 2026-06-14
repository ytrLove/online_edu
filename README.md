# Online Education Platform

一个功能完整的在线教育平台，支持课程管理、在线考试、资源管理、学习统计等功能。

## 项目概述

本平台采用前后端分离架构，为教师和学生提供在线教学与学习的完整解决方案。

### 主要功能

- **用户系统**：注册登录、角色权限、菜单管理
- **课程管理**：课程创建、章节课时、选课管理
- **考试系统**：试卷组卷、题库管理、作业布置、自动批改
- **资源管理**：教学资源上传下载、分类管理
- **讨论论坛**：课程讨论、话题互动
- **学习统计**：学习行为分析、成绩统计、教学报告
- **AI 助手**：基于 DeepSeek 的智能问答

## 技术栈

### 后端

- Java 17 + Spring Boot 3
- Spring Security + JWT 认证
- MyBatis-Plus
- MySQL + Redis
- MinIO 对象存储

### 前端

- Vue 3 + Vite
- Element Plus
- Tailwind CSS
- Pinia 状态管理

## 项目结构

```
├── backend/online_edu/          # 后端服务
│   └── src/main/java/
│       ├── com.user/            # 用户认证模块
│       ├── com.course/          # 课程管理模块
│       ├── com.exam/            # 考试系统模块
│       ├── com.comm/            # 讨论论坛模块
│       ├── com.stat/            # 学习统计模块
│       └── com.common/          # 公共模块
│
├── frontend/
│   ├── admin/                   # 教师端管理后台
│   └── user/                    # 学生端前台
│
├── nginx/                       # Nginx 配置
│   ├── admin.conf               # 管理后台配置
│   └── user.conf                # 学生端配置
│
├── docker-compose.yml           # Docker 编排文件
└── .env.example                 # 环境变量模板
```

---

## 开发环境搭建

### 1. 安装 JDK 17

**下载地址：** https://adoptium.net/temurin/releases/?version=17

**安装步骤：**

1. 选择操作系统（Windows/macOS/Linux）
2. 下载 JDK 17 安装包（.msi / .pkg / .tar.gz）
3. 运行安装程序，按默认选项安装

**配置环境变量（Windows）：**

1. 右键「此电脑」→ 属性 → 高级系统设置 → 环境变量
2. 新建系统变量：
   - 变量名：`JAVA_HOME`
   - 变量值：`C:\Program Files\Eclipse Adoptium\jdk-17.x.x`（根据实际安装路径）
3. 编辑 `Path` 变量，添加：`%JAVA_HOME%\bin`

**验证安装：**

```bash
java -version
# 输出应为：openjdk version "17.x.x"
```

---

### 2. 安装 Node.js 18+

**下载地址：** https://nodejs.org/

**安装步骤：**

1. 下载 LTS 版本（18.x 或更高）
2. 运行安装程序，按默认选项安装
3. 安装完成后自动包含 npm

**验证安装：**

```bash
node -v    # 输出应为：v18.x.x 或更高
npm -v     # 输出应为：9.x.x 或更高
```

**安装 pnpm（推荐的包管理器）：**

```bash
npm install -g pnpm

# 验证
pnpm -v
```

---

### 3. 安装 MySQL 8.0

**下载地址：** https://dev.mysql.com/downloads/mysql/

**安装步骤：**

1. 选择操作系统，下载对应安装包
2. Windows：下载 MySQL Installer
3. 运行安装程序，选择「Developer Default」或「Custom」
4. 设置 root 密码（建议设置为 `root` 或自定义强密码）
5. 完成安装后启动 MySQL 服务

**创建数据库：**

打开 MySQL 命令行或 MySQL Workbench，执行：

```sql
CREATE DATABASE new_online_edu CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

**验证安装：**

```bash
mysql -u root -p
# 输入密码后进入 MySQL 命令行
SHOW DATABASES;
```

---

### 4. 安装 Redis

**下载地址：** https://redis.io/download

**Windows 用户：**
- 下载地址：https://github.com/tporadowski/redis/releases
- 下载 `.msi` 安装包并安装

**macOS 用户（Homebrew）：**

```bash
brew install redis
brew services start redis
```

**Linux 用户（Ubuntu/Debian）：**

```bash
sudo apt update
sudo apt install redis-server
sudo systemctl start redis
```

**验证安装：**

```bash
redis-cli ping
# 输出应为：PONG
```

---

### 5. 安装 Maven 3.8+

**下载地址：** https://maven.apache.org/download.cgi

**安装步骤：**

1. 下载 Binary zip 文件
2. 解压到目录（如 `C:\apache-maven-3.9.x`）

**配置环境变量（Windows）：**

1. 新建系统变量：
   - 变量名：`MAVEN_HOME`
   - 变量值：`C:\apache-maven-3.9.x`
2. 编辑 `Path` 变量，添加：`%MAVEN_HOME%\bin`

**验证安装：**

```bash
mvn -v
# 输出应为：Apache Maven 3.9.x
```

**配置国内镜像（加速依赖下载）：**

编辑 `~/.m2/settings.xml`，添加阿里云镜像：

```xml
<mirrors>
  <mirror>
    <id>aliyunmaven</id>
    <mirrorOf>central</mirrorOf>
    <name>阿里云公共仓库</name>
    <url>https://maven.aliyun.com/repository/public</url>
  </mirror>
</mirrors>
```

---

### 6. 安装 Git（可选）

**下载地址：** https://git-scm.com/download

**验证安装：**

```bash
git --version
```

---

### 7. 安装 IDE（推荐）

**后端开发（二选一）：**

| IDE | 下载地址 | 说明 |
|-----|----------|------|
| IntelliJ IDEA | https://www.jetbrains.com/idea/download/ | 推荐 Community 版（免费） |
| Eclipse | https://www.eclipse.org/downloads/ | 选择 Java Developers 版本 |

**前端开发（二选一）：**

| IDE | 下载地址 | 说明 |
|-----|----------|------|
| VS Code | https://code.visualstudio.com/ | 轻量级，插件丰富 |
| WebStorm | https://www.jetbrains.com/webstorm/download/ | JetBrains 出品，功能强大 |

---

## 本地项目配置

### 1. 克隆项目

```bash
git clone https://github.com/ytrLove/online_edu.git
cd online_edu
```

### 2. 配置环境变量

复制环境变量模板：

```bash
# Windows
copy .env.example .env

# macOS/Linux
cp .env.example .env
```

编辑 `.env` 文件，填入你的实际配置：

```env
# MySQL 配置
MYSQL_ROOT_PASSWORD=root          # MySQL root 密码
MYSQL_USERNAME=edu_user           # 数据库用户名
MYSQL_PASSWORD=edu_password       # 数据库密码

# JWT 密钥（用于用户认证，可自定义）
JWT_SECRET=your_random_secret_key_here

# OpenAI API Key（AI 助手功能，可选）
OPENAI_API_KEY=sk-xxxxxxxxxxxx

# MinIO 配置（文件存储，本地开发可使用默认值）
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin
```

### 3. 初始化数据库

使用 MySQL 客户端执行初始化脚本：

```bash
# 方式一：命令行
mysql -u root -p new_online_edu < backend/online_edu/database_scripts/14_full_db_init.sql

# 方式二：使用 MySQL Workbench
# 打开文件 backend/online_edu/database_scripts/14_full_db_init.sql
# 选择 new_online_edu 数据库执行
```

### 4. 启动后端服务

```bash
cd backend/online_edu

# 清理并安装依赖
mvn clean install

# 启动服务
mvn spring-boot:run
```

**验证后端启动：**

- 访问 http://localhost:8080
- API 文档：http://localhost:8080/swagger-ui.html

### 5. 启动前端服务

**教师端管理后台：**

```bash
cd frontend/admin

# 安装依赖
pnpm install

# 启动开发服务器
pnpm dev
```

访问 http://localhost:5173

**学生端前台：**

```bash
cd frontend/user

# 安装依赖
pnpm install

# 启动开发服务器
pnpm dev
```

访问 http://localhost:5174（或终端显示的地址）

---

## Docker 一键部署（推荐）

### 前置要求

- Docker 20.10+：https://docs.docker.com/get-docker/
- Docker Compose 2.0+：https://docs.docker.com/compose/install/

### 部署步骤

**1. 配置环境变量**

```bash
cp .env.example .env
# 编辑 .env 文件填入配置
```

**2. 构建前端**

```bash
cd frontend/admin
pnpm install && pnpm build

cd ../user
pnpm install && pnpm build
```

**3. 启动所有服务**

```bash
docker-compose up -d
```

**4. 查看服务状态**

```bash
docker-compose ps
```

**5. 访问服务**

| 服务 | 地址 | 说明 |
|------|------|------|
| 教师端管理后台 | http://localhost:8081 | 管理课程、考试、用户 |
| 学生端前台 | http://localhost:8082 | 学生学习入口 |
| 后端 API | http://localhost:8080/api | 接口服务 |
| MinIO 控制台 | http://localhost:9001 | 文件存储管理 |

### Docker 常用命令

```bash
# 停止所有服务
docker-compose down

# 停止并删除数据卷（清空数据）
docker-compose down -v

# 查看后端日志
docker-compose logs -f backend

# 重启某个服务
docker-compose restart backend

# 重新构建并启动
docker-compose up -d --build
```

---

## 服务架构

```
                    ┌─────────────────┐
                    │   Nginx Proxy   │
                    │  (Port 8081/8082)│
                    └────────┬────────┘
                             │
         ┌───────────────────┴───────────────────┐
         │                                       │
         ▼                                       ▼
┌─────────────────┐                   ┌─────────────────┐
│   Admin 前端    │                   │    User 前端    │
│  (静态文件)     │                   │   (静态文件)    │
└─────────────────┘                   └─────────────────┘
         │                                       │
         └───────────────────┬───────────────────┘
                             │ API Request
                             ▼
                    ┌─────────────────┐
                    │  Backend API    │
                    │  (Port 8080)    │
                    └────────┬────────┘
                             │
         ┌───────────────────┼───────────────────┐
         │                   │                   │
         ▼                   ▼                   ▼
┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│      MySQL      │ │      Redis      │ │      MinIO      │
│   (Port 3306)   │ │   (Port 6379)   │ │   (Port 9000)   │
└─────────────────┘ └─────────────────┘ └─────────────────┘
```

---

## 常见问题

### Q1: Maven 下载依赖很慢？

配置阿里云镜像，参考上方「安装 Maven」部分的配置说明。

### Q2: 前端安装依赖报错？

```bash
# 清除缓存重新安装
rm -rf node_modules pnpm-lock.yaml
pnpm install
```

### Q3: 端口被占用？

```bash
# Windows 查找占用端口的进程
netstat -ano | findstr :8080

# 结束进程
taskkill /PID <进程ID> /F
```

### Q4: MySQL 连接失败？

1. 确认 MySQL 服务已启动
2. 检查用户名密码是否正确
3. 确认 `new_online_edu` 数据库已创建

### Q5: Redis 连接失败？

1. 确认 Redis 服务已启动
2. 默认端口 6379，无需密码

---

## 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

- 项目地址：https://github.com/ytrLove/online_edu
