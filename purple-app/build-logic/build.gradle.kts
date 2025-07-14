plugins {
  `kotlin-dsl`
}

repositories {
  mavenCentral()
}

val jvmVersion: String by project

java.toolchain.languageVersion.set(JavaLanguageVersion.of(jvmVersion))

dependencies {
  implementation(libs.kotlin.gradle.plugin)
  implementation(libs.kotlin.stdlib)
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
