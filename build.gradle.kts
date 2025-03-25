import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.9.25"
	jacoco
}

group = "ar.edu.unsam.algo2"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}
val kotestVersion = "5.9.1"
dependencies {
	implementation(kotlin("stdlib"))
	implementation("org.uqbar-project:geodds-xtend:1.0.3")
	testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
	testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
	testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "21"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.test {
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
}

jacoco {
	toolVersion = "0.8.12"
}

tasks.jacocoTestReport {
	reports {
		xml.required.set(true)
		csv.required.set(false)
		html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
	}
}

tasks.register("runOnGitHub") {
	dependsOn("jacocoTestReport")
	group = "custom"
	description = "$ ./gradlew runOnGitHub # runs on GitHub Action"
}
