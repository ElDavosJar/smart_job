# SkillGrid - Job Matching Platform

## 📋 Descripción del Proyecto

SkillGrid es una plataforma de matching entre empleadores y buscadores de empleo, desarrollada con **Domain-Driven Design (DDD)** y **Clean Architecture** en Spring Boot.

## 🏗️ Arquitectura del Sistema

### Patrón de Arquitectura
- **Clean Architecture** - Separación clara entre capas
- **Domain-Driven Design (DDD)** - Modelo de dominio rico
- **Hexagonal Architecture** - Puertos y adaptadores

### Bounded Contexts
1. **Members** - Gestión de usuarios (Empleadores y JobSeekers)
2. **Employers** - Perfiles y gestión de empleadores
3. **JobSeekers** - Perfiles y gestión de buscadores de empleo
4. **Jobs** - Publicaciones de trabajo
5. **Matching** - Algoritmos de matching
6. **Commons** - Utilidades compartidas

## 📁 Estructura del Proyecto

```
com.skillgrid
├── commons/
│   ├── ids/                    # Value Objects de IDs
│   │   ├── UserId.java
│   │   ├── MemberId.java
│   │   ├── EmployerId.java
│   │   ├── JobSeekerId.java
│   │   ├── CountryId.java
│   │   └── CityId.java
│   └── utils/                  # Utilidades compartidas
│       ├── Email.java
│       └── Phone.java
├── members/
│   └── domain/
│       └── model/
│           ├── Member.java     # Entidad principal
│           ├── Address.java    # Value Object de dirección
│           ├── ContactInfo.java # Value Object de contacto
│           └── BirthDate.java  # Value Object de fecha nacimiento
├── employers/
│   └── domain/
│       └── model/
│           ├── Employer.java
│           └── EmployerProfile.java
├── jobseekers/
│   └── domain/
│       └── model/
│           ├── JobSeeker.java
│           └── JobSeekerProfile.java
├── jobs/
│   └── domain/
│       └── model/
│           └── JobPosting.java
├── matching/
│   └── domain/
│       └── service/
│           └── MatchingService.java
└── infrastructure/
    ├── CountrySyncCommand.java
    ├── CountrySyncController.java
    └── SimpleCountrySync.java
```

## 🎯 Value Objects (VOs) Implementados

### IDs (Value Objects de Identificación)
- **UserId** - ID genérico de usuario
- **MemberId** - ID específico de miembro
- **EmployerId** - ID de empleador
- **JobSeekerId** - ID de buscador de empleo
- **CountryId** - ID de país
- **CityId** - ID de ciudad

### Utilidades
- **Email** - Validación completa RFC 5322
- **Phone** - Validación flexible internacional

### Domain VOs
- **Address** - Dirección con referencias a CountryId y CityId
- **ContactInfo** - Información de contacto (Email + Phone)
- **BirthDate** - Fecha de nacimiento con validación de edad

## 🚀 Características Técnicas

### Patrón Builder
```java
Member member = Member.builder()
    .id(memberId)
    .firstName("John")
    .lastName("Doe")
    .contactInfo(contactInfo)
    .address(address)
    .birthDate(birthDate)
    .build();
```

### Factory Methods
```java
// Crear nueva instancia
Member.create(memberId, "John", "Doe", contactInfo);

// Reconstruir desde persistencia
Member.rehydrate(memberId, "John", "Doe", contactInfo, address, birthDate, createdAt, updatedAt);
```

### Domain Methods
```java
// Cambios que retornan nuevas instancias (inmutabilidad)
Member updated = member.changeName("Jane", "Smith");
Member withNewContact = member.changeContactInfo(newContactInfo);
```

## 📋 Validaciones Implementadas

### Email Validation
- ✅ RFC 5322 compliance
- ✅ Longitud máxima 254 caracteres
- ✅ Formato correcto con @
- ✅ Dominios válidos
- ✅ Sin espacios consecutivos
- ✅ Sin puntos al inicio/fin

### Phone Validation
- ✅ Mínimo 7 dígitos
- ✅ Máximo 25 caracteres
- ✅ Solo números y caracteres permitidos
- ✅ Formatos internacionales aceptados

### BirthDate Validation
- ✅ Fecha mínima: 1900-01-01
- ✅ Edad máxima: 16 años (usuario debe tener al menos 16)
- ✅ No fechas futuras
- ✅ Formato válido

### Address Validation
- ✅ Line1 obligatorio
- ✅ CityId obligatorio
- ✅ CountryId obligatorio
- ✅ Campos opcionales: line2, state, postalCode

## 🗄️ Base de Datos

### Configuración PostgreSQL
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/skillgrid_db
spring.datasource.username=postgres
spring.datasource.password=123456
```

### Migraciones Flyway
- `V1__Create_Countries_Table.sql` - Tabla de países con prefijos telefónicos

## 🌐 API REST

### Sincronización de Países
```bash
# Endpoint para sincronizar países desde REST Countries API
POST /api/admin/countries/sync

# Descarga automáticamente:
# - 250+ países
# - Prefijos telefónicos
# - Monedas
# - Idiomas
# - Datos geográficos
```

## 🧪 Testing

### Framework
- **JUnit 5** - Testing moderno
- **AssertJ** - Assertions fluídas
- **Mockito** - Mocks cuando sea necesario

### Cobertura de Tests
- ✅ **Value Objects** - Validaciones completas
- ✅ **Entities** - Comportamiento de dominio
- ✅ **Factory Methods** - Creación correcta
- ✅ **Domain Methods** - Lógica de negocio
- ✅ **Edge Cases** - Casos límite

## 📚 Principios DDD Aplicados

### 1. **Ubiquitous Language**
- Employer (Contratante)
- JobSeeker (Buscador de empleo)
- Member (Usuario base)

### 2. **Bounded Contexts**
- Separación clara entre dominios
- Context Maps definidos
- Comunicación vía IDs

### 3. **Entities & Value Objects**
- **Entities**: Member, Employer, JobSeeker
- **Value Objects**: IDs, Email, Phone, Address, BirthDate
- **Aggregates**: Member como aggregate root

### 4. **Domain Services**
- Lógica de negocio pura
- Sin dependencias de infraestructura
- Testeable unitariamente

### 5. **Factory Pattern**
- Creación consistente de objetos
- Validación en punto de creación
- Builder pattern para objetos complejos

### 6. **Repository Pattern**
- Abstracción de persistencia
- Interfaces en dominio
- Implementaciones en infraestructura

## 🚀 Próximos Pasos

### Fase 1: Completar Dominio
- [ ] Implementar Employer aggregate
- [ ] Implementar JobSeeker aggregate
- [ ] Crear JobPosting entity
- [ ] Desarrollar MatchingService

### Fase 2: Infraestructura
- [ ] Repositories JPA
- [ ] Controllers REST
- [ ] DTOs de API
- [ ] Mappers

### Fase 3: Aplicación
- [ ] Casos de uso
- [ ] Servicios de aplicación
- [ ] Validaciones de negocio
- [ ] Logging y auditoría

### Fase 4: Testing & CI/CD
- [ ] Cobertura 80%+ de tests
- [ ] Integración continua
- [ ] Despliegue automatizado
- [ ] Documentación API

## 🛠️ Tecnologías Utilizadas

- **Java 17** - Lenguaje principal
- **Spring Boot 3.2.0** - Framework
- **PostgreSQL** - Base de datos
- **Flyway** - Migraciones
- **JUnit 5** - Testing
- **Maven** - Gestión de dependencias
- **Git** - Control de versiones

## 📞 Contacto

Proyecto desarrollado con enfoque en **Domain-Driven Design** y **Clean Architecture** para una plataforma escalable de matching laboral.

---

*Desarrollado el 18 de septiembre de 2025*