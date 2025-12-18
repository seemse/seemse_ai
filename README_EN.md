# Seemse AI

<div align="center">

### Enterprise-Grade AI Assistant Platform

An **open-source, role-based knowledge base system** built upon [RuoYi-AI](https://github.com/ageerle/ruoyi-ai), inheriting the RuoYi ecosystem’s high availability and extensibility. It focuses on the core scenarios of **granular permission control + intelligent knowledge management**, addressing common pain points in traditional knowledge bases—such as chaotic permissions, data leakage, and inefficient search—while balancing **secure storage** and **efficient knowledge sharing**.

**[🇨🇳 中文](README.md)** | **[📖 Documentation]()** | **[🚀 Live Demo]()** | **[🐛 Issue Tracker]()** | **[💡 Feature Suggestions]()**

</div>

## 🔗 Project Origin

This project is developed based on [RuoYi-AI](https://github.com/ageerle/ruoyi-ai) (the AI-enhanced version of RuoYi), retaining RuoYi’s core capabilities—**user management, role-based access control, system configuration**, etc.—while deeply customizing it for **knowledge base scenarios**:

* Extended dedicated modules: **knowledge categorization, upload / editing / retrieval**
* Enhanced **fine-grained permission tiers** and **knowledge security controls**
* Integrated **AI-powered semantic search** and **knowledge graph visualization**
* Optimized for **enterprise / organizational collaboration** and compliance requirements

> We sincerely thank the RuoYi-AI team for providing an excellent foundational framework. This project will continuously sync upstream stability updates and security patches.

## 🌟 Core Features (Enhanced from RuoYi-AI)

### 1. Granular Permission Control (Key Enhancement)

* Built upon RuoYi’s native permission system, extended with **four-tier knowledge-specific permissions**: **View / Edit / Manage / Super Admin**
* Supports **custom role permissions + resource-level isolation**, adaptable to multi-department or multi-scenario needs
* Dynamic permission adjustments: temporary access grants, automatic revocation after project cycles, one-click freeze for departed employees
* Permission inheritance: supports hierarchical inheritance based on organizational structure, simplifying configuration for large teams

### 2. Full-Scenario Knowledge Management (New Modules)

* Multi-format support: documents (Word/PDF/Markdown), images, videos, audio—compatible with RuoYi-AI’s attachment storage
* Structured storage: multi-level categorization (Department / Project / Topic), integrated with RuoYi’s org chart, with category-level permission control
* Smart search: leverages RuoYi-AI’s AI semantic analysis for keyword/full-text search + fuzzy matching + category filtering
* Knowledge Graph: visualizes relationships between knowledge items to enable rapid tracing and contextual learning

### 3. Security & Compliance Assurance (Deep Optimization)

* Content security: inherits RuoYi’s encrypted data storage; adds dynamic watermarks, download restrictions, and sensitive word filtering
* Operation auditing: extends RuoYi’s logging system with knowledge-specific audit logs (access / edit / download / share), supporting traceability and export
* Compliance-ready: meets regulatory requirements for handling confidential materials in government/enterprise environments, with permission-change audits and sensitive-access alerts

### 4. Team Collaboration Capabilities (New Features)

* Real-time co-editing: supports collaborative document editing with comment tracking, integrated with RuoYi’s user system
* Custom approval workflows: configurable multi-step review rules tied to RuoYi roles to ensure content accuracy
* Knowledge interaction: enables comments, likes, favorites, and secure sharing to promote internal knowledge circulation

### 5. Inherited Advantages from RuoYi-AI

* Mature user / role / menu permission system—ready out of the box
* Frontend-backend decoupled architecture—easy for secondary development and extension
* Docker deployment support—enables rapid environment setup
* Built-in AI capabilities (text summarization, translation, OCR, etc.)—extensible for intelligent knowledge processing

## 📋 Use Cases

* Internal enterprise training & knowledge repository (permission isolation + knowledge retention)
* Cross-departmental or multi-branch knowledge sharing (tiered authorization + org-chart alignment)
* R&D project documentation management (technical specs, API docs, patent files with strict access control)
* Government/enterprise classified document storage (audit compliance + sensitive data protection)
* Educational institution course material management (role-based access for instructors vs. students + content protection)

## 🚀 Quick Start

### Environment Requirements (Same as RuoYi-AI)

* Backend: Java 1.8+
* Frontend: Vue 3 + Element Plus
* Database: MySQL 8.0+ / PostgreSQL 13+
* Middleware: Redis 6.0+ (caching), MinIO (file storage, optional)
* JDK: 1.8+
* Maven: 3.6+
* Node.js: 14+

### Installation & Deployment

#### 1. Clone the Repository

```bash
# Clone this project
git clone https://github.com/seemse/seemse_ai.git
cd rbkbs

# (Optional) Add upstream RuoYi-AI remote
git remote add upstream https://github.com/ageerle/ruoyi-ai.git
