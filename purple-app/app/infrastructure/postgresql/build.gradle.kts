plugins {
  id("purple-common-conventions")
}

dependencies {
  implementation(project(":app:domain"))
  implementation(libs.bundles.postgresql)
  testImplementation(libs.hikari)
  testImplementation(libs.testcontainers.postgresql)
}
