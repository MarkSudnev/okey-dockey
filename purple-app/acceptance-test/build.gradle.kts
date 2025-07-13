plugins {
  alias(libs.plugins.jvm)
}

dependencies {
  testImplementation(platform(libs.http4k.bom))
  testImplementation(libs.http4k.core)
  testImplementation(libs.http4k.client.okhttp)
  testImplementation(libs.http4k.kotest)
  testImplementation(libs.kotlin.test)
  testImplementation(libs.kotest.assertions)
  testImplementation(libs.junit.jupiter.engine)
  testImplementation(libs.junit.jupiter.params)
  testImplementation(kotlin("test"))
}

kotlin {
  jvmToolchain(21)
}

tasks.named<Test>("test") {
  useJUnitPlatform()
}
