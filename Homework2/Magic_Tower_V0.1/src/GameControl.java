import java.util.Random;
import java.util.Scanner;

public class GameControl {
    GameData gameData;
    Menu menu;
    GUI gui;

    GameControl(GameData gameData, Menu menu) {
        this.gameData = gameData;
        this.menu = menu;
    }

    GameControl(GameData gameData, Menu menu, GUI gui) {
        this.gameData = gameData;
        this.menu = menu;
        this.gui = gui;
    }

    void gameStart() {
        Scanner keyboardInput = new Scanner(System.in);
        while (true) {
            System.out.println("Current operation: H (a/s/d/w to move, q to stay, 0 for menu)");
            String inputH = keyboardInput.next();
            handlePlayerInput(1, inputH.charAt(0));

            System.out.println("Current operation: I (a/s/d/w to move, q to stay, 0 for menu)");
            String inputI = keyboardInput.next();
            handlePlayerInput(2, inputI.charAt(0));

            gameData.printMap();

            if (gui != null) {
                gui.refreshGUI();
            }
        }
    }

    void handlePlayerInput(int player, char inC) {
        if (inC == '0') {
            menu.enterMenu();
            return;
        }
        if (inC == 'q') {
            System.out.println((player == 1 ? "H" : "I") + " chooses not to move");
            return;
        }
        int currX = (player == 1) ? gameData.pX1 : gameData.pX2;
        int currY = (player == 1) ? gameData.pY1 : gameData.pY2;
        int tX = currX, tY = currY;
        switch (inC) {
            case 'a': tY--; break;
            case 'd': tY++; break;
            case 'w': tX--; break;
            case 's': tX++; break;
            default:
                System.out.println("Invalid input! Use a/s/d/w/q/0");
                return;
        }

        if (tX < 0 || tX >= gameData.H || tY < 0 || tY >= gameData.W) {
            System.out.println((player == 1 ? "H" : "I") + " can't move out of bounds!");
            return;
        }

        int target = gameData.map[gameData.currentLevel][tX][tY];
        System.out.println((player == 1 ? "H" : "I") + " target: (" + tX + "," + tY + "), value: " + target);

        if (target == 0) {
            System.out.println((player == 1 ? "H" : "I") + " hits a wall!");
            return;
        }

        if (player == 1) {
            handleHeroHMovement(tX, tY, target);
        } else {
            handleHeroIMovement(tX, tY, target);
        }
    }

    private void handleHeroHMovement(int tX, int tY, int target) {
        switch (target) {
            case 1:
                moveHeroH(tX, tY);
                break;
            case 2:
                gameData.keyNum1++;
                System.out.println("H gets a key! Total: " + gameData.keyNum1);
                moveHeroH(tX, tY);
                break;
            case 3:
                if (gameData.keyNum1 > 0) {
                    gameData.keyNum1--;
                    handleDoorReward(1);
                    moveHeroH(tX, tY);
                } else {
                    System.out.println("H needs a key to open the door!");
                }
                break;
            case 4:
                gameData.hOnStair = true;
                moveHeroH(tX, tY);
                checkStairCondition();
                break;
            case 5:
                winGame();
                break;
            case 7:
                if (gameData.map[gameData.currentLevel][tX][tY] != 4) {
                    System.out.println("Can't move to I's position!");
                    return;
                }
                moveHeroH(tX, tY);
                break;
            default:
                if (target > 10) {
                    gameData.heroHealth1 += target;
                    System.out.println("H gets +" + target + " health! Current health: " + gameData.heroHealth1);
                    moveHeroH(tX, tY);
                } else if (target < 0) { // 怪物
                    handleMonsterAttack(1, tX, tY, -target);
                } else {
                    System.out.println("H can't move here (unknown element: " + target + ")");
                }
        }
    }

    private void handleHeroIMovement(int tX, int tY, int target) {
        switch (target) {
            case 1:
                moveHeroI(tX, tY);
                break;
            case 2:
                gameData.keyNum2++;
                System.out.println("I gets a key! Total: " + gameData.keyNum2);
                moveHeroI(tX, tY);
                break;
            case 3:
                if (gameData.keyNum2 > 0) {
                    gameData.keyNum2--;
                    handleDoorReward(2);
                    moveHeroI(tX, tY);
                } else {
                    System.out.println("I needs a key to open the door!");
                }
                break;
            case 4:
                gameData.iOnStair = true;
                moveHeroI(tX, tY);
                checkStairCondition();
                break;
            case 5:
                winGame();
                break;
            case 6:
                if (gameData.map[gameData.currentLevel][tX][tY] != 4) {
                    System.out.println("Can't move to H's position!");
                    return;
                }
                moveHeroI(tX, tY);
                break;
            default:
                if (target > 10) {
                    gameData.heroHealth2 += target;
                    System.out.println("I gets +" + target + " health! Current health: " + gameData.heroHealth2);
                    moveHeroI(tX, tY);
                } else if (target < 0) {
                    handleMonsterAttack(2, tX, tY, -target);
                } else {
                    System.out.println("I can't move here (unknown element: " + target + ")");
                }
        }
    }
    private void checkStairCondition() {
        if (gameData.hOnStair && gameData.iOnStair) {
            System.out.println("Both heroes reached the stair! Moving to next level...");
            moveToNextLevel();
            gameData.hOnStair = false;
            gameData.iOnStair = false;
        } else if (gameData.hOnStair) {
            System.out.println("H is on stair, waiting for I...");
        } else if (gameData.iOnStair) {
            System.out.println("I is on stair, waiting for H...");
        }
    }

    private void moveToNextLevel() {
        if (gameData.currentLevel + 1 >= gameData.L) {
            System.out.println("No more levels!");
            return;
        }
        gameData.map[gameData.currentLevel][gameData.pX1][gameData.pY1] = 1;
        gameData.map[gameData.currentLevel][gameData.pX2][gameData.pY2] = 1;

        gameData.currentLevel++;

        for (int i = 0; i < gameData.H; i++) {
            for (int j = 0; j < gameData.W; j++) {
                if (gameData.map[gameData.currentLevel][i][j] == 6) {
                    gameData.pX1 = i;
                    gameData.pY1 = j;
                }
                if (gameData.map[gameData.currentLevel][i][j] == 7) {
                    gameData.pX2 = i;
                    gameData.pY2 = j;
                }
            }
        }
    }

    private void handleDoorReward(int player) {
        Random random = new Random();
        if (random.nextInt(2) == 0) {
            int heal = 10 + random.nextInt(21);
            if (player == 1) {
                gameData.heroHealth1 += heal;
                System.out.println("H gets +" + heal + " health!");
            } else {
                gameData.heroHealth2 += heal;
                System.out.println("I gets +" + heal + " health!");
            }
        } else {
            if (player == 1) {
                gameData.isImmune1 = true;
                System.out.println("H gets immune! (Blocks 1 attack)");
            } else {
                gameData.isImmune2 = true;
                System.out.println("I gets immune! (Blocks 1 attack)");
            }
        }
    }

    private void handleMonsterAttack(int player, int tX, int tY, int attack) {
        if (player == 1) {
            if (gameData.isImmune1) {
                gameData.isImmune1 = false;
                System.out.println("H's immune blocks " + attack + " damage!");
                moveHeroH(tX, tY);
            } else {
                gameData.heroHealth1 -= attack;
                System.out.println("H takes " + attack + " damage! Health: " + gameData.heroHealth1);
                if (gameData.heroHealth1 <= 0) {
                    loseGame("H");
                } else {
                    moveHeroH(tX, tY);
                }
            }
        } else {
            if (gameData.isImmune2) {
                gameData.isImmune2 = false;
                System.out.println("I's immune blocks " + attack + " damage!");
                moveHeroI(tX, tY);
            } else {
                gameData.heroHealth2 -= attack;
                System.out.println("I takes " + attack + " damage! Health: " + gameData.heroHealth2);
                if (gameData.heroHealth2 <= 0) {
                    loseGame("I");
                } else {
                    moveHeroI(tX, tY);
                }
            }
        }
    }

    private void moveToNextLevel(int player) {
        if (gameData.currentLevel + 1 >= gameData.L) {
            System.out.println("No more levels!");
            return;
        }
        if (player == 1) {
            gameData.map[gameData.currentLevel][gameData.pX1][gameData.pY1] = 1;
        } else {
            gameData.map[gameData.currentLevel][gameData.pX2][gameData.pY2] = 1;
        }
        gameData.currentLevel++;
        for (int i = 0; i < gameData.H; i++) {
            for (int j = 0; j < gameData.W; j++) {
                if (gameData.map[gameData.currentLevel][i][j] == (player == 1 ? 6 : 7)) {
                    if (player == 1) {
                        gameData.pX1 = i;
                        gameData.pY1 = j;
                    } else {
                        gameData.pX2 = i;
                        gameData.pY2 = j;
                    }
                    return;
                }
            }
        }
    }

    private void winGame() {
        System.out.println("Congratulations! You Win!!");
        System.exit(0);
    }

    private void loseGame(String hero) {
        System.out.println(hero + " has no health left! You Lose!!");
        System.exit(0);
    }

    void moveHeroH(int tX, int tY) {
        gameData.map[gameData.currentLevel][gameData.pX1][gameData.pY1] = 1;
        int targetVal = gameData.map[gameData.currentLevel][tX][tY];
        if (targetVal != 4) {
            gameData.map[gameData.currentLevel][tX][tY] = 6;
        }

        gameData.pX1 = tX;
        gameData.pY1 = tY;
        System.out.println("H moved to (" + tX + "," + tY + ")");
    }

    void moveHeroI(int tX, int tY) {
        gameData.map[gameData.currentLevel][gameData.pX2][gameData.pY2] = 1;
        int targetVal = gameData.map[gameData.currentLevel][tX][tY];
        if (targetVal != 4) {
            gameData.map[gameData.currentLevel][tX][tY] = 7;
        }
        gameData.pX2 = tX;
        gameData.pY2 = tY;
        System.out.println("I moved to (" + tX + "," + tY + ")");
    }
}