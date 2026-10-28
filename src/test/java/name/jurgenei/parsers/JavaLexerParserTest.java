package name.jurgenei.parsers;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.TokenStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class JavaLexerParserTest {

    private List<File> testJavaFiles;
    private Constructor<?> lexerConstructor;
    private Constructor<?> parserConstructor;
    private Method compilationUnitMethod;

    @Before
    public void setupTestFiles() throws Exception {
        testJavaFiles = TestResourceDirectoryProvider.getJavaFilesInExamples();
        Assert.assertFalse("No Java example test files found in src/test/resources/examples", testJavaFiles.isEmpty());
        loadParserClasses();
    }

    private void loadParserClasses() throws Exception {
        final Class<?> lexerClass = Class.forName("name.jurgenei.parsers.JavaLexer");
        final Class<?> parserClass = Class.forName("name.jurgenei.parsers.JavaParser");
        lexerConstructor = lexerClass.getConstructor(CharStream.class);
        parserConstructor = parserClass.getConstructor(TokenStream.class);
        compilationUnitMethod = parserClass.getMethod("compilationunit");
    }

    @Test
    public void canParseJavaExampleFiles() throws Exception {
        for (File javaFile : testJavaFiles) {
            parseCompilationUnit(javaFile);
        }
    }

    @Test
    public void canParseJava21RecordPatternFixture() throws Exception {
        final List<File> java21Files = testJavaFiles.stream()
                .filter(file -> "Java21RecordPatterns.java".equals(file.getName()))
                .collect(Collectors.toList());
        Assert.assertEquals("Expected exactly one Java 21 record pattern fixture", 1, java21Files.size());
        parseCompilationUnit(java21Files.get(0));
    }

    @Test
    public void lexerTokenizesInput() throws Exception {
        final File testFile = testJavaFiles.get(0);
        try (InputStream is = Files.newInputStream(testFile.toPath())) {
            final CharStream charStream = CharStreams.fromStream(is, StandardCharsets.UTF_8);
            final Lexer lexer = (Lexer) lexerConstructor.newInstance(charStream);
            final CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            tokenStream.fill();
            Assert.assertTrue("Lexer should produce at least one token", tokenStream.getNumberOfOnChannelTokens() > 0);
        }
    }

    private void parseCompilationUnit(final File javaFile) throws Exception {
        try (InputStream is = Files.newInputStream(javaFile.toPath())) {
            final CharStream charStream = CharStreams.fromStream(is, StandardCharsets.UTF_8);
            final Lexer lexer = (Lexer) lexerConstructor.newInstance(charStream);
            final CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            final Parser parser = (Parser) parserConstructor.newInstance(tokenStream);

            parser.removeErrorListeners();
            parser.addErrorListener(new BaseErrorListener() {
                @Override
                public void syntaxError(final Recognizer<?, ?> recognizer,
                                        final Object offendingSymbol,
                                        final int line,
                                        final int charPositionInLine,
                                        final String msg,
                                        final RecognitionException e) {
                    Assert.fail("Parse error in " + javaFile.getName() + " at line " + line + ": " + msg);
                }
            });

            final Object tree = compilationUnitMethod.invoke(parser);
            Assert.assertNotNull("Parse tree should not be null for " + javaFile.getName(), tree);
        }
    }
}
