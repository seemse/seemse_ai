
Seemse AI

<div align="center">
Enterprise-Grade AI Assistant Platform

Production-ready AI platform with deep integration of FastGPT, Coze, DIFY, and advanced RAG technology

[📖 Chinese README](README.md) [📚 Documentation]() [🚀 Live Demo]() [🐛 Report Bug]() [💡 Request Feature]()

</div>

✨ Key Features
🤖 Advanced AI Engine
Multi-Model Support: OpenAI GPT-4, Aure, ChatGLM, Qwen, ZhipuAI
AI Platform Integration: Deep integration with FastGPT, Coze, DIFY, and other leading AI platforms
Spring AI MCP Integration: Extensible tool ecosystem powered by the Model Context Protocol (MCP)
Streaming Chat: Real-time conversation via SSE/WebSocket for smooth user experience
AI Copilot: Intelligent code analysis and automated project scaffolding
🌟 AI Platform Ecosystem
FastGPT Deep Integration: Native support for FastGPT APIs—knowledge retrieval, workflow orchestration, and context management
Coze Official SDK: Full integration with ByteDance’s Coze platform, enabling Bot dialogues and streaming responses
DIFY Full Compatibility: Leverages the DIFY Java Client for application orchestration, workflows, and knowledge base operations
Unified Chat Interface: Standardized API layer supporting seamless switching between AI platforms and load balancing
🧠 Enterprise RAG Solution
Private Knowledge Base: Built on Langchain4j with BGE-large-h-v1.5 Chinese embedding model
Vector Database Support: Compatible with Milvus, Weaviate, Qdrant, and more
Privacy-First Architecture: Fully on-premise deployment option to safeguard sensitive enterprise data
Flexible Inference Backends: Supports Ollama, vLLM, and other local LLM serving frameworks
🎨 Creative AI Tools
AI Art Generation: Integrated with DALL·E-3, MidJourney, and Stable Diffusion
Smart PPT Creation: Automatically transforms text into polished presentation slides
Multi-Modal Understanding: Processes text, images, and documents intelligently

🔐 Tiered-Permission Knowledge Base System

Seemse AI implements a three-layer architecture to deliver secure, scalable, and fine-grained knowledge management tailored for enterprise environments.
🏗️ 1. Core Infrastructure Layer — Foundational Capabilities
Cloud LLM Module
Provides semantic understanding and generative capabilities for knowledge-based Q&A
Supports major cloud models: Qwen, Deepseek, Doubao (Bean), and more
Enables dynamic model switching or hybrid usage based on business needs
All model interactions are standardized through the Model Context Protocol (MCP) for consistency and extensibility

⚙️ 2. Core Functionality Layer — Business Logic & Permission Control
Knowledge Base Management
Handles ingestion, chunking, embedding, and storage of knowledge documents
Serves as the structured data backbone for accurate, context-aware AI responses
Supports multiple file formats: PDF, Word, Excel, TXT, and more
External Data Integration
Connects to enterprise systems (e.g., pricing engines, product catalogs, CRM, ERP)
Synchronizes external business data into the knowledge base via MCP interfaces
Supports scheduled or event-triggered updates to ensure knowledge freshness
Admin Console (Central Control Hub)
🔒 Granular Permission Management (Critical for Security)
Enforces dual-dimension access control: who can see what and what content is visible to whom
Supports permission policies by:
Department, Role, User Tag, and Content Filter
Specific users, documents, or even document segments
Allows content owners or authorized managers to:
Restrict certain users/departments from querying sensitive topics
Explicitly allow or deny access to specific knowledge categories
Prevents LLM data leakage: All AI responses are filtered in real time against active permission rules
📤 Document Upload
Centralized upload interface in the admin panel for batch ingestion and metadata tagging
Optionally allows authorized end users to upload documents directly (with inherited permissions)
All uploaded content automatically enforces the uploader’s access policy

💬 3. User Interaction Layer — End-User Experience
End-User Portal
Intuitive interface for non-technical users
Natural language query input for knowledge retrieval
Optional self-service document upload (subject to permission validation)
All responses strictly comply with backend-defined access controls
Voice Integration
Built-in Speech-to-Text (ASR) and Text-to-Speech (TTS) capabilities
Enables voice-based Q&A for use cases like meetings, customer service, and mobile apps
Voice interactions are governed by the same permission framework as text queries

🛠️ Tech Stack
Core Framework
Backend: Spring Boot 3.4, Spring AI, Langchain4j
Database: MySQL 8.0, Redis, Vector Databases (Milvus / Weaviate / Qdrant)
Frontend: Vue 3, Vben Admin, Naive UI
Authentication: Sa-Token + JWT dual-layer security
System Components
File Processing: PDF, Word, Excel parsing; intelligent image analysis
Real-Time Communication: WebSocket for bidirectional messaging, SSE for streaming responses
Observability: Comprehensive logging, performance monitoring, and health checks
💡 Tip: This project is an enhanced evolution of the ruoyi-ai platform, addressing its original limitations in large model and MCP integration. It now supports universal MCP calls for any database or API, enabling true enterprise-grade flexibility, security, and interoperability.
