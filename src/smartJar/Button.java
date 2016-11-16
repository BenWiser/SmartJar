package smartJar;

import org.lwjgl.input.Mouse;

public class Button{
	private Image backButton, buttonActive;
	private static float buttonWidth, buttonHeight;
	private int x, y;
	private String text;
	private Action action;
	private Font font;
	private boolean clicked;
	
	static {
		buttonWidth = 150;
		buttonHeight = 50;
	}
	
	public static interface Action {
		public void clicked();
	}
	
	public Button(String text, int x, int y) {
		backButton = new Image("images/backButton.png", buttonWidth, buttonHeight);
		buttonActive = new Image("images/buttonActive.png", buttonWidth, buttonHeight);
		
		this.text = text;
		
		this.x = x;
		this.y = y;
		
		font = new Font("Arial", 15, false, false);
	}
	
	public static float getButtonWidth() {
		return buttonWidth;
	}
	
	public static float getButtonHeight() {
		return buttonHeight;
	}
	
	public Button(String text, int x, int y, Action action) {
		this(text, x, y);
		this.action = action;
	}
	
	public Button(String text, int x, int y, Action action, float width, float height) {
		this(text, x, y, action);
		buttonWidth = width;
		buttonHeight = height;
		backButton.setDim(buttonWidth, buttonHeight);
		buttonActive.setDim(buttonWidth, buttonHeight);
	}
	
	
	public void render() {
		font.drawString(text, x+buttonWidth/2-font.getWidth(text)/2, y+buttonHeight/4);
		if (!clicked) backButton.render(x, y);
		else buttonActive.render(x, y);
	}
	
	public void update() {
		if (Mouse.isButtonDown(0)) {
		
			if (Mouse.getX()<x) return;
			if (Mouse.getX()>x+buttonWidth) return;
			if (SharedVariables.HEIGHT - Mouse.getY()<y) return;
			if (SharedVariables.HEIGHT - Mouse.getY()>y+buttonHeight) return;
			
			clicked = true;
			
		}
		
		if (!Mouse.isButtonDown(0) && clicked) {
			clicked = false;
			
			action.clicked();
		}
	}
}
