# 💡 Tekton Challenge Backend

API REST desarrollada con **Spring Boot 3.5.7** siguiendo una **arquitectura por capas**.  
El servicio expone endpoints para realizar operaciones matemáticas y consultar el historial de cálculos.

Incluye:
- Spring Boot 3.5.7
- Spring Data JPA con conexión a base de datos PostgreSQL 
- Spring Web y validaciones
- OpenAPI/Swagger para documentación automática
- Dockerfile y Gradle como gestor de construcción
- Tests con JUnit 5 y mockito

---

## ⚙️ Requisitos previos

Asegúrate de tener instalado lo siguiente:

| Requisito | Versión recomendada                           |
|------------|-----------------------------------------------|
| **Java** | 21 o superior                                 |
| **Gradle** | 9.0.0 *(opcional si usas el wrapper)*         |
| **Docker** | 24+ *(opcional, solo si deseas contenerizar)* |
| **Git** | 2.43+                                         |

---

## 🚀 Ejecución local

### 🔹 1. Clonar el repositorio

```bash
git clone https://github.com/<tu-usuario>/tekton-challenge-backend.git
cd tekton-challenge-backend
