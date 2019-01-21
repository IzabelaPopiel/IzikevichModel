package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;

import java.util.Arrays;

public class Controller {

    ObservableList<String> types = FXCollections.observableArrayList();

    @FXML
    private TextField txtT0;
    @FXML
    private TextField txtTK;
    @FXML
    private TextField txtDt;
    @FXML
    private TextField txtI;
    @FXML
    private LineChart<Number, Number> chartV;
    @FXML
    private LineChart<Number, Number> chartI;
    @FXML
    private ChoiceBox<String> chooseType;
    @FXML
    private Button btnDraw;
    @FXML
    private Button btnClear;
    @FXML
    private Label lblParameters;
    @FXML
    private TextArea txtAreaResults;


    private FirstOrderDifferentialEquations neuron;
    private NeuronPath neuronPath;
    private TurningPoint turningPoint;

    private double a;
    private double b;
    private double c;
    private double d;

    public void initialize() {
        types.addAll("RS", "IB", "CH", "FS", "LTS", "TC");
        chooseType.getItems().addAll(types);
        chooseType.setValue(types.get(0));
    }

    @FXML
    void btnDrawPressed(ActionEvent event) {
        calculate();
        drawChats();
        displayDynamicParameters();
    }

    @FXML
    void chooseTypeClicked(ActionEvent event) {

        if (chooseType.getValue().equals("RS")) {
            a = 0.02;
            b = 0.2;
            c = -65;
            d = 8;
        }

        if (chooseType.getValue().equals("IB")) {
            a = 0.02;
            b = 0.2;
            c = -55;
            d = 4;
        }

        if (chooseType.getValue().equals("CH")) {
            a = 0.02;
            b = 0.2;
            c = -50;
            d = 2;
        }

        if (chooseType.getValue().equals("FS")) {
            a = 0.1;
            b = 0.2;
            c = -65;
            d = 2;
        }

        if (chooseType.getValue().equals("LTS")) {
            a = 0.02;
            b = 0.25;
            c = -65;
            d = 2;
        }
        if (chooseType.getValue().equals("TC")) {
            a = 0.02;
            b = 0.25;
            c = -65;
            d = 0.05;
        }
        lblParameters.setText("a = " + a + ", b = " + b + ", c = " + c + ", d = " + d);
    }

    @FXML
    void btnClearPressed(ActionEvent event) {
        chartV.getData().clear();
        chartI.getData().clear();
        txtT0.clear();
        txtTK.clear();
        txtDt.clear();
        txtI.clear();
        txtAreaResults.clear();
    }

    void calculate() {

        double synapticCurrent = Double.parseDouble(txtI.getText());
        double v0 = c;
        double u0 = b * v0;
        double t0 = Double.parseDouble(txtT0.getText());
        double tk = Double.parseDouble(txtTK.getText());
        ;
        double delay = 0.15;
        double dt = Double.parseDouble(txtDt.getText());

        neuron = new Neuron(a, b, synapticCurrent, (tk - t0) * delay + t0);
        FirstOrderIntegrator eulerNeuron = new ClassicalRungeKuttaIntegrator(dt);
        neuronPath = new NeuronPath(synapticCurrent, (tk - t0) * delay + t0);
        turningPoint = new TurningPoint(c, d);
        eulerNeuron.addStepHandler(neuronPath);
        eulerNeuron.addEventHandler(turningPoint, 0.1, 0.001, 2000);

        double[] yStart = new double[]{u0, v0};
        double[] yStop = new double[]{0, 0};

        eulerNeuron.integrate(neuron, t0, yStart, tk, yStop);


    }

    void drawChats() {

        XYChart.Series<Number, Number> uSeries = new XYChart.Series<Number, Number>();
        XYChart.Series<Number, Number> vSeries = new XYChart.Series<Number, Number>();
        XYChart.Series<Number, Number> iSeries = new XYChart.Series<Number, Number>();
        uSeries.setName("u");
        vSeries.setName("v");
        iSeries.setName("I");

        for (int i = 0; i < neuronPath.gettValues().size(); i++) {
            uSeries.getData().add(new XYChart.Data<Number, Number>(neuronPath.gettValues().get(i), neuronPath.getuValues().get(i)));
            vSeries.getData().add(new XYChart.Data<Number, Number>(neuronPath.gettValues().get(i), neuronPath.getvValues().get(i)));
            iSeries.getData().add(new XYChart.Data<Number, Number>(neuronPath.gettValues().get(i), neuronPath.getiValues().get(i)));
        }

        chartV.getData().addAll(uSeries);
        chartV.getData().addAll(vSeries);
        chartI.getData().addAll(iSeries);
        chartV.setLegendVisible(true);
        chartI.setLegendVisible(true);
    }

    void displayDynamicParameters() {
        neuronPath.calculateDynamicParameters();
        txtAreaResults.appendText("Maksymalny potencjał iglicy: " + neuronPath.getMaxPeak() + "\n");
        txtAreaResults.appendText("Sredni potencjał iglicy: " + neuronPath.getPeaksMean() + " odchylenie standardowe: " + neuronPath.getPeaksStd() + "\n");
        txtAreaResults.appendText("Odstepy czasowe miedzy iglicami: " + Arrays.toString( neuronPath.getSpikesPeriod())+ "\n");

    }

}
