plugins {
  id("kotlin-common-conventions")
  application
}

dependencies {
  implementation(platform(libs.http4k.bom))
  implementation(libs.http4k.core)

}

application {
  mainClass = "pl.sudneu.purple.presentation.ServiceKt"
}
