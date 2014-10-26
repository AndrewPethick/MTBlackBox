/**
 * Created by colton on 10/25/14.
 */
package application;

import static java.lang.Math.*;
import edu.mines.jtk.util.*;

public class MTForward {

    private double mu = 4.*PI*pow(10.0, -7.0);
    private double[] sig, d, w;
    private double[] pa, phi;
    private int m;
    private int n;

    public static void main(String[] args){
        double[] d = new double[2];
        d[0] = 500.;
        d[1] = 50.;

        double[] sig = new double[3];
        sig[0] = 0.1;
        sig[1] = 10.;
        sig[2] = 0.1;

        double[] w = new double[4];
        for(int i=0; i<w.length; ++i){
            w[i] = pow(10.0, 2);
        }

        MTForward mtf = new MTForward(sig, d, w);
        mtf.calcPaPhi();
        double[] phi = mtf.getPhi();
        double[] pa = mtf.getPa();

        System.out.println("PA");
        for(int i=0; i<pa.length; ++i){
            System.out.println(pa[i]);
        }

        System.out.println("PHI");
        for(int i=0; i<phi.length; ++i){
            System.out.println(phi[i]);
        }
    }

    MTForward(double[] sig, double[] d, double[] w){
        this.sig=sig;
        this.d = d;
        this.w = w;
        m = w.length;
        n = sig.length;
        pa = new double[m];
        phi = new double[m];
    }

    public void calcPaPhi() {
        Cdouble[] zn = new Cdouble[n];
        for (int i = 0; i < m; ++i) {
            Cdouble a = (Cdouble.DBL_I.times(w[i] * mu * sig[n - 1])).sqrt();
//            System.out.println("a: " + a);
            zn[n - 1] = (a.neg()).over(sig[n - 1]);

            for (int k = n - 2; k >= 0; --k) {
                a = (Cdouble.DBL_I.times(w[i] * mu * sig[k])).sqrt();
                Cdouble p1 = a.over(sig[k]);
                Cdouble p2 = (a.times(d[k]).tanh().neg())
                        .plus(zn[k+1].over(a).times(sig[k]));
                Cdouble p3 = ((zn[k+1].times((a.times(d[k])).tanh()).over(a).times(sig[k])).neg()).plus(1.0);
                zn[k] = p1.times(p2).over(p3);
            }

                pa[i] = pow(zn[0].abs(), 2) / (w[i] * mu);
                phi[i] = atan(zn[0].i / zn[0].r);

        }
//        System.out.println("Z: " + zn[0]);
//        System.out.println("Z: " + zn[n-1]);

    }

    public double[] getPhi(){
        return phi;
    }

    public double[] getPa(){
        return pa;
    }

}