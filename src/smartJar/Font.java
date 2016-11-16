package smartJar;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;



public class Font {
	private int fontDrawSize;
	private Vector3f color;
	private TrueTypeFont font;
	
	public Font(String font, int drawFontSize, boolean bold, boolean italic) {
		color = new Vector3f(1,1,1);
		java.awt.Font awtFont;
		if (bold)
		awtFont = new java.awt.Font(font,java.awt.Font.BOLD, drawFontSize);
		else if (italic)
		awtFont = new java.awt.Font(font,java.awt.Font.ITALIC, drawFontSize);
		else 
		awtFont = new java.awt.Font(font,java.awt.Font.PLAIN, drawFontSize);
		this.font = new TrueTypeFont(awtFont, false);
		fontDrawSize = drawFontSize;
	}
	
	public int getFontSize() {
		return fontDrawSize;
	}
	
	public int getWidth(String string) {
		return font.getWidth(string);
	}
	
	public void changeColor(float red, float green, float blue) {
		color.x = red;
		color.y = green;
		color.z = blue;
	}
	
	public void drawCharacter(char code, int x, int y) {
		drawString("" + (char) code, x, y);
	}
	
	public void drawString(String string, float x, float y) {
		drawString(string, (int) x, (int) y);
	}
	
	public void drawString(String string, int x, int y) {
		font.drawString(x, y, string, new Color(color.x, color.y, color.z));
		glColor3f(1,1,1);
	}
}
