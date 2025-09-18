# SkillGrid - Job Matching Platform

## 📋 Overview

SkillGrid is a job matching platform built with Domain-Driven Design (DDD) and Clean Architecture using Spring Boot.

## 🏗️ Architecture

### Bounded Contexts
- **Members**: User management and profiles
- **Employers**: Employer-specific functionality
- **JobSeekers**: Job seeker-specific functionality
- **Jobs**: Job postings management
- **Matching**: Matching algorithms
- **Commons**: Shared utilities and value objects

### Tech Stack
- Java 21
- Spring Boot 3.2.0
- PostgreSQL
- Maven

## 📁 Project Structure

```
src/main/java/com/skillgrid/
├── commons/              # Shared components
│   ├── ids/             # ID value objects
│   └── utils/           # Utilities (Email, Phone)
├── members/             # Member bounded context
│   └── domain/model/    # Domain entities
├── employers/           # Employer bounded context
├── jobseekers/          # JobSeeker bounded context
├── jobs/               # Jobs bounded context
├── matching/           # Matching bounded context
└── infrastructure/     # External integrations
```

## 🚀 Getting Started

### Prerequisites
- Java 21
- Maven 3.6+
- PostgreSQL

### Build
```bash
mvn clean compile
```

### Run
```bash
mvn spring-boot:run
```

## 📊 Current Status

### ✅ Completed
- [x] DDD architecture setup
- [x] Value objects (IDs, Email, Phone)
- [x] Member entity with domain logic
- [x] Address and ContactInfo value objects
- [x] BirthDate validation
- [x] Builder pattern implementation
- [x] Basic infrastructure setup

### 🔄 Next Steps
- [ ] Complete Employer aggregate
- [ ] Complete JobSeeker aggregate
- [ ] Implement JobPosting entity
- [ ] Add repositories and services
- [ ] Create REST controllers
- [ ] Add authentication/authorization

## 🧪 Testing

```bash
mvn test
```

## 📞 Contact

SkillGrid development project.