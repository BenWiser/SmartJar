package smartJar;

import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;
import static org.lwjgl.opengl.GL11.GL_GREATER;
import static org.lwjgl.opengl.GL11.glAlphaFunc;
import static org.lwjgl.opengl.GL11.glEnable;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class Driver {
	/*
	 * Smart Jar - By Rupert Ben Wiser
	 * 
	 * This little project is a java drag and drop program made for my
	 * game Proxy. Feel free to use it on your own projects.
	 * 
	 */
	
	public static LoadCodeFile loadModuleFile;
	
	public static void main(String[] args) {
		try {
			Display.setDisplayMode(new DisplayMode(SharedVariables.WIDTH, SharedVariables.HEIGHT));
			Display.setTitle("Smart JAR");
			Display.create();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		GL11.glMatrixMode(GL11.GL_PROJECTION_MATRIX);
			GL11.glLoadIdentity();
			GL11.glOrtho(0, SharedVariables.WIDTH, SharedVariables.HEIGHT, 0, -1, 1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW_MATRIX);
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClearColor(0.1f, 0.8f, 0.9f, 1);
		
		glEnable(GL_ALPHA_TEST);
	    glAlphaFunc(GL_GREATER, 0.2f);
	    
	    loadModuleFile = new LoadCodeFile();
		
		while (!Display.isCloseRequested()) {
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			
			BlockEditor.update();
			BlockEditor.render();
			
			Display.update();
			Display.sync(60);
		}
		
		loadModuleFile.dispose();
		Display.destroy();
	}
}
