import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.json.JSONObject;

public class GraficoController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label tempoL;

    @FXML
    private Label esecutoriL;

    @FXML
    private Button startBTN;

    @FXML
    private BarChart<String, Integer> graficoCRT;

    @FXML
    void initialize() {

        final float tempoMassimo = PythonOperazioniTempoGraficoRT.TEMPO_ESECUZIONE_MASSIMO == 0f ? 5.0f : PythonOperazioniTempoGraficoRT.TEMPO_ESECUZIONE_MASSIMO;
        final int esecutori = Math.round(PythonOperazioniTempoGraficoRT.ESECUTORI_MOLTIPLICATORE == 0 ? Runtime.getRuntime().availableProcessors() : Runtime.getRuntime().availableProcessors() * PythonOperazioniTempoGraficoRT.ESECUTORI_MOLTIPLICATORE);

        Axis<String> x = graficoCRT.getXAxis();
        x.setLabel("Tempo di Esecuzione (s)");
        x.setAutoRanging(true);

        Axis<Integer> y = graficoCRT.getYAxis();
        y.setLabel("Operazioni Eseguite");
        y.setAutoRanging(true);

        startBTN.setOnAction(e -> {

            startBTN.setDisable(true);

            new Thread(new Runnable() {

                @Override
                public void run() {

                    try {

                        String risultato = "";
                        ArrayList<String> risultati = new ArrayList<String>();

                        PrintWriter pwFinale = new PrintWriter(new FileWriter("./risultati.json"));
                        PrintWriter pwConsecutivo = new PrintWriter(new FileWriter("./risultati-str.dat"));

                        JSONObject data = null;

                        XYChart.Series multithreadingSerie = new XYChart.Series();
                        XYChart.Series multiprocessingSerie = new XYChart.Series();

                        multithreadingSerie.setName("Multithreading");
                        multiprocessingSerie.setName("Multiprocessing");

                        Platform.runLater(() -> {
                            graficoCRT.getData().add(multithreadingSerie);
                            graficoCRT.getData().add(multiprocessingSerie);
                            graficoCRT.setLegendVisible(true);
                        });

                        for(float tempo = 1f; tempo <= tempoMassimo; tempo += 1f) {

                            risultato = runPY(tempo, esecutori);
                            if(risultato != null) {

                                risultati.add(risultato);
                                pwConsecutivo.println(risultato);
                                data = new JSONObject(risultato);

                                final float tempoF = tempo;
                                final int esecutoriF = esecutori;
                                final int operazioniMultithreading = data.getInt("multithreading");
                                final int operazioniMultiprocessing = data.getInt("multiprocessing");

                                Platform.runLater(() -> {
                                    tempoL.setText(tempoF + " (s)");
                                    esecutoriL.setText(esecutoriF + "");
                                    multithreadingSerie.getData().add(new XYChart.Data<String, Integer>(tempoF + "s", operazioniMultithreading));
                                    multiprocessingSerie.getData().add(new XYChart.Data<String, Integer>(tempoF + "s", operazioniMultiprocessing));
                                });
                            }
                        }

                        pwConsecutivo.close();

                        pwFinale.println(risultati.toString());
                        pwFinale.close();

                    } catch (Exception e) {
                        System.out.println("exception happened - here's what I know: ");
                        e.printStackTrace();
                    }
                }

                private String runPY(float tempoEsecuzione, int numeroEsecutori) {

                    String risultato = null;
                    String errore = null;

                    try {

                        Process p = Runtime.getRuntime().exec("py main.py " + tempoEsecuzione + " " + numeroEsecutori);

                        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                        // Lettura output del programma
                        risultato = stdInput.readLine();

                        // Lettura eventuali errori
                        System.out.println("Here is the standard error of the command (if any):\n");
                        while ((errore = stdError.readLine()) != null) {
                            System.out.println(errore);
                        }

                        System.out.println(risultato);

                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                    return risultato;
                }
            }).start();

        });

    }


}
