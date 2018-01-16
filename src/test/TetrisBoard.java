package test;

import java.awt.*;


/**
 * Represents a Tetris board -- essentially a 2-d grid of booleans. Supports
 * tetris pieces and row clearing.  Does not do any drawing or have any idea of
 * pixels. Instead, just represents the abstract 2-d board.
 */

public final class TetrisBoard implements Board {
	
	public boolean[][] board;
	public int maxheight, ox, oy, rowscleared,flag;
	//ox and oy hold the origin of the current piece
	public int columnheights[];
	public Piece cp; // this is the current piece
	public Result result;
	public Action action;
	//All wk arrays hold wallkick data.
	int[] wk1 = {0,0,-1,0,-1,1,0,-2,-1,-2};
	int[] wk2 = {0,0,1,0,1,1,0,-2,1,-2};
	int[] wk3 = {0,0,-2,0,1,0,-2,-1,1,2};
	int[] wk4 = {0,0,-1,0,2,0,-1,2,2,-1};

    // JTetris will use this constructor
    public TetrisBoard(int width, int height) 
    {
    	board = new boolean[height][width];
    	maxheight=0;ox=0;oy=0;rowscleared=0;
    	cp=null;
    	columnheights = new int[width];
    	result = null;
    	action = null;
    }

    @Override
    //Moves the current piece according to the action and returns success if the action was successful, else returns some other result.
    public Result move(Action act) 
    {
    	rowscleared=0;
    	action = act;
    	if(cp==null)
    		{
    		result = Result.NO_PIECE; 
    		return Result.NO_PIECE;
    		}
    	if(act==Action.LEFT)
    	{
    		//check if legal
    		if(checkLeftLegal(cp)==false)
    			{
    			result = Result.OUT_BOUNDS;  
    			return Result.OUT_BOUNDS;
    			}
    		erasePiece(cp);
    		ox--;
    		placePiece(cp);
    		result = Result.SUCCESS; 
    		return Result.SUCCESS;
    	}
    	if(act==Action.RIGHT)
    	{
    		if(checkRightLegal(cp)==false) 
    		{
    			result = Result.OUT_BOUNDS; 
    			return Result.OUT_BOUNDS;
    		}
    		erasePiece(cp);
    		ox++;
    		placePiece(cp);
    		result = Result.SUCCESS; 
    		return Result.SUCCESS;
    	}
    	
    	if(act==Action.DOWN)
    	{
    		action = Action.DOWN;
    		if(checkDownLegal(cp)==false)
    		{
    			updateColumnHeight();
    			clearRow();
    			cp=null;
    			result = Result.PLACE; 
    			return Result.PLACE;
    		}
    		erasePiece(cp);
    		updateColumnHeight();
    		oy--;
    		placePiece(cp);
    		result = Result.SUCCESS;
    		return result;
    	}
    	
    	if(act==Action.NOTHING){
    		result = Result.SUCCESS;
    		return Result.SUCCESS;}
    	
    	if(act==Action.CLOCKWISE)
    	{
    		erasePiece(cp);
    		int temp1=ox; int temp2=oy;
    		changeOrigin();
    		flag++;
    		cp=cp.nextClockwiseRotation();
    		if(wallkickclockwise()==Result.SUCCESS) {result=Result.SUCCESS; return Result.SUCCESS;}
    		else{
    			cp=cp.nextClockwiseRotation().nextClockwiseRotation().nextClockwiseRotation();
    			ox=temp1;oy=temp2;
    			placePiece(cp);
    			flag--;
    			result = Result.OUT_BOUNDS;
    			return Result.OUT_BOUNDS;
    		}
    	}
    	
    	if(act==Action.COUNTERCLOCKWISE)
    	{
    		erasePiece(cp);
    		int temp1=ox; int temp2=oy;
    		changeOrigin(); flag++;
    		cp=cp.nextClockwiseRotation(); changeOrigin();flag++;
    		cp=cp.nextClockwiseRotation(); changeOrigin();flag++;
    		cp=cp.nextClockwiseRotation();
    		if(wallkickcounterclockwise()==Result.SUCCESS) {result=Result.SUCCESS; return Result.SUCCESS;}
    		else{
    			cp=cp.nextClockwiseRotation();
    			ox=temp1;oy=temp2;
    			placePiece(cp);
    			flag-=3;
    			result = Result.OUT_BOUNDS;
    			return Result.OUT_BOUNDS;
    		}
    	}
    	
    	if(act==Action.DROP)
    	{
    		erasePiece(cp);
    		updateColumnHeight();
    		oy=dropHeight(cp, ox);
    		placePiece(cp);
    		cp=null;
    		result = Result.PLACE;
    		updateColumnHeight();
    		clearRow();
    		return result;
    	}
    	
    	if(act==Action.HOLD)
    	{
    		
    	}
    	result = Result.NO_PIECE;
    	return result;
    }

    @Override
    //tests the move on a new board
    public Board testMove(Action act) 
    { 
    	TetrisBoard board2 = new TetrisBoard(getWidth(), getHeight());
    	board2.setBoard(board, ox, oy,rowscleared,getLastAction(),getLastResult());
    	board2.updateColumnHeight();
    	board2.setPiece(cp);
    	board2.move(act);
    	board2.updateColumnHeight();
    	return (Board)board2;
    }

    @Override
    //gives the board its next piece
    public void nextPiece(Piece p) 
    {
    	oy = getHeight()-p.getHeight();
    	ox = getWidth()/2;
    	placePiece(p);
    	cp = p;
    	flag=0;
    }

    @Override
    //checks if 2 boards are equal
    public boolean equals(Object other) 
    {
    	if(other==null){return false;}
    	Board board2 = (Board)other;
    	
    	if(board2.getWidth()!=getWidth()) {return false;}
    	
    	if(board2.getHeight()!=getHeight()) {return false;}
    	
    	for(int i=0; i<getHeight();i++)
    	{
    		for(int j=0;j<getWidth();j++)
    		{
    			if(board2.getGrid(j, i)!=board[i][j]) {return false;}
    		}
    	}
    	
    	
    	if(board2.getRowsCleared()!=getRowsCleared()) {return false;}
    	if(board2.getLastAction()!=action) {return false;}
    	if(board2.getLastResult()!=result) {return false;}
    	
    	return true;
    }

    //Returns the current Piece
    public Piece getCurrentPiece()
    {
    	return cp;
    }
    
    @Override
    public Result getLastResult() { return result; }

    @Override
    public Action getLastAction() { return action; }

    @Override
    public int getRowsCleared() { return rowscleared; }

    @Override
    public int getWidth() { return board[0].length; }

    @Override
    public int getHeight() { return board.length; }

    @Override
    public int getMaxHeight() { return maxheight; }

    @Override
    public int dropHeight(Piece piece, int x)
    {
    	int tempy = oy;
    	int a[]= piece.getSkirt();
    	
    	int dropheight = 1000000;
    	for(int i = 0; i<piece.getWidth();i++)
    	{
    		dropheight=Math.min(tempy+a[i]-getColumnHeight(x+i),dropheight);
    	}
    	return tempy-dropheight;
    }

    @Override
    public int getColumnHeight(int x) { return columnheights[x]; }

    @Override
    public int getRowWidth(int y) 
    {
    	int rw = 0;
    	for(int i=0;i<board[0].length;i++)
    	{
    		if(board[y][i]==true) {rw++;}
    	}
    	return rw;
    }

    @Override
    public boolean getGrid(int x, int y) { return board[y][x]; }
    
    
    public void placePiece(Piece p)
    {
    	Point a[] = p.getBody();
    	for(int i = 0; i<a.length; i++)
    	{
    		board[oy+(int)a[i].getY()][ox+(int)a[i].getX()]=true;
    	}
    }
    
    //Erases the current piece from the board.
    public void erasePiece(Piece p)
    {
    	Point a[] = p.getBody();
    	for(int i = 0; i<a.length; i++)
    	{
    		board[oy+(int)a[i].getY()][ox+(int)a[i].getX()]=false;
    	}
    }
    
    //checks if the current piece can move one space right
    public boolean checkRightLegal(Piece p)
    {
    	int a[] = p.getRightSkirt();
    	for(int i =0;i<p.getHeight();i++)
    	{
    		if(ox+a[i]+1>=getWidth()) return false;
    		if(board[oy+i][ox+a[i]+1]==true) return false;
    	}
    	return true;
    }
    
    //checks if the current piece can move one space left
    public boolean checkLeftLegal(Piece p)
    {
    	int a[] = p.getLeftSkirt();
    	for(int i =0;i<p.getHeight();i++)
    	{
    		if(ox+a[i]-1<0) return false;
    		if(board[oy+i][ox+a[i]-1]) return false;
    	}
    	return true;
    }
    
    //checks if the current piece can move one space down
    public boolean checkDownLegal(Piece p)
    {
    	int a[] = p.getSkirt();
    	for(int i =0;i<p.getWidth();i++)
    	{
    		if(oy+a[i]-1<0) return false;
    		if(board[oy+a[i]-1][ox+i]==true) return false;
    	}
    	return true;
    }
    
    //checks if the piece is in a 'legal' position
    public boolean checklegal(Piece p) 
    {
    	Point a[] = p.getBody();
    	for(int i = 0; i<4; i++)
    	{
    		if(ox+a[i].getX()>=getWidth()||ox<0)return false;
    		if(oy+a[i].getY()>=getHeight()||oy<0)return false;
    		if(board[oy+(int)a[i].getY()][ox+(int)a[i].getX()]==true)return false;
    	}
    	return true;
	}
    //updates the height of each column
    public void updateColumnHeight()
    {	
    	maxheight=0;
    	for(int i = 0;i<getWidth();i++)
    	{
        	int fl=0;
    		gohere:
    		for(int j = getHeight()-4;j>=0;j--)
    		{
    			if(board[j][i]==true)
    			{
    				columnheights[i]=j+1;
    				maxheight = Math.max(maxheight, columnheights[i]);
    				fl=1;
    				break gohere;
    			}
    		}
        	if(fl==0)
    		{
        		columnheights[i]=0;
        		maxheight = Math.max(maxheight, columnheights[i]);
    		}
        	
        	maxheight = Math.max(maxheight, columnheights[i]);
    	}
    	
    }
    
    //Clears all fully filled rows
    public void clearRow()
    { 
    	int i = 0;
    	
    	for(i=0;i<getHeight();i++)
    	{
    		int fl =0;
    		lolol:
    		for(int j=0;j<getWidth();j++)
    		{
    			if(board[i][j]==false) {fl=1; break lolol;}
    		}
    		if(fl==0)
    		{for(int j=0;j<getWidth();j++)
    		{
    			board[i][j]=false;
    		}
    		rowscleared++;
    		maxheight--;
    		for(int k = i+1;k<getHeight();k++)
    		{
    			for(int l = 0;l<getWidth();l++)
    			{
    				if(board[k][l]==true) {board[k-1][l]=true; board[k][l]=false;}
    			}
    		}
    		updateColumnHeight();
    		clearRow();}
    	}
    }
    
    //Changes the origin based on the rotation
    public void changeOrigin()
    {
			if(TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints(("0 0  0 1  0 2  0 3")))) 
			{
				if(flag%4==1) {ox-=2;oy++;}
				else {ox--;oy+=2;}
			}
			else if(TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints(("0 0  1 0  1 1  2 0")))) {ox++;oy--;}
			else if(TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints(("0 0  1 0  1 1  1 2")))) {oy++;}
			else if(TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints(("0 0  0 1  0 2  1 0")))) {ox--;}
			else if(TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints(("0 1  1 0  1 1  2 0")))) 
			{
				if(flag%4==2) {}
				else {ox++;oy--;}
			}
			else if(TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints(("0 0  1 0  1 1  2 1"))))
			{
				if(flag%4==2) {}
				else {ox++;oy--;}
			}

			else if(TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints(("0 0  1 0  2 0  3 0"))))
			{
				if(flag%4==2) {ox++;oy--;}
				else {ox+=2;oy-=2;}
			}
			else if(TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints(("0 0  0 1  0 2  1 1")))) {ox--;}
			else if(TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints(("0 0  0 1  1 0  2 0")))) {ox++; oy--;}
			else if(TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints(("0 0  0 1  1 1  2 1")))) {}
			else if(TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints(("0 0  0 1  1 1  1 2")))) 
			{
				if(flag%4==1) {ox--;}
				else {oy++;}
			}
			else if(TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints(("0 1  0 2  1 0  1 1")))) 
			{
				if(flag%4==1){ox--;}
				else {oy++;}
			}
			
			else if(TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints(("0 1  1 0  1 1  2 1")))) {}
			else if(TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints(("0 0  0 1  0 2  1 2")))) {ox--;}
			else if(TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints(("0 2  1 0  1 1  1 2")))) {oy++;}

			else if(TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints(("0 1  1 0  1 1  1 2")))) {oy++;}
			else if(TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints(("0 1  1 1  2 0  2 1")))) {}
			else if(TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints(("0 0  1 0  2 0  2 1")))) {ox++; oy--;}
    }
    
    //Wallkicks for clockwise rotations
    public Result wallkickclockwise()
    {	
    	int tempox=ox;
    	int tempoy=oy;
    	
    	int q = cp.getId();
    	
    	//check if piece is T or L
    	if(TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints("0 0  1 0  1 1  2 0"))||TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints("0 0  0 1  0 2  1 1"))||
    	   TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints("0 1  1 0  1 1  2 1"))||TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints("0 1  1 0  1 1  1 2"))||
    	   TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints("0 0  1 0  1 1  1 2"))||TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints("0 0  0 1  1 0  2 0"))||
    	   TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints("0 0  0 1  0 2  1 2"))||TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints("0 1  1 1  2 0  2 1"))||
    	   TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints("0 0  0 1  0 2  1 0"))||TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints("0 0  0 1  1 1  2 1"))||
    	   TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints("0 2  1 0  1 1  1 2"))||TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints("0 0  1 0  2 0  2 1")))
    	{
    		switch (q) {
			case 3:{
				for(int i = 0;i<5;i++)
				{
					ox=tempox; oy=tempoy;
					ox+=wk1[2*i]; oy+=wk1[2*i+1];
					if(checklegal(cp)==true){
						placePiece(cp);
						return Result.SUCCESS;
					}
				}
			}
			
			case 0:{
				for(int i = 0;i<5;i++)
				{
					ox=tempox; oy=tempoy;
					ox-=wk1[2*i]; oy-=wk1[2*i+1];
					if(checklegal(cp)==true){
						placePiece(cp);
						return Result.SUCCESS;
					}
				}
			}
			
			case 1:{
				for(int i = 0;i<5;i++)
				{
					ox=tempox; oy=tempoy;
					ox+=wk2[2*i]; oy+=wk2[2*i+1];
					if(checklegal(cp)==true){
						placePiece(cp);
						return Result.SUCCESS;
					}
				}
			}
			
			case 2:{
				for(int i = 0;i<5;i++)
				{
					ox=tempox; oy=tempoy;
					ox-=wk2[2*i]; oy-=wk2[2*i+1];
					if(checklegal(cp)==true){
						placePiece(cp);
						return Result.SUCCESS;
					}
				}
			}
			break;
			}
	    	result = Result.OUT_BOUNDS;
	    	return Result.OUT_BOUNDS;
    	}
    	
    	
    	else if(TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints("0 0  0 1  0 2  0 3"))||TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints("0 0  1 0  2 0  3 0")))
    	{
    		switch (q) {
			case 1:{
				for(int i = 0;i<5;i++)
				{
					ox=tempox; oy=tempoy;
					ox+=wk3[2*i]; oy+=wk3[2*i+1];
					if(checklegal(cp)==true){
						placePiece(cp);
						return Result.SUCCESS;
					}
				}
			}
			
			case 2:{
				for(int i = 0;i<5;i++)
				{
					ox=tempox; oy=tempoy;
					ox+=wk4[2*i]; oy+=wk4[2*i+1];
					if(checklegal(cp)==true){
						placePiece(cp);
						return Result.SUCCESS;
					}
				}
			}
			
			case 3:{
				for(int i = 0;i<5;i++)
				{
					ox=tempox; oy=tempoy;
					ox-=wk3[2*i]; oy-=wk3[2*i+1];
					if(checklegal(cp)==true){
						placePiece(cp);
						return Result.SUCCESS;
					}
				}
			}
			
			case 0:{
				for(int i = 0;i<5;i++)
				{
					ox=tempox; oy=tempoy;
					ox-=wk4[2*i]; oy-=wk4[2*i+1];
					if(checklegal(cp)==true){
						placePiece(cp);
						return Result.SUCCESS;
					}
				}
			}
			break;
			}
    		result=Result.OUT_BOUNDS;
    		return result;
    		
    	}
    	else{
	    	switch (q) {
			case 1:{
				for(int i = 0;i<5;i++)
				{
					ox=tempox; oy=tempoy;
					ox+=wk1[2*i]; oy+=wk1[2*i+1];
					if(checklegal(cp)==true){
						placePiece(cp);
						return Result.SUCCESS;
					}
				}
			}
			
			case 2:{
				for(int i = 0;i<5;i++)
				{
					ox=tempox; oy=tempoy;
					ox-=wk1[2*i]; oy-=wk1[2*i+1];
					if(checklegal(cp)==true){
						placePiece(cp);
						return Result.SUCCESS;
					}
				}
			}
			
			case 3:{
				for(int i = 0;i<5;i++)
				{
					ox=tempox; oy=tempoy;
					ox+=wk2[2*i]; oy+=wk2[2*i+1];
					if(checklegal(cp)==true){
						placePiece(cp);
						return Result.SUCCESS;
					}
				}
			}
			
			case 0:{
				for(int i = 0;i<5;i++)
				{
					ox=tempox; oy=tempoy;
					ox-=wk2[2*i]; oy-=wk2[2*i+1];
					if(checklegal(cp)==true){
						placePiece(cp);
						return Result.SUCCESS;
					}
				}
			}
			break;
			}
	    	result = Result.OUT_BOUNDS;
	    	return Result.OUT_BOUNDS;
    	}
    }
    
    
  //Wallkicks for counterclockwise rotations
    public Result wallkickcounterclockwise()
    {	
    	int tempox=ox;
    	int tempoy=oy;
    	int q = cp.getId();
    	
    	//check if piece is T or L
    	if(TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints("0 0  1 0  1 1  2 0"))||TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints("0 0  0 1  0 2  1 1"))||
    	   TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints("0 1  1 0  1 1  2 1"))||TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints("0 1  1 0  1 1  1 2"))||
    	   TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints("0 0  1 0  1 1  1 2"))||TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints("0 0  0 1  1 0  2 0"))||
    	   TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints("0 0  0 1  0 2  1 2"))||TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints("0 1  1 1  2 0  2 1"))||
    	   TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints("0 0  0 1  0 2  1 0"))||TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints("0 0  0 1  1 1  2 1"))||
    	   TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints("0 2  1 0  1 1  1 2"))||TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints("0 0  1 0  2 0  2 1")))
    	{
    		switch (q) {
			case 3:{
				for(int i = 0;i<5;i++)
				{
					ox=tempox; oy=tempoy;
					ox+=wk1[2*i]; oy+=wk1[2*i+1];
					if(checklegal(cp)==true){
						placePiece(cp);
						return Result.SUCCESS;
					}
				}
			}
			
			case 0:{
				for(int i = 0;i<5;i++)
				{
					ox=tempox; oy=tempoy;
					ox-=wk2[2*i]; oy-=wk2[2*i+1];
					if(checklegal(cp)==true){
						placePiece(cp);
						return Result.SUCCESS;
					}
				}
			}
			
			case 1:{
				for(int i = 0;i<5;i++)
				{
					ox=tempox; oy=tempoy;
					ox+=wk2[2*i]; oy+=wk2[2*i+1];
					if(checklegal(cp)==true){
						placePiece(cp);
						return Result.SUCCESS;
					}
				}
			}
			
			case 2:{
				for(int i = 0;i<5;i++)
				{
					ox=tempox; oy=tempoy;
					ox-=wk1[2*i]; oy-=wk1[2*i+1];
					if(checklegal(cp)==true){
						placePiece(cp);
						return Result.SUCCESS;
					}
				}
			}
			break;
			}
	    	result = Result.OUT_BOUNDS;
	    	return Result.OUT_BOUNDS;
    	}
    	
    	//Check is piece is the stick
    	else if(TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints("0 0  0 1  0 2  0 3"))||TetrisPiece.pointEqual(cp.getBody(), Piece.parsePoints("0 0  1 0  2 0  3 0")))
    	{
    		switch (q) {
			case 1:{
				for(int i = 0;i<5;i++)
				{
					ox=tempox; oy=tempoy;
					ox-=wk4[2*i]; oy-=wk4[2*i+1];
					if(checklegal(cp)==true){
						placePiece(cp);
						return Result.SUCCESS;
					}
				}
			}
			
			case 2:{
				for(int i = 0;i<5;i++)
				{
					ox=tempox; oy=tempoy;
					ox+=wk3[2*i]; oy+=wk3[2*i+1];
					if(checklegal(cp)==true){
						placePiece(cp);
						return Result.SUCCESS;
					}
				}
			}
			
			case 3:{
				for(int i = 0;i<5;i++)
				{
					ox=tempox; oy=tempoy;
					ox+=wk4[2*i]; oy+=wk4[2*i+1];
					if(checklegal(cp)==true){
						placePiece(cp);
						return Result.SUCCESS;
					}
				}
			}
			
			case 0:{
				for(int i = 0;i<5;i++)
				{
					ox=tempox; oy=tempoy;
					ox-=wk3[2*i]; oy-=wk3[2*i+1];
					if(checklegal(cp)==true){
						placePiece(cp);
						return Result.SUCCESS;
					}
				}
			}
			break;
			}
    		result=Result.OUT_BOUNDS;
    		return result;
    		
    	}
    	else{
	    	switch (q) {
			case 1:{
				for(int i = 0;i<5;i++)
				{
					ox=tempox; oy=tempoy;
					ox+=wk1[2*i]; oy+=wk1[2*i+1];
					if(checklegal(cp)==true){
						placePiece(cp);
						return Result.SUCCESS;
					}
				}
			}
			
			case 2:{
				for(int i = 0;i<5;i++)
				{
					ox=tempox; oy=tempoy;
					ox-=wk2[2*i]; oy-=wk2[2*i+1];
					if(checklegal(cp)==true){
						placePiece(cp);
						return Result.SUCCESS;
					}
				}
			}
			
			case 3:{
				for(int i = 0;i<5;i++)
				{
					ox=tempox; oy=tempoy;
					ox+=wk2[2*i]; oy+=wk2[2*i+1];
					if(checklegal(cp)==true){
						placePiece(cp);
						return Result.SUCCESS;
					}
				}
			}
			
			case 0:{
				for(int i = 0;i<5;i++)
				{
					ox=tempox; oy=tempoy;
					ox-=wk1[2*i]; oy-=wk1[2*i+1];
					if(checklegal(cp)==true){
						placePiece(cp);
						return Result.SUCCESS;
					}
				}
			}
			break;
			}
	    	result = Result.OUT_BOUNDS;
	    	return Result.OUT_BOUNDS;
    	}
    }
    
     
    
    public void setBoard(boolean [][] newBoard, int newox, int newoy, int newrowscleared, Board.Action newlastaction, Board.Result newlastresult )
    {
    	for(int i=0;i<getHeight();i++)
    	{
    		for(int j=0;j<getWidth();j++)
    		{
    			board[i][j]=newBoard[i][j];
    		}
    	}
    	ox = newox;
    	oy = newoy;
    	rowscleared = newrowscleared;
    	action = newlastaction;
    	result = newlastresult;
    }
    
    public void setPiece(Piece newPiece)
    {
    	cp=newPiece;
    }
}