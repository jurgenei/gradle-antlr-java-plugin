package name.jurgenei.parsers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class TestResourceDirectoryProvider {

    private static final String EXAMPLES_DIR = "src/test/resources/examples";

    private TestResourceDirectoryProvider() {
    }

    public static List<File> getJavaFilesInExamples() throws Exception {
        final Path base = Path.of(EXAMPLES_DIR);
        if (!Files.isDirectory(base)) {
            throw new IllegalStateException("Examples directory not found: " + EXAMPLES_DIR);
        }
        return Files.walk(base)
                .filter(path -> path.toString().endsWith(".java"))
                .filter(path -> {
                    final String errorsFile = path.toString() + ".errors";
                    return !Files.exists(Path.of(errorsFile));
                })
                .map(Path::toFile)
                .sorted(Comparator.comparing(File::getAbsolutePath))
                .collect(Collectors.toList());
    }
}
