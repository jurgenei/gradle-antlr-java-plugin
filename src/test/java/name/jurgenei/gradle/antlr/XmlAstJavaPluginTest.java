package name.jurgenei.gradle.antlr;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Assert;
import org.junit.Test;

public class XmlAstJavaPluginTest {

    @Test
    public void registersPreconfiguredTaskType() {
        final Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("java");

        new XmlAstJavaPlugin().apply(project);

        final Object task = project.getTasks().getByName("javaXmlAst");
        Assert.assertNotNull(task);
        Assert.assertTrue(task instanceof XmlAstJavaGradleTask);
    }

    @Test
    public void preconfiguresJavaDefaults() {
        final Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("java");

        new XmlAstJavaPlugin().apply(project);

        final XmlAstJavaGradleTask task = (XmlAstJavaGradleTask) project.getTasks().getByName("javaXmlAst");
        Assert.assertEquals("java", task.getGrammar().get());
        Assert.assertEquals("name.jurgenei.parsers.JavaParser", task.getParserClassName().get());
        Assert.assertEquals("name.jurgenei.parsers.JavaLexer", task.getLexerClassName().get());
        Assert.assertEquals("compilationunit", task.getStartRule().get());
        Assert.assertTrue(task.getIncludes().get().contains("**/*.java"));
        Assert.assertEquals(".xml", task.getTargetExtension().get());
        Assert.assertEquals("compact", task.getOutputFormat().get());
    }

    @Test
    public void supportsXirOutputConfiguration() {
        final Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("java");

        new XmlAstJavaPlugin().apply(project);

        final XmlAstJavaGradleTask task = (XmlAstJavaGradleTask) project.getTasks().getByName("javaXmlAst");
        task.getTargetExtension().set(".xir");
        task.getOutputFormat().set("beautified");

        Assert.assertEquals(".xir", task.getTargetExtension().get());
        Assert.assertEquals("beautified", task.getOutputFormat().get());
    }
}
