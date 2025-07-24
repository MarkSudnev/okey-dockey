
rootProject.name = "purple-app"

include("app:presentation", "app:domain", "app:infrastructure")

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
