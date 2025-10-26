# Usa la imagen base con Java 21
FROM eclipse-temurin:21-jdk AS build

# Establece el directorio de trabajo
WORKDIR /app

# Copia los archivos de Gradle y dependencias
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Descarga dependencias
RUN ./gradlew dependencies --no-daemon

# Copia el código fuente
COPY src ./src

# Compila la aplicación
RUN ./gradlew clean bootJar --no-daemon

# Imagen final ligera
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia el JAR generado
COPY --from=build /app/build/libs/*.jar app.jar

# Expone el puerto
EXPOSE 8080

# Ejecuta la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
