plugins {
  id("purple-common-conventions")
}

dependencies {
  implementation(project(":app:domain"))
  implementation(libs.http4k.connect.amazon.s3)

  testImplementation(libs.http4k.connect.amazon.s3.fake)
}
