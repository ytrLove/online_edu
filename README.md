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

## 快速开始

### 方式一：本地开发环境

#### 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.8+

#### 1. 配置环境变量

复制 `.env.example` 为 `.env` 并填入实际配置：

```bash
cp .env.example .env
```

编辑 `.env` 文件：

```env
# OpenAI API Key
OPENAI_API_KEY=your_api_key_here

# JWT Secret
JWT_SECRET=your_jwt_secret_here

# MinIO Configuration
MINIO_URL=http://your_minio_host:9000
MINIO_ACCESS_KEY=your_access_key
MINIO_SECRET_KEY=your_secret_key

# MySQL Configuration
MYSQL_USERNAME=your_mysql_username
MYSQL_PASSWORD=your_mysql_password
```

#### 2. 启动后端

```bash
cd backend/online_edu

# 安装依赖
mvn clean install

# 运行项目
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动。

#### 3. 启动前端

**教师端管理后台：**

```bash
cd frontend/admin
pnpm install
pnpm dev
```

**学生端前台：**

```bash
cd frontend/user
pnpm install
pnpm dev
```

---

### 方式二：Docker 一键部署（推荐）

#### 前置要求

- Docker 20.10+
- Docker Compose 2.0+

#### 1. 配置环境变量

```bash
cp .env.example .env
```

编辑 `.env` 文件配置数据库密码、JWT 密钥等：

```env
MYSQL_ROOT_PASSWORD=root
MYSQL_USERNAME=edu_user
MYSQL_PASSWORD=edu_password
JWT_SECRET=your_jwt_secret_here
OPENAI_API_KEY=your_api_key_here
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin
```

#### 2. 构建前端

```bash
# 构建管理后台
cd frontend/admin
pnpm install
pnpm build

# 构建学生端
cd ../user
pnpm install
pnpm build
```

#### 3. 启动所有服务

```bash
# 在项目根目录执行
docker-compose up -d
```

首次启动会自动：
- 拉取 MySQL、Redis、MinIO、Nginx 镜像
- 构建后端应用镜像
- 初始化数据库（执行 `database_scripts/14_full_db_init.sql`）
- 创建数据卷持久化存储

#### 4. 查看服务状态

```bash
docker-compose ps
```

#### 5. 访问服务

| 服务 | 地址 | 说明 |
|------|------|------|
| 教师端管理后台 | http://localhost:8081 | 管理课程、考试、用户 |
| 学生端前台 | http://localhost:8082 | 学生学习入口 |
| 后端 API | http://localhost:8080/api | 接口服务 |
| MinIO 控制台 | http://localhost:9001 | 文件存储管理 |
| MySQL | localhost:3306 | 数据库 |
| Redis | localhost:6379 | 缓存服务 |

#### 6. 常用命令

```bash
# 停止所有服务
docker-compose down

# 停止并删除数据卷
docker-compose down -v

# 查看日志
docker-compose logs -f backend

# 重启某个服务
docker-compose restart backend

# 重新构建并启动
docker-compose up -d --build
```

### Docker 服务架构

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

## API 文档

启动后端服务后，访问 `http://localhost:8080/swagger-ui.html` 查看 API 文档。

## 功能模块说明

### 课程管理

- 课程创建与编辑
- 章节课时管理
- 教学资源关联
- 学生选课退课

### 考试系统

- 题库管理（单选、多选、判断、简答）
- 试卷自动/手动组卷
- 在线考试与作业
- 成绩统计与分析

### 学习统计

- 学习行为追踪
- 课程学习进度
- 成绩分析报告
- 教学效果评估

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
