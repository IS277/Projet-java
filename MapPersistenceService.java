import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MapPersistenceService {

    public void saveMap(
            MapManager manager,
            String fileName) throws IOException {

        try (ObjectOutputStream out =
                new ObjectOutputStream(
                        new FileOutputStream(fileName))) {

            out.writeObject(manager);
        }
    }

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