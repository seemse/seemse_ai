# Seemse AI

<div align="center">

### Enterprise-Grade AI Assistant Platform

*Production-ready AI platform with deep integration of FastGPT, Coze, DIFY, and advanced RAG technology*

**[📖 中文 README](README.md)** | **[📚 Documentation]()** | **[🚀 Live Demo]()** | **[🐛 Report Bug]()** | **[💡 Request Feature]()**

</div>

## ✨ Key Features

### 🔐 Tiered-Permission Knowledge Base System
An enterprise-grade knowledge hub designed for internal teams, integrating **knowledge storage, management, retrieval, and fine-grained access control** in one system. Ensures data security from both **user dimension** and **content dimension**.

🏗️ **Core Support Layer**: Foundational system capabilities  
☁️ Cloud LLM Module: Provides semantic understanding and content generation for knowledge-based Q&A. Supports integration with cloud LLMs such as **Qwen**, **Deepseek**, and **Doubao**, with flexible model switching or combination based on business needs.

⚙️ **Core Function Layer**: Core business logic and permission control  
📚 Knowledge Base Management Module: Handles document ingestion, processing, and storage to serve as the data foundation for Q&A.  
🔌 External Data Integration Module: Connects to external business systems (e.g., pricing systems, product catalogs) to enrich knowledge sources. Synchronizes real-time business data into the knowledge base via **MCP interfaces**.  
🛡️ Admin Management Module (Central Control Point):  
 • **Permission Management**: Enforces security from user and content perspectives. Supports granular access policies by **department, role, user tag, or content filter**. Permissions are set by the uploader or authorized content owner. Allows explicit configuration to **allow or deny specific users/departments from querying certain types of information**. **Critical attention must be paid to prevent information leakage via LLMs.**  
 • 📤 Document Upload: Centralized upload interface in admin panel; also supports authorized users uploading documents via the frontend. All content inherits defined permission policies.

💬 **User Interaction Layer**: Direct user-facing interfaces  
🖥️ User Portal: Standard interface for end users. Offers text-based Q&A and supports document upload (subject to permission checks).  
🎤 Voice Integration: Provides **Text-to-Speech (TTS)** and **Speech-to-Text (ASR)** capabilities, enabling voice-based interaction with the knowledge base for use cases like meetings, customer service, and mobile apps.

### 🤖 Advanced AI Engine
- **Multi-Model Support**: OpenAI GPT-4, Aure, ChatGLM, Qwen, ZhipuAI
- **AI Platform Integration**: Deep integration with **FastGPT**, **Coze**, **DIFY** and other leading AI platforms
- **Spring AI MCP Integration**: Extensible tool ecosystem with Model Context Protocol
- **Streaming Chat**: Real-time SSE/WebSocket communication
- **AI Copilot**: Intelligent code analysis and project scaffolding

### 🌟 AI Platform Ecosystem
- **FastGPT Deep Integration**: Native FastGPT API support with knowledge base retrieval, workflow orchestration and context management
- **Coze Official SDK**: Integration with ByteDance Coze platform official SDK, supporting Bot conversations and streaming responses
- **DIFY Full Compatibility**: Using DIFY Java Client for app orchestration, workflows and knowledge base management
- **Unified Chat Interface**: Standardized chat service interface supporting seamless platform switching and load balancing

### 🧠 Enterprise RAG Solution
- **Local Knowledge Base**: Langchain4j + BGE-large-h-v1.5 embeddings
- **Vector Database Support**: Milvus, Weaviate, Qdrant
- **Privacy-First**: On-premise deployment with local LLM support
- **Ollama & vLLM Compatible**: Flexible model deployment options

### 🎨 Creative AI Tools
- **AI Art Generation**: DALL·E-3, MidJourney, Stable Diffusion integration
- **PPT Creation**: Automated slide generation from text input
- **Multi-Modal Processing**: Text, image, and document understanding

## 🛠️ Tech Stack

### 🏗️ Core Framework
- **Backend**: Spring Boot 3.4, Spring AI, Langchain4j
- **Database**: MySQL 8.0, Redis, Vector Databases (Milvus/Weaviate/Qdrant)
- **Frontend**: Vue 3, Vben Admin, Naive UI
- **Authentication**: Sa-Token, JWT

### 🔧 System Components
- **File Processing**: PDF, Word, Excel parsing, intelligent image analysis
- **Real-time Communication**: WebSocket real-time communication, SSE streaming
- **System Monitoring**: Comprehensive logging, performance monitoring, health checks

> 💡 **Tip**: This project is an enhanced version of the ruoyi-ai project, with improvements in the integration and invocation of large models and MCP (the original ruoyi-ai had limitations). It supports MCP calls for any database and any API.
