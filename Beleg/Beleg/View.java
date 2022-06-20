package Beleg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class View {
		  private Presenter p;
		  private Panel ap = new Panel();
		  public  JTextField tfi;
		  public  JTextField tfr;
		  int xpix, ypix;
		  BufferedImage image;

		  public View(Presenter p) {
		    this.p = p;
		  }

		  public void setDim(int xpix, int ypix) {
		    this.xpix = xpix;
		    this.ypix = ypix;
		    image = new BufferedImage(xpix, ypix, BufferedImage.TYPE_INT_RGB);
		    initView();
		  }

		  private void initView() {
		    JFrame f = new JFrame();
		    JPanel  sp = new JPanel( new FlowLayout());
		    JButton sb = new JButton("Start");
		    sb.addActionListener( p);

		    tfr = new JTextField("-0.743643887037151");
		    tfi = new JTextField("0.131825904205330");
		    sp.add(tfr);
		    sp.add(tfi);
		    sp.add(sb);

		    //f.setLayout( new BorderLayout() );
		    f.add(ap, BorderLayout.CENTER);
		    f.add(sp, BorderLayout.SOUTH);
		    f.setSize(xpix, ypix+100);
		    f.setVisible(true);
		  }

		  public void update(Color[][] c) {
		    for (int y = 0; y < ypix; y++) {
		      for (int x = 0; x < xpix; x++) {
		        if (c[x][y] != null) image.setRGB(x, y, c[x][y].getRGB());
		      }
		    }
		    ap.repaint();
		  }

		  class Panel extends JPanel {
		    public void paintComponent(Graphics g) {
		      super.paintComponent(g);
		      g.drawImage(image, 0, 0, null); // see javadoc
		    }
		  }
		}
