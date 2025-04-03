# **Planify-app**
## Descripción
  Planify-app es una aplicación backend desarrollada con Spring Boot para la gestión de finanzas personales. Permite llevar un control ordenado de las cuentas y realizar cálculos financieros mediante una API externa.

## **Tecnologías utilizadas**
  
  * Lenguaje: Java 21
  * Framework: Spring Boot 3.4.3
  * Base de datos: PostgreSQL (NEON, nube)
  * Seguridad: OAuth2, JWT
  * Arquitectura: MVC
  * Documentación: SpringDoc (OpenAPI)
  * Motor de plantillas: Thymeleaf
  * Pruebas: JUnit

## **Requisitos previos**
Antes de ejecutar el proyecto, asegúrate de tener instalado:
* JDK 21
* Maven 3+
* PostgreSQL configurado en NEON

## **Instalación y configuración**
  1. Clona el repositorio:
     
         git clone <URL_DEL_REPOSITORIO>
         cd Planify-app
  2. Configura las variables de entorno en `application.properties`:

         spring.datasource.url=jdbc:postgresql://<NEON_HOST>:<PORT>/<DB_NAME>
         spring.datasource.username=<DB_USER>
         spring.datasource.password=<DB_PASSWORD>
         spring.security.oauth2.client.registration.<provider>.client-id=<CLIENT_ID>
         spring.security.oauth2.client.registration.<provider>.client-secret=<CLIENT_SECRET>
  3. Instala las dependencias:

         mvn clean install
## **Ejecución del proyecto**
Para iniciar la aplicación en modo desarrollo:

    mvn spring-boot:run

## **Endpoints principales**
La API cuenta con los siguientes endpoints:

| Método  | Endpoint         | Descripción               |
|---------|----------------|--------------------------|
| `POST`  | `/auth/login`  | Autenticación de usuario |
| `GET`   | `/users`       | Listado de usuarios      |
| `GET`   | `/accounts`    | Listado de cuentas       |
| `POST`  | `/transactions` | Registrar transacción   |

Para ver la documentación completa de la API:

    http://localhost:8080/swagger-ui/index.html

## **Pruebas**
Para ejecutar las pruebas unitarias:

    mvn test
## **Despliegue**
Para empaquetar y desplegar el proyecto en producción:

    mvn package
El archivo `.jar` generado estará en `target/Planify-app-0.0.1-SNAPSHOT.jar`.
## **Contribución**
Si deseas contribuir al proyecto, sigue estos pasos:
  1. Haz un fork del repositorio.
  2. Crea una rama con tu nueva funcionalidad: `git checkout -b feature/nueva-funcionalidad`.
  3. Sube los cambios y haz un pull request.
