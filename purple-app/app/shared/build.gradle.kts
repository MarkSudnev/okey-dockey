plugins {
  id("purple-common-conventions")
}

dependencies {
  testFixtures(libs.hikari)
  testFixtures(libs.database.h2)
}
