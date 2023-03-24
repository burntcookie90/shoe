plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.ksp)
  alias(libs.plugins.maven.publish)
}

dependencies {
  implementation(project(":annotations"))
  testImplementation(project(":annotations"))
  ksp(libs.autoservice.ksp)
  implementation(libs.autoservice.annotations)
  implementation(libs.kotlin.stdlib)
  testImplementation(libs.kotlin.stdlib)
  implementation(libs.kotlinx.coroutines.core)
  testImplementation(libs.kotlinx.coroutines.core)
  implementation(libs.compose.runtime)
  testImplementation(libs.compose.runtime)
  implementation(libs.kotlinPoet.core)
  implementation(libs.kotlinPoet.ksp)
  implementation(libs.ksp)
  implementation(libs.inject)
  testImplementation(libs.inject)
  testImplementation(kotlin("test"))
//  testImplementation(libs.compileTesting.core)
//  testImplementation(libs.compileTesting.ksp)
  testImplementation(libs.zsweers.compileTesting.core)
  testImplementation(libs.zsweers.compileTesting.ksp)
}

ksp {
  arg("autoserviceKsp.verify", "true")
  arg("autoserviceKsp.verbose", "true")
}

tasks.test {
  useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  kotlinOptions.freeCompilerArgs += "-Xopt-in=com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview"
}
