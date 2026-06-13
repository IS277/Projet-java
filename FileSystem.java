/**
 * Interface for file system operations.
 */
/**
 * Abstraction layer for file-system operations on serialised maps.
 *
 * <p>Declaring persistence as an interface decouples the business layer from any
 * specific storage technology. Implementations may write to disk, a network share
 * or an in-memory structure for testing purposes.</p>
 *
 * @author Maissa Tirsane, Anas Chokri, Iyed Souissi, Valery Vo-Van
 * @version 1.0
 */
public interface FileSystem {

    /**
     * Reads a {@link Map} object from the specified storage path.
     *
     * @param path location of the resource to read
     * @return the {@link Map} instance deserialised from the given path
     */
    /** Reads a map from the given file path. */
    Map read(String path);

    /**
     * Writes a {@link Map} object to the specified storage path.
     *
     * @param map  the map to persist; must not be {@code null}
     * @param path destination path where the map will be written
     */
    /** Writes a map to the given file path. */
    void write(Map map, String path);
}
