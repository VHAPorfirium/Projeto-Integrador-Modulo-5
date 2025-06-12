
# Projeto Integrador

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.5-brightgreen) 
![Java](https://img.shields.io/badge/Java-17-orange) 
![Android](https://img.shields.io/badge/Android-12-blue) 
![Gradle](https://img.shields.io/badge/Gradle-7.4.2-blueviolet)

## Índice

- [Visão Geral](#visão-geral)  
- [Tecnologias](#tecnologias)  
- [Arquitetura](#arquitetura)  
  - [Backend: Arquitetura em Camadas](#backend-arquitetura-em-camadas)  
  - [Android: MVVM](#android-mvvm)  
- [Estrutura de Pastas](#estrutura-de-pastas)  
  - [Backend (Spring Boot)](#backend-spring-boot)  
  - [Android (Kotlin / Java)](#android-kotlin--java)  
- [Como Rodar](#como-rodar)  
  - [Pré-requisitos](#pré-requisitos)  
  - [Configuração do Backend](#configuração-do-backend)  
  - [Configuração do App Android](#configuração-do-app-android)  
- [APIs e Endpoints](#apis-e-endpoints)   

---

## Visão Geral

O **Projeto Integrador** é um sistema completo para gerenciamento de clínicas de baixo custo, focado em:

- **Cadastro de pacientes**  
- **Fila de atendimento em tempo real**  
- **Agendamento de retorno**  
- **Dashboard de indicadores**  
- **Notificações via FCM**  
- **App Android** integrado ao backend  

---

## Tecnologias

- **Backend**: Spring Boot, Spring Data JPA, PostgreSQL, Swagger/OpenAPI  
- **Android**: Android SDK, Kotlin (ou Java), AndroidX, FCM (Firebase Cloud Messaging), MVVM, LiveData  
- **Build**: Gradle  
- **Controle de Versão**: Git/GitHub  

---

## Arquitetura

### Backend: Arquitetura em Camadas

```text
┌────────────────────────────┐
│ 1. Camada de Apresentação  │  ← Controllers (REST API)
└────────────────────────────┘
            ↓
┌────────────────────────────┐
│ 2. Camada de Serviço       │  ← Services (Regras de negócio)
└────────────────────────────┘
            ↓
┌────────────────────────────┐
│ 3. Camada de Domínio       │  ← Entities & Repository Interfaces
└────────────────────────────┘
            ↓
┌────────────────────────────┐
│ 4. Camada de Infraestrutura│  ← Implementações (JPA Repositories, Configurações)
└────────────────────────────┘
            ↓
┌────────────────────────────┐
│ 5. Banco de Dados          │  ← PostgreSQL
└────────────────────────────┘
````

### Android: MVVM

```text
┌─────────────┐      (data-binding / observers)      ┌─────────────┐
│   View(s)   │ ───────────────────────────────────▶│ ViewModel   │
│(Activities/ │◀─────────────────────────────────── │             │
│ Fragments)  │      (user actions)                └─────────────┘
└─────────────┘                 │                       
                                │ Update  
                                ▼                       
                             ┌──────────────┐             
                             │    Model     │             
                             │ (data layer) │             
                             └──────────────┘             
```

---

## Estrutura de Pastas

### Backend (Spring Boot)

```text
ProjetoIntegrador/
├── gradlew                          
├── build.gradle                     
├── settings.gradle                  
└── src/
    └── main/
        ├── java/
        │   └── br.com.projetoIntegrador
        │       ├── config            # Configurações (Segurança, Swagger, JPA)
        │       ├── controller        # REST Controllers
        │       ├── dto               # Data Transfer Objects
        │       ├── exception         # Handlers de erro
        │       ├── model             # JPA Entities
        │       ├── repository        # Interfaces de repositório
        │       ├── service           # Services (lógica de negócio)
        │       └── ProjetoIntegradorApplication.java
        └── resources/
            ├── application.yml      # Configurações (DB, FCM, etc.)
            └── db/
                └── migrations       # Scripts Flyway/Liquibase
```

### Android (Kotlin / Java)

```text
app/
├── manifests/
│   └── AndroidManifest.xml
├── java/
│   └── br.com.projetoIntegrador
│       ├── fcm                   # Firebase Cloud Messaging
│       ├── network               # Retrofit / API Clients
│       ├── presentation
│       │   ├── adapter           # RecyclerView Adapters
│       │   ├── ui
│       │   │   ├── activity      # Activities (Views)
│       │   │   └── fragment      # Fragments (Views)
│       │   └── viewmodel         # ViewModels (MVVM)
│       ├── repository            # Data Repositories
│       └── util                  # Utilitários e extensões
└── res/                          # Layouts, drawables, values (colors, dimens)
```

---

## Como Rodar

### Pré-requisitos

* Java 21+
* Gradle 7+
* Android Studio Flamingo ou superior
* Emulador ou dispositivo Android (API 21+)
* PostgreSQL 12+

### Configuração do Backend

1. **Clone o repositório**

   ```bash
   git clone https://github.com/SeuUsuario/ProjetoIntegrador.git
   cd ProjetoIntegrador
   ```
2. **Ajuste `application.yml`** com as credenciais do seu banco e chave FCM.
3. **Execute as migrações** (Flyway/Liquibase).
4. **Inicie a aplicação**

   ```bash
   ./gradlew bootRun
   ```
5. **Swagger UI** disponível em `http://localhost:8080/swagger-ui.html`

### Configuração do App Android

1. **Abra o módulo `app/`** no Android Studio.
2. **Coloque o `google-services.json`** na pasta `app/`.
3. **Atualize a BASE\_URL** em `network/ApiClient.kt` (use `10.0.2.2` no emulador).
4. **Compile e rode** no dispositivo/emulador.

---

## APIs e Endpoints

| Método | Endpoint                    | Descrição                      |
| ------ | --------------------------- | ------------------------------ |
| POST   | `/api/pacientes`            | Criar novo paciente            |
| GET    | `/api/pacientes/{id}`       | Buscar paciente por ID         |
| PUT    | `/api/pacientes/{id}`       | Atualizar paciente             |
| DELETE | `/api/pacientes/{id}`       | Desativar paciente             |
| POST   | `/api/atendimentos/checkin` | Check-in na fila               |
| POST   | `/api/atendimentos/next`    | Chamar próximo paciente        |
| GET    | `/api/retornos`             | Listar agendamentos de retorno |
| POST   | `/api/retornos`             | Agendar retorno                |

> Consulte a documentação completa em `/swagger-ui.html`
