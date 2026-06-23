package chess;

public class Bishop implements IntBishop {
    private String color;
    private char posX;
    private int posY;

    public Bishop(String color, char posX, int posY) {
        this.color = color;
        this.posX = posX;
        this.posY = posY;
    }

    @Override
    public boolean isValidMove(char toX, int toY) {
        if (toX < 'a' || toX > 'h' || toY < 1 || toY > 8) {
            return false;
        }
        if (toX == posX && toY == posY) {
            return false;
        }
        return Math.abs(toX - posX) == Math.abs(toY - posY);
    }

    @Override
    public void printValues() {
        System.out.println("Color: " + color + ", Position: " + posX + posY);
    }
}
