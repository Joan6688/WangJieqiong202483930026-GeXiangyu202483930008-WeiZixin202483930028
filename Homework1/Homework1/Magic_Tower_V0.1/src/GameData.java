import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;
import java.lang.reflect.Field;

public class GameData implements Serializable {
    int pX1, pY1;
    int pX2, pY2;
    int heroHealth1, heroHealth2;
    int keyNum1, keyNum2;
    boolean isImmune1, isImmune2;
    int L, H, W, currentLevel;
    int[][][] map;
    boolean hOnStair;
    boolean iOnStair;

    void readMapFromFile(String filePath) {
        hOnStair = false;
        iOnStair = false;
        currentLevel = 0;
        isImmune1 = false;
        isImmune2 = false;
        heroHealth1 = 105;
        heroHealth2 = 105;
        keyNum1 = 0;
        keyNum2 = 0;
        pX1 = -1;
        pY1 = -1;
        pX2 = -1;
        pY2 = -1;

        try {
            Scanner in = new Scanner(new File(filePath));
            L = in.nextInt();
            H = in.nextInt();
            W = in.nextInt();
            map = new int[L][H][W];

            for (int level = 0; level < L; level++) {
                for (int row = 0; row < H; row++) {
                    for (int col = 0; col < W; col++) {
                        map[level][row][col] = in.nextInt();

                        if (level == currentLevel) {
                            if (map[level][row][col] == 6 && pX1 == -1) {
                                pX1 = row;
                                pY1 = col;
                                System.out.println("H+ (level+1) + ）：(" + pX1 + "," + pY1 + ")");
                            }
                            else if (map[level][row][col] == 7 && pX2 == -1) {
                                pX2 = row;
                                pY2 = col;
                                System.out.println("I+ (level+1) + ）：(" + pX2 + "," + pY2 + ")");
                            }
                        }
                    }
                }
            }

            if (pX1 == -1) {
                pX1 = 3;
                pY1 = 3;
                System.out.println("unknownH：(" + pX1 + "," + pY1 + ")");
            }
            if (pX2 == -1) {
                pX2 = 3;
                pY2 = 2;
                System.out.println("unknownI：(" + pX2 + "," + pY2 + ")");
            }

        } catch (IOException e) {
            System.out.println("Error with files:" + e.toString());
        }
    }
    void printMap() {
        char C[] = { 'W', '_', 'K', 'D', 'S', 'E', 'H', 'I'};
        for (int j = 0; j < H; j++) {
            for (int k = 0; k < W; k++) {
                int val = map[currentLevel][j][k];
                if (val < 0) {
                    System.out.print("M ");
                } else if (val > 10) {
                    System.out.print("P ");
                } else {
                    System.out.print(C[val] + " ");
                }
            }
            System.out.print("\n");
        }
        String immuneTip1 = isImmune1 ? "Immune: Effective (1 attack block) " : "Immune: None ";
        String immuneTip2 = isImmune2 ? "Immune: Effective (1 attack block) " : "Immune: None ";
        System.out.println("H: Health:" + heroHealth1 + "  KeyNum:" + keyNum1 + "  " + immuneTip1 + "Menu:press 0");
        System.out.println("I: Health:" + heroHealth2 + "  KeyNum:" + keyNum2 + "  " + immuneTip2 + "Menu:press 0");
    }

    void copyFields(Object source) {
        try {
            Class<?> clazz = this.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(source);
                field.set(this, value);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}