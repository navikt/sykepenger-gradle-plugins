import no.nav.sykepenger.gradleplugins.SykepengerVersions

plugins {
    id("no.nav.sykepenger.module")
    id("org.jetbrains.kotlin.jvm")
}

plugins.withId("java-test-fixtures") {
    configurations.named("testFixturesImplementation") {
        extendsFrom(configurations.named("implementation").get())
    }
    configurations.named("testImplementation") {
        extendsFrom(configurations.named("testFixturesImplementation").get())
    }
}

kotlin {
    jvmToolchain(25)
}

dependencies {
    implementation(platform(SykepengerVersions.KTOR_BOM))
    implementation(platform(SykepengerVersions.MICROMETER_BOM))
    implementation(platform(SykepengerVersions.NETTY_BOM))
    implementation(platform(SykepengerVersions.PROMETHEUS_METRICS_BOM))
    implementation(platform(SykepengerVersions.JACKSON3_BOM))
    implementation(platform(SykepengerVersions.JACKSON2_BOM))
    implementation(platform(SykepengerVersions.JETTY_BOM))
    implementation(platform(SykepengerVersions.JETTY_EE10_BOM))

    testImplementation(platform(SykepengerVersions.JUNIT_BOM))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(kotlin("test"))

    constraints {
        implementation(SykepengerVersions.OPENTELEMETRY_API)
        implementation(SykepengerVersions.LOGBACK_CORE)
        implementation(SykepengerVersions.LOGBACK_CLASSIC)
        implementation(SykepengerVersions.HANDLEBARS)
        implementation(SykepengerVersions.LZ4_JAVA)
        implementation(SykepengerVersions.KAFKA_CLIENTS)
        implementation(SykepengerVersions.SNAPPY_JAVA) {
            because("GHSA-q233-g92q-qxp6, GHSA-xqrx-qh46-34m7, GHSA-gwq9-9ffj-mvrv: kafka-clients drar inn sårbar snappy-java 1.1.10.7")
        }
        implementation(SykepengerVersions.BCPROV)
        implementation(SykepengerVersions.BCPKIX)
        implementation(SykepengerVersions.BCUTIL)
        implementation(SykepengerVersions.GUAVA) {
            because("GHSA-xxph-c9ww-hj94: ktor-server-auth-jwt drar inn sårbar Guava 33.6.0-jre via jwks-rsa")
        }
        implementation(SykepengerVersions.HTTPCLIENT5) {
            because(
                "wiremock 3.13.2 drar inn sårbar httpclient5 5.5.1 med httpcore5/httpcore5-h2 5.3.6. " +
                    "httpclient5 5.6.5 drar inn httpcore5/httpcore5-h2 5.4.4, som også retter sårbarhetene der.",
            )
        }
        implementation(SykepengerVersions.HTTPCLIENT5_FLUENT)
        implementation(SykepengerVersions.SWAGGER_PARSER) {
            because(
                "GHSA-3w8q-xq97-5j7x: smiley4 ktor-openapi drar inn swagger-parser 2.1.39, som via " +
                    "swagger-compat-spec-parser 1.0.75 drar inn sårbar rhino 1.7.7.2. " +
                    "Kan fjernes når smiley4 bruker swagger-parser 2.1.48 eller nyere.",
            )
        }
    }
}

tasks {
    named<Test>("test") {
        useJUnitPlatform()
        testLogging {
            events("skipped", "failed")
            showStackTraces = true
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }
}
