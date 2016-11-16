package smartJar;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class LoadCodeFile extends JFrame{
	
	private static final long serialVersionUID = 1L;
	
	JFileChooser fileChooser;
	
	public LoadCodeFile() {
		LoadCodeFile parent = this;
		fileChooser = new JFileChooser();
		fileChooser.addActionListener((e) -> {
			if (e.getActionCommand().equals("ApproveSelection")) {
				BlockEditor.addCodeToLoad(fileChooser.getSelectedFile().getAbsolutePath());
				parent.setVisible(false);
			} else if (e.getActionCommand().equals("CancelSelection")){
				parent.setVisible(false);
			}
		});
		
		super.setTitle("Please select a file to load");
		super.setSize(500, 300);
		super.add(fileChooser);
	}

}
