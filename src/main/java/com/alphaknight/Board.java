package com.alphaknight;

import java.util.Stack;

/**
 * Represents the board using an array of 64 integers. Values in the array from 1 to 6 indicate the presence of a white
 * piece type, and values from -1 to -6 represent a black piece. Zeroes represent an empty square.
 *
 * Array indices visualized:
 *
 * 56 57 58 59 60 61 62 63  BLACK
 * 48 49 50 51 52 53 54 55
 * 40 41 42 43 44 45 46 47
 * 32 33 34 35 36 37 38 39
 * 24 25 26 27 28 29 30 31
 * 16 17 18 19 20 21 22 23
 * 8  9  10 11 12 13 14 15
 * 0  1  2  3  4  5  6  7   WHITE
 */
public class Board {
    public static final byte EMPTY = 0;
    public static final byte PAWN = 1;
    public static final byte KNIGHT = 2;
    public static final byte BISHOP = 3;
    public static final byte ROOK = 4;
    public static final byte QUEEN = 5;
    public static final byte KING = 6;

    public static final byte WHITE = 1;
    public static final byte BLACK = -1;

    /**
     * Represents a move.
     */
    public class Move {
        final byte from;
        final byte to;
        final byte piece;
        final byte captured;

        /**
         * Constructor.
         *
         * @param from     Square of piece to move
         * @param to       Square to move piece to
         * @param captured Value present in the "to" square
         */
        public Move(byte from, byte to, byte moved, byte captured) throws IllegalArgumentException {
            if(from < 0 || from > 63 || to < 0 || to > 63)
                throw new IllegalArgumentException("Square out of bounds");
            if(from == to)
                throw new IllegalArgumentException("Not a move");
            if(moved < -6 || moved > 6 || captured < -6 || captured > 6)
                throw new IllegalArgumentException("Piece value is undefined");

            // Make sure the piece doesn't move off the sides of the board
            int fromCol = from % 8;
            int toCol = to % 8;

            if (fromCol < fromCol + ((to - from) % 8) && fromCol > toCol)
                throw new IllegalArgumentException("\"to\" goes beyond right side of board");

            if (fromCol > fromCol + ((to - from) % 8) && fromCol < toCol)
                throw new IllegalArgumentException("\"to\" goes beyond left side of board");

            this.from = from;
            this.to = to;
            this.piece = moved;
            this.captured = captured;
        }

        @Override
        public String toString() {
            String str;
            switch (piece) {
                case PAWN:   str = "";
                    break;
                case KNIGHT: str = "N";
                    break;
                case BISHOP: str = "B";
                    break;
                case ROOK:   str = "R";
                    break;
                case QUEEN:  str = "Q";
                    break;
                case KING:   str = "K";
                    break;
                default:     str = "";
            }
            int file = 'a' + to % 8;
            int rank = '1' + to / 8;
            if(captured != EMPTY) str += "x";
            return str + (char)file + (char)rank;
        }
    }

    // Instance variables
    private byte[] squares;
    private byte colorToMove;
    private boolean check;
    private boolean checkmate;
    private Boolean stalemate;
    private Stack<Move> moveHistory;
    // Used to keep track of castling rights
    private Boolean whiteKingMoved, whiteLRookMoved, whiteRRookMoved;
    private Boolean blackKingMoved, blackLRookMoved, blackRRookMoved;

    /**
     * Default constructor. Initializes the board for a new game.
     */
    public Board() {
        squares = new byte[64];
        colorToMove = WHITE;
        check = false;
        checkmate = false;
        stalemate = false;
        moveHistory = new Stack<Move>();
        whiteKingMoved = whiteLRookMoved = whiteRRookMoved = false;
        blackKingMoved = blackLRookMoved = blackRRookMoved = false;

        initPieces(WHITE);
        initPieces(BLACK);
    }

    /**
     * Moves a piece. Throws IllegalMoveException if the move is invalid.
     *
     * @param from The square of the piece to move
     * @param to   The square to move the piece to
     * @throws IllegalMoveException
     */
    public void move(int from, int to) throws IllegalMoveException {
        if (squares[from] > 0 && colorToMove == BLACK || squares[from] < 0 && colorToMove == WHITE)
            throw new IllegalMoveException("Tried to move piece during opponent's turn");
        if (squares[from] == EMPTY)
            throw new IllegalMoveException("No piece found");
        if (squares[to] * colorToMove > 0)
            throw new IllegalMoveException("Tried to capture piece of the same color");

        // Try to make the move.
        // If the method called returns true, the piece was piece, else throw IllegalMoveException
        Move m = new Move((byte)from, (byte)to, squares[from], squares[to]);
        Boolean moveIsValid;

        switch (Math.abs(m.piece)) {
            case PAWN:   moveIsValid = movePawn(m);
                break;
            case KNIGHT: moveIsValid = moveKnight(m);
                break;
            case BISHOP: moveIsValid = moveBishop(m);
                break;
            case ROOK:   moveIsValid = moveRook(m);
                break;
            case QUEEN:  moveIsValid = moveQueen(m);
                break;
            case KING:   moveIsValid = moveKing(m);
                break;
            default:     moveIsValid = false;
        }

        if (!moveIsValid) throw new IllegalMoveException("Invalid move from " + from + " to " + to);

        // If the move left the king in check, undo it and throw exception
        if (inCheck(colorToMove)) {
            squares[m.from] = m.piece;
            squares[m.to] = m.captured;
            throw new IllegalMoveException("Move leaves king in check");
        }

        // Move was valid! Update the board
        moveHistory.push(m);
        colorToMove = (byte)-colorToMove;
        check = inCheck(colorToMove);
        if (check) {
            if(!hasMoves(colorToMove)) checkmate = true;
            stalemate = false;
        }
        else {
            if(!hasMoves(colorToMove)) stalemate = true;
            checkmate = false;
        }
    }

    public void move(int srcCol, int srcRow, int dstCol, int dstRow) throws IllegalMoveException {
        move((srcRow - 1) * 8 + srcCol - 1, (dstRow - 1) * 8 + dstCol - 1);
    }

    public byte[] getSquares() {
        return squares;
    }

    public Boolean getCheck() {
        return inCheck(colorToMove);
    }

    public Boolean getCheckmate() {
        return checkmate;
    }

    public Boolean getStalemate() {
        return stalemate;
    }

    public int getColorToMove() {
        return colorToMove;
    }

    private boolean movePawn(Move move) {
        // TODO -- add stuff for en passant
        int jump = move.to - move.from;

        // Make sure sign of jump matches color
        if (jump * colorToMove == 7 || jump * colorToMove == 9)
            if (move.captured == EMPTY) return false;

        else if (jump * colorToMove == 8)
            if (move.captured != EMPTY) return false;

        else return false;

        squares[move.from] = EMPTY;
        squares[move.to] = move.piece;

        return true;
    }

    private boolean moveKnight(Move move) {
        // Possible "jumps" from the current square
        byte[] moves = {-17, 15, 17, -15};

        for (byte m : moves) {
            // If this is a valid "jump" and captured piece is opposite color (or empty), try the move
            if (move.to - move.from == m) {
                squares[move.to] = move.piece;
                squares[move.from] = EMPTY;

                return true;
            }
        }

        return false;
    }

    private boolean moveBishop(Move move) {
        byte[] moves = {7, 9, -7, -9};

        for (byte m : moves) {
            // If to - from is divisible by m, and both numbers have the same sign, try sliding
            if ((move.to - move.from) % m == 0 && (move.to - move.from) * m > 0) {
                // Check every diagonal between from and to and make sure it's empty
                int jumps = 1;
                while (jumps * m + move.from != move.to) {
                    if (squares[jumps * m + move.from] != EMPTY) return false;
                    jumps++;
                }
                squares[move.to] = move.piece;
                squares[move.from] = EMPTY;

                return true;
            }
        }
        return false;
    }

    private boolean moveRook(Move move) {
        // TODO
        return false;
    }

    private boolean moveQueen(Move move) {
        // TODO
        return false;
    }

    private boolean moveKing(Move move) {
         // TODO
        return false;
    }

    private boolean inCheck(byte color) {
        // TODO
        return false;
    }

    private Boolean hasMoves(byte color) {
        // TODO
        return false;
    }

    /**
     * Puts pieces on the board for a given color and adds them to the color's piece array.
     *
     * @param color The color of the pieces to initialize
     */
    private void initPieces(byte color) {
        for (int i = 0; i < 16; i++) {
            // If this is for black subtract 63 from i and take absolute value
            int index = color == WHITE ? i : Math.abs(i - 63);
            if (i >= 8 && i <= 15) squares[index] = (byte)(PAWN * color);
            else if (i == 0 || i == 7) squares[index] = (byte)(ROOK * color);
            else if (i == 1 || i == 6) squares[index] = (byte)(KNIGHT * color);
            else if (i == 2 || i == 5) squares[index] = (byte)(BISHOP * color);
            else if (i == 3) squares[index] = color == WHITE ? QUEEN : -KING;
            else if (i == 4) squares[index] = color == WHITE ? KING : -QUEEN;
        }
    }

    private void printBoard() {
        String str = "";
        for (int i = 63; i >= 0; i--) {
            switch (Math.abs(squares[i])) {
                case EMPTY:  str = "[ ] " + str;
                    break;
                case PAWN:   str = "[p] " + str;
                    break;
                case KNIGHT: str = "[k] " + str;
                    break;
                case BISHOP: str = "[b] " + str;
                    break;
                case ROOK:   str = "[r] " + str;
                    break;
                case QUEEN:  str = "[q] " + str;
                    break;
                case KING:   str = "[K] " + str;
                    break;
            }
            if (i%8 == 0)  {
                System.out.println(str);
                str = "";
            }
        }

        int moveCount = 0;
        for(Move move : moveHistory) {
            if(moveCount % 2 == 0) str += (moveCount/2 + 1) + ". ";
            str += move.toString() + " ";
            moveCount++;
        }
        System.out.println("\n" + str + "\n");
    }

    public static void main(String[] args) {
        Board b = new Board();
        b.printBoard();

        try {
            b.move(5, 2, 5, 4);
            b.printBoard();

            b.move(4, 7, 4, 6);
            b.printBoard();

            b.move(2, 1, 3, 3);
            b.printBoard();

            b.move(5, 7, 5, 5);
            b.printBoard();

            b.move(4, 2, 4, 4);
            b.printBoard();

            b.move(5, 5, 4, 4);
            b.printBoard();
        }
        catch (IllegalMoveException ime) {
            System.out.println(ime.getMessage());
        }

    }
}

