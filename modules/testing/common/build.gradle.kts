plugins {
    id("org.springframework.boot") apply false

    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    api("org.junit.jupiter:junit-jupiter")
    api("net.jqwik:jqwik:1.3.10")
    api("io.mockk:mockk:1.10.5")
    api("org.assertj:assertj-core")
    api("io.kotlintest:kotlintest-assertions:3.4.2")
    api("com.h2database:h2")

    api("info.novatec.testit:testutils-logrecorder-logback:0.3.4")

    api("org.springframework.boot:spring-boot-starter-test")
}
