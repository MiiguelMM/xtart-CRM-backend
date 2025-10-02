# XTART Solutions - Backend

Este es el backend del proyecto **XTART Solutions**, desarrollado con **Spring Boot**. Expone una API REST para la gestión de empleados, clientes, productos y ventas.

> Proyecto final del primer año del ciclo de **DAM (Desarrollo de Aplicaciones Multiplataforma)**.

---

## 🛠️ Tecnologías utilizadas

- ☕ Java 21
- 🧰 Spring Boot
- 📦 Spring Data JPA
- 🗄️ MySQL
- 🌐 REST APIs

---

## 🚀 ¿Cómo ejecutar?

1. Clona el repositorio:

```bash
git clone https://github.com/TU_USUARIO/xtart-backend.git
cd xtart-backend
```

2. Crea una base de datos `xtart_db` en MySQL.

3. Configura el archivo `application.properties`:


```properties  

spring.datasource.url=jdbc:mysql://localhost:3306/xtart_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true  
spring.jpa.properties.hibernate.format_sql=true 
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=xtartsolutionscrm@gmail.com
spring.mail.password=jhlv dqdv qbpw yqth
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true


```

4. Ejecuta el proyecto:

```bash
./mvnw spring-boot:run
```

La API estará disponible en `http://localhost:8080`.

Aunque esta subida en netify :

---

## 📁 Estructura básica

```
src/
└── main/
    ├── java/
    │   └── com/xtart/...
    └── resources/
        └── application.properties
```

---

## 👨‍💻 Autor

Desarrollado por **Miguel Melgarejo Sánchez**  
🔗 [LinkedIn]() · 🐙 [GitHub](https://github.com/MiiguelMM)

---
