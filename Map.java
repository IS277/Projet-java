import java.util.Date;


public class Map {

    private String fileName;
    private String format;
    private Date creationDate;
    private FileSystem fileSystem;

    
    /** Creates a new map. */
    public Map(String fileName, String format, FileSystem fileSystem) {
        this.fileName = fileName;
        this.format = format;
        this.creationDate = new Date();
        this.fileSystem = fileSystem;
    }

    /** Exports this map using the file system. */
    public void export() {
        fileSystem.write(this, fileName);
    }

    /** Imports a map using the file system. */
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