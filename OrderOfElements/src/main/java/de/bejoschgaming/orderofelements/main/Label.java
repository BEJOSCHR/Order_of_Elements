package de.bejoschgaming.orderofelements.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class Label extends JLabel {

	private int displayedFPS = 0;
    private long nextSecond = System.currentTimeMillis() + 1000;
    private int framesInCurrentSecond = 0;
    private int framesInLastSecond = 0;
    private long nextRepaintDelay = 0;
    private int maxFPS = 120;
    private boolean showFPS = true;
	
	public Label(JFrame frame) {
		
		this.setBounds(0, 0, frame.getWidth(), frame.getHeight());
		this.setVisible(true);
		
	}

	@Override
    protected void paintComponent(Graphics g) {

		// MAX FPS GRENZE SCHAFFEN
		long now = System.currentTimeMillis();
		try {
			if(nextRepaintDelay > now) {
				Thread.sleep(nextRepaintDelay - now);
		    }
		    nextRepaintDelay = now + 1000 / (maxFPS - 41);
		}catch (InterruptedException e) {}
		
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		//CONTENT
		
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		if(OOE_Main_Client.map != null) {
			OOE_Main_Client.map.draw(g);
		}
		
		// DRAW FPS
		if(showFPS == true) {
		    g.setColor(Color.WHITE);
		    g.setFont(new Font("Arial", Font.BOLD, (int) (12)));
		    g.drawString("" + getCurrentFPSValue(), 0 + 2, 0 + 13);
		}
		
		// DRAW MOUSE POS
		if(FrameEvents.isShowMousePos() == true) {
			g.setColor(Color.RED);
			g.drawLine(0, FrameEvents.getMY(), this.getWidth(), FrameEvents.getMY());
			g.drawLine(FrameEvents.getMX(), 0, FrameEvents.getMX(), this.getHeight());
			g.setFont(new Font("Arial", Font.BOLD, (int) (12)));
		    g.drawString(FrameEvents.getMX()+":"+FrameEvents.getMY(), FrameEvents.getMX() + 4, FrameEvents.getMY() - 5);
		}
		
		// CALCULATE FPS
		calculateFPS();
		
		repaint();

    }

    /**
     * Berechnet und updatet die FPS
     */
    private void calculateFPS() {
		long currentTime = System.currentTimeMillis();
		if (currentTime > nextSecond) {
		    nextSecond += 1000;
		    framesInLastSecond = framesInCurrentSecond;
		    framesInCurrentSecond = 0;
		}
		framesInCurrentSecond++;
		displayedFPS = framesInLastSecond;
    }

    /**
     * Gibt die derzeitigen FPS an
     * 
     * @return {@link Integer}, die derzeitigen FPS
     */
    public int getCurrentFPSValue() {
    	return displayedFPS;
    }

    public void setShowFPS(boolean showFPS) {
    	this.showFPS = showFPS;
    }

    public boolean isShowingFPS() {
    	return showFPS;
    }
	
}
