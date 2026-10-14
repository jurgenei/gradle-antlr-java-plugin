# Gradle ANTLR Java Plugin

![Conformance](https://img.shields.io/badge/Conformance-Check--All%20Passing-brightgreen)

[![Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/name.jurgenei.gradle.antlr.java?label=Plugin%20Portal)](https://plugins.gradle.org/plugin/name.jurgenei.gradle.antlr.java)
[![Build and Test](https://github.com/jurgenei/gradle-antlr-java-plugin/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/jurgenei/gradle-antlr-java-plugin/actions/workflows/ci.yml?query=branch%3Amain)
[![Coverage CI](https://github.com/jurgenei/gradle-antlr-java-plugin/actions/workflows/coverage.yml/badge.svg?branch=main)](https://github.com/jurgenei/gradle-antlr-java-plugin/actions/workflows/coverage.yml?query=branch%3Amain)
[![Coverage](https://codecov.io/gh/jurgenei/gradle-antlr-java-plugin/graph/badge.svg?branch=main)](https://app.codecov.io/gh/jurgenei/gradle-antlr-java-plugin?branch=main)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/java-21+-green.svg)](https://www.oracle.com/java/)
[![Gradle](https://img.shields.io/badge/gradle-8+-blue.svg)](https://gradle.org/)

`gradle-antlr-java-plugin` provides preconfigured XML AST conversion support for Java sources using ANTLR Java grammars.

## Install

```groovy
plugins {
    id 'name.jurgenei.gradle.antlr.java' version '0.1.1'
}
```

## What Plugin Adds

- `XmlAstJavaGradleTask` task type
- `javaXmlAst` pre-registered task
- Runtime classpath and `classes` dependency wiring when `java` plugin present

Default task conventions:

- `grammar = java`
- `parserClassName = name.jurgenei.parsers.JavaParser`
- `lexerClassName = name.jurgenei.parsers.JavaLexer`
- `startRule = compilationunit`
- `includes = ['**/*.java']`

## Quick Start

```groovy
plugins {
    id 'java'
    id 'name.jurgenei.gradle.antlr.java'
}

tasks.named('javaXmlAst', name.jurgenei.gradle.antlr.XmlAstJavaGradleTask) {
    sourceDirectory.set(layout.projectDirectory.dir('src/main/java'))
    destinationDirectory.set(layout.buildDirectory.dir('xmlast-java'))
    targetExtension.set('.xml')
    continueOnError.set(true)
}
```

Run:

```bash
./gradlew javaXmlAst
```

S-expression output variant:

```groovy
tasks.named('javaXmlAst', name.jurgenei.gradle.antlr.XmlAstJavaGradleTask) {
    targetExtension.set('.xir')
    outputFormat.set('beautified')
}
```

## Testing and Coverage

- Java grammar sources:
  - `src/main/antlr/name/jurgenei/parsers/JavaLexer.g4`
  - `src/main/antlr/name/jurgenei/parsers/JavaParser.g4`
- Example corpus copied from `antlr/grammars-v4`:
  - `src/test/resources/examples`

Commands:

```bash
./gradlew clean check
./gradlew coverage
./gradlew allSecurityChecks
```

Coverage gate enforced in `check`: **60% line coverage minimum**.

## Publish to Gradle Plugin Portal

Set credentials:

```bash
export GRADLE_PUBLISH_KEY=...
export GRADLE_PUBLISH_SECRET=...
```

Optional signing for Maven publication:

```bash
export ORG_GRADLE_PROJECT_signingKey=...
export ORG_GRADLE_PROJECT_signingPassword=...
```

Publish:

```bash
./gradlew publishPlugins
```

## CI and Code Scans

- `.github/workflows/ci.yml` -> build + tests + SpotBugs + OWASP Dependency-Check
- `.github/workflows/coverage.yml` -> JaCoCo + Codecov upload
- `.github/workflows/codeql.yml` -> GitHub CodeQL analysis
- `.github/workflows/test-on-push.yml` -> fast test validation

## Troubleshooting

- `ClassNotFoundException` for parser/lexer:
  - Ensure generated sources run (`generateParserSources`)
  - Ensure `classes` completed before task execution
- No output files:
  - Check `sourceDirectory` and `includes`
  - Use `force = true` for full rerun
- Parse entry failures:
  - Confirm parser start rule exists (`compilationunit`)
