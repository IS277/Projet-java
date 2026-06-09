import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MapPersistenceService {

    public void saveMap(
            MapManager manager,
            String fileName) throws IOException {

        ObjectOutputStream out =
                new ObjectOutputStream(
                        new FileOutputStream(fileName));

        out.writeObject(manager);

        out.close();
    }

    public MapManager loadMap(
            String fileName)
            throws IOException, ClassNotFoundException {

        ObjectInputStream in =
                new ObjectInputStream(
                        new FileInputStream(fileName));

        MapManager manager =
                (MapManager) in.readObject();

        in.close();

        return manager;
    }
}