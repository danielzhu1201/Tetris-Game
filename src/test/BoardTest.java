package test;
import static org.junit.Assert.*;

import org.junit.Test;

import assignment.JTetris;
import test.Board.Action;
import test.Board.Result;

public class BoardTest {
	
	String[] pieceStrings = { "0 0  1 0  2 0  3 0",
    	    "0 1  1 1  2 1  2 0",
    	    "0 0  0 1  1 1  2 1",
    	    "0 0  1 0  1 1  2 1",
    	    "0 1  1 1  1 0  2 0",
    	    "0 0  0 1  1 0  1 1",
    	    "0 1  1 0  1 1  2 1"};

	@Test
	public void test() {
        // We should be able to create "non-standard" board sizes.
        Board board = new TetrisBoard(JTetris.WIDTH, 5 * JTetris.HEIGHT);
        Board newboard = new TetrisBoard(JTetris.WIDTH, 4 * JTetris.HEIGHT);

        // Equal method.
          assertEquals(board, board);
          assertEquals(newboard, newboard);
          assertNotEquals(board, newboard);
          
          // Trying to move results in NO_PIECE initially, and should result in
          // no change of board state.
          
          assertEquals(Board.Result.NO_PIECE, board.move(Board.Action.DOWN));

          
        Piece piece = (Piece)TetrisPiece.getPiece(pieceStrings[6]);
        board.nextPiece(piece);
        
        //move to far left and drop
        while(board.move(Action.LEFT)==Result.SUCCESS) {
        	board.move(Action.LEFT);
        }
        board.move(Action.RIGHT);
        board.move(Action.DROP);
        
        
        TetrisBoard tetrisBoard = (TetrisBoard)board;
        //testing ox and oy
        assertEquals(0, tetrisBoard.oy);
        assertEquals(1, tetrisBoard.ox);
        
     // Since cp is now null, this should be true.
        assertEquals(Board.Result.NO_PIECE, board.move(Board.Action.DOWN));
        
        // Checking ColumnHeight
        assertEquals(0, tetrisBoard.getColumnHeight(0));
        assertEquals(2, tetrisBoard.getColumnHeight(1));
        assertEquals(false, 1 == tetrisBoard.getColumnHeight(2));
        
        //Checking max height
        assertEquals(2, board.getMaxHeight());
        
        //rotations
        assertEquals(true, TetrisPiece.pointEqual(piece.next.getBody(),Piece.parsePoints("0 1  1 0  1 1  1 2")));
        
        //Add a new different piece
        piece = (Piece)TetrisPiece.getPiece(pieceStrings[0]);
        board.nextPiece(piece);
        
        //Move it down by distance 3;
        board.move(Action.DOWN);board.move(Action.DOWN);board.move(Action.DOWN);
        
        tetrisBoard = (TetrisBoard)board;
        //store ox and oy
        int tempox=tetrisBoard.ox;
        int tempoy=tetrisBoard.oy;
        
        //rotate that piece
        board.move(Action.CLOCKWISE);
        assertEquals(tempox+2, tetrisBoard.ox);
        assertEquals(tempoy-2, tetrisBoard.oy);
        assertEquals(false,tempoy-1==tetrisBoard.oy);
        
        // We should be able to use the standard movements on the board.
        assertEquals(Board.Result.SUCCESS, board.move(Board.Action.DOWN));
        assertEquals(Board.Result.SUCCESS, board.move(Board.Action.NOTHING));
        assertEquals(Board.Result.SUCCESS, board.move(Board.Action.DOWN));
        assertEquals(Board.Result.SUCCESS, board.move(Board.Action.LEFT));
        assertEquals(Board.Result.SUCCESS, board.move(Board.Action.CLOCKWISE));
        assertEquals(Board.Result.SUCCESS, board.move(Board.Action.COUNTERCLOCKWISE));
        
        //Add a new piece.
        piece = (Piece)TetrisPiece.getPiece(pieceStrings[6]);
        board.nextPiece(piece);
        while(board.move(Action.LEFT)==Result.SUCCESS) {
        	board.move(Action.LEFT);
        }
        //testing wallkick with T piece
        board.move(Action.DOWN);
        board.move(Action.COUNTERCLOCKWISE);
        assertEquals(Board.Result.SUCCESS,board.move(Action.LEFT) );
        board.move(Action.DROP);
        
        //testing wallkicks with L part 2
        piece = (Piece)TetrisPiece.getPiece(pieceStrings[2]);
	    board.nextPiece(piece);
	    board.move(Action.COUNTERCLOCKWISE);
	    while(board.move(Action.LEFT)==Result.SUCCESS) {
	        	board.move(Action.LEFT);
	    }
	    assertEquals(Board.Result.SUCCESS, board.move(Action.CLOCKWISE));
	    tetrisBoard = (TetrisBoard)board;
	    tempox=tetrisBoard.ox;
	    tempoy=tetrisBoard.oy;
	    assertEquals(tempox, tetrisBoard.ox);
	    assertEquals(tempoy, tetrisBoard.oy);
	    
	    //moving right
	    piece = (Piece)TetrisPiece.getPiece(pieceStrings[3]);
	    board.nextPiece(piece);
	    while(board.move(Action.RIGHT)==Result.SUCCESS) {
	        	board.move(Action.RIGHT);
	    }
	    
	    //drops and checking the updateColumnHeight method
	    board.move(Board.Action.DROP);
	    tetrisBoard = (TetrisBoard)board;
	    assertEquals(tetrisBoard.getWidth()-piece.getWidth(), tetrisBoard.ox);
	    assertEquals(0, tetrisBoard.oy);
	    
	    piece = (Piece)TetrisPiece.getPiece(pieceStrings[4]);
	    board.nextPiece(piece);
	    Board board2 = board.testMove(Action.LEFT);
	    board.move(Action.LEFT);
	    
	    //testMove
	    assertEquals((TetrisBoard)board2, (TetrisBoard)board);
	    board2=board.testMove(Action.DROP);
	    board.move(Action.DROP);
	    assertEquals((TetrisBoard)board2, (TetrisBoard)board);
	    
	    //nextPiece
	    board.nextPiece((Piece)TetrisPiece.getPiece(pieceStrings[0]));
	    assertEquals(TetrisPiece.getPiece(pieceStrings[0]),(TetrisPiece)board.getCurrentPiece());
	    
	    //getRowsCleared
	    assertEquals(0, board2.getRowsCleared());
	    
	    board.move(Action.DOWN);
	    //getLastAction
	    assertEquals(Action.DOWN, board.getLastAction());
	    //getLastResult
	    assertEquals(Result.SUCCESS, board.getLastResult());
	    
	    //getGrid
	    assertEquals(true, board.getGrid(2, 0));
	    assertEquals(false, board.getGrid(0, 0));
	    assertEquals(false, board.getGrid(1, 0)==board.getGrid(1, 1));
	    
	    //
	}

}
