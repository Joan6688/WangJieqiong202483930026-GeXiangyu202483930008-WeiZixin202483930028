import java.awt.*;
import javax.swing.*;

public class GUI {
	GameData gameData;
	JFrame f;
	JLabel[][] b;

	JPanel statusPanel;
	JLabel hero1StatusLabel;
	JLabel hero2StatusLabel;

	private static final int CELL_SIZE = 40;

	GUI(GameData gameData) {
		this.gameData = gameData;
		f = new JFrame("Magic Tower");
		f.setLayout(new BorderLayout());

		JPanel gamePanel = new JPanel();
		gamePanel.setLayout(null);
		gamePanel.setPreferredSize(new Dimension(gameData.W * CELL_SIZE, gameData.H * CELL_SIZE));

		b = new JLabel[gameData.H][gameData.W];
		for (int i = 0; i < gameData.H; i++) {
			for (int j = 0; j < gameData.W; j++) {
				b[i][j]=new JLabel();
				b[i][j].setBounds(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
				f.add(b[i][j]);
			}
		}

		statusPanel = new JPanel();
		statusPanel.setLayout(new GridLayout(2, 1));
		hero1StatusLabel = new JLabel("H: Health: " + gameData.heroHealth1 + "  KeyNum: " + gameData.keyNum1);
		hero2StatusLabel = new JLabel("I: Health: " + gameData.heroHealth2 + "  KeyNum: " + gameData.keyNum2);
		statusPanel.add(hero1StatusLabel);
		statusPanel.add(hero2StatusLabel);

		f.add(gamePanel, BorderLayout.CENTER);
		f.add(statusPanel, BorderLayout.SOUTH);

		f.pack();
		f.setResizable(true);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		refreshGUI();
	}
	public void refreshGUI()
	{
		for (int i = 0; i < gameData.H; i++) {
			for (int j = 0; j < gameData.W; j++) {
				Image scaledImage = chooseImage(gameData.map[gameData.currentLevel][i][j]);
				b[i][j].setIcon(new ImageIcon(scaledImage));
			}
		}

		String immuneTip1 = gameData.isImmune1 ? " Immune: Yes " : " Immune: No ";
		String immuneTip2 = gameData.isImmune2 ? " Immune: Yes " : " Immune: No ";
		hero1StatusLabel.setText("H: Health: " + gameData.heroHealth1 + "  KeyNum: " + gameData.keyNum1 + immuneTip1);
		hero2StatusLabel.setText("I: Health: " + gameData.heroHealth2 + "  KeyNum: " + gameData.keyNum2 + immuneTip2);

		f.revalidate();
		f.repaint();
	}
	private static Image chooseImage(int index){
		ImageIcon[] icons = new ImageIcon[10];
		Image scaledImage;
		try {
			icons[0] = new ImageIcon("images/Wall.jpg");
			icons[1] = new ImageIcon("images/Floor.jpg");
			icons[2] = new ImageIcon("images/Key.jpg");
			icons[3] = new ImageIcon("images/Door.jpg");
			icons[4] = new ImageIcon("images/Stair.jpg");
			icons[5] = new ImageIcon("images/Exit.jpg");
			icons[6] = new ImageIcon("images/Hero1.jpg");
			icons[7] = new ImageIcon("images/Hero2.jpg");
			icons[8] = new ImageIcon("images/Potion.jpg");
			icons[9] = new ImageIcon("images/Monster.jpg");

			for (int i = 0; i < icons.length; i++) {
				if (icons[i] == null || icons[i].getImageLoadStatus() != MediaTracker.COMPLETE) {
					System.err.println("Failed to load image at index " + i);
				}
			}
		} catch (Exception e) {
			System.err.println("Error loading images: " + e.getMessage());
		}
		if (index > 10)
			scaledImage = icons[8].getImage().getScaledInstance(CELL_SIZE, CELL_SIZE, Image.SCALE_SMOOTH);
		else if (index < 0)
			scaledImage = icons[9].getImage().getScaledInstance(CELL_SIZE, CELL_SIZE, Image.SCALE_SMOOTH);
		else
			scaledImage = icons[index].getImage().getScaledInstance(CELL_SIZE, CELL_SIZE, Image.SCALE_SMOOTH);

		return scaledImage;
	}
}
