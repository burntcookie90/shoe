plugins {
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.ksp) apply false
}

buildscript {
  dependencies {
    classpath(kotlin("gradle-plugin", version = "1.8.10"))
  }
}

group = "com.vishnurajeevan"
version = "1.0-SNAPSHOT"

allprojects {
  repositories {
    mavenCentral()
  }
}
