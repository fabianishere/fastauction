/*
 * Copyright (c) 2021 Fabian Mastenbroek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import me.champeau.jmh.JMHTask
import org.jetbrains.kotlin.allopen.gradle.*

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.allopen") version "1.6.10"
    id("me.champeau.jmh") version "0.6.6"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
}


repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(17))
    }
    explicitApi()
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf("--enable-preview", "--add-modules", "jdk.incubator.vector"))
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs(listOf("--enable-preview", "--add-modules", "jdk.incubator.vector"))
}

configure<AllOpenExtension> {
    annotation("org.openjdk.jmh.annotations.State")
}

jmh {
    jmhVersion.set("1.34")

    // profilers.add("perfasm")
    profilers.add("gc")

    jvmArgs.addAll(listOf("--enable-preview", "--add-modules", "jdk.incubator.vector"))
    includeTests.set(false) // Do not include tests by default
}

tasks.named("jmh", JMHTask::class) {
    outputs.upToDateWhen { false } // XXX Do not cache the output of this task

    testRuntimeClasspath.setFrom() // XXX Clear test runtime classpath to eliminate duplicate dependencies on classpath
}

tasks.withType<me.champeau.jmh.JmhBytecodeGeneratorTask> {
    jvmArgs.addAll(listOf("--enable-preview", "--add-modules", "jdk.incubator.vector"))
}
