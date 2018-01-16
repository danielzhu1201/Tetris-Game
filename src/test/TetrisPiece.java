package test;

import java.awt.*;


/**
 * An immutable representation of a tetris piece in a particular rotation.
 * Each piece is defined by the blocks that make up its body.
 *
 * You need to implement this.
 */
public final class TetrisPiece extends Piece {

    /**
     * Parse a "piece string" of the form "x1 y1 x2 y2 ..." into a TetrisPiece
     * where the corresponding (x1, y1), (x2, y2) positions have been filled in.
     */
	private Point[] points;
	private int w,h,id;
	//id is useful for wallkicks.
	private int[] sk; int[] lsk; int[] rsk;
	
	public TetrisPiece(Point[] array)
	{
		points = array;
		// add more variables for width, height, skirt
		int temp=0, temp2 = 0;
		for(int i =0;i<points.length;i++)
		{
			temp = Math.max(temp,(int)points[i].getX());
			temp2 = Math.max(temp2,(int)points[i].getY());
		}
		w = temp + 1;
		h = temp2 + 1;
		//four rotations of piece
		sk = new int[w];
		lsk = new int[h];
		rsk = new int[h];
		for(int i = 0; i<w; i++)
		{
			int temp3 = 100000;
			for(int j =0;j<points.length;j++)
			{
				if(points[j].getX()==i)
				{
					temp3 = Math.min(temp3,(int) points[j].getY());
				}
			}
			sk[i]=temp3;
		}
		
		for(int i = 0; i<h; i++)
		{
			int temp3 = 100000;
			for(int j =0;j<points.length;j++)
			{
				if(points[j].getY()==i)
				{
					temp3 = Math.min(temp3,(int) points[j].getX());
				}
			}
			lsk[i]=temp3;
		}
		
		for(int i = 0; i<h; i++)
		{
			int temp3 = -100000;
			for(int j =0;j<points.length;j++)
			{
				if(points[j].getY()==i)
				{
					temp3 = Math.max(temp3,(int) points[j].getX());
				}
			}
			rsk[i]=temp3;
		}

	}
	
	
	public static Piece getPiece(String pieceString)
	{ 
		Point[] pieceArray = parsePoints(pieceString);
		Piece piece = new TetrisPiece(pieceArray);
		piece.setId(0);
		piece.next = new TetrisPiece(rotate(piece));
		piece.next.setId(1);
		piece.next.next = new TetrisPiece(rotate(piece.next));
		piece.next.next.setId(2);
		piece.next.next.next = new TetrisPiece(rotate(piece.next.next));
		piece.next.next.next.setId(3);
		piece.next.next.next.next = piece;
		return piece;
	}

	public static Piece createNextPiece(String pieceString)
	{
		Point[] pieceArray = parsePoints(pieceString);
		Piece piece = new TetrisPiece(pieceArray);
		return piece;
	}
    
	@Override
    public int getWidth() { return w; }

    @Override
    public int getHeight() { return h; }

    @Override
    public Point[] getBody() { return points; }

    @Override
    public int[] getSkirt() { return sk; }
    
   @Override
    public int[] getLeftSkirt() { return lsk; }
    
    @Override
    public int[] getRightSkirt() { return rsk;}
    

    @Override
    public boolean equals(Object other) 
    {
    	if(other==null){return false;}
    	Piece temp = (Piece)other;
    	Point array2[] = temp.getBody();
    	
    	if(this==null&&temp==null) {return true;}
    	
    	if(points.length!=array2.length)
    		return false;
//    	return points.equals(other);
    	else
    	{
    		return pointEqual(points, array2);
    	}
    }
    
  
    public static boolean pointEqual(Point[] p1, Point[] p2)
    {
    	if(p1.length!=p2.length) return false;
    	
    	int trueCounter =0;
    	for(Point x:p1)
    	{
    		for(Point y:p2)
    		{
    			if(x.getX()==y.getX()&&x.getY()==y.getY())
    				trueCounter++;
    				
    		}
    	}
    	
    	if(trueCounter == p1.length)
    		return true;
    	else 
    		return false;
    }
    
    //Rotates the piece
    public static Point[] rotate(Piece piece)
    
    {
		//square
		if(pointEqual(piece.getBody(), parsePoints(("0 0  0 1  1 0  1 1")))) return parsePoints(("0 0  0 1  1 0  1 1"));
		//stick
		if(pointEqual(piece.getBody(), parsePoints(("0 0  0 1  0 2  0 3")))) return parsePoints(("0 0  1 0  2 0  3 0"));
		if(pointEqual(piece.getBody(), parsePoints(("0 0  1 0  2 0  3 0")))) return parsePoints(("0 0  0 1  0 2  0 3"));
		//The T
		if(pointEqual(piece.getBody(), parsePoints(("0 0  1 0  1 1  2 0")))) return parsePoints(("0 0  0 1  0 2  1 1"));
		if(pointEqual(piece.getBody(), parsePoints(("0 0  0 1  0 2  1 1")))) return parsePoints(("0 1  1 0  1 1  2 1"));
		if(pointEqual(piece.getBody(), parsePoints(("0 1  1 0  1 1  2 1")))) return parsePoints(("0 1  1 0  1 1  1 2"));
		if(pointEqual(piece.getBody(), parsePoints(("0 1  1 0  1 1  1 2")))) return parsePoints(("0 0  1 0  1 1  2 0"));
		//The L part 1
		if(pointEqual(piece.getBody(), parsePoints(("0 0  1 0  1 1  1 2")))) return parsePoints(("0 0  0 1  1 0  2 0"));
		if(pointEqual(piece.getBody(), parsePoints(("0 0  0 1  1 0  2 0")))) return parsePoints(("0 0  0 1  0 2  1 2"));
		if(pointEqual(piece.getBody(), parsePoints(("0 0  0 1  0 2  1 2")))) return parsePoints(("0 1  1 1  2 0  2 1"));
		if(pointEqual(piece.getBody(), parsePoints(("0 1  1 1  2 0  2 1")))) return parsePoints(("0 0  1 0  1 1  1 2"));
		//The L part 2
		if(pointEqual(piece.getBody(), parsePoints(("0 0  0 1  0 2  1 0")))) return parsePoints(("0 0  0 1  1 1  2 1"));
		if(pointEqual(piece.getBody(), parsePoints(("0 0  0 1  1 1  2 1")))) return parsePoints(("0 2  1 0  1 1  1 2"));
		if(pointEqual(piece.getBody(), parsePoints(("0 2  1 0  1 1  1 2")))) return parsePoints(("0 0  1 0  2 0  2 1"));
		if(pointEqual(piece.getBody(), parsePoints(("0 0  1 0  2 0  2 1")))) return parsePoints(("0 0  0 1  0 2  1 0"));
		//The Dog part 1
		if(pointEqual(piece.getBody(), parsePoints(("0 1  1 0  1 1  2 0")))) return parsePoints(("0 0  0 1  1 1  1 2"));
		if(pointEqual(piece.getBody(), parsePoints(("0 0  0 1  1 1  1 2")))) return parsePoints(("0 1  1 0  1 1  2 0"));
		//The Dog part 2
		if(pointEqual(piece.getBody(), parsePoints(("0 0  1 0  1 1  2 1")))) return parsePoints(("0 1  0 2  1 0  1 1"));
		if(pointEqual(piece.getBody(), parsePoints(("0 1  0 2  1 0  1 1")))) return parsePoints(("0 0  1 0  1 1  2 1"));

		return null;
    }
    
    public void setId(int qwerty)
    {
    	id=qwerty;
    }
    
    public int getId()
    {
    	return id;
    }
}