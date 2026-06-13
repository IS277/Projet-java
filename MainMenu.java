import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * JavaFX entry point and main menu of the Emergency Dispatcher graphical interface.
 *
 * <p>Displays the welcome screen from which the dispatcher can open the interactive
 * map or quit the application. All domain logic is handled by {@link MapView};
 * this class is purely navigational.</p>
 *
 * @author Maissa Tirsane, Anas Chokri, Iyed Souissi, Valery Vo-Van
 * @version 1.0
 * @see MapView
 */
public class MainMenu extends Application {

    /**
     * Builds and shows the main menu window.
     *
     * @param primaryStage the top-level window provided by the JavaFX runtime
     */
    @Override
    public void start(Stage primaryStage) {

        Label title = new Label("Emergency Dispatcher");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label subtitle = new Label("Dynamic patient assignment system");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");

        Button btnMap  = new Button("Open map");
        Button btnQuit = new Button("Quit");

        btnMap.setPrefWidth(220);
        btnQuit.setPrefWidth(220);

        btnMap.setOnAction(e -> {
            MapView mapView = new MapView();
            mapView.show();
        });

        btnQuit.setOnAction(e -> primaryStage.close());

        VBox root = new VBox(12, title, subtitle, btnMap, btnQuit);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        primaryStage.setTitle("Emergency Dispatcher");
        primaryStage.setScene(new Scene(root, 320, 280));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * JVM entry point that hands control to the JavaFX runtime.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        launch(args);
    }
}
