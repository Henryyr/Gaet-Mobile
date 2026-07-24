plugins {
    kotlin("jvm")
    id("application")
}

dependencies {
    implementation("com.google.firebase:firebase-admin:9.4.2")
    implementation(project(":core-utils"))
}

application {
    mainClass.set("com.example.gaetdriver.admin.DatabaseSeederKt")
}

tasks.withType<JavaExec> {
    workingDir = rootProject.projectDir
}