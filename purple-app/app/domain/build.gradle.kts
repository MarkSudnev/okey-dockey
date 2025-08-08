plugins {
  id("purple-common-conventions")
}

dependencies {
  implementation(project(":app:shared"))
  testImplementation(libs.mockk)
}
