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

  plugins.withId("com.vanniktech.maven.publish.base") {
    configure<MavenPublishBaseExtension> {

      pom {
        name.set("shoe")
        description.set("A ksp processor to shoe in your composable presenters.")
        inceptionYear.set("2023")
        url.set("https://github.com/burntcookie90/shoe")
        licenses {
          license {
            name.set("The Apache License, Version 2.0")
            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
          }
        }
        developers {
          developer {
            id.set("burntcookie90")
            name.set("Vishnu Rajeevan")
            url.set("https://github.com/burntcookie90")
          }
        }
        scm {
          url.set("https://github.com/burntcookie90/shoe")
          connection.set("scm:git:git://github.com/burntcookie90/shoe.git")
          developerConnection.set("scm:git:ssh://git@github.com/burntcookie90/shoe.git")
        }
      }
    }
  }
}

mavenPublishing {
  publishToMavenCentral(SonatypeHost.DEFAULT)

  signAllPublications()
}

