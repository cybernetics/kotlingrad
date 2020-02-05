plugins {
  idea
  kotlin("jvm") version "1.4.0-dev-2139"
  id("de.fayard.refreshVersions") version "0.8.6"
}

allprojects {
  group = "edu.umontreal"
  version = "0.2.4"

  repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/kotlin-dev")
  }
}

idea.module {
  excludeDirs.add(file("latex"))
  isDownloadJavadoc = true
  isDownloadSources = true
}