package sample;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

public class Neuron implements FirstOrderDifferentialEquations {

    private double a;
    private double b;

    double synapticCurrent;
    double tJump;

    public Neuron(double a, double b, double synapticCurrent, double tJump) {
        this.a = a;
        this.b = b;
        this.synapticCurrent = synapticCurrent;
        this.tJump = tJump;
    }

    @Override
    public int getDimension() {
        return 2;
    }

    @Override
    public void computeDerivatives(double t, double[] u, double[] dudt) throws MaxCountExceededException, DimensionMismatchException {
        int currentOn = 0;
        if (t > tJump) {
            currentOn = 1;
        }

        dudt[0] = a * (b * u[1] - u[0]);
        dudt[1] = 0.04 * u[1] * u[1] + 5 * u[1] + 140 - u[0] + synapticCurrent * currentOn;
    }
}
