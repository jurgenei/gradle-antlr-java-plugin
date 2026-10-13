package name.jurgenei.gradle.antlr;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class XmlAstJavaPluginFunctionalTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void registersJavaXmlAstTask() throws Exception {
        final File projectDir = temporaryFolder.newFolder("functional-registers-java-xmlast");
        writeSettings(projectDir);
        writeBuildFile(projectDir, """
                plugins {
                    id 'java'
                    id 'name.jurgenei.gradle.antlr.java'
                }
                """);

        final BuildResult result = run(projectDir, "tasks", "--all");
        Assert.assertTrue("Expected javaXmlAst task to be listed", result.getOutput().contains("javaXmlAst"));
    }

    @Test
    public void preconfiguredDefaultsAreApplied() throws Exception {
        final File projectDir = temporaryFolder.newFolder("functional-java-defaults");
        writeSettings(projectDir);
        writeBuildFile(projectDir, """
                plugins {
                    id 'java'
                    id 'name.jurgenei.gradle.antlr.java'
                }

                tasks.register('printJavaDefaults') {
                    doLast {
                        def t = tasks.named('javaXmlAst').get()
                        println "grammar=${t.grammar.get()}"
                        println "parserClassName=${t.parserClassName.get()}"
                        println "lexerClassName=${t.lexerClassName.get()}"
                        println "startRule=${t.startRule.get()}"
                        println "targetExtension=${t.targetExtension.get()}"
                        println "outputFormat=${t.outputFormat.get()}"
                    }
                }
                """);

        final BuildResult result = run(projectDir, "printJavaDefaults");
        final String output = result.getOutput();

        Assert.assertTrue(output.contains("grammar=java"));
        Assert.assertTrue(output.contains("parserClassName=name.jurgenei.parsers.JavaParser"));
        Assert.assertTrue(output.contains("lexerClassName=name.jurgenei.parsers.JavaLexer"));
        Assert.assertTrue(output.contains("startRule=compilationunit"));
        Assert.assertTrue(output.contains("targetExtension=.xml"));
        Assert.assertTrue(output.contains("outputFormat=compact"));
    }

    @Test
    public void supportsXirOutputConfiguration() throws Exception {
        final File projectDir = temporaryFolder.newFolder("functional-java-xir-overrides");
        writeSettings(projectDir);
        writeBuildFile(projectDir, """
                plugins {
                    id 'java'
                    id 'name.jurgenei.gradle.antlr.java'
                }

                tasks.named('javaXmlAst', name.jurgenei.gradle.antlr.XmlAstJavaGradleTask) {
                    targetExtension.set('.xir')
                    outputFormat.set('beautified')
                }

                tasks.register('printJavaXirDefaults') {
                    doLast {
                        def t = tasks.named('javaXmlAst').get()
                        println "targetExtension=${t.targetExtension.get()}"
                        println "outputFormat=${t.outputFormat.get()}"
                    }
                }
                """);

        final BuildResult result = run(projectDir, "printJavaXirDefaults");
        final String output = result.getOutput();

        Assert.assertTrue(output.contains("targetExtension=.xir"));
        Assert.assertTrue(output.contains("outputFormat=beautified"));
    }

    private static BuildResult run(final File projectDir, final String... args) {
        return GradleRunner.create()
                .withProjectDir(projectDir)
                .withArguments(args)
                .withPluginClasspath()
                .build();
    }

    private static void writeSettings(final File projectDir) throws Exception {
        Files.writeString(
                projectDir.toPath().resolve("settings.gradle"),
                "rootProject.name = 'java-plugin-functional-test'\n",
                StandardCharsets.UTF_8);
    }

    private static void writeBuildFile(final File projectDir, final String content) throws Exception {
        Files.writeString(
                projectDir.toPath().resolve("build.gradle"),
                content,
                StandardCharsets.UTF_8);
    }
}
