plugins {
  id("purple-common-conventions")
  application
}

dependencies {
  implementation(project(":app:domain"))
  implementation(project(":app:infrastructure:aws"))
  implementation(platform(libs.http4k.bom))
  implementation(libs.http4k.core)
  implementation(libs.http4k.config)
  implementation(libs.http4k.connect.amazon.s3)
  implementation(libs.kafka.clients)
  implementation(libs.kafka.connect.json)
  implementation(libs.database.postgresql)
  implementation(libs.hikari)
  implementation(libs.jackson.kotlin)

  testImplementation(libs.http4k.kotest)
  testImplementation(libs.database.h2)
  testImplementation(libs.http4k.connect.amazon.s3.fake)
}

application {
  mainClass = "pl.sudneu.purple.presentation.ServiceKt"
}
