plugins {
  id("purple-common-conventions")
}

dependencies {
  implementation(project(":app:domain"))
  implementation(platform(libs.http4k.bom))
  implementation(libs.http4k.core)
  implementation(libs.http4k.jackson)
  implementation(libs.jackson.kotlin)
}
