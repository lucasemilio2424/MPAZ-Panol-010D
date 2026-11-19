# MPAZ Pañol

Aplicación Android de gestión de inventario, solicitudes, préstamos y devoluciones de equipamiento de la Escuela Escritora Marcela Paz.

## Desarrollo
- Curso: DSY1105, sección 010D, Duoc UC.
- Desarrollo individual: Lucas Arenas.
- Android: Kotlin, Jetpack Compose, MVVM, Room (SQLite), Retrofit, recursos nativos de batería y vibración.
- Servicios propios: Spring Boot, Docker, PostgreSQL (Supabase), despliegue Render.
- Backend: https://github.com/lucasemilio2424/MPAZ-Panol-Backend-010D
- Gestión y seguimiento: https://trello.com/b/708Ixk0p/mpaz-panol-010d

## Funcionalidades
- Inicio de sesión y registro local de usuarios con roles ADMIN y SOLICITANTE.
- Catálogo de equipos y control de existencias.
- Creación y edición de equipos.
- Solicitudes de préstamo, aprobación, rechazo, entrega y devolución.
- Actualización del stock a través del backend en la nube.
- Información meteorológica actual de Santiago mediante API pública.
- Lectura de batería y vibración de confirmación.

## Endpoints
### API propia: Equipos
Base: https://mpaz-equipos-010d.onrender.com/
- GET /api/equipos
- GET /api/equipos/{id}
- POST /api/equipos
- PUT /api/equipos/{id}
- POST /api/equipos/{id}/reservar?cantidad=N
- POST /api/equipos/{id}/devolver?cantidad=N

### API propia: Solicitudes
Base: https://mpaz-solicitudes-010d.onrender.com/
- GET /api/solicitudes
- GET /api/solicitudes/{id}
- POST /api/solicitudes
- PUT /api/solicitudes/{id}/aprobar
- PUT /api/solicitudes/{id}/rechazar
- PUT /api/solicitudes/{id}/entregar
- PUT /api/solicitudes/{id}/devolver

### API externa
Open-Meteo, sin clave, integrada con Retrofit en la pantalla inicial:
- GET https://api.open-meteo.com/v1/forecast?latitude=-33.45&longitude=-70.66&current=temperature_2m,precipitation,wind_speed_10m&timezone=America%2FSantiago

## Ejecución
1. Clonar la rama `experiencia3` en Android Studio.
2. Sincronizar Gradle.
3. Ejecutar el módulo `app` en emulador o dispositivo Android con internet.
4. Los dos microservicios deben estar accesibles en Render; la primera petición puede demorar por suspensión de instancias gratuitas.
5. Ejecutar pruebas Android: `./gradlew testDebugUnitTest` en macOS/Linux o `gradlew.bat testDebugUnitTest` en Windows.
6. Construir APK firmado desde Build > Generate Signed App Bundle or APK > APK > release.

## Arquitectura y documentación
Dos diagramas se encuentran en la rama `documents`, directorio `docs/`.
Los datos de usuarios se almacenan localmente en Room/SQLite; equipos y solicitudes se almacenan en PostgreSQL a través de microservicios.
La rama `experiencia3` contiene la integración cloud Android.

## Firma y APK
- APK firmado de **release** generado el 19/11/2026: [descargar/consultar app-release.apk](app/release/app-release.apk) (aprox. 12,3 MB). Compilación de firma completada correctamente en Android Studio.
- Evidencia de Git: commit de APK `0865fd2` en rama `experiencia3` ([ver commit](https://github.com/lucasemilio2424/MPAZ-Panol-010D/commit/0865fd2)).
- Entrega preparada por el estudiante: `MPAZ_Panol_Android.zip`, `MPAZ_Panol_Backend.zip`, `MPAZ_Panol_Diagramas.zip` y APK firmada, para adjuntar en AVA.
- Archivo de firma: `MPAZ_Panol_Release.jks`; conservar en equipo local seguro y entregar al docente únicamente por medio privado si lo exige.
- No publicar claves privadas, contraseñas ni archivos `.jks` en GitHub público.
- Evidencia visual de generación y ubicación de `app-release.apk`, y de la creación del `.jks`: adjuntar capturas en la entrega de AVA. No publicar pantallas que revelen contraseñas.
- Diagrama de microservicios: [ver imagen](https://github.com/lucasemilio2424/MPAZ-Panol-010D/blob/documents/docs/01-diagrama-microservicios.png).
- Diagrama Android MVVM: [ver imagen](https://github.com/lucasemilio2424/MPAZ-Panol-010D/blob/documents/docs/02-diagrama-android-mvvm.png).
- Enlace al tablero de Trello: https://trello.com/b/708Ixk0p/mpaz-panol-010d.

## Pruebas y alcance
- Backend: se han ejecutado 9 pruebas JUnit con éxito.
- Android: pruebas JUnit de reglas de solicitudes en `app/src/test`. La cobertura global del 80 % debe medirse antes de declararla satisfecha.
- Flujo cloud comprobado: creación de equipo, solicitud de 2 unidades, aprobación, entrega, descuento y devolución del stock.
- Uso educativo con datos ficticios: las operaciones administrativas del backend público requieren autenticación y autorización en el servidor antes de operar con datos reales.
