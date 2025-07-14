import org.gradle.accessors.dm.LibrariesForLibs

plugins {
  kotlin("jvm")
}

val libs = the<LibrariesForLibs>()

dependencies {
  implementation(libs.result4k)

  testImplementation(libs.kotlin.test)
  testImplementation(libs.junit.jupiter.engine)
  testImplementation(libs.http4k.kotest)
}

val jvmVersion: String by project

java.toolchain.languageVersion.set(JavaLanguageVersion.of(jvmVersion))
kotlin.jvmToolchain(jvmVersion.toInt())

tasks.named<Test>("test") {
  useJUnitPlatform()
}
