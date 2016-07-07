import java.util.ArrayList;
public class Computer {
	/*Strategy: If there are no hits, scan the board for the points
	 * with the most unhit spaces to its left, right, top and bottom.
	 * The one with the highest amount of those spaces will be attacked.
	 * If there are multiple potential spots with the higest amount of open
	 * space around it, one space is randomly chosen.
	 * 
	 * If there is a hit, check around the hit to see if it can hit
	 * around that point. For multiple directions, it will randomize
	 * whichever direction is best.
	 * If there is a chain of hits it will try to follow the chain,
	 * or if it is actually ships next to each other in the opposite
	 * orientation of the chain, it will rotate to the other direction.
	 */
	//implements paragraph one of the strategy given above.
	private Point noHits(Grid grid){
		ArrayList<Point> possibleAttacks = new ArrayList<Point>();
		int temp;
		int maxOpenSpaces = 0;
		for(int i = 0;i < 9;i++){
			for(int j = 0;j < 9;j++){
				if(grid.getFired(i, j)==0){
					temp = numOpenSpaces(i,j,-1,0,grid);//adds spaces to left
					temp += numOpenSpaces(i,j,1,0,grid);//adds spaces to right
					temp += numOpenSpaces(i,j,0,-1,grid);//adds spaces to bottom
					temp += numOpenSpaces(i,j,0,1,grid);//adds spaces to top
					if(temp > maxOpenSpaces){
						possibleAttacks.clear();
						possibleAttacks.add(new Point(i,j));
						maxOpenSpaces = temp;
					}
					else if(temp == maxOpenSpaces){
						possibleAttacks.add(new Point(i,j));
					}
				}
			}
		}
		if(possibleAttacks.size()==1)//prevent an uneccesary call to Math.random
			return possibleAttacks.get(0);
		else{
			int pos = (int)(Math.random()*possibleAttacks.size());
			return possibleAttacks.get(pos);
		}
	}
	//this method scans all the spaces in a direction simply
	//by taking in an x and y value to add to the coords of
	//point p. Ex:to check left addX = -1, addY = 0
	private int numOpenSpaces(int startx, int starty, int addX, int addY, Grid grid){
		int x = startx;
		int y = starty;
		int numOpenSpaces = 0;
		boolean chainOver = false;
		while(!chainOver){
			if(grid.isValid(new Point(x+addX,y+addY))//checks if point is out of bounds
					&&grid.getFired(x+addX, y+addY)==0){//checks if point has already been attacked
				x+=addX;
				y+=addY;
				numOpenSpaces++;
			}
			else{
				chainOver = true;
			}
		}
		return numOpenSpaces;
	}
	
	//Checks for any hits and if there are hits,
	//chooses method that handles that.
	//Else, chooses method that handles no hits.
	public Point fireAt(Grid grid){
		ArrayList<Point> hits = scanForHits(grid);
		if(hits.isEmpty())
			return noHits(grid);
		else
			return hits(grid, hits);
	}
	//Gets all the hits on the grid.
	//If there are none, returns an empty ArrayList.
	//ArrayList hits will always contain points in
	//increasing horizontal order for each row, and
	private ArrayList<Point> scanForHits(Grid grid){
		ArrayList<Point> hits = new ArrayList<Point>();
		for(int i = 0;i < grid.getRows();i++){
			for(int j = 0;j<grid.getColumns();j++){
				if(grid.getFired(i, j)==1)
					hits.add(new Point(i,j));
			}
		}
		return hits;
	}
	//Assumes that there is either one hit, or there
	//is a chain of hits on a single ship.
	//If there are two ships adjacent to each other,
	//and the Computer has already hit both of them
	//in a single direction, (ex: / * * / where the
	//ships are vertically aligned) it will return
	//a point in that vertical direction.
	//This method completes the second half of the
	//strategy described in the beginning (starting at line 14).
	
	private Point hits(Grid grid, ArrayList<Point> hits){
		if(hits.size()>1){//chain of points on one ship
			Point p1 = hits.get(0);//To get the direction of the chain
			Point p2 = hits.get(hits.size()-1);//it will check the difference in the x,y of 2 points
			if(p2.getX()-p1.getX()>0){//horizontally
				if(checkAdjacentShips(grid,hits,true))
					return twoShipsAdjacent(grid,hits,true);
				else if(checkLeftSideChain(grid,hits,false)){//if the left side of the chain can't be hit, hit the right
					return new Point(p2.getX()+1,p2.getY());
				}
				else if(checkRightSideChain(grid,hits,false)){//same for the right side
					return new Point(p1.getX()-1,p1.getY());
				}
				else{//just randomly choose left or right
					if(Math.random()<0.5)
						return new Point(p1.getX()-1,p1.getY());
					else
						return new Point(p2.getX()+1,p2.getY());
						
				}
			}
			else{//vertically
				if(checkAdjacentShips(grid,hits,false))
					return twoShipsAdjacent(grid,hits,false);
				else if(checkTopSideChain(grid,hits,false))//if the top side of the chain can't be hit, hit the bottom
					return new Point(p1.getX(),p1.getY()-1);
				else if(checkBottomSideChain(grid,hits,false))//same for the bottom side
					return new Point(p2.getX(),p2.getY()+1);
				else{//just randomly choose left or right
					if(Math.random()<0.5)
						return new Point(p1.getX(),p1.getY()-1);
					else
						return new Point(p2.getX(),p2.getY()+1);
						
				}
			}
		}
		else{//single hit
			Point p = hits.get(0);//This creates 2 points for the list,so it can be passed into the check chain methods
			hits.add(p);//which will be checking around this one point to make things more simple.
			ArrayList<Point> temp = new ArrayList<Point>();
			if(!checkLeftSideChain(grid,hits,false))
				temp.add(new Point(p.getX()-1,p.getY()));
			else if(!checkRightSideChain(grid,hits,false))
				temp.add(new Point(p.getX()+1,p.getY()));
			else if(!checkTopSideChain(grid,hits,false))
				temp.add(new Point(p.getX(),p.getY()+1));
			else if(!checkBottomSideChain(grid,hits,false))
				temp.add(new Point(p.getX(),p.getY()-1));
			//now temp is an arraylist containing all the possible points to attack around this hit
			return temp.get((int)(Math.random()*temp.size()));
		}
	}
	//returns true if left side of hit chain has been hit and is a miss,
	//or is outside of the grid, or contains a dead ship
	private boolean checkLeftSideChain(Grid grid, ArrayList<Point> hits,boolean adj){
		int checknum = 1;
		if(adj)
			checknum = 2;
		Point p = hits.get(0);
		if(p.getX()-1<0)
			return true;
		else if(grid.getFired(p.getX()-1, p.getY())>=checknum)
			return true;
		else
			return false;
	}
	//returns true if right side of hit chain has been hit and is a miss,
	//or is outside of the grid, or contains a dead ship
	private boolean checkRightSideChain(Grid grid, ArrayList<Point> hits, boolean adj){
		int checknum = 1;
		if(adj)
			checknum = 2;
		Point p = hits.get(hits.size()-1);
		if(p.getX()+1>9)
			return true;
		else if(grid.getFired(p.getX()+1, p.getY())>=checknum)
			return true;
		else
			return false;
	}
	//returns true if bottom side of hit chain has been hit and is a miss,
	//or is outside of the grid, or contains a dead ship
	private boolean checkBottomSideChain(Grid grid, ArrayList<Point> hits, boolean adj){
		int checknum = 1;
		if(adj)
			checknum = 2;
		Point p = hits.get(0);
		if(p.getY()-1<0)
			return true;
		else if(grid.getFired(p.getX(), p.getY()-1)>=checknum)
			return true;
		else{
			return false;
		}
	}
	//returns true if top side of hit chain has been hit and is a miss,
	//or is outside of the grid, or contains a dead ship
	private boolean checkTopSideChain(Grid grid, ArrayList<Point> hits, boolean adj){
		int checknum = 1;
		if(adj)
			checknum = 2;
		Point p = hits.get(hits.size()-1);
		if(p.getY()+1>9)
			return true;
		else if(grid.getFired(p.getX(), p.getY()+1)>=checknum)
			return true;
		else{
			return false;
		}
	}
	//returns true if there are two adjacent ships
	private boolean checkAdjacentShips(Grid grid, ArrayList<Point> hits, boolean horizontal){
		if(horizontal){
			boolean x = checkLeftSideChain(grid,hits,true);
			boolean y =	checkRightSideChain(grid,hits, true);
			return x&&y;
		}
		else{
			boolean x = checkBottomSideChain(grid,hits,true);
			boolean y =	checkTopSideChain(grid,hits, true);
			return x&&y;
		}
	}
	//Simply returns a point to hit based on the first point in the hits ArrayList.
	//Basically attacking whatever ship is closer to the bottom-left corner
	private Point twoShipsAdjacent(Grid grid, ArrayList<Point> hits, boolean horizontal){
		Point p = hits.get(0);
		ArrayList<Point> pointList = new ArrayList<Point>();
		pointList.add(p);
		pointList.add(p);
		ArrayList<Point> temp = new ArrayList<Point>();
		if(horizontal){//the ships are going vertically then
			if(!checkTopSideChain(grid,pointList,false))
				temp.add(new Point(p.getX(),p.getY()+1));
			else if(!checkBottomSideChain(grid,pointList,false))
				temp.add(new Point(p.getX(),p.getY()-1));
			//now temp is an arraylist containing all the possible points to attack around this hit
			return temp.get((int)(Math.random()*temp.size()));
		}
		else{//ships are going horizontally then
			if(!checkLeftSideChain(grid,pointList,false))
				temp.add(new Point(p.getX()-1,p.getY()));
			else if(!checkRightSideChain(grid,pointList,false))
				temp.add(new Point(p.getX()+1,p.getY()));
			//now temp is an arraylist containing all the possible points to attack around this hit
			return temp.get((int)(Math.random()*temp.size()));
		}
	}
	
}
