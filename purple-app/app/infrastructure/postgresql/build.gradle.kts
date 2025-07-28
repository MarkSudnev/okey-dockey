plugins {
  id("purple-common-conventions")
}

dependencies {
  implementation(project(":app:domain"))
  testImplementation(libs.hikari)
  testImplementation(libs.database.h2)
}
