package assignment;

import java.util.*;

import test.Board;

public class TetrisDeepMind implements Brain {

    private ArrayList<Board> options;
    private ArrayList<Board.Action> firstMoves;

    /**
     * Decide what the next move should be based on the state of the board.
     */
    public Board.Action nextMove(Board currentBoard) {
        // Fill the our options array with versions of the new Board
        options = new ArrayList<>();
        firstMoves = new ArrayList<>();
        enumerateOptions(currentBoard);

        int best = 0;
        int bestIndex = 0;

        // Check all of the options and get the one with the highest score
        for (int i = 0; i < options.size(); i++) {
            int score = scoreBoard(options.get(i));
            if (score > best) {
                best = score;
                bestIndex = i;
            }
        }

        // We want to return the first move on the way to the best Board
        return firstMoves.get(bestIndex);
    }

    private void enumerateOptions(Board currentBoard) {
        // We can always drop our current Piece
        options.add(currentBoard.testMove(Board.Action.DROP));
        firstMoves.add(Board.Action.DROP);

         //Now we'll add all the places to the left we can DROP with rotations
        Board left = currentBoard.testMove(Board.Action.LEFT);
        while (left.getLastResult() == Board.Result.SUCCESS) {
            options.add(left.testMove(Board.Action.DROP));
            firstMoves.add(Board.Action.LEFT);
            
            left.move(Board.Action.LEFT);
        }

        // And then the same thing to the right
        Board right = currentBoard.testMove(Board.Action.RIGHT);
        while (right.getLastResult() == Board.Result.SUCCESS) {
            options.add(right.testMove(Board.Action.DROP));
            firstMoves.add(Board.Action.RIGHT);
            right.move(Board.Action.RIGHT);
        }

		Board clockwise1=currentBoard.testMove(Board.Action.CLOCKWISE);
		while (clockwise1.getLastResult() == Board.Result.SUCCESS) {
	            options.add(clockwise1.testMove(Board.Action.DROP));
	            firstMoves.add(Board.Action.CLOCKWISE);
	            clockwise1.move(Board.Action.LEFT);
		}
	
		Board clockwise2=currentBoard.testMove(Board.Action.CLOCKWISE);
		while (clockwise2.getLastResult() == Board.Result.SUCCESS) {
	            options.add(clockwise2.testMove(Board.Action.DROP));
	            firstMoves.add(Board.Action.CLOCKWISE);
	            clockwise2.move(Board.Action.RIGHT);
		}
	
	
			Board counterclockwise1=currentBoard.testMove(Board.Action.COUNTERCLOCKWISE);
		while (counterclockwise1.getLastResult() == Board.Result.SUCCESS) {
	            options.add(counterclockwise1.testMove(Board.Action.DROP));
	            firstMoves.add(Board.Action.COUNTERCLOCKWISE);
	            counterclockwise1.move(Board.Action.LEFT);
		}
	
		Board counterclockwise2=currentBoard.testMove(Board.Action.COUNTERCLOCKWISE);
		while (counterclockwise2.getLastResult() == Board.Result.SUCCESS) {
	            options.add(counterclockwise2.testMove(Board.Action.DROP));
	            firstMoves.add(Board.Action.COUNTERCLOCKWISE);
	            counterclockwise2.move(Board.Action.RIGHT);
	    }
		
//		Board temp=currentBoard.testMove(Board.Action.CLOCKWISE);
//		Board doubleclockwise1=temp.testMove(Board.Action.CLOCKWISE);
//
//		while (doubleclockwise1.getLastResult() == Board.Result.SUCCESS) {
//	            options.add(doubleclockwise1.testMove(Board.Action.DROP));
//	            firstMoves.add(Board.Action.CLOCKWISE);
//	            doubleclockwise1.move(Board.Action.LEFT);
//	    }
//		
//		Board temp2=currentBoard.testMove(Board.Action.CLOCKWISE);
//		Board doubleclockwise2=temp2.testMove(Board.Action.CLOCKWISE);
//
//		while (doubleclockwise2.getLastResult() == Board.Result.SUCCESS) {
//	            options.add(doubleclockwise2.testMove(Board.Action.DROP));
//	            firstMoves.add(Board.Action.CLOCKWISE);
//	            doubleclockwise2.move(Board.Action.RIGHT);
//	    }
	} 

    /**
     * Since we're trying to avoid building too high,
     * we're going to give higher scores to Boards with
     * MaxHeights close to 0. We're also going to consider the number of holes and the continuity.
     */
    private int scoreBoard(Board newBoard) {
    	
    	newBoard.updateColumnHeight();
    	int sumofheights=0;
    	int bumps=0;
    	int zz=0;
    	for(int i=0;i<newBoard.getWidth();i++)
    	{
    		sumofheights+=newBoard.getColumnHeight(i);
    	}
    	
    	for(int i =0;i<newBoard.getWidth()-1;i++)
    	{
    		bumps= bumps+ Math.abs(newBoard.getColumnHeight(i)-newBoard.getColumnHeight(i+1));
    	}
    	zz = 100000-51*(sumofheights+10*newBoard.getRowsCleared())-18*bumps-35*holes(newBoard)+76*newBoard.getRowsCleared();
    	return zz;
    }
    
    public int holes(Board newBoard) {
    	int noofholes=0;
    	for(int j=0;j<newBoard.getHeight()-5;j++) {
    		for(int i =0;i<newBoard.getWidth();i++)
    		{
    			if(newBoard.getGrid(i, j)==false&&newBoard.getGrid(i, j+1)==true)
    			{
    				noofholes++;
    			}
    		}
    	}
    	return noofholes;
    }
}

