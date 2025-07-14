plugins {
  id("kotlin-common-conventions")
  application
}

dependencies {
  implementation(platform(libs.http4k.bom))
  implementation(libs.http4k.core)
  implementation(libs.http4k.config)
  implementation(libs.http4k.connect.amazon.s3)
  testImplementation(libs.http4k.kotest)

}

application {
  mainClass = "pl.sudneu.purple.presentation.ServiceKt"
}
