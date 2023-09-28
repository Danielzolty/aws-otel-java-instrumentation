import com.google.cloud.tools.jib.gradle.BuildImageTask
import com.google.cloud.tools.jib.gradle.JibExtension
import software.amazon.adot.configureImages

plugins {
  java

  application
  id("java-library")
  id("com.google.cloud.tools.jib")
}

dependencies {
  implementation("commons-logging:commons-logging")
  implementation("com.sparkjava:spark-core")
  api("com.sparkjava:spark-core:2.9.4")
  api("io.opentelemetry:opentelemetry-api:1.30.0")
  api("com.squareup.okhttp3:okhttp:4.11.0")
  api(platform("software.amazon.awssdk:bom:2.20.144"))
  api("software.amazon.awssdk:s3")
  // implementation("io.opentelemetry:opentelemetry-api")
  implementation("log4j:log4j:1.2.17")
  // implementation("org.slf4j:log4j-over-slf4j:1.7.13")
  // implementation("org.apache.logging.log4j:log4j-core")
  implementation("software.amazon.awssdk:s3")
  implementation("software.amazon.awssdk:sts")
//  implementation("org.slf4j:slf4j-api")
//  implementation("org.slf4j:slf4j-simple")

  // runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl")
}

application {
  mainClass.set("com.amazon.sampleapp.App")
}

configurations {
  // In order to test the real log4j library we need to remove the log4j transitive
  // dependency 'log4j-over-slf4j' brought in by :testing-common which would shadow
  // the log4j module under test using a proxy to slf4j instead.
  testImplementation {
    exclude("org.slf4j", "log4j-over-slf4j")
  }
}

jib {
  configureImages(
    "public.ecr.aws/aws-otel-test/aws-opentelemetry-java-base:alpha",
    "public.ecr.aws/aws-otel-test/aws-otel-java-spark-log4j1",
    localDocker = rootProject.property("localDocker")!!.equals("true"),
    multiPlatform = !rootProject.property("localDocker")!!.equals("true"),
    tags = setOf("latest", "${System.getenv("COMMIT_HASH")}"),
  )
}

tasks {
  named("jib") {
    dependsOn(":otelagent:jib")
  }
  named("jibDockerBuild") {
    dependsOn(":otelagent:jibDockerBuild")
  }
  register<BuildImageTask>("jibBuildWithoutAgent") {
    val j = JibExtension(project)
    j.configureImages(
      "eclipse-temurin:17",
      "public.ecr.aws/aws-otel-test/aws-otel-java-spark-without-auto-instrumentation-agent",
      localDocker = false,
      multiPlatform = !rootProject.property("localDocker")!!.equals("true"),
      tags = setOf("latest", "${System.getenv("COMMIT_HASH")}"),
    )
    setJibExtension(j)
  }
}
