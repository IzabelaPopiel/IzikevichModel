package sample;

import org.apache.commons.math3.ode.events.EventHandler;

public class TurningPoint implements EventHandler {

    private double c;
    private double d;

    public TurningPoint(double c, double d) {
        this.c = c;
        this.d = d;
    }

    @Override
    public void init(double v, double[] doubles, double v1) {
    }

    @Override
    public double g(double v, double[] u) {
        return  (u[1] - 30);
        //zdarzenie to kiedy funckja ma wartosc 0
        //szukam miejsc zerowych u[1] -> v
    }

    @Override
    public Action eventOccurred(double v, double[] doubles, boolean b) {
        return Action.RESET_STATE; // zmienia wartosc stanu
    }

    @Override
    public void resetState(double v, double[] u) {
        u[0] = u[0] + d;
        u[1] = c;
    }
}
