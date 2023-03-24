plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.maven.publish)
}

group = "com.vishnurajeevan"
version = "1.0-SNAPSHOT"

dependencies {
  implementation(libs.kotlin.stdlib)
}

