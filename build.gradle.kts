plugins {
    id("java")
    war
}

group = "com.github.losevskiyfz"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("jakarta.ejb:jakarta.ejb-api:4.0.1")
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.1.0")
    implementation("jakarta.validation:jakarta.validation-api:3.1.1")

    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")
    implementation("org.eclipse.persistence:eclipselink:4.0.5")
    implementation("org.xerial:sqlite-jdbc:3.49.1.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}