package com.alphaknight;

/**
 * Exception for illegal moves.
 */
public class IllegalMoveException extends Exception {
    public IllegalMoveException() {
        super();
    }

    public IllegalMoveException(String msg) {
        super(msg);
    }
}
