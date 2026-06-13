import java.util.Date;


/**
 * Descriptor for a named, formatted map file managed by the persistence layer.
 *
 * <p>Records the file name, the storage format and the creation timestamp.
 * Delegates actual I/O to the {@link FileSystem} injected at construction time,
 * keeping this class free of any dependency on a specific storage technology.</p>
 *
 * @author Maissa Tirsane, Anas Chokri, Iyed Souissi, Valery Vo-Van
 * @version 1.0
 */
public class Map {

    private String fileName;
    private String format;
    private Date creationDate;
    private FileSystem fileSystem;

    
    /**
     * Creates a new map descriptor and records the current instant as the creation time.
     *
     * @param fileName   name of the target file on the storage medium
     * @param format     format string identifying the serialisation strategy
     * @param fileSystem file-system implementation that will handle I/O operations
     */
    public Map(String fileName, String format, FileSystem fileSystem) {
        this.fileName = fileName;
        this.format = format;
        this.creationDate = new Date();
        this.fileSystem = fileSystem;
    }

    /**
     * Persists this map to the storage medium via the injected {@link FileSystem}.
     */
    public void export() {
        fileSystem.write(this, fileName);
    }

    /**
     * Loads and returns the map stored at the file name via the injected {@link FileSystem}.
     *
     * @return the {@link Map} instance read from the storage medium
     */
    public Map importing() {
        return fileSystem.read(fileName);
    }

    
    
    public String getFileName(){ 
        return fileName;
    }
    public String getFormat(){ 
        return format;
    }
    public Date getCreationDate(){
        return creationDate;
    }
}
