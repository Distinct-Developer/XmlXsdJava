plugins {
  id("java")
  id("application")
}

application {
  mainClass.set("za.co.distincttech.XmlXsdMain")
}

group = "za.co.distincttech"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  testImplementation(platform("org.junit:junit-bom:6.0.0"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")

  implementation("jakarta.platform:jakarta.jakartaee-api:10.0.0")
  runtimeOnly("org.glassfish.jaxb:jaxb-runtime:4.0.3")
}

tasks.test {
  useJUnitPlatform()
}