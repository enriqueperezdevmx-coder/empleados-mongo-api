# Empleados API MongoDB

API REST para administrar empleados, construida en la Academia Java CDMX: primero con MySQL (Semana 3, del 24 al 26 de septiembre de 2026; está en la etiqueta v1-mysql) y después **migrada a MongoDB** (Semana 4, 28 y 29 de septiembre).

Alumno: Enrique Pérez Sánchez

## Tecnologías
* Java 17
* Spring Boot 4.1.1
* Spring Data MongoDB
* Bean Validation
* springdoc-openapi (Swagger)
* MongoDB 8.2 y DbGate en Docker
* WSL2 con Ubuntu 24.04

## Cómo levantarla
```bash
docker compose up -d
./mvnw spring-boot:run
```
* Swagger: http://localhost:8080/swagger-ui.html
* DbGate: http://localhost:3000

Datos de ejemplo (30 empleados, una sola vez):
```bash
docker exec -i empleados-mongo mongoimport --maintainInsertionOrder -u academia -p academia123 --authenticationDatabase admin -d empleados_db -c empleados < datos/semilla-empleados.json
```

## Endpoints
| Verbo | Ruta | Qué hace |
|---|---|---|
| GET | `/api/empleados?page=0&size=10&sort=id,asc` | Lista por páginas |
| GET | `/api/empleados/{id}` | Un empleado (404 si no existe) |
| POST | `/api/empleados` | Crea (201 + Location; 400 datos inválidos; 409 email repetido) |
| PUT | `/api/empleados/{id}` | Modifica (200; 400; 404; 409) |
| DELETE | `/api/empleados/{id}` | Borra (204; 404) |
| GET | `/api/empleados/buscar?departamento=&texto=&activo=&salarioMinimo=&salarioMaximo=&ciudad=&habilidad=` | Búsqueda con filtros opcionales, sin importar acentos, por páginas |
| GET | `/api/empleados/departamento/{departamento}` | Los de un departamento, por apellidos |
| GET | `/api/empleados/salarios?minimo=&maximo` | Los de un rango de salario, del mayor al menor |
| GET | `/api/empleados/estadisticas/departamentos` | Por departamento: empleados, activos y salario promedio, mínimo y máximo (agregación) |

## De MySQL a MongoDB
| Concepto | MySQL (v1-mysql) | MongoDB (ahora) |
|---|---|---|
| Dónde vive un empleado | Una fila de la tabla empleados | Un documento de la colección empleados |
| id | Long consecutivo (1, 2, 3) | String: un ObjectId de 24 caracteres |
| Repositorio | JpaRepository + @Query JPQL | MongoRepository + MongoTemplate/Criteria |
| Email único | @Column(unique = true) | @Indexed(unique = true) + auto-index-creation |
| Dirección y habilidades | Serían 2 tablas más y un JOIN | Dentro del mismo documento: subdocumento y array |
| Transacciones | @Transactional | No hay (un solo servidor); cada documento se guarda atómicamente |

`git diff v1-mysql --stat` muestra exactamente qué archivos cambiaron.

## Evidencia
| Día | Archivos |
|---|---|---|
| Semana 3 (MySQL) | `evidencia/dia1/`, `evidencia/dia2/`, `evidencia/dia3/` |
| Lunes 28: la migración | `evidencia/s4-dia1/crud.txt`, `evidencia/s4-dia1/mongo.txt` |
| Martes 29: lo que Mongo hace distinto | `evidencia/s4-dia2/busquedas-antes.txt`, `evidencia/s4-dia2/comparacion-antes.txt`, `evidencia/s4-dia2/busquedas.txt`, `evidencia/s4-dia2/comparacion.txt`, `evidencia/s4-dia2/mongo.txt`, `evidencia/s4-dia2/tipos-y-agregacion.txt` |

## Qué aprendí y qué me costó
Aprendí a estructurar modelos no relacionales embebiendo documentos y listas en lugar de usar tablas foráneas con JOINs. Comprobé la importancia de validar regresiones silenciosas como la falta de soporte de acentos en regex y la necesidad de forzar el manejo de tipos exactos (Decimal128 y Date) en Extended JSON. Lo que más trabajo requirió fue ajustar la validación de ordenamiento dinámico que MongoDB por defecto ignora.
