import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class PythonTempoPerEseguireGraficoLD extends Application {

    public static int OPERAZIONI_MASSIME = 0;
    public static float ESECUTORI_MOLTIPLICATORE = 0;

    public static void main(String[] args) {

        if(args != null && args.length > 0) {
            OPERAZIONI_MASSIME = Integer.parseInt(args[0]);
        }

        if(args != null && args.length > 1) {
            ESECUTORI_MOLTIPLICATORE = Float.parseFloat(args[1]);
        }

        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {

        try {

            stage.setTitle("Cabbia René 870029");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("grafico.fxml"));
            AnchorPane layout = (AnchorPane) loader.load();
            Scene scene = new Scene(layout);

            stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
            stage.setScene(scene);
            stage.show();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}