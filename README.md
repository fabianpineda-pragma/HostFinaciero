# HostFinanciero · Android

App nativa **«Mi Banco»** (Kotlin + Views) que sirve de punto de partida para la charla *Flutter add-to-app*.

Es una app completa por sí sola: un formulario de solicitud de crédito con validación, una vista previa en vivo de los datos que viajarán por el canal y un banner superior **«Android Nativo»** para identificar la plataforma en la demo.

**Todavía no conoce a Flutter.** Los comentarios del código marcan dónde entra cada paso de la parte práctica:

| Paso | Archivo |
|---|---|
| A1 · revisar el host | `gradle.properties`, `app/build.gradle.kts` |
| A2 · repositorios y el módulo como subproyecto | `settings.gradle.kts` |
| A3 · Java 17, ABIs y la dependencia `:flutter` | `app/build.gradle.kts` |
| A4 · precalentar el engine | nueva clase `Application` + `AndroidManifest.xml` |
| A5 · la Activity de Flutter y el canal | nueva `SimuladorActivity` + `AndroidManifest.xml` |
| A6 · enviar los parámetros, abrir y recibir el resultado | `MainActivity.kt` |

## Requisitos y ejecución

AGP 8.9.1 · Gradle 8.12 · Kotlin 2.1.0 · JDK 17 para ejecutar Gradle · compileSdk/targetSdk 36 · minSdk 24.

```bash
./gradlew :app:installDebug
```

El módulo Flutter (`financial_simulator`) se espera **al lado** de esta carpeta.
