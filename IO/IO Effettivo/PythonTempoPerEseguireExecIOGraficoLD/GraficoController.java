import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
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
    private Label ripetizioniL;

    @FXML
    private Label esecutoriL;

    @FXML
    private Button startBTN;

    @FXML
    private LineChart<Integer, Float> graficoCRT;

    @FXML
    void initialize() {

        final int ripetizioniOUT = PythonTempoPerEseguireExecIOGraficoLD.RIPETIZIONI == 0 ? 50 : PythonTempoPerEseguireExecIOGraficoLD.RIPETIZIONI;
        final int esecutori = Math.round(PythonTempoPerEseguireExecIOGraficoLD.ESECUTORI_MOLTIPLICATORE == 0 ? Runtime.getRuntime().availableProcessors() : Runtime.getRuntime().availableProcessors() * PythonTempoPerEseguireExecIOGraficoLD.ESECUTORI_MOLTIPLICATORE);

        Axis<Integer> x = graficoCRT.getXAxis();
        x.setLabel("Ripetizioni di Scrittura");
        x.setTickLength(esecutori);
        x.setAutoRanging(true);

        Axis<Float> y = graficoCRT.getYAxis();
        y.setLabel("Tempo di Esecuzione (s)");
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

                        for(int ripetizioni = 10; ripetizioni <= ripetizioniOUT; ripetizioni += 10) {

                            risultato = runPY(ripetizioni, esecutori);
                            if(risultato != null) {
                                risultati.add(risultato);
                                pwConsecutivo.println(risultato);
                            }
                        }

                        pwConsecutivo.close();

                        pwFinale.println(risultati.toString());
                        pwFinale.close();

                        Iterator<String> i = risultati.iterator();
                        while(i.hasNext()) {
                            data = new JSONObject(i.next());

                            final int ripetizioniF = data.getInt("ripetizioni");
                            final int esecutoriF = data.getInt("esecutori");
                            final float multithreadingTime = data.getFloat("multithreading");
                            final float multiprocessingTime = data.getFloat("multiprocessing");

                            Platform.runLater(() -> {
                                ripetizioniL.setText("" + ripetizioniF);
                                esecutoriL.setText("" + esecutoriF);
                                multithreadingSerie.getData().add(new XYChart.Data<String, Float>(ripetizioniF + "", multithreadingTime));
                                multiprocessingSerie.getData().add(new XYChart.Data<String, Float>(ripetizioniF + "", multiprocessingTime));
                            });
                        }

                    } catch (Exception e) {
                        System.out.println("exception happened - here's what I know: ");
                        e.printStackTrace();
                    }
                }

                private String runPY(int ripetizioni, int numeroEsecutori) {

                    String risultato = null;
                    String errore = null;

                    try {

                        Process p = Runtime.getRuntime().exec("py main.py " + ripetizioni + " " + numeroEsecutori);

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
