import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `maven-publish`
  id("shipshape")
  idea
}

// TODO: Maybe move this into the plugin somehow?
val generatedSourcesPath = file("src/main/kotlin/gen")
kotlin.sourceSets["main"].kotlin.srcDir(generatedSourcesPath)

idea.module {
  generatedSourceDirs.add(generatedSourcesPath)
}

dependencies {
//  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.0")
  api("com.github.breandan:kaliningraph")

  // Mathematical libraries
  implementation("ch.obermuhlner:big-math:2.3.0")
  implementation("org.jetbrains.bio:viktor:1.0.1")

  // Needed for codegen
  implementation("org.jetbrains.kotlin:kotlin-reflect")

//  val kmathVersion by extra { "0.2.0-dev-2" }
//  testImplementation("kscience.kmath:kmath-core:$kmathVersion")
//  testImplementation("kscience.kmath:kmath-ast:$kmathVersion")
//  testImplementation("kscience.kmath:kmath-prob:$kmathVersion")
//  implementation("com.ionspin.kotlin:bignum:0.1.0")
//  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:_")

  testImplementation("org.nd4j:nd4j-native-platform:1.0.0-beta7")

//  val tfVersion by extra { "-SNAPSHOT" }
//  testImplementation("com.github.tensorflow:java:$tfVersion")
//  testImplementation("com.github.tensorflow:tensorflow-core-platform:$tfVersion")
  val ejmlVersion = "0.39"
  testImplementation("org.ejml:ejml-kotlin:$ejmlVersion")
  testImplementation("org.ejml:ejml-all:$ejmlVersion")
  testImplementation("com.github.breandan:tensor:master-SNAPSHOT")

  // Property-based testing
//  testImplementation("io.kotest:kotest-runner-junit5:4.3.0")
//  testImplementation("io.kotest:kotest-assertions-core:4.3.0")
//  testImplementation("io.kotest:kotest-property:4.3.0")
  testImplementation("io.kotlintest:kotlintest-runner-junit5:3.4.2")
  testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")

  // Symbolic fuzzing interpreter
  testImplementation("org.jetbrains.kotlin:kotlin-scripting-jsr223")
}

tasks {
  withType<KotlinCompile> { dependsOn("genShapes") }

  val genNotebookJSON by creating(JavaExec::class) {
    main = "edu.umontreal.kotlingrad.utils.codegen.NotebookGenKt"
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf(projectDir.path, project.version.toString())
  }

  val installPathLocal =
    "${System.getProperty("user.home")}/.jupyter_kotlin/libraries"

  val jupyterInstall by registering(Copy::class) {
    dependsOn(genNotebookJSON)
    dependsOn("publishToMavenLocal")
    val installPath = findProperty("ath") ?: installPathLocal
    doFirst { mkdir(installPath) }
    from(file("kotlingrad.json"))
    into(installPath)
    doLast { logger.info("Kotlin∇ notebook was installed in: $installPath") }
  }

  val sourcesJar by registering(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
  }
}

publishing {
  publications.create<MavenPublication>("default") {
    from(components["java"])
    artifact(tasks["sourcesJar"])

    pom {
      description.set("Kotlin∇: Differentiable Functional Programming with Algebraic Data Types")
      name.set("Kotlin∇")
      url.set("https://github.com/breandan/kotlingrad")
      licenses {
        license {
          name.set("The Apache Software License, Version 1.0")
          url.set("http://www.apache.org/licenses/LICENSE-3.0.txt")
          distribution.set("repo")
        }
      }
      developers {
        developer {
          id.set("Breandan Considine")
          name.set("Breandan Considine")
          email.set("bre@ndan.co")
          organization.set("Université de Montréal")
        }
      }
      scm {
        url.set("https://github.com/breandan/kotlingrad")
      }
    }
  }
}