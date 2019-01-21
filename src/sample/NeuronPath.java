package sample;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.util.ArrayList;
import java.util.Arrays;

public class NeuronPath implements StepHandler {

    ArrayList<Double> uValues = new ArrayList<>();
    ArrayList<Double> vValues = new ArrayList<>();
    ArrayList<Double> tValues = new ArrayList<>();
    ArrayList<Double> iValues = new ArrayList<>();

    double synapticCurrent;
    double tJump;

    private double maxPeak;
    private double peaksMean;
    private double peaksStd;
    private double[] spikesPeriod;
    private org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction PolynomialSplineFunction;

    public NeuronPath(double synapticCurrent, double tJump) {
        this.synapticCurrent = synapticCurrent;
        this.tJump = tJump;
    }

    public ArrayList<Double> getuValues() {
        return uValues;
    }

    public ArrayList<Double> getvValues() {
        return vValues;
    }

    public ArrayList<Double> gettValues() {
        return tValues;
    }

    public ArrayList<Double> getiValues() {
        return iValues;
    }

    public double[] getSpikesPeriod() {
        return spikesPeriod;
    }

    public double getMaxPeak() {
        return maxPeak;
    }

    public double getPeaksMean() {
        return peaksMean;
    }

    public double getPeaksStd() {
        return peaksStd;
    }

    @Override
    public void init(double v, double[] doubles, double v1) {

    }

    @Override
    public void handleStep(StepInterpolator stepInterpolator, boolean b) throws MaxCountExceededException {
        double t = stepInterpolator.getCurrentTime();
        double[] u = stepInterpolator.getInterpolatedState();

        if (t > tJump) iValues.add(synapticCurrent);
        else iValues.add(0.0);

        uValues.add(u[0]);
        vValues.add(u[1]);
        tValues.add(t);
    }


    void calculateDynamicParameters() {

        double[] v = new double[vValues.size()];
        double[] t = new double[tValues.size()];

        for (int i = 0; i < vValues.size(); i++) {
            t[i] = tValues.get(i);
            v[i] = vValues.get(i);
        }
        maxPeak = StatUtils.max(v);

        LinearInterpolator interp = new LinearInterpolator();
        PolynomialSplineFunction f = interp.interpolate(t, v);

        ArrayList<Double> spikesValues = new ArrayList<>();
        ArrayList<Double> spikesTime = new ArrayList<>();

        boolean check1 = false;
        for (int i = 0; i < v.length - 1; i++) {
            if (v[i] > 0.95 * 30) {
                check1 = true;
            }

            if (check1) {
                if (v[i] < v[i - 1]) {
                    spikesValues.add(v[i - 1]);
                    spikesTime.add(t[i - 1]);
                    check1 = false;
                }
            }
        }

        System.out.println(spikesValues);
        System.out.println(spikesTime);

        double[] spikesV = new double[spikesValues.size()];
        double[] spikesT = new double[spikesTime.size()];

        for (int i = 0; i < spikesValues.size(); i++) {
            spikesV[i] = spikesValues.get(i);
            spikesT[i] = spikesTime.get(i);
        }

        System.out.println(Arrays.toString(spikesT));
        peaksMean = StatUtils.mean(spikesV);
        peaksStd = new StandardDeviation().evaluate(spikesV);
        spikesPeriod = new double[spikesT.length - 1];
        for (int i = 0; i < spikesT.length - 1; i++) {
            spikesPeriod[i] = spikesT[i+1] - spikesT[i];
        }

        System.out.printf(Arrays.toString(spikesPeriod));
    }
}
