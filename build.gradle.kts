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
  // Testing
  testImplementation(platform("org.junit:junit-bom:6.0.0"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")

  // JAXB
  runtimeOnly("org.glassfish.jaxb:jaxb-runtime:4.0.3")

  // Jakarta
  implementation("jakarta.platform:jakarta.jakartaee-api:10.0.0")

  // Apache POI
  implementation("org.apache.poi:poi:5.5.1")
  implementation("org.apache.poi:poi-ooxml:5.5.1")
}

tasks.test {
  useJUnitPlatform()
}