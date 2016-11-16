package smartJar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class CodeBlock {
	private Image blockImage, blockRoundedImage, blockRoundedPeerImage;
	private int width, height;
	private int x, y;
	private Vector3f color;
	private List<CodeBlock> children, peers;
	private CodeBlock parent;
	private static boolean blockIsGrabbed;
	private boolean baseBlock, grabbed;
	private static CodeBlock floatingBlock;
	private String blockText, codeText;
	private static Font arial;
	public static final int canvasX, canvasY, canvasWidth, canvasHeight;
	private static List<CodeBlock> blocks;
	public static final int HEIGHT;
	
	public static String currentCode;
	
	private boolean allowedChildren, allowedPeers, peerBlock, symbolBlock;
	
	static {
		blockIsGrabbed = false;
		arial = new Font("Arial", 16, true, false);
		
		canvasX = 500;
		canvasY = 60;
		
		HEIGHT = 35;
		
		canvasWidth = SharedVariables.WIDTH - canvasX - 40;
		canvasHeight = SharedVariables.HEIGHT - canvasY - 60;
		
		blocks = new ArrayList<>();
		
		currentCode = "";
	}
	
	static void addBlock(CodeBlock block) {
		int mouseX, mouseY;
		
		mouseX = Mouse.getX();
		mouseY = SharedVariables.HEIGHT - Mouse.getY();
		
		if (mouseX < canvasX) return;
		if (mouseX > canvasX + canvasWidth) return;
		
		if (mouseY < canvasY) return;
		if (mouseY > canvasY + canvasHeight) return;
		
		int height;
		height = 0;
		
		for (CodeBlock nextBlock: blocks) {
			height += nextBlock.height();
		}
		
		CodeBlock result;
		result = null;
		
		for (CodeBlock nextBlock: blocks) {
			result = nextBlock.atY(mouseY);
			if (result != null) break;
		}
		
		if (result == null) {
			if (!block.isPeer())
				blocks.add( block.changeCoords(new Vector2f(canvasX, canvasY+height)) );
		} else
			result.addBlockToBlock( block.setParent(result) );
		
		resize();
	}
	
	public static void createCode() {
		String code;
		code = "";
		
		for (CodeBlock nextBlock: CodeBlock.blocks) {
			code += createCode(nextBlock, 0)+"\n";
		}

		currentCode = code;
	}
	
	static String createCode(CodeBlock block, int levels) {
		String code;
		code = "";
		
		for (int i = 0; i < levels; i++)
			code += " ";
		
		code += block.codeText;
		
		if (!block.isSymbol())
		code += "(";
		
		for (CodeBlock peer: block.getPeers()) {
			code += peer.codeText;
		}
		
		if (!block.isSymbol())
		code += ")";
		
		if (block.isAllowedChildren()) {
			code += "\n";
			for (int i = 0; i < levels; i++)
				code += " ";
			code += "{\n";
		}
		
		String childrenCode;
		childrenCode = "";
		
		for (CodeBlock child: block.getChildren())
			childrenCode += CodeBlock.createCode(child, levels+1);
		
		code += childrenCode;
		
		if (block.isAllowedChildren()) {
			for (int i = 0; i < levels; i++)
				code += " ";
			code += "}\n";
		}
		
		if (!block.isAllowedChildren())
			code += ";\n";
		
		return code;
	}
	
	public boolean isAllowedPeers() {
		return allowedPeers;
	}
	
	public boolean isAllowedChildren() {
		return allowedChildren;
	}
	
	public void setAllowedChildren(boolean allowedChildren) {
		this.allowedChildren = allowedChildren;
	}
	
	public void changeColor(Vector3f color) {
		this.color = new Vector3f(color.x, color.y, color.z);
	}
	
	public static void loadBlocks(List<CodeBlock> baseBlocks, List<String> data) {
		String codeFlat;
		codeFlat = "";
		
		for (String d: data) codeFlat += d;
		
		loadBlocks(baseBlocks, codeFlat, false);
	}
	
	public static void loadBlocks(List<CodeBlock> baseBlocks, String location, boolean loadFile) {
		try {
			List<CodeBlock> tempBlocks;
			tempBlocks = new ArrayList<>();
			
			CodeBlock currentBlock, tempParent;
			currentBlock = null;
			tempParent = null;
			
			BufferedReader reader;
			reader = null;
			
			if (loadFile) {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(location))));
			}
			
			String fullFile, line, currentText;
			line = "";
			fullFile = "";
			currentText = "";
			
			if (loadFile) {
				while ((line = reader.readLine()) != null) {
					fullFile += line;
				}
			} else {
				fullFile = location;
			}
				
			int peerCount;
			peerCount = 0;
			
			for (int i = 0; i < fullFile.length(); i++) {
				char currentChar;
				currentChar = fullFile.charAt(i);
				
				if (currentChar == ';' && currentText.length() == 0) {
					
					currentBlock.setAllowedChildren(false);
					
					currentBlock.changeColor(new Vector3f(0,0.8f,1));
					
					currentText = "";
					
					continue;
				}
				
				if (currentChar == '{') {
					if (currentText.replace(" ", "").length()>0) {
						CodeBlock temp;
						temp = 
								new CodeBlock(cleanText(currentText), cleanText(currentText), new Vector3f(0,1,0), false, new Vector2f(0, 0) , true, true, false, 
										false).checkIfBlockExists(baseBlocks);
						
						if (tempParent!=null)
							temp.setParent(tempParent);
						
						if (currentBlock == null)
							tempBlocks.add(temp);
						else if (temp.getParent() == null)
							tempBlocks.add(temp);
						else
							temp.getParent().addChild(temp);
						
						currentBlock = temp;
						currentBlock.setSymbol(true);
					}
					
					tempParent = currentBlock;
					
					
					currentText = "";
					continue;
				}
				
				if (currentChar == '}') {
					tempParent = tempParent.getParent();
					
					
					currentText = "";
					continue;
				}
				
				if (peerCount>0) {
					if (currentChar == '(') peerCount++;
					if (currentChar == ')') peerCount--;
					
					if (peerCount==0) {
						if (currentText.length() == 0) {
							currentText = "";
							continue;
						}
						
						CodeBlock temp;
						
						temp = new CodeBlock(currentText, currentText, new Vector3f(1,0.8f,0), false, new Vector2f(0, 0) , false, false, true, 
								false).checkIfBlockExists(baseBlocks);
						
						currentBlock.addPeer(temp);
						temp.setParent(currentBlock);
						
						currentText = "";
						continue; 
					}
						
					
					currentText += currentChar;
					continue;
				}
				
				if (currentChar == '(' || currentChar == ';' || currentChar == '{') {
					CodeBlock temp;
					temp = 
							new CodeBlock(cleanText(currentText), cleanText(currentText), new Vector3f(0,1,0), false, new Vector2f(0, 0) , true, true, false, 
									false).checkIfBlockExists(baseBlocks);
					
					if (tempParent!=null)
						temp.setParent(tempParent);
					
					if (currentBlock == null)
						tempBlocks.add(temp);
					else if (temp.getParent() == null)
						tempBlocks.add(temp);
					else
						temp.getParent().addChild(temp);
					
					currentBlock = temp;
					
					if (currentChar == '(')
						peerCount = 1;
					
					if (currentChar == ';') {
						currentBlock.setAllowedChildren(false);
						
						currentBlock.changeColor(new Vector3f(0,0.8f,1));
						
						currentBlock.setSymbol(true);
						
						peerCount = 0;
					}
					
					currentText = "";
					continue;
				}
				
				currentText += currentChar;
			}
			
			blocks = tempBlocks;
			resize();
			
			if (loadFile) {
				reader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	CodeBlock setSymbol(boolean symbolBlock) {
		this.symbolBlock = symbolBlock;
		
		return this;
	}
	
	static String cleanText(String text) {
		int i;
		for (i = 0; i < text.length(); i++) {
			if (text.charAt(i) != ' ')
				break;
		}
		
		text = text.substring(i, text.length());
		
		return text;
	}
	
	CodeBlock checkIfBlockExists(List<CodeBlock> baseBlocks) {
		
		for (CodeBlock block: baseBlocks)
			if (block.codeText.replaceAll(" ", "").equals(codeText.replaceAll(" ", ""))) {
				blockText = block.blockText;
				
				blockImage.changeWidth(arial.getWidth(blockText));
				
				break;
			}
		
		return this;
	}
	
	
	static void resize() {
		NumberReference yValue;
		yValue = new NumberReference(canvasY);
		
		for (CodeBlock nextBlock: blocks)
			nextBlock.resize(nextBlock, yValue);
		
		createCode();
	}
	
	public CodeBlock getParent() {
		return parent;
	}
	
	public boolean isPeer() {
		return peerBlock;
	}
	
	static boolean isSomethingGrabbed() {
		return CodeBlock.blockIsGrabbed;
	}
	
	static void somethingIsGrabbed() {
		blockIsGrabbed = true;
	}
	
	static void somethingIsntGrabbed() {
		blockIsGrabbed = false;
	}
	
	int getWidth() {
		return (int) (blockImage.getWidth() + blockRoundedImage.getWidth()*2);
	}
	
	private static class NumberReference {
		private int number;
		
		public NumberReference(int number) {
			this.number = number;
		}
		
		public int getNumber() {
			return number;
		}
		
		public void changeNumber(int number) {
			this.number += number;
		}
	}

	
	void resize(CodeBlock block, NumberReference yValue) {
		block.changeY(yValue);
		block.changeX();
		
		for (CodeBlock child: children) child.resize(child, yValue);
		
		int width;
		width = getWidth();
		
		for (CodeBlock peer: peers) {
			
			peer.changeCoords( new Vector2f(x+width, getY()) );
			width+=peer.getWidth();
		}
	}
	
	void changeY(NumberReference yValue) {
		y = yValue.getNumber();
		yValue.changeNumber((int) blockImage.getHeight());
	}
	
	void changeX() {
		if (parent == null) {
			x = canvasX;
			return;
		}
		
		int changeX;
		changeX = canvasX+HEIGHT;
		
		CodeBlock temp;
		temp = parent;
		
		while ((temp = temp.getParent()) != null) {
			changeX+=HEIGHT;
		}
		
		x = changeX;
	}
	
	public static List<String> getCurrentCode() {
		return Arrays.asList( CodeBlock.currentCode.split("\n") );
	}
	
	public CodeBlock(String blockText, String codeText, Vector3f color, boolean baseBlock, Vector2f pos, 
			boolean allowedChildren, boolean allowedPeers) {
		width = arial.getWidth(blockText);
		height = HEIGHT;
		
		blockImage = new Image("images/whiteBlock.png", width, height);
		blockRoundedImage = new Image("images/whiteBlockRounded.png", HEIGHT, height);
		blockRoundedPeerImage = new Image("images/whiteBlockRoundedPeer.png", HEIGHT, height);
		
		x = (int) pos.x;
		y = (int) pos.y;
		
		this.color = new Vector3f(color.x, color.y, color.z);
		children = new ArrayList<>();
		peers = new ArrayList<>();
		
		grabbed = false;
		
		this.baseBlock = baseBlock;
		
		this.blockText = blockText;
		
		this.codeText = codeText;
		
		parent = null;
		
		this.allowedChildren = allowedChildren;
		this.allowedPeers = allowedPeers;
		
		peerBlock = false;
		
		symbolBlock = false;
	}
	
	public CodeBlock(String blockText, String codeText, Vector3f color, boolean baseBlock, Vector2f pos, 
			boolean allowedChildren, boolean allowedPeers, boolean peer, boolean symbol) {
		this(blockText, codeText, color, baseBlock, pos, allowedChildren, allowedPeers);
		peerBlock = peer;
		symbolBlock = symbol;
	}
	
	List<CodeBlock> getPeers() {
		return peers;
	}
	
	List<CodeBlock> getChildren() {
		return children;
	}
	
	CodeBlock grab() {
		if (!CodeBlock.isSomethingGrabbed()) {
			grabbed = true;
			CodeBlock.somethingIsGrabbed();
		}
		
		return this;
	}
	
	public CodeBlock makePeer() {
		this.peerBlock = true;
		
		return this;
	}
	
	public CodeBlock makeSymbol() {
		this.symbolBlock = true;
		
		return this;
	}
	
	public boolean isSymbol() {
		return this.symbolBlock;
	}
	
	CodeBlock changeCoords(Vector2f pos) {
		this.x = (int) pos.x;
		this.y = (int) pos.y;
		
		return this;
	}
	
	CodeBlock setParent(CodeBlock parent) {
		this.parent = parent;
		
		return this;
	}
	
	CodeBlock checkIfSelected(int mouseX, int mouseY) {
		for (CodeBlock block: children) {
			CodeBlock temp = block.checkIfSelected(mouseX, mouseY);
			
			if (temp != null) return temp;
		}
		
		if (mouseX > x && mouseX < x+blockImage.getWidth()) {
				if (mouseY > y && mouseY < y+blockImage.getHeight()) {
						return this;
				}
		}
			
		return null;
	}
	
	void liftBlock() {
		if (CodeBlock.isSomethingGrabbed()) return;
		
		if (parent != null)
			parent.getChildren().remove(this);
		else
			CodeBlock.blocks.remove(this);
		
		parent = null;
		
		CodeBlock.floatingBlock = this;
		CodeBlock.somethingIsGrabbed();
		grabbed = true;
	}
	
	int getX() {
		return x;
	}
	
	int getY() {
		return y;
	}
	
	void makeChild() {
		if (CodeBlock.isSomethingGrabbed()) return;
		
		if (!baseBlock) return;
		
		int mouseX, mouseY;
		
		mouseX = Mouse.getX();
		mouseY = SharedVariables.HEIGHT - Mouse.getY();
		
		if (mouseX < x - blockRoundedImage.getWidth()) return;
		if (mouseX > x + blockImage.getWidth() + blockRoundedImage.getWidth()) return;
		
		if (mouseY < y) return;
		if (mouseY > y + blockImage.getHeight()) return;
			
			
		CodeBlock child;
		child = new CodeBlock(blockText, codeText, color, false, new Vector2f(0, 0) , allowedChildren, allowedPeers, peerBlock, 
				symbolBlock).grab();
		
		floatingBlock = child;
	}
	
	CodeBlock makeBlock() {
		CodeBlock child;
		child = new CodeBlock(blockText, codeText, color, false, new Vector2f(0, 0) , allowedChildren, allowedPeers, peerBlock, 
				symbolBlock).grab().setParent(parent);
		
		return child;
	}
	
	CodeBlock atY(int mouseY) {
		
		if (mouseY > y &&  mouseY < y+blockImage.getHeight()) return this;
		
		CodeBlock result;
		result = null;
		
		for (CodeBlock block: children) {
			
			result = block.atY(mouseY);
			
			if (result != null) break;
		}
				
		return result;
	}
	
	void addBlockToBlock(CodeBlock block) {
		addChild(block);
		addPeer(block);
	}
	
	void addChild(CodeBlock child) {
		if (!allowedChildren) return;
		
		if (!child.isPeer())
			children.add(child);
	}
	
	void removeChild(CodeBlock child) {
		children.remove(child);
	}
	
	void addPeer(CodeBlock peer) {
		if (!allowedPeers) return;
		
		if (peer.isPeer())
			peers.add(peer);
	}
	
	int height() {
		int height;
		height = 0;
		
		for (CodeBlock block: children) height += block.height();
		
		return (int) blockImage.getHeight() + height;
	}
	
	void offsetBlocks() {
		
	}
	
	void offsetY(int y) {
		this.y += y;
		
		for (CodeBlock child: children) child.offsetY(y);
	}
	
	public int getTextWidth() {
		return arial.getWidth(blockText);
	}
	
	void render() {
		arial.changeColor(1, 1, 1);
		arial.drawString(blockText, x-1, y+9);
		arial.changeColor(0, 0, 0);
		arial.drawString(blockText, x, y+10);
		
		if (!peerBlock) {
			GL11.glColor3f(color.x, color.y, color.z);
				blockRoundedImage.render(x - blockRoundedImage.getWidth(), y);
				blockImage.render(x, y);
				GL11.glPushMatrix();
					GL11.glTranslatef(x + blockImage.getWidth() + blockRoundedImage.getWidth(), y, 0);
					GL11.glScalef(-1, 1, 1);
					blockRoundedImage.render(0, 0);
				GL11.glPopMatrix();
			GL11.glColor3f(1, 1, 1);
		} else {
			GL11.glColor3f(color.x, color.y, color.z);
				blockRoundedPeerImage.render(x - blockRoundedPeerImage.getWidth(), y);
				blockImage.render(x, y);
				GL11.glPushMatrix();
					GL11.glTranslatef(x + blockImage.getWidth() + blockRoundedPeerImage.getWidth(), y, 0);
					GL11.glScalef(-1, 1, 1);
					blockRoundedPeerImage.render(0, 0);
				GL11.glPopMatrix();
			GL11.glColor3f(1, 1, 1);
		}
			
		for (CodeBlock child: children) child.render();
		
		for (CodeBlock peer: peers) peer.render();
	}
	
	void render(int drawX, int drawY) {
		
		if (parent != null) {
			drawX += x - parent.getX();
			drawY += y - parent.getY();
		}
		
		arial.changeColor(1, 1, 1);
		arial.drawString(blockText, drawX-1, drawY+9);
		arial.changeColor(0, 0, 0);
		arial.drawString(blockText, drawX, drawY+10);
		
		if (!peerBlock) {
			GL11.glColor3f(color.x, color.y, color.z);
				blockRoundedImage.render(drawX - blockRoundedImage.getWidth(), drawY);
				blockImage.render(drawX, drawY);
				GL11.glPushMatrix();
					GL11.glTranslatef(drawX + blockImage.getWidth() + blockRoundedImage.getWidth(), drawY, 0);
					GL11.glScalef(-1, 1, 1);
					blockRoundedImage.render(0, 0);
				GL11.glPopMatrix();
			GL11.glColor3f(1, 1, 1);
		} else {
			GL11.glColor3f(color.x, color.y, color.z);
				blockRoundedPeerImage.render(drawX - blockRoundedPeerImage.getWidth(), drawY);
				blockImage.render(drawX, drawY);
				GL11.glPushMatrix();
					GL11.glTranslatef(drawX + blockImage.getWidth() + blockRoundedPeerImage.getWidth(), drawY, 0);
					GL11.glScalef(-1, 1, 1);
					blockRoundedPeerImage.render(0, 0);
				GL11.glPopMatrix();
			GL11.glColor3f(1, 1, 1);
		}
			
		
		for (CodeBlock child: children) {
			child.render(drawX, drawY);
		}
		
		for (CodeBlock peer: peers) peer.render(drawX, drawY);
	}
	
	void update() {
		if (grabbed) {
			
			if (!Mouse.isButtonDown(0)) {
				grabbed = false;
				CodeBlock.somethingIsntGrabbed();
				
				CodeBlock.addBlock(this);
				
				floatingBlock = null;
				resize();
			}
		}
		
		if (Mouse.isButtonDown(0)) {
			makeChild();
		}
		
		for (CodeBlock child: children) child.update();
		
		for (CodeBlock peer: peers) peer.update();
	}
	
	static void renderFloating() {
		
		if (floatingBlock != null) floatingBlock.render(Mouse.getX(), SharedVariables.HEIGHT - Mouse.getY() - 25);
		
		for (CodeBlock block: blocks) block.render();
	}
	
	static void updateFloating() {
		if (floatingBlock != null) floatingBlock.update();
		
		for (CodeBlock block: blocks) block.update();
		
		Selected:
		if (Mouse.isButtonDown(0)) {
			if (CodeBlock.isSomethingGrabbed()) break Selected;
			
			int mouseX, mouseY;
			mouseX = Mouse.getX();
			mouseY = SharedVariables.HEIGHT - Mouse.getY();
			
			CodeBlock result;
			result = null;
			
			for (CodeBlock block: blocks) {
				result = block.checkIfSelected(mouseX, mouseY);
				
				if (result != null) {
					result.liftBlock();
					break;
				}
			}
			
			
		}
	}
}
