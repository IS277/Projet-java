/**
 * Interface for file system operations.
 */
public interface FileSystem {

    /** Reads a map from the given file path. */
    Map read(String path);

    /** Writes a map to the given file path. */
    void write(Map map, String path);
}