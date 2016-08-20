package com.alphaknight;

/**
 * Represents the board using an array of 64 integers. Values in the array from 1 to 6 indicate the presence of a white
 * piece type, and values from -1 to -6 represent a black piece. Zeroes represent an empty square. This class should be
 * treated as immutable, so any changes to the game state can only be made by creating a new Board instance.
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
     * Integer pair representing a move from one square to another.
     */
    public class Move {
        final byte from;
        final byte to;
        final byte captured;

        /**
         * Constructor.
         *
         * @param from Square of piece to move
         * @param to   Square to move piece to
         */
        public Move(byte from, byte to, byte captured) {
            this.from = from;
            this.to = to;
            this.captured = captured;
        }
    }

    // Instance variables
    private final byte[] squares;
    private final byte colorToMove;
    private final boolean check;
    private final boolean checkmate;
    private final Boolean stalemate;
    private final Board previousBoard;
    private final Move previousMove;
    // Used to keep track of castling rights
    private final Boolean whiteKingMoved, whiteLRookMoved, whiteRRookMoved;
    private final Boolean blackKingMoved, blackLRookMoved, blackRRookMoved;

    /**
     * Default constructor. Initializes the board for a new game.
     */
    public Board() {
        squares = new byte[64];
        colorToMove = WHITE;
        check = false;
        checkmate = false;
        stalemate = false;
        previousBoard = null;
        previousMove = null;
        whiteKingMoved = whiteLRookMoved = whiteRRookMoved = false;
        blackKingMoved = blackLRookMoved = blackRRookMoved = false;

        initPieces(WHITE);
        initPieces(BLACK);
    }

    /**
     * Constructor. Creates a new board from the passed in one representing the game state after executing the given
     * move.
     *
     * @param originalBoard the initial game state
     * @param move          the move to make
     */
    private Board(Board originalBoard, Move move) {
        // Get a copy of original board's squares array and update it
        squares = originalBoard.squares.clone();
        squares[move.to] = squares[move.from];
        squares[move.from] = 0;
        // Switch colorToMove to opposite color
        colorToMove = (byte)-originalBoard.colorToMove;
        check = inCheck(colorToMove);

        if(check) {
            checkmate = hasMoves(colorToMove);
            stalemate = false;
        }
        else {
            stalemate = !hasMoves(colorToMove);
            checkmate = false;
        }

        previousBoard = originalBoard;
        previousMove = move;

        whiteKingMoved = squares[4] != KING || originalBoard.whiteKingMoved;
        whiteLRookMoved = squares[0] != ROOK || originalBoard.whiteLRookMoved;
        whiteRRookMoved = squares[7] != ROOK || originalBoard.whiteRRookMoved;
        blackKingMoved = squares[60] != KING || originalBoard.blackKingMoved;
        blackLRookMoved = squares[56] != ROOK || originalBoard.blackLRookMoved;
        blackRRookMoved = squares[63] != ROOK || originalBoard.blackRRookMoved;
    }

    /**
     * Returns a new board based off this one representing the state of the game after making the given move.
     *
     * @param from The square of the piece to move
     * @param to   The square to move the piece to
     * @return     The new board
     * @throws IllegalMoveException
     */
    public Board move(int from, int to) throws IllegalMoveException {
        if(from < 0 || from > 63 || to < 0 || to > 63)
            throw new IllegalMoveException("Move out of bounds");
        if(from == to)
            throw new IllegalMoveException("Not a move");
        if (squares[from] > 0 && colorToMove == BLACK || squares[from] < 0 && colorToMove == WHITE)
            throw new IllegalMoveException("Tried to move piece during opponent's colorToMove");
        if(squares[from] == EMPTY)
            throw new IllegalMoveException("No piece found");

        // Try to make the move.
        // If the method called returns true, move was successful, else throw IllegalMoveException
        Boolean moveIsValid;
        switch (Math.abs(squares[from])) {
            case PAWN:   moveIsValid = movePawn();
                break;
            case KNIGHT: moveIsValid = moveKnight();
                break;
            case BISHOP: moveIsValid = moveBishop();
                break;
            case ROOK:   moveIsValid = moveRook();
                break;
            case QUEEN:  moveIsValid = moveQueen();
                break;
            case KING:   moveIsValid = moveKing();
                break;
            default:     moveIsValid = false;
        }

        if (!moveIsValid) throw new IllegalMoveException("From: " + from + " To: " + to);
        // Make the new board
        return new Board(this, new Move((byte)from, (byte)to, squares[to]));
    }

    public byte[] getSquares() {
        return squares;
    }

    private Boolean movePawn() {
        // TODO
        return false;
    }

    private Boolean moveKnight() {
        // TODO
        return false;
    }

    private Boolean moveBishop() {
        // TODO
        return false;
    }

    private Boolean moveRook() {
        // TODO
        return false;
    }

    private Boolean moveQueen() {
        // TODO
        return false;
    }

    private Boolean moveKing() {
         // TODO
        return false;
    }

    private boolean inCheck(byte color) {
        // TODO
        return false;
    }

    private Boolean kingCanMove(byte color) {
        // TODO
        return false;
    }

    private Boolean hasMoves(byte color) {
        // TODO
        return false;
    }

    /**
     * Puts pieces on the board for a given color.
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
    }

    public static void main(String[] args) {
        Board b = new Board();
        b.printBoard();
    }
}

