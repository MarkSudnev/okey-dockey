plugins {
  id("kotlin-common-conventions")
}

dependencies {
  testImplementation(project(":app:presentation"))
  testImplementation(platform(libs.http4k.bom))
  testImplementation(libs.http4k.core)
  testImplementation(libs.http4k.client.okhttp)
  testImplementation(libs.http4k.connect.amazon.s3.fake)
  testImplementation(libs.http4k.kotest)
  testImplementation(kotlin("test"))
}
