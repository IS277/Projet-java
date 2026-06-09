import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainMenu extends Application {

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

    public static void main(String[] args) {
        launch(args);
    }
}
