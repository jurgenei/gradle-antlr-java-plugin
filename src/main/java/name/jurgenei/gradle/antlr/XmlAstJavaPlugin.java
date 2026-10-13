package name.jurgenei.gradle.antlr;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Registers Java-specific XML AST task type preconfigured from {@link XmlAstJavaGradleTask}.
 */
public final class XmlAstJavaPlugin implements Plugin<Project> {

    /**
     * Creates Java XML AST plugin.
     */
    public XmlAstJavaPlugin() {
    }

    @Override
    public void apply(final Project project) {
        LanguagePluginSupport.registerXmlAstTask(
                project,
                "javaXmlAst",
                XmlAstJavaGradleTask.class,
                "Convert Java file trees to XML AST output.");
        LanguagePluginSupport.wireJavaRuntimeClasspath(project, XmlAstJavaGradleTask.class);
    }
}
