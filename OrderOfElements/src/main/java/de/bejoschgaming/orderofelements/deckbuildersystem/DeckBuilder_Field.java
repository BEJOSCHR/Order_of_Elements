package de.bejoschgaming.orderofelements.deckbuildersystem;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;

import de.bejoschgaming.orderofelements.graphics.GraphicsHandler;

public class DeckBuilder_Field {

	private int x, y;
	private boolean unplaceable = false;
	
	public DeckBuilder_Field(int x, int y, boolean unplaceable) {
		
		this.x = x;
		this.y = y;
		this.unplaceable = unplaceable;
		
	}
	
	public void draw(Graphics g) {
		this.draw(g, Color.BLACK);
	}
	public void draw(Graphics g, Color color) {
		
		//This size is the length of one part of the 6-Eck as well as the distance from the center to each point
		int masterLength = DeckBuilder_Data.displayFieldSize;
		
		int centerX = getCenterX();
		int centerY = getCenterY();
		
		//Start Top-Center Point then to the right in order
		int p1_x = centerX, p1_y = centerY-masterLength;
		int p2_x = centerX+masterLength, p2_y = centerY-masterLength/2;
		int p3_x = centerX+masterLength, p3_y = centerY+masterLength/2;
		int p4_x = centerX, p4_y = centerY+masterLength;
		int p5_x = centerX-masterLength, p5_y = centerY+masterLength/2;
		int p6_x = centerX-masterLength, p6_y = centerY-masterLength/2;
		
		int[] listX = {p1_x, p2_x, p3_x, p4_x, p5_x, p6_x};
		int[] listY = {p1_y, p2_y, p3_y, p4_y, p5_y, p6_y};
		
		Polygon polygon = new Polygon(listX, listY, 6);
		
		if(this.unplaceable == true) {
			g.setColor(Color.DARK_GRAY);
		}else {
			g.setColor(Color.LIGHT_GRAY);
		}
		g.fillPolygon(polygon);
		
		g.setColor(color);
		g.drawPolygon(polygon);
		
		//MITTELPUNKT
		if(DeckBuilder_Data.showFieldCenterPoints == true) {
			g.drawRoundRect(centerX, centerY, 1, 1, 1, 1);
		}
			
		if(DeckBuilder_Data.showFieldCords == true) {
			GraphicsHandler.drawCentralisedText(g, Color.DARK_GRAY, new Font("Arial", Font.BOLD, GraphicsHandler.getRelativTextSize(10)), this.x+":"+this.y, p4_x, p4_y-12);
		}
		
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getCenterX() {
		return this.x * DeckBuilder_Data.displayFieldSize + DeckBuilder_Data.map_offset_X;
	}
	public int getCenterY() {
		return this.y * (int) (DeckBuilder_Data.displayFieldSize*3.0/2.0) + DeckBuilder_Data.map_offset_Y;
	}
	public boolean isUnplaceable() {
		return unplaceable;
	}
	
}