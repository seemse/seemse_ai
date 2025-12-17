# Seemse AI

<div align="center">

### Enterprise-Grade AI Assistant Platform

*Production-ready AI platform with deep integration of FastGPT, Coze, DIFY, and advanced RAG technology*

**[📖 Chinese README](README.md)** | **[📚 Documentation]()** | **[🚀 Live Demo]()** | **[🐛 Report Bug]()** | **[💡 Request Feature]()**

</div>

## ✨ Key Features

### 🔐 Tiered-Permission Knowledge Base
An enterprise-grade knowledge hub designed for internal teams, integrating **knowledge storage**, **management**, **retrieval**, and **fine-grained access control** in one system. Ensures data security from both **user dimension** and **content dimension**.

- **🏗️ Core Support Layer**: Foundational system capabilities
- **☁️ Cloud LLM Module**: Provides semantic understanding and content generation for knowledge-based Q&A. Supports integration with cloud LLMs such as **Qwen**, **Deepseek**, and **Doubao**, with flexible **model switching** or **combination** based on business needs

- **⚙️ Core Function Layer**: Core business logic and permission control
- **📚 Knowledge Base Management Module**: Handles document ingestion, processing, and storage to serve as the **data foundation** for Q&A
- **🔌 External Data Integration Module**: Connects to external business systems to enrich knowledge sources; synchronizes real-time business data into the knowledge base via **MCP interfaces**
- **🛡️ Admin Management Module (Central Control Point)**:
    - **Permission Management**: Enforces security from **user** and **content** perspectives. Supports granular access policies by **department**, **role**, **user tag**, or **content filter**. Permissions are set by the **uploader** or **authorized content owner**. Allows explicit configuration to **allow or deny specific users/departments from querying certain types of information**. **Critical attention must be paid to prevent information leakage via LLMs.**
    - **📤 Document Upload**: Centralized upload interface in admin panel; also supports authorized users uploading documents via the frontend. All content automatically **inherits defined permission policies**

- **💬 User Interaction Layer**: Direct user-facing interfaces
- **🖥️ User Portal**: Standard interface for end users. Offers **text-based Q&A** and supports **document upload** (subject to permission checks)
- **🎤 Voice Integration**: Provides **Text-to-Speech (TTS)** and **Speech-to-Text (ASR)** capabilities, extending knowledge base Q&A to **voice interaction scenarios**, suitable for meetings, customer service, mobile apps, etc.

### 🤖 Advanced AI Engine
- **Multi-Model Support**: OpenAI GPT-4, Aure, ChatGLM, Qwen, ZhipuAI
- **AI Platform Integration**: Deep integration with **FastGPT**, **Coze**, **DIFY** and other leading AI platforms
- **Spring AI MCP Integration**: Extensible tool ecosystem based on **Model Context Protocol (MCP)**
- **Streaming Chat**: Powered by **SSE/WebSocket** for smooth conversational experience
- **AI Copilot**: Built-in intelligent code analysis and project scaffolding

### 🌟 AI Platform Ecosystem
- **FastGPT Deep Integration**: Native support for **FastGPT API**, including knowledge retrieval, workflow orchestration, and context management
- **Coze Official SDK**: Integrated with ByteDance’s **Coze platform** official SDK, supporting Bot conversations and streaming responses
- **DIFY Full Compatibility**: Uses **DIFY Java Client** for app orchestration, workflows, and knowledge base management
- **Unified Chat Interface**: Standardized chat service interface enabling seamless platform switching and load balancing

### 🧠 Enterprise RAG Solution
- **Private Knowledge Base**: Built on **Langchain4j** framework + **BGE-large-h-v1.5** Chinese embedding model
- **Vector Database Support**: Compatible with **Milvus**, **Weaviate**, **Qdrant**
- **Data Privacy Guaranteed**: Supports **fully on-premise deployment** to protect enterprise data
- **Flexible Model Deployment**: Works with local inference frameworks like **Ollama** and **vLLM**

### 🎨 Creative AI Tools
- **AI Art Generation**: Deep integration with **DALL·E-3**, **MidJourney**, **Stable Diffusion**
- **Smart PPT Creation**: One-click conversion of text into polished presentation slides
- **Multi-Modal Understanding**: Intelligent processing of **text**, **images**, and **documents**

## 🛠️ Tech Stack

### 🏗️ Core Framework
- **Backend**: **Spring Boot 3.4** + **Spring AI** + **Langchain4j**
- **Database**: **MySQL 8.0** + **Redis** + Vector Databases (**Milvus/Weaviate/Qdrant**)
- **Frontend**: **Vue 3** + **Vben Admin** + **Naive UI**
- **Authentication**: Dual protection with **Sa-Token** and **JWT**

### 🔧 System Components
- **File Processing**: Parsing of **PDF**, **Word**, **Excel**, plus intelligent image analysis
- **Real-Time Communication**: **WebSocket** for real-time interaction, **SSE** for streaming responses
- **System Monitoring**: Comprehensive logging, performance monitoring, and health checks

> 💡 **Tip**: This project is an enhanced version of **ruoyi-ai**, improving large model and **MCP** integration (the original ruoyi-ai had limitations). It **supports MCP calls for any database and any API**.
