# Proyecto COVID-19 Stats
Este proyecto es una aplicación Java desarrollada con 
Spring Boot que consume datos de una API pública de estadísticas de COVID-19,
procesa esta información y la almacena en una base de datos MySQL.
Está diseñado aplicando buenas prácticas como el principio de 
responsabilidad única (SRP) y separando la lógica en capas claras: configuración, servicio, repositorio y modelo.

## Tecnologías utilizadas
* Java 11

* Spring Boot

* Maven

* JPA / Hibernate

* MySQL

* Log4j2


## Estructura del proyecto
```
ProyectoCovidStats/
├── config/             # Clases de configuración (RestTemplate, tareas programadas)
├── model/              # Entidades JPA (Report, Province, Region)
├── repository/         # Interfaces JPA para persistencia
├── service/            # Lógica para consumir API y procesar datos
├── util/               # Cliente HTTP para interactuar con la API externa
├── resources/
│   ├── application.properties  # Configuración de conexión a DB, logs, etc.
│   └── log4j2.xml              # Configuración de logging
└── Covid19TrackerApplication.java  # Clase principal para ejecutar la aplicación
```
## Funcionamiento
1. **Inicio de la aplicación** : al iniciar, Spring Boot configura los beans necesarios para la ejecución del proyecto.

2. **Tarea programada**: mediante properties, se configura una tarea que se ejecuta 15 segundos después del arranque y puede programarse para que se repita.

3. **Consumo de la API**: la clase ApiClient hace solicitudes HTTP a la API pública de COVID-19.

4. **Procesamiento de datos**: ApiService transforma los datos recibidos en objetos de dominio, y CovidDataService los almacena usando los repositorios JPA.

5. **Persistencia**: los datos se guardan en la db covidstats, que contiene:

* **Province**: tabla que almacena información sobre provincias, con los siguientes campos:
  * id (clave primaria)
  * name (nombre de la provincia)
  * region_id (clave foránea a Region)
  
* **Region**: tabla que almacena información sobre regiones, con los siguientes campos:
  * id (clave primaria)
  * iso (código ISO de la región)
  * name (nombre de la región)
  
* **Report**: tabla que almacena información sobre los reportes de COVID-19, con los siguientes campos:
  * id (clave primaria)
  * confirmed (número de casos confirmados)
  * date (fecha del reporte)
  * deaths (número de muertes)
  * recovered (número de recuperados)
  * province_id (clave foránea a Province)
  
  
## Autores
* [DIMAS FABIÁN JIMÉNEZ LOBOS] -- [0905-20- 6300] -- [djimenezl7@miumg.edu.gt]
* [RUDY GEOVANY GONZÁLEZ ARANA] -- [0905-23- 6867] -- [rgonzaleza11@miumg.edu.gt]
* [OLIVER ISAAC GODOY SALGUERO] -- [0905-23-10816] -- [ogodoys@miumg.edu.gt]
