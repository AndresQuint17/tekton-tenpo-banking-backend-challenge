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

Se recomienda seguir los siguientes pasos para probar funcionalidad localmente sin tener que crear la base de datos por separado.

### 🔹 1. Clonar el repositorio

```bash

git clone https://github.com/AndresQuint17/tekton-tenpo-banking-backend-challenge.git
cd tekton-tenpo-banking-backend-challenge
```

### 🔹 2. Crear y ejecutar la base de datos y la aplicación en un contenedor de docker
```bash

docker compose up -d
```
### 🔹 3. Collection Postman

Descarga la collección de postman desde:

https://www.postman.com/crimson-shadow-606771/workspace/tekton

Comienza a probar la API

### 🐳 Docker Hub (Opcional)

Si no quieres descargar el código fuente, puedes realizar los siguientes pasos:

```bash

# 1. Descargar la imagen
docker pull andresdavidqc/banca-api:latest

# 2. Ejecutar la imagen
# NOTA: Los valores de las variables de entorno (DB_*) deben coincidir
# con la configuración de tu base de datos PostgreSQL.
docker run -d \
    --name banca-api-prod \
    -p 8080:8080 \
    -e SPRING_DATASOURCE_URL="jdbc:postgresql://<HOST_DB>:5432/banca_db" \
    -e SPRING_DATASOURCE_USERNAME="banca_user" \
    -e SPRING_DATASOURCE_PASSWORD="banca_pass" \
    andresdavidqc/banca-api:latest
```
