/* 
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


 * Main menu of the Emergency Hospital Voronoi application.
 * Entry point of the JavaFX application.
 
public class MainMenu extends Application {

    @Override
    public void start(Stage primaryStage) {
        

        Label title = new Label("Emergency Dispatcher");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label subtitle = new Label("Dynamic patient assignment system");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");

    
        Button btnLoad    = new Button("Load hospitals (CSV)");
        Button btnMap     = new Button("Open map");
        Button btnSim     = new Button("Start simulation");
        Button btnImport  = new Button("Import map");
        Button btnExport  = new Button("Export map");
        Button btnQuit    = new Button("Quit");

        // Uniform button width
        double btnWidth = 220;
        for (Button btn : new Button[]{btnLoad, btnMap, btnSim, btnImport, btnExport, btnQuit}) {
            btn.setPrefWidth(btnWidth);
        }

        // Actions (à brancher sur les autres modules)
        btnLoad.setOnAction(e -> System.out.println("TODO: load CSV"));
        btnMap.setOnAction(e -> System.out.println("TODO: open map view"));
        btnSim.setOnAction(e -> System.out.println("TODO: start simulation"));
        btnImport.setOnAction(e -> System.out.println("TODO: import binary map"));
        btnExport.setOnAction(e -> System.out.println("TODO: export binary map"));
        btnQuit.setOnAction(e -> primaryStage.close());

    
        VBox root = new VBox(12, title, subtitle, btnLoad, btnMap, btnSim, btnImport, btnExport, btnQuit);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        primaryStage.setTitle("Emergency Dispatcher");
        primaryStage.setScene(new Scene(root, 320, 420));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
*/