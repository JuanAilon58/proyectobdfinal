@echo off
echo ==================================================
echo   FLASHBUY CHAOS DEMO - REGLA 5
echo ==================================================

:menu
echo.
echo 1. Matar una replica de Catalog Service (Auto-healing)
echo 2. Detener Redis (Validar resiliencia de Cache)
echo 3. Simular caida de Notification Service (Validar colas Kafka)
echo 4. Reiniciar Postgres (Validar persistencia de datos)
echo 5. Salir
echo.
set /p opt="Selecciona una accion destructiva: "

if %opt%==1 (
    echo [!] Matando contenedor de Catalog...
    docker stop flashbuy-catalog-service-1
    echo [v] Verifica en Grafana como las otras 2 replicas absorben el trafico.
    goto menu
)

if %opt%==2 (
    echo [!] Deteniendo Redis...
    docker stop flashbuy-redis
    echo [v] El Cart Service seguira vivo pero con degradacion controlada.
    goto menu
)

if %opt%==3 (
    echo [!] Deteniendo Notification Service...
    docker stop flashbuy-notification-service-1
    echo [v] Los eventos se acumularan en Kafka. Al re-iniciarlo, se procesaran sin perdida.
    goto menu
)

if %opt%==4 (
    echo [!] Reiniciando Base de Datos...
    docker restart flashbuy-postgres
    echo [v] Gracias a los volumenes, no se perdera ninguna orden ni producto.
    goto menu
)

if %opt%==5 exit
