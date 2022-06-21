package Beleg.Beleg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class Presenter implements ActionListener {
		  protected Model m;
		  protected View v;

		  // I do not understand at all these parameters
		  double xmin = -1.666, xmax = 1, ymin = -1, ymax = 1; // Parameter des Ausschnitts
		  // static double  cr = -0.3735,  ci = 0.655;
		  private double cr = -0.743643887036151;
		  private double ci = 0.131825904205330;
		  double zoomRate = 1.5;
		  int xpix = 640, ypix = 480;
        public Object actionPerformed;

		  public void setModelAndView(Model m, View v) {
		    this.m = m;
		    this.v = v;
		    v.setDim(xpix, ypix);
		    m.setParameter(xpix, ypix);
		  }

		  @Override
		  public synchronized void actionPerformed(ActionEvent e) {
		    cr = Double.parseDouble( v.tfr.getText() );
		    ci = Double.parseDouble( v.tfi.getText() );
		    notifyAll();
		  }

		  public double setCr(){
			return cr;
		  }
		  public double getCr(){
			return cr;
		  }
		  public double setCi(){
			return ci;
		  }
		  public double getCi(){
			return ci;
		  }
		}
