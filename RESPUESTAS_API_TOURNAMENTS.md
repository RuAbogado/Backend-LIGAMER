# 📋 **Guía de Respuestas API - Tournaments**

## 🎯 **Estructura General de Respuesta**

Todas las respuestas siguen este formato:

```json
{
  "success": boolean,
  "message": "Descripción clara de lo que pasó",
  "data": {},  // Solo si hay datos (null si no)
  "errorCode": "CÓDIGO_ERROR",  // Solo en errores
  "timestamp": "2025-11-15T10:17:07"
}
```

---

## ✅ **ÉXITO: Crear Torneo**

**Endpoint:**
```
POST http://localhost:8080/api/tournaments
```

**Headers:**
```
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

**Body:**
```json
{
  "name": "Torneo Apertura 2025",
  "description": "Torneo de fútbol amateur",
  "rules": "11 vs 11, reglas FIFA",
  "startDate": "2025-12-01",
  "endDate": "2026-02-28"
}
```

**Response: 201 Created**
```json
{
  "success": true,
  "message": "Torneo creado exitosamente",
  "data": {
    "id": 5,
    "name": "Torneo Apertura 2025",
    "description": "Torneo de fútbol amateur",
    "rules": "11 vs 11, reglas FIFA",
    "startDate": "2025-12-01",
    "endDate": "2026-02-28",
    "active": true,
    "createdByEmail": "organizador@email.com"
  },
  "errorCode": null,
  "timestamp": "2025-11-15T10:17:07"
}
```

---

## ✅ **ÉXITO: Obtener Todos los Torneos**

**Endpoint:**
```
GET http://localhost:8080/api/tournaments
```

**Response: 200 OK**
```json
{
  "success": true,
  "message": "Torneos obtenidos exitosamente",
  "data": [
    {
      "id": 1,
      "name": "Torneo 1",
      "description": "Primer torneo",
      "rules": "Reglas básicas",
      "startDate": "2025-10-01",
      "endDate": "2025-11-30",
      "active": true,
      "createdByEmail": "admin@email.com"
    },
    {
      "id": 2,
      "name": "Torneo 2",
      "description": "Segundo torneo",
      "rules": "Reglas avanzadas",
      "startDate": "2025-11-01",
      "endDate": "2026-01-31",
      "active": true,
      "createdByEmail": "organizer@email.com"
    }
  ],
  "errorCode": null,
  "timestamp": "2025-11-15T10:17:07"
}
```

---

## ✅ **ÉXITO: Obtener Detalle de Torneo**

**Endpoint:**
```
GET http://localhost:8080/api/tournaments/3
```

**Response: 200 OK**
```json
{
  "success": true,
  "message": "Detalle del torneo obtenido",
  "data": {
    "id": 3,
    "name": "Torneo Principal",
    "description": "Torneo de clasificación",
    "rules": "Reglas FIFA estándar",
    "startDate": "2025-11-15",
    "endDate": "2026-01-20",
    "active": true,
    "createdByEmail": "organizador@utez.edu.mx",
    "teams": [
      {
        "id": 1,
        "name": "Los Rebeldes",
        "ownerEmail": "20233tn207@utez.edu.mx"
      },
      {
        "id": 2,
        "name": "Los Rebeldes 2",
        "ownerEmail": "admin@gmail.com"
      }
    ]
  },
  "errorCode": null,
  "timestamp": "2025-11-15T10:17:07"
}
```

---

## ✅ **ÉXITO: Actualizar Torneo**

**Endpoint:**
```
PUT http://localhost:8080/api/tournaments/3
```

**Headers:**
```
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

**Body:**
```json
{
  "name": "Torneo Actualizado",
  "description": "Nueva descripción",
  "rules": "Nuevas reglas"
}
```

**Response: 200 OK**
```json
{
  "success": true,
  "message": "Torneo actualizado exitosamente",
  "data": {
    "id": 3,
    "name": "Torneo Actualizado",
    "description": "Nueva descripción",
    "rules": "Nuevas reglas",
    "startDate": "2025-11-15",
    "endDate": "2026-01-20",
    "active": true,
    "createdByEmail": "organizador@utez.edu.mx"
  },
  "errorCode": null,
  "timestamp": "2025-11-15T10:17:07"
}
```

---

## ✅ **ÉXITO: Eliminar Torneo**

**Endpoint:**
```
DELETE http://localhost:8080/api/tournaments/5
```

**Headers:**
```
Authorization: Bearer {JWT_TOKEN_ADMIN}
Content-Type: application/json
```

**Response: 200 OK**
```json
{
  "success": true,
  "message": "Torneo eliminado exitosamente",
  "data": null,
  "errorCode": null,
  "timestamp": "2025-11-15T10:17:07"
}
```

---

## ✅ **ÉXITO: Obtener Tabla de Posiciones**

**Endpoint:**
```
GET http://localhost:8080/api/tournaments/3/standings
```

**Response: 200 OK**
```json
{
  "success": true,
  "message": "Tabla de posiciones obtenida",
  "data": [
    {
      "id": 1,
      "teamName": "Los Rebeldes",
      "teamId": 1,
      "played": 5,
      "won": 3,
      "drawn": 1,
      "lost": 1,
      "goalsFor": 12,
      "goalsAgainst": 5,
      "goalDifference": 7,
      "points": 10,
      "position": 1
    },
    {
      "id": 2,
      "teamName": "Los Rebeldes 2",
      "teamId": 2,
      "played": 5,
      "won": 2,
      "drawn": 2,
      "lost": 1,
      "goalsFor": 10,
      "goalsAgainst": 6,
      "goalDifference": 4,
      "points": 8,
      "position": 2
    }
  ],
  "errorCode": null,
  "timestamp": "2025-11-15T10:17:07"
}
```

---

## ✅ **ÉXITO: Obtener Calendario de Partidos**

**Endpoint:**
```
GET http://localhost:8080/api/tournaments/3/matches
```

**Response: 200 OK**
```json
{
  "success": true,
  "message": "Calendario de partidos obtenido",
  "data": [
    {
      "id": 1,
      "homeTeamName": "Los Rebeldes",
      "homeTeamId": 1,
      "awayTeamName": "Los Rebeldes 2",
      "awayTeamId": 2,
      "homeScore": 2,
      "awayScore": 1,
      "matchDate": "2025-11-10T15:00:00",
      "status": "FINISHED"
    },
    {
      "id": 2,
      "homeTeamName": "Los Rebeldes 3",
      "homeTeamId": 3,
      "awayTeamName": "Los Rebeldes 4",
      "awayTeamId": 4,
      "homeScore": 0,
      "awayScore": 0,
      "matchDate": "2025-11-15T18:00:00",
      "status": "PENDING"
    }
  ],
  "errorCode": null,
  "timestamp": "2025-11-15T10:17:07"
}
```

---

## ✅ **ÉXITO: Inscribir Equipo a Torneo**

**Endpoint:**
```
POST http://localhost:8080/api/tournaments/3/teams?teamId=1
```

**Headers:**
```
Authorization: Bearer {JWT_TOKEN_PROPIETARIO}
Content-Type: application/json
```

**Body:** (vacío)

**Response: 201 Created**
```json
{
  "success": true,
  "message": "Equipo inscrito al torneo exitosamente",
  "data": null,
  "errorCode": null,
  "timestamp": "2025-11-15T10:17:07"
}
```

---

## ✅ **ÉXITO: Retirar Equipo de Torneo**

**Endpoint:**
```
DELETE http://localhost:8080/api/tournaments/3/teams/1
```

**Headers:**
```
Authorization: Bearer {JWT_TOKEN_PROPIETARIO_O_ADMIN}
Content-Type: application/json
```

**Response: 200 OK**
```json
{
  "success": true,
  "message": "Equipo retirado del torneo exitosamente",
  "data": null,
  "errorCode": null,
  "timestamp": "2025-11-15T10:17:07"
}
```

---

## ❌ **ERROR: Torneo No Encontrado**

**Response: 404 Not Found**
```json
{
  "success": false,
  "message": "Torneo no encontrado.",
  "data": null,
  "errorCode": "NOT_FOUND",
  "timestamp": "2025-11-15T10:17:07"
}
```

---

## ❌ **ERROR: Equipo No Encontrado**

**Response: 404 Not Found**
```json
{
  "success": false,
  "message": "Equipo no encontrado.",
  "data": null,
  "errorCode": "NOT_FOUND",
  "timestamp": "2025-11-15T10:17:07"
}
```

---

## ❌ **ERROR: No Autorizado - No eres el dueño**

**Endpoint:**
```
POST http://localhost:8080/api/tournaments/3/teams?teamId=2
```

**Headers:**
```
Authorization: Bearer {JWT_TOKEN_OTRO_USUARIO}
```

**Response: 403 Forbidden**
```json
{
  "success": false,
  "message": "No estás autorizado. Solo el dueño del equipo puede inscribirse.",
  "data": null,
  "errorCode": "UNAUTHORIZED",
  "timestamp": "2025-11-15T10:17:07"
}
```

---

## ❌ **ERROR: Equipo Ya Inscrito**

**Endpoint:**
```
POST http://localhost:8080/api/tournaments/3/teams?teamId=1
```

(Si el equipo 1 ya está inscrito en el torneo 3)

**Response: 409 Conflict**
```json
{
  "success": false,
  "message": "El equipo ya está inscrito en este torneo.",
  "data": null,
  "errorCode": "CONFLICT",
  "timestamp": "2025-11-15T10:17:07"
}
```

---

## ❌ **ERROR: Equipo No Está Inscrito**

**Endpoint:**
```
DELETE http://localhost:8080/api/tournaments/3/teams/5
```

(Si el equipo 5 no está inscrito en el torneo 3)

**Response: 409 Conflict**
```json
{
  "success": false,
  "message": "El equipo no está inscrito en este torneo.",
  "data": null,
  "errorCode": "CONFLICT",
  "timestamp": "2025-11-15T10:17:07"
}
```

---

## ❌ **ERROR: Sin Autenticación**

**Endpoint:**
```
POST http://localhost:8080/api/tournaments/3/teams?teamId=1
```

(Sin header `Authorization`)

**Response: 401 Unauthorized**
```json
{
  "success": false,
  "message": "Acceso denegado",
  "data": null,
  "errorCode": "UNAUTHORIZED",
  "timestamp": "2025-11-15T10:17:07"
}
```

---

## ❌ **ERROR: Rol Insuficiente - Solo ADMIN**

**Endpoint:**
```
DELETE http://localhost:8080/api/tournaments/3
```

(Con token de ORGANIZADOR, no ADMIN)

**Response: 403 Forbidden**
```json
{
  "success": false,
  "message": "Acceso denegado",
  "data": null,
  "errorCode": "UNAUTHORIZED",
  "timestamp": "2025-11-15T10:17:07"
}
```

---

## ❌ **ERROR: Error Interno del Servidor**

**Response: 500 Internal Server Error**
```json
{
  "success": false,
  "message": "Error al crear el torneo: Error inesperado en la base de datos",
  "data": null,
  "errorCode": "ERROR",
  "timestamp": "2025-11-15T10:17:07"
}
```

---

## 📊 **Tabla Resumen de Códigos de Error**

| HTTP | errorCode | Significado | Ejemplo |
|------|-----------|------------|---------|
| 201 | N/A | ✅ Creado | Torneo creado exitosamente |
| 200 | N/A | ✅ OK | Operación exitosa |
| 400 | BAD_REQUEST | ❌ Solicitud inválida | Datos incompletos |
| 401 | UNAUTHORIZED | ❌ Sin autenticación | Falta JWT |
| 403 | UNAUTHORIZED | ❌ Permiso denegado | No eres dueño |
| 404 | NOT_FOUND | ❌ No existe | Torneo no encontrado |
| 409 | CONFLICT | ❌ Conflicto | Equipo ya inscrito |
| 500 | ERROR | ❌ Error servidor | Exception no manejada |

---

## 🧪 **Checklist de Pruebas**

- [ ] POST /tournaments (crear) → 201
- [ ] GET /tournaments (listar) → 200
- [ ] GET /tournaments/{id} (detalles) → 200
- [ ] PUT /tournaments/{id} (actualizar) → 200
- [ ] DELETE /tournaments/{id} (eliminar) → 200
- [ ] GET /tournaments/{id}/standings (tabla) → 200
- [ ] GET /tournaments/{id}/matches (calendario) → 200
- [ ] POST /tournaments/{id}/teams (inscribir) → 201
- [ ] DELETE /tournaments/{id}/teams/{teamId} (retirar) → 200
- [ ] Intentar inscribir sin ser dueño → 403
- [ ] Intentar inscribir equipo ya inscrito → 409
- [ ] Torneo inexistente → 404
- [ ] Sin JWT → 401

---

¡Ahora cada respuesta tiene un mensaje claro! 🎉
