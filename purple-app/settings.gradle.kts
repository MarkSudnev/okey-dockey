
rootProject.name = "purple-app"

include(
  "logging",
  "app:shared",
  "app:presentation",
  "app:domain",
  "app:infrastructure:aws",
  "app:infrastructure:openai",
  "app:infrastructure:postgresql"
)

dependencyResolutionManagement {
  repositories {
    mavenCentral()
  }
}

pluginManagement {
  includeBuild("build-logic")
}

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
