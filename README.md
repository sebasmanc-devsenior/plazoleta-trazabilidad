# Plazoleta de comidas · Microservicio de trazabilidad

Microservicio que guarda el historial de cambios de estado de cada pedido (HU 17). El microservicio de
restaurantes le avisa cada vez que un pedido cambia de estado, y el cliente consulta aquí el historial de
sus pedidos.

- **Puerto:** 8083
- **Base de datos:** MongoDB (`plazoleta_trazabilidad`, colección `order_logs`)
- **Java 25, Spring Boot 4.1.1, arquitectura hexagonal (arquetipo del curso)**

## Historias de usuario

| HU | Qué hace | Rama |
| --- | --- | --- |
| 17 | Registrar cada cambio de estado de un pedido y consultar el historial de los pedidos propios | `historia17-consultar-trazabilidad` |

## Endpoints

| Método | Ruta | Quién lo llama |
| --- | --- | --- |
| POST | `/internal/order-logs` | El microservicio de restaurantes, con `Authorization: Bearer <token interno>` |
| GET | `/api/orders/{orderId}/logs` | Un CLIENTE, con su JWT; solo de sus propios pedidos |

Registro que envía restaurantes:

```json
{ "orderId": 5, "clientId": 7, "restaurantId": 1, "employeeId": 9, "previousStatus": "PENDIENTE", "newStatus": "EN_PREPARACION" }
```

La fecha (`changedAt`) la pone este microservicio al recibir el registro.

Historial que ve el cliente, del cambio más antiguo al más reciente:

```json
[
  { "orderId": 5, "previousStatus": null,             "newStatus": "PENDIENTE",      "employeeId": null, "changedAt": "..." },
  { "orderId": 5, "previousStatus": "PENDIENTE",      "newStatus": "EN_PREPARACION", "employeeId": 9,    "changedAt": "..." },
  { "orderId": 5, "previousStatus": "EN_PREPARACION", "newStatus": "LISTO",          "employeeId": 9,    "changedAt": "..." }
]
```

Respuestas del historial: 200, 401 (sin token o token inválido), 403 (no es cliente, o el pedido es de otro
cliente), 404 (el pedido no tiene registros), 503 (MongoDB no responde).

## Arquitectura hexagonal (arquetipo del curso)

```
com.plazoleta.traceability
├── domain
│   ├── model        OrderLog (valida el registro)
│   └── exception    OrderLogsNotFoundException, ForbiddenOperationException
├── application
│   ├── port/input   RecordOrderLogUseCase, GetOrderLogsUseCase
│   ├── port/output  OrderLogRepositoryPort
│   ├── service      RecordOrderLogService, GetOrderLogsService
│   └── dto          RecordOrderLogCommand, OrderLogResponse
└── infrastructure
    ├── input/rest   InternalOrderLogController, OrderLogController, exceptionhandler
    ├── output/mongo document, repository, adapter (MongoDB)
    └── configuration BeanConfiguration, OpenApiConfiguration, security (lectura del JWT)
```

## Cómo ejecutarlo

1. Instala MongoDB Community Server como servicio de Windows (puerto 27017). La base y la colección se crean
   solas la primera vez que se guarda un registro.
2. Copia la configuración: `Copy-Item .env.example .env` y pon en `APP_JWT_SECRET` y `USERS_INTERNAL_TOKEN`
   los mismos valores que tienen los demás microservicios.
3. Arranca el servicio:

```powershell
.\mvnw.cmd spring-boot:run
```

4. Swagger: http://localhost:8083/swagger-ui.html

Para ver los datos: MongoDB Compass → `mongodb://localhost:27017` → base `plazoleta_trazabilidad` →
colección `order_logs`.

## Pruebas

```powershell
.\mvnw.cmd test
```

Las pruebas no necesitan MongoDB: el puerto de salida se reemplaza por un doble de prueba.
