import java.sql.SQLException;

public class MagicTowerMain {
    static GameData gameData;
    static GameControl gameControl;
    static Menu menu;
    static GUI gui;
    public static void main(String[] args) {
        try {
            Login login = new Login();
            gameData = new GameData();
            gameData.readMapFromFile("Map.in");
            gameData.printMap();
            gui = new GUI(gameData);
            menu = new Menu(gameData);
            menu.loadMenu("Menu.XML");
            gameControl = new GameControl(gameData, menu, gui);
            gameControl.gameStart();
        } catch (ClassNotFoundException | SQLException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}