package chess;

public class MyChessInterfaceClass {
    public static void main(String[] args) {
        Rook rook = new Rook("white", 'e', 4);
        Bishop bishop = new Bishop("black", 'c', 3);

        System.out.println("Rook move to d4: " + rook.isValidMove('d', 4));
        System.out.println("Rook move to e7: " + rook.isValidMove('e', 7));

        System.out.println("Bishop move to f6: " + bishop.isValidMove('f', 6));
        System.out.println("Bishop move to a1: " + bishop.isValidMove('a', 1));

        bishop.printValues();
    }
}
