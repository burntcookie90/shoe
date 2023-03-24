import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost

plugins {
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.ksp) apply false
  alias(libs.plugins.maven.publish)
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

mavenPublishing {
  publishToMavenCentral(SonatypeHost.S01, true)
  signAllPublications()
}

