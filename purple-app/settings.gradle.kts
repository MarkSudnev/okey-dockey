
rootProject.name = "purple-app"

include("acceptance-test", "app:presentation", "app:domain")

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
