package serap.sillyge.io;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public final class ResourceUtils {

    private ResourceUtils() {}

    public static InputStream getResourceAsStream(String path) {
        return Objects.requireNonNull(Object.class.getClassLoader().getResourceAsStream(path), "Resource not found: " + path);
    }

    public static Path getResourceAsPath(String path) throws URISyntaxException {
        URL resource = Object.class.getClassLoader().getResource(path);
        if(resource == null) {
            throw new RuntimeException(new FileNotFoundException("Failed to get uri for path " + path));
        }
        return Paths.get(resource.toURI());
    }
}
