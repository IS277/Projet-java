import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Service class responsible for saving and loading application data.
 *
 * This class provides persistence operations for the project by using Java
 * object serialization. It allows a complete {@link MapManager} instance to
 * be stored in a file and restored later.
 *
 * The persistence mechanism is used to save the current state of the map,
 * including hospitals, patients and geometric structures managed by the
 * application.
 *
 * This class belongs to the service layer because its responsibility is to
 * provide technical functionality without containing business logic.
 *
 * <p><b>Class type:</b> Service class.</p>
 *
 * @author Maïssa Tirsane, Anas Chokri, Iyed Souissi, Valery Vo-Van
 * @version 1.0
 */
public class MapPersistenceService {

    /**
     * Saves the current map state into a file.
     *
     * The entire {@link MapManager} object is serialized and written to disk.
     * This allows the application state to be restored later without manually
     * recreating hospitals, patients or other objects.
     *
     * A try-with-resources block is used to automatically close the stream,
     * even if an exception occurs during the writing process.
     *
     * @param manager map manager containing the data to save
     * @param fileName destination file name
     * @throws IOException if an error occurs while writing the file
     */
    public void saveMap(
            MapManager manager,
            String fileName) throws IOException {

        try (ObjectOutputStream out =
                     new ObjectOutputStream(
                             new FileOutputStream(fileName))) {

            out.writeObject(manager);
        }
    }

    /**
     * Loads a previously saved map from a file.
     *
     * The file content is deserialized and converted back into a
     * {@link MapManager} instance. The returned object contains the complete
     * state that was saved earlier.
     *
     * A try-with-resources block is used to ensure that the input stream is
     * automatically closed after reading.
     *
     * @param fileName file containing the serialized map
     * @return reconstructed MapManager instance
     * @throws IOException if an error occurs while reading the file
     * @throws ClassNotFoundException if a serialized class cannot be found
     *                                during deserialization
     */
    public MapManager loadMap(
            String fileName)
            throws IOException, ClassNotFoundException {

        try (ObjectInputStream in =
                     new ObjectInputStream(
                             new FileInputStream(fileName))) {

            return (MapManager) in.readObject();
        }
    }
}