# Seemse AI

<div align="center">

### 企业级AI助手平台

*开箱即用的智能AI平台，深度集成 FastGPT、扣子(Coze)、DIFY 等主流AI平台，提供先进的RAG技术和多模型支持*

**[🇺🇸 English](README_EN.md)** | **[📖 使用文档]()** | **[🚀 在线体验]()** | **[🐛 问题反馈]()** | **[💡 功能建议]()**

</div>

## ✨ 核心亮点

### 🤖 智能AI引擎
- **多模型接入**：支持 OpenAI GPT-4、Aure、ChatGLM、通义千问、智谱AI 等主流模型
- **AI平台集成**：深度集成 **FastGPT**、**扣子(Coze)**、**DIFY** 等主流AI应用平台
- **Spring AI MCP 集成**：基于模型上下文协议，打造可扩展的AI工具生态系统
- **实时流式对话**：采用 SSE/WebSocket 技术，提供丝滑的对话体验
- **AI 编程助手**：内置智能代码分析和项目脚手架生成能力

### 🌟 AI平台生态集成
- **FastGPT 深度集成**：原生支持 FastGPT API，包括知识库检索、工作流编排和上下文管理
- **扣子(Coe) 官方SDK**：集成字节跳动扣子平台官方SDK，支持Bot对话和流式响应
- **DIFY 完整兼容**：使用 DIFY Java Client，支持应用编排、工作流和知识库管理
- **统一聊天接口**：提供统一的聊天服务接口，支持多平台无缝切换和负载均衡

### 🧠 本地化RAG方案
- **私有知识库**：基于 Langchain4j 框架 + BGE-large-h-v1.5 中文向量模型
- **多种向量库**：支持 Milvus、Weaviate、Qdrant 等主流向量数据库
- **数据安全可控**：支持完全本地部署，保护企业数据隐私
- **灵活模型部署**：兼容 Ollama、vLLM 等本地推理框架

### 🎨 AI创作工具
- **AI 绘画创作**：深度集成 DALL·E-3、MidJourney、Stable Diffusion
- **智能PPT生成**：一键将文本内容转换为精美演示文稿
- **多模态理解**：支持文本、图片、文档等多种格式的智能处理

## 🛠️ 技术架构

### 🏗️ 核心框架
- **后端架构**：Spring Boot 3.4 + Spring AI + Langchain4j
- **数据存储**：MySQL 8.0 + Redis + 向量数据库（Milvus/Weaviate/Qdrant）
- **前端技术**：Vue 3 + Vben Admin + Naive UI
- **安全认证**：Sa-Token + JWT 双重保障

### 🔧 系统组件
- **文档处理**：PDF、Word、Excel 解析，图像智能分析
- **实时通信**：WebSocket 实时通信，SSE 流式响应
- **系统监控**：完善的日志体系、性能监控、服务健康检查


> 💡 **小贴士**：本项目基于ruoyi-ai项目进行的改造，在其基础上完善了大模型和MCP的集成调用（原ruoyi-ai在缺陷），支持任意数据库、任意API的MCP调用。



