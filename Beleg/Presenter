class ApfelPresenter implements ActionListener {
		  protected ApfelModel2 m;
		  protected ApfelView2 v;

		  // I do not understand at all these parameters
		  double xmin = -1.666, xmax = 1, ymin = -1, ymax = 1; // Parameter des Ausschnitts
		  // static double  cr = -0.3735,  ci = 0.655;
		  double cr = -0.743643887036151, ci = 0.131825904205330;
		  double zoomRate = 1.5;
		  int xpix = 640, ypix = 480;

		  public void setModelAndView(ApfelModel m, ApfelView v) {
		    this.m = m;
		    this.v = v;
		    v.setDim(xpix, ypix);
		    m.setParameter(xpix, ypix);
		  }

		  /** Komplette Berechnung aller Bilder */
		  synchronized void apfel() {
		    try {
		      wait();
		    } catch (InterruptedException e) {
		      e.printStackTrace();
		    }
		    for (int i = 1; i < 70; i++) { // Iterationen bis zum Endpunkt
		      System.out.println(i + " VergrÃ¶ÃŸerung: " + 2.6 / (xmax - xmin) + " xmin: " + xmin + " xmax: " + xmax);
		      m.apfel_bild(xmin, xmax, ymin, ymax);
		      double xdim = xmax - xmin;
		      double ydim = ymax - ymin;
		      xmin = cr - xdim / 2 / zoomRate;
		      xmax = cr + xdim / 2 / zoomRate;
		      ymin = ci - ydim / 2 / zoomRate;
		      ymax = ci + ydim / 2 / zoomRate;
		    }
		  }
		  
		  // I think this is the first thing to happen
		  @Override
		  public synchronized  void actionPerformed(ActionEvent e) {
		    cr = Double.parseDouble( v.tfr.getText() );
		    ci = Double.parseDouble( v.tfi.getText() );
		    notifyAll();
		  }
		}
