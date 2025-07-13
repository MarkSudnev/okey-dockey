plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "purple-app"

include("acceptance-test", "app:presentation")

dependencyResolutionManagement {
  repositories {
    mavenCentral()
  }
}
