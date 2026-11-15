# Plazoleta de comidas · Microservicio de trazabilidad

Microservicio que guarda el historial de cambios de estado de cada pedido (HU 17). El microservicio de
restaurantes le avisa cada vez que un pedido cambia de estado, y el cliente consulta aquí el historial de
sus pedidos. El propietario consulta aquí la eficiencia de los pedidos de su restaurante (HU 18).

- **Puerto:** 8083
- **Base de datos:** MongoDB (`plazoleta_trazabilidad`, colección `order_logs`)
- **Java 25, Spring Boot 4.1.1, arquitectura hexagonal (arquetipo del curso)**

## Historias de usuario

| HU | Qué hace | Rama |
| --- | --- | --- |
| 17 | Registrar cada cambio de estado de un pedido y consultar el historial de los pedidos propios | `historia17-consultar-trazabilidad` |
| 18 | Eficiencia: tiempo de cada pedido entregado (de PENDIENTE a ENTREGADO) y ranking de empleados por tiempo medio | `historia18-eficiencia-pedidos` |

## Endpoints

| Método | Ruta | Quién lo llama |
| --- | --- | --- |
| POST | `/internal/order-logs` | El microservicio de restaurantes, con `Authorization: Bearer <token interno>` |
| GET | `/api/orders/{orderId}/logs` | Un CLIENTE, con su JWT; solo de sus propios pedidos |
| GET | `/api/restaurants/{restaurantId}/efficiency` | El PROPIETARIO de ese restaurante, con su JWT |

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

Eficiencia (HU 18): solo cuentan los pedidos entregados; el tiempo va desde que el cliente crea el pedido
(`PENDIENTE`) hasta que se entrega (`ENTREGADO`). El ranking ordena a los empleados del menor al mayor tiempo
medio. Para confirmar que quien consulta es el dueño, este servicio llama a restaurantes
(`GET /internal/restaurants/{id}` con el token interno). Respuestas: 200, 401, 403 (no es propietario, o el
restaurante es de otro), 404 (el restaurante no existe), 503 (restaurantes o MongoDB no responden).

## Arquitectura hexagonal (arquetipo del curso)

```
com.plazoleta.traceability
├── domain
│   ├── model        OrderLog (valida el registro), OrderEfficiency, EmployeeEfficiency (cálculo de la HU 18)
│   └── exception    OrderLogsNotFoundException, ForbiddenOperationException, RestaurantNotFoundException,
│                    IntegrationUnavailableException
├── application
│   ├── handler      IOrderLogHandler, IEfficiencyHandler (+ implementación)
│   ├── port/input   puertos de entrada (interfaces): RecordOrderLogPort, GetOrderLogsPort, GetRestaurantEfficiencyPort
│   ├── port/output  OrderLogRepositoryPort, RestaurantDirectoryPort
│   ├── usecase      casos de uso, clases concretas con toda la lógica: RecordOrderLogUseCase, GetOrderLogsUseCase, GetRestaurantEfficiencyUseCase
│   └── dto          RecordOrderLogCommand, OrderLogResponse, RestaurantEfficiencyResponse, ...
└── infrastructure
    ├── input/rest   InternalOrderLogController, OrderLogController, EfficiencyController, dto, mapper,
    │                exceptionhandler
    ├── output/mongo document, repository, adapter (MongoDB)
    ├── output/http  RemoteRestaurantDirectoryAdapter (dueño del restaurante)
    └── configuration BeanConfiguration, OpenApiConfiguration, security (lectura del JWT)
```

Flujo de cada petición: `Controller` (solo HTTP: token interno o JWT, request DTO y mapper a comando) →
`IXHandler` (aplicación: coordina y delega, sin reglas de negocio) → puerto de entrada (`port/input`, interfaz) →
caso de uso (`usecase`, clase concreta con toda la lógica) → puertos de salida → adaptadores.

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

## Cobertura de pruebas (JaCoCo)

Al correr las pruebas se genera el reporte de cobertura:

```powershell
.\mvnw.cmd test
```

El reporte queda en `target/site/jacoco/index.html` (ábrelo en el navegador). Muestra qué porcentaje de
instrucciones y de ramas del código ejecutan las pruebas, por paquete y por clase.
