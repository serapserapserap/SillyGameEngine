package serap.sillyge.io.resource.type;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface Extension {
    /**
     * @return the string form of the extension
     */
    String getExtension();

    @NotNull
    @Contract(pure = true)
    static Extension of(String extension) {
        return () -> ResourceType.cleanExtension(extension);
    }
}
