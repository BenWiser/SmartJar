package smartJar;

import java.io.FileInputStream;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class Image{
	private Texture texture;
	private float width, height;
	private float x, y;
	
	public Image(Texture texture) {
		this.texture = texture;
	}
	
	public Image(String textureLocation) {
		try {
			this.texture = TextureLoader.getTexture("PNG", new FileInputStream(textureLocation));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Image(String textureLocation, float width, float height) {
		this(textureLocation);
		this.width = width;
		this.height = height;
	}
	
	public void setDim(float width, float height) {
		setWidth(width);
		setHeight(height);
	}
	
	public void setWidth(float width) {
		this.width = width;
	}
	
	public void setHeight(float height) {
		this.height = height;
	}
	
	public void setCoords(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void render(float x, float y) {
		setCoords(x,y);
		render();
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
	public void changeWidth(float x) {
		this.width = x;
	}
	
	public void changeHeight(float y) {
		this.height = y;
	}
	
	public void render() {
		texture.bind();
		
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, 0);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2f(0, 0);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex2f(width, 0);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex2f(width, height);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex2f(0, height);
		GL11.glEnd();
		GL11.glPopMatrix();
	}
}
