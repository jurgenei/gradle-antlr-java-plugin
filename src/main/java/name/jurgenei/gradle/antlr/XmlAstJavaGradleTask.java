package name.jurgenei.gradle.antlr;

import org.gradle.api.model.ObjectFactory;
import org.gradle.work.DisableCachingByDefault;

import javax.inject.Inject;
import java.util.List;

/**
 * Java-flavored {@link XmlAstGradleTask} with parser defaults preconfigured.
 */
@DisableCachingByDefault(because = "XmlAstGradleTask performs external parser loading and file-system driven conversion not yet declared for safe caching")
public abstract class XmlAstJavaGradleTask extends XmlAstGradleTask {

    /**
     * Creates preconfigured Java XML AST task.
     *
     * @param objects Gradle object factory.
     */
    @Inject
    public XmlAstJavaGradleTask(final ObjectFactory objects) {
        super(objects);
        LanguageTaskDefaults.of(
                "java",
                "name.jurgenei.parsers.JavaParser",
                "name.jurgenei.parsers.JavaLexer",
                "compilationunit",
                List.of("**/*.java"))
            .applyTo(this);
    }
}
