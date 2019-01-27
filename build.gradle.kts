plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm").version("1.3.11")

  application
}

group = "org.baaahs"
version = "1.0-SNAPSHOT"

//sourceCompatibility = 1.8

repositories {
  mavenCentral()
}

dependencies {
  compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  implementation("com.github.sarxos:webcam-capture:0.3.12")

  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
  testCompile("junit:junit:4.12")
}

//compileKotlin {
//    kotlinOptions.jvmTarget = "1.8"
//}
//compileTestKotlin {
//    kotlinOptions.jvmTarget = "1.8"
//}

application {
  mainClassName = "org.baaahs.Main"
}