import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GUI {
	GameData gameData;
	GameControl gameControl;
	JFrame f;
	JLabel[][] b;

	JPanel gamePanel;
	JPanel statusPanel;
	JLabel hero1StatusLabel;
	JLabel hero2StatusLabel;
	JLabel currentPlayerLabel;
	
	// 当前玩家标志，1表示玩家1(H)，2表示玩家2(I)
	private int currentPlayer = 1;

	private static final int CELL_SIZE = 40;

	GUI(GameData gameData) {
		this.gameData = gameData;
		f = new JFrame("Magic Tower");
		f.setLayout(new BorderLayout());

		gamePanel = new JPanel();
		gamePanel.setLayout(null);
		gamePanel.setPreferredSize(new Dimension(gameData.W * CELL_SIZE, gameData.H * CELL_SIZE));

		b = new JLabel[gameData.H][gameData.W];
		for (int i = 0; i < gameData.H; i++) {
			for (int j = 0; j < gameData.W; j++) {
				b[i][j]=new JLabel();
				b[i][j].setBounds(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
				gamePanel.add(b[i][j]);  // 修正：添加到gamePanel而不是JFrame
			}
		}

		statusPanel = new JPanel();
		statusPanel.setLayout(new GridLayout(3, 1));
		// 添加当前玩家提示标签
		currentPlayerLabel = new JLabel("当前轮到玩家 H 移动", SwingConstants.CENTER);
		currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 14));
		hero1StatusLabel = new JLabel("H: Health: " + gameData.heroHealth1 + "  KeyNum: " + gameData.keyNum1);
		hero2StatusLabel = new JLabel("I: Health: " + gameData.heroHealth2 + "  KeyNum: " + gameData.keyNum2);
		statusPanel.add(currentPlayerLabel);
		statusPanel.add(hero1StatusLabel);
		statusPanel.add(hero2StatusLabel);

		f.add(gamePanel, BorderLayout.CENTER);
		f.add(statusPanel, BorderLayout.SOUTH);

		// 添加键盘监听器
		f.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				char key = e.getKeyChar();
				// 处理当前玩家的输入或特殊功能键
				if (key == 'a' || key == 's' || key == 'd' || key == 'w' || key == 'q' || key == '0') {
					// 处理当前玩家的移动
					gameControl.handlePlayerInput(currentPlayer, key);
					
					// 切换到下一个玩家
					currentPlayer = (currentPlayer == 1) ? 2 : 1;
					
					// 更新当前玩家显示
					currentPlayerLabel.setText("当前轮到玩家 " + ((currentPlayer == 1) ? "H" : "I") + " 移动");
					
					// 刷新界面
					if (gameControl != null && gameControl.gui != null) {
						gameControl.gui.refreshGUI();
					}
				} else if (key == 'b' || key == 'v') {
					// 特殊功能键，不影响玩家轮次
					gameControl.handlePlayerInput(currentPlayer, key);
				}
			}
		});

		f.pack();
		f.setResizable(true);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setFocusable(true);
		f.requestFocusInWindow();

		refreshGUI();
	}

	// 设置GameControl引用
	public void setGameControl(GameControl gameControl) {
		this.gameControl = gameControl;
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
		
		// 更新当前玩家显示
		currentPlayerLabel.setText("当前轮到玩家 " + ((currentPlayer == 1) ? "H" : "I") + " 移动");

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