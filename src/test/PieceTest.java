package test;

import static org.junit.Assert.*;

import org.junit.Test;

public class PieceTest {
	
	String[] pieceStrings = { "0 0  1 0  2 0  3 0",
    	    "0 1  1 1  2 1  2 0",
    	    "0 0  0 1  1 1  2 1",
    	    "0 0  1 0  1 1  2 1",
    	    "0 1  1 1  1 0  2 0",
    	    "0 0  0 1  1 0  1 1",
    	    "0 1  1 0  1 1  2 1"};

	@Test
	public void test() {
		Piece testPiece = TetrisPiece.getPiece(pieceStrings[0]);
		assertEquals(1, testPiece.getHeight()); //height
		assertEquals(4, testPiece.getWidth());  //width
		
		//testing equals method
		Piece testPieceComparison = new TetrisPiece(Piece.parsePoints("2 0  3 0  0 0  1 0"));
		assertEquals(true, testPiece.equals(testPieceComparison));
		Piece testPieceComparison2 = TetrisPiece.getPiece("0 0  0 1  1 0  1 1");
		assertEquals(false, testPiece.equals(testPieceComparison2));
		
		//getBody - test point comparison first
		assertEquals(false, TetrisPiece.pointEqual(Piece.parsePoints(pieceStrings[0]), Piece.parsePoints(pieceStrings[5])));
		assertEquals(true, TetrisPiece.pointEqual(Piece.parsePoints(pieceStrings[0]), testPiece.getBody()));
		assertEquals(false, TetrisPiece.pointEqual(Piece.parsePoints(pieceStrings[1]), testPiece.getBody()));
		
		//getSkirt
		int [] expectedSkirts = {0,0};
		for(int x=0;x<testPieceComparison2.getSkirt().length;x++)
		{
			assertEquals(expectedSkirts[x], testPieceComparison2.getSkirt()[x]);
		}
		
		//nextRotations
		assertEquals(true, TetrisPiece.pointEqual(testPiece.next.getBody(),Piece.parsePoints(("0 0  0 1  0 2  0 3"))));
		
		testPiece = TetrisPiece.getPiece(pieceStrings[2]);
		assertEquals(true, TetrisPiece.pointEqual(testPiece.nextRotation().getBody(),Piece.parsePoints(("0 0  0 1  0 2  1 0"))));
		
		assertEquals(false, TetrisPiece.pointEqual(testPiece.nextClockwiseRotation().getBody(),Piece.parsePoints(("0 0  0 1  0 2  1 0"))));
		assertEquals(true, TetrisPiece.pointEqual(testPiece.nextClockwiseRotation().getBody(),Piece.parsePoints(("0 2  1 0  1 1  1 2"))));
		
		assertArrayEquals(testPiece.nextRotation().nextRotation().getBody(), testPiece.nextClockwiseRotation().nextClockwiseRotation().getBody());
		
		assertArrayEquals(testPiece.getBody(), testPiece.nextRotation().nextClockwiseRotation().getBody());
	}

}
