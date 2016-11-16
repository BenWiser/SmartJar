package smartJar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class BlockEditor {
	private static List<CodeBlock> blocks;
	private static Image backgroundBlockImage, backgroundBaseImage;
	private static int y;
	private static int grow;
	private static Button loadCode, saveCode;
	private static List<String> loadCodes;
	private static String currentFile;
	
	static {
		y = 20;
		
		grow = 0;
		
		blocks = new ArrayList<>();
		loadCodes = new ArrayList<>();
		
		/*
		 * As this was made for my game all the blocks you see below are specific to
		 * it. Rather than try think up general purpose blocks you might need I 
		 * decided to rather just leave my blocks down below to demonstrate to you
		 * how to use them.
		 */
		//Hidden blocks
		blocks.add( new CodeBlock("every direction around robot", "int direction = 0; direction < 360; direction++", new Vector3f(1,0.8f,0) , true, new Vector2f(0, -100), false, false).makePeer());
		blocks.add( new CodeBlock("enemy found in direction", "scanDirectionForEnemy(direction) != -1", new Vector3f(1,0.8f,0) , true, new Vector2f(0, -100), false, false).makePeer());
		
		
		//Visible blocks
		blocks.add( new CodeBlock("if", "if", new Vector3f(0,1,0) , true, new Vector2f(80, getY()), true, true) );
		blocks.add( new CodeBlock("for", "for", new Vector3f(0,1,0) , true, new Vector2f(80, getY()), true, true) );
		blocks.add( new CodeBlock("while", "while", new Vector3f(0,1,0) , true, new Vector2f(80, getY()), true, true) );
		
		blocks.add( new CodeBlock("An enemy is locked", "checkIfEnemyLocked()", new Vector3f(1,0.8f,0) , true, new Vector2f(80, getY()), false, false).makePeer());
		blocks.add( new CodeBlock("An enemy is not locked", "!checkIfEnemyLocked()", new Vector3f(1,0.8f,0) , true, new Vector2f(80, getY()), false, false).makePeer());
		blocks.add( new CodeBlock("Locked is near", "distanceOfLocked() < 32", new Vector3f(1,0.8f,0) , true, new Vector2f(80, getY()), false, false).makePeer());
		blocks.add( new CodeBlock("Locked is far", "distanceOfLocked() > 30", new Vector3f(1,0.8f,0) , true, new Vector2f(80, getY()), false, false).makePeer());
		
		blocks.add( new CodeBlock("Attacked locked enemy with arms", "attackLockedMelee", new Vector3f(0,0.8f,1) , true, new Vector2f(80, getY()), false, false));
	
		
		backgroundBlockImage = new Image("images/lineSelected.png", CodeBlock.canvasWidth+40, CodeBlock.canvasHeight+10);
		backgroundBaseImage = new Image("images/lineSelected.png", CodeBlock.canvasX-40, CodeBlock.canvasHeight+10);
	
		loadCode = new Button("Load code", 40, CodeBlock.canvasY-10 - 
				(int) Button.getButtonHeight(), () -> Driver.loadModuleFile.setVisible(true));
		
		saveCode = new Button("Save code", 40 + (int) Button.getButtonWidth(), 
				CodeBlock.canvasY-10 -(int) Button.getButtonHeight(),
				() -> saveCode(currentFile));
	}
	
	public static void update() {
		loadCode.update();
		saveCode.update();
		
		for (CodeBlock block: blocks) block.update();
		CodeBlock.updateFloating();
		
		if (grow<180) grow+=10;
		
		if (loadCodes.size()>0) {
			for (String file: loadCodes) {
				loadCode(file);
				currentFile = file;
			}
			
			loadCodes.clear();
		}
	}
	
	public static int getY() {
		y+=40;
		return y;
	}
	
	public static void render() {
		GL11.glPushMatrix();
			float growFactor = (float) Math.sin(Math.toRadians(grow));
			
			growFactor *= 0.05;
			
			int width = (int) (backgroundBaseImage.getWidth() + backgroundBlockImage.getWidth()) / 2;
			int height = (int) (backgroundBaseImage.getHeight() + backgroundBlockImage.getHeight()) / 4;
			
			GL11.glTranslatef(-growFactor*width, -growFactor*height, 1);
			GL11.glScalef(1+growFactor, 1+growFactor, 1+growFactor);
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			
			CodeBlock.renderFloating();
			for (CodeBlock block: blocks) block.render();
			
			backgroundBlockImage.render(CodeBlock.canvasX-40, CodeBlock.canvasY-10);
			backgroundBaseImage.render(40, CodeBlock.canvasY-10);
			loadCode.render();
			saveCode.render();
		GL11.glPopMatrix();
	}
	
	public static void loadCode(List<String> code) {
		CodeBlock.loadBlocks(blocks, code);
	}
	
	public static void loadCode(String code) {
		CodeBlock.loadBlocks(blocks, code, true);
	}
	
	public static List<String> getCode() {
		return CodeBlock.getCurrentCode();
	}
	
	public static void addCodeToLoad(String code) {
		loadCodes.add(code);
	}
	
	public static void saveCode(String file) {
		try {
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(file))));
			
			List<String> code = CodeBlock.getCurrentCode();
			
			for (String line: code) {
				writer.write(line+"\n");
			}
			
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(2);
		}
	}
}
