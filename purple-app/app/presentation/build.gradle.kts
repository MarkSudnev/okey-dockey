plugins {
  alias(libs.plugins.jvm)
  application
}

dependencies {
  implementation(platform(libs.http4k.bom))
  implementation(libs.http4k.core)

  testImplementation(libs.kotlin.test)
  testImplementation(libs.junit.jupiter.engine)
  testImplementation(libs.http4k.kotest)
}

kotlin {
  jvmToolchain(21)
}

application {
  mainClass = "pl.sudneu.purple.presentation.ServiceKt"
}

tasks.named<Test>("test") {
  useJUnitPlatform()
}

