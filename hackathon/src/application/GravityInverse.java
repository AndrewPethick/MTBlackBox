/**
 * Created by colton on 10/26/14.
 */

package application;

import edu.mines.jtk.lapack.DMatrix;
import static java.lang.Math.*;

public class GravityInverse {

    private DMatrix G;
    private DMatrix R;
    private DMatrix pos, sig, d;
    private DMatrix mirls, pirls, mlsq, plsq, mwls, pwls;

    private int xlen=27;
    private int zlen=16;
    private double dz=.25;
    private double dx=1;
    private double gamma=6.672E-8;

    private int models=xlen*zlen;

    public static void main(String[] args){
        GravityInverse g = new GravityInverse();
        g.soln();
        DMatrix lsq = g.getLSQ();
        DMatrix irls = g.getIRLS();
        DMatrix wlsq = g.getWLSQ();
        DMatrix G = g.getG();
        // System.out.println(wlsq);
        System.out.println(irls);
        // System.out.println(lsq);
        DMatrix data = g.getDIRLS();
        System.out.println(data);

    }

    GravityInverse(){
        double[][] xar = new double[1][xlen];
        xar[0] = new double[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27};
        double[][] sar = new double[1][xlen];
        sar[0] = new double[] {0.029,0.026333333,0.026333333,0.023333333,-0.034,0.037666667,-0.028,0.018333333,0.026333333,0.034666667,0.074666667,0.033,0.038,0.023,0.029,0.025,0.3915,0.088666667,0.033666667,0.034333333,0.075834938,0.103666667,0.046,0.033666667,0.182666667,0.05,0.040333333};
        double[][] dar = new double[1][xlen];
        dar[0] = new double[] {0,-0.004334443,-0.00102575,-0.011643216,-0.015677353,-0.020383215,-0.028084018,-0.039177196,-0.052787778,-0.064257542,-0.074637509,-0.084348429,-0.113071845,-0.13413256,-0.172375091,-0.185610816,-0.175190424,-0.132776015,-0.132038324,-0.09997267,-0.097712059,-0.108479795,-0.107392813,-0.128640698,-0.099550493,-0.069488522,-0.092985194};
        for(int i=0; i<xlen; ++i){
            sar[0][i] = sar[0][i]/1000./1000.;
            dar[0][i] = dar[0][i]/1000./1000.;
        }

        pos = new DMatrix(xar);
        sig = new DMatrix(sar);
        d = new DMatrix(dar);
        d = d.transpose();
        G = new DMatrix(xlen,models);

        double[] x = new double[] {-0.5,0.5,0.5,-0.5,-0.5};
        double[] z = new double[] {0.,0.,dz, dz, 0};
        double[] r = new double[5];
        for(int i=0; i<5; ++i){
            r[i] = sqrt(x[i]*x[i]+z[i]*z[i]);
        }

        double sum_sides=0.0;
        int counter=0;

        for(int j=1; j<=models; ++j){
            for(int i=1; i<=xlen; ++i){
                sum_sides=0.0;
                for(int n=1; n<=4; ++n){
                    sum_sides+= ((PI*(z[n]-z[n-1])/2.0) + (z[n]*atan(z[n]/x[n])-z[n-1]*atan(z[n-1]/x[n-1]))+0.5*(x[n]+x[n-1])*log10(r[n]/r[n-1]));
                }
                for(int k=0; k<5; ++k){
                    x[k] -= dx;
                    r[k] = sqrt(x[k]*x[k]+z[k]*z[k]);
                }
                G.set(i-1,j-1,abs(2.0*gamma*sum_sides));
            }
            x[0] = -0.5+(j-counter*xlen)*dx;
            x[1] = 0.5+(j-counter*xlen)*dx;
            x[2] = 0.5+(j-counter*xlen)*dx;
            x[3] = -0.5+(j-counter*xlen)*dx;
            x[4] = -0.5+(j-counter*xlen)*dx;

            for(int k=0; k<5; ++k){
              r[k] = sqrt(x[k]*x[k]+z[k]*z[k]);
            }

            if(j>=(counter+1)*xlen){
                for(int k=0; k<5; ++k){
                    z[k] += dz;
                }
                x = new double[] {-0.5,0.5,0.5,-0.5,-0.5};
                for(int i=0; i<5; ++i){
                    r[i] = sqrt(x[i]*x[i]+z[i]*z[i]);
                }
                ++counter;
            }
        }
    }

    public void soln(){
    	
    	DMatrix w = new DMatrix(xlen, xlen);
    	for(int i=0; i<xlen; ++i){
    	    w.set(i, i, 1.0/sig.get(0,i));
    	}
    	mwls = ((((G.transpose().times(w.transpose())).times(w).times(G)).inverse()).times((G.transpose()).times(w.transpose()).times(w))).times(d);
    	pwls = G.times(mwls);
    	
        mlsq = (((G.transpose().times(G)).inverse()).times(G.transpose())).times(d);
        plsq = G.times(mlsq);

        DMatrix mii_old = new DMatrix(mlsq);
        DMatrix mii_new = new DMatrix(mlsq);
        int k=0;
        while (k!=1) {
            DMatrix r = d.minus(G.times(mii_old));
            int q = r.getRowCount();
            DMatrix R = new DMatrix(q,q);
            for (int i = 0; i < xlen; ++i) {
                if (abs(r.get(i, 0)) < 0.05) {
                    R.set(i, i, 1.0 / 0.05);
                } else {
                    R.set(i, i, 1.0 / abs(r.get(0, i)));
                }
            }
            mii_new = ((G.transpose().times(R).times(G)).inverse()).times(G.transpose()).times(R).times(d);
            if((mii_new.minus(mii_old)).norm2()/(1 + mii_new.norm2()) < 0.01){
                k=1;
            }
            mii_old = new DMatrix(mii_new);
            pirls = G.times(mii_new);
            mirls = new DMatrix(mii_new);
        }

        DMatrix tmp = new DMatrix(zlen, xlen);
        k=0;
        double min = min(mirls);
        for(int i=0; i<zlen; ++i){
            for(int j=0; j<xlen; ++j){
                tmp.set(i,j,mirls.get(k,0)-min);
                ++k;
            }
        }
        mirls = new DMatrix(tmp);
        
        k=0;
        min = min(mwls);
        for(int i=0; i<zlen; ++i){
            for(int j=0; j<xlen; ++j){
                tmp.set(i,j,mwls.get(k,0)-min);
                ++k;
            }
        }
        mwls = new DMatrix(tmp);
        
        k=0;
        min = min(mlsq);
        for(int i=0; i<zlen; ++i){
            for(int j=0; j<xlen; ++j){
                tmp.set(i,j,mlsq.get(k,0)-min);
                ++k;
            }
        }
        mlsq = new DMatrix(tmp);
    }

    public DMatrix getIRLS(){
        return mirls;
    }

    public DMatrix getLSQ(){
        return mlsq;
    }

    public DMatrix getWLSQ(){
        return mwls;
    }
    
    public DMatrix getDIRLS(){
        return pirls;
    }

    public DMatrix getDLSQ(){
        return plsq;
    }

    public DMatrix getDWLSQ(){
        return pwls;
    }
    
    
    public DMatrix getG(){
        return G;
    }
    
    public DMatrix getData(){
    	return d;
    }
    
    public DMatrix getSTD(){
    	return sig;
    }
    
    public DMatrix getPosition(){
    	return pos;
    }
    
    public double min(DMatrix x){
		double min = x.get(0, 0);
		for(int i=0; i<x.getRowCount(); ++i){
			for(int j=0; j<x.getColumnCount(); ++j){
				if(min>x.get(i, j)){
					min = x.get(i, j);
				}
			}
		}
		return min;
    	
    }

}