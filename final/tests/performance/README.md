# Guía de Ejecución de Pruebas de Rendimiento (Regla 4)

Este documento detalla cómo ejecutar las pruebas y dónde observar las métricas requeridas.

## 1. Requisitos
- [k6](https://k6.io/) instalado localmente.
- Docker Compose corriendo (`docker-compose up -d`).

## 2. Ejecución de Pruebas

### Carga Mínima (50k peticiones)
```bash
k6 run --vus 50 --duration 10m final/tests/performance/load-test.js
```

### Picos (Spikes)
```bash
k6 run final/tests/performance/spike-test.js
```

### Falla Inducida (Chaos Test)
Mientras corre un test de carga, ejecuta:
```bash
docker stop flashbuy-catalog-1
```
Observa cómo el API Gateway activa el **Circuit Breaker** y el sistema sigue operando en modo degradado.

## 3. Métricas a Reportar (Prometheus/Grafana)
Accede a Grafana (`http://localhost:3000`) para ver:
- **Throughput:** Peticiones por segundo (RPS) en el Gateway.
- **Latencia:** `http_req_duration` (avg, p95, p99) reportado por k6.
- **Tasa de Error:** % de respuestas HTTP 5xx.
- **Uso de Recursos:** Panel de "Docker Container Metrics" para CPU y Memoria.
- **Comportamiento ante caídas:** El tiempo que tarda Docker en levantar una nueva réplica (Auto-healing).
