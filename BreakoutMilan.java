

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutMilan extends GraphicsProgram {

/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 600;
	public static final int APPLICATION_HEIGHT = 700;

/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10; // must be 10 (2 is for testing)

/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10; // must be 10 (2 is for testing)

/** Separation between bricks */
	private static final int BRICK_SEP = 4;

/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

/** Number of turns */
	private static final int NTURNS = 3;
	
/** Animation delay or paust time between ball moves */	
	private static final int DELAY = 10;

	
	
	
/* Method: run() */
/** Runs the Breakout program. */
	public void run() {
		welcomeMessage();
		setup();
		
		for(int i=0; i < NTURNS; i++) {
			
			play ();
			if(brickCounter == 0) {
				removeAll();
				Victory();

		        }

		}

		gameOver();

	}
			

	

	
	
	private void setup() {
		//setup bricks
		double x0 = (WIDTH - ((NBRICKS_PER_ROW * BRICK_WIDTH )+(BRICK_SEP * (NBRICKS_PER_ROW - 1))))/2;
		double y0 = BRICK_Y_OFFSET;
		
		for (int row = 0; row < NBRICKS_PER_ROW; row++) {
			for (int col = 0; col < NBRICK_ROWS; col++) {
				double yb = y0 + (BRICK_HEIGHT + BRICK_SEP)* row;
				double xb = x0 + (BRICK_WIDTH + BRICK_SEP) *col;
				bricks = new GRect (xb,yb,BRICK_WIDTH,BRICK_HEIGHT);
				bricks.setFilled(true);
				add(bricks);
				/* now we have to fill bricks with five colors, every two rows with different color */
				if (row < 2) {
					bricks.setColor(Color.RED);
				}
				if (row == 2 || row == 3) {
					bricks.setColor(Color.ORANGE);
				}
				if (row == 4 || row == 5) {
					bricks.setColor(Color.YELLOW);
				}
				if (row == 6 || row == 7) {
					bricks.setColor(Color.GREEN);
				}
				if (row == 8 || row == 9) {
					bricks.setColor(Color.CYAN);
				} 	
				
			}
		}
		
        // now, setup the paddle
		double xp = WIDTH/2 - PADDLE_WIDTH / 2;
		double yp = HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		paddle = new GRect (xp, yp,PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add (paddle);
		
		//we must call this method to be able to get mouse events
		addMouseListeners();

	}
	
	  //Called on mouse drag to reposition the PADDLE
	  //with this code we do not let the paddle move off the edge of the window 
	  public void mouseDragged(MouseEvent e) {
			double x = e.getX();
			double minX = PADDLE_WIDTH / 2;
			double maxX = WIDTH - PADDLE_WIDTH / 2;
			if (x < minX) {
				x = minX;
			} else if (x > maxX) {
				x = maxX;
			}
			paddle.setLocation(x - minX, HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		}
	
	  
	  
	  
	  private void play() {
		  welcomeMessage();
		  //now, setup the ball in the center and move ball
		  double cx = WIDTH/2 - BALL_RADIUS;
		  double cy = HEIGHT/2 - BALL_RADIUS;
		  ball = new GOval (cx,cy , 2*BALL_RADIUS, 2*BALL_RADIUS );
		  ball.setFilled(true);
		  add (ball);
		  
		  // starting velocity vx and vy
		  vy = 4.0;
		  vx = rgen.nextDouble(1.0, 3.0); //This code sets vx to be a random double in the range 1.0 to 3.0 and then makes it negative half the time.
		  if (rgen.nextBoolean(0.5)) vx = -vx;
		  
          //bouncing ball
			while (true) {
				bouncingBall();
				if (ball.getY() >= getHeight()) {
					break;
				}
				if(brickCounter == 0) {
					break;
				}
			}
			  
	  }	  
 
			  
      private void bouncingBall() {		
			 ball.move (vx,vy);
			 if (ball.getX() > WIDTH-2*BALL_RADIUS || ball.getX() < 0 ) {
				  vx = -vx;
		  } 
			 else if (ball.getY() < 0) {
				  vy = -vy;
		  }

		  // Checking for collisions
			 GObject collider = getCollidingObject();
			 if (collider == paddle) {
			  
				// We need to make sure that the ball bounces off the top part of the paddle  
				if(ball.getY() >= getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - BALL_RADIUS*2) {
					vy = -vy;
					bounceClip.play();
				}
			}

			 else if (collider != null) {
				remove(collider); 
				vy = -vy;
				brickCounter--;
				bounceClip.play();
			}

			 pause(DELAY);
		  }
	  
	  
        // returns the object involved in the collision, if any, and null otherwise
		private GObject getCollidingObject() {
			
			if((getElementAt(ball.getX(), ball.getY())) != null) {
		         return getElementAt(ball.getX(), ball.getY());
		      }
			else if (getElementAt( (ball.getX() + BALL_RADIUS*2), ball.getY()) != null ){
		         return getElementAt(ball.getX() + BALL_RADIUS*2, ball.getY());
		      }
			else if(getElementAt(ball.getX(), (ball.getY() + BALL_RADIUS*2)) != null ){
		         return getElementAt(ball.getX(), ball.getY() + BALL_RADIUS*2);
		      }
			else if(getElementAt((ball.getX() + BALL_RADIUS*2), (ball.getY() + BALL_RADIUS*2)) != null ){
		         return getElementAt(ball.getX() + BALL_RADIUS*2, ball.getY() + BALL_RADIUS*2);
		      }
			//need to return null if there are no objects present
			else{
		         return null;
		      }
		}
		  
		  
		private void Victory() {
			GLabel Winner = new GLabel ("Winner!!", getWidth()/2, getHeight()/2 );
			Winner.setFont("SansSerif-30");
			Winner.move(-Winner.getWidth()/2, -2*Winner.getHeight());
			Winner.setColor(Color.RED);
			add (Winner);
		}  
		
		private void gameOver() {
			GLabel end = new GLabel ("GAME OVER!! Your score is: " + (100 - brickCounter) , getWidth()/2, getHeight()/2);
			end.setFont("SansSerif-30");
			end.move(-end.getWidth()/2, -end.getHeight());
			end.setColor(Color.RED);
			add (end);
		}
		
		private void welcomeMessage() {
			GLabel Welcome = new GLabel ("Press mouse to start!!", getWidth()/2, getHeight()/2);
			Welcome.setFont("SansSerif-30");
			Welcome.move(-Welcome.getWidth()/2, -Welcome.getHeight());
			Welcome.setColor(Color.RED);
			add (Welcome);
			waitForClick();
			remove(Welcome);
			
		}
		  
	/* private instance variables */
	private GRect bricks;
	private GRect paddle;
	private GOval ball;
	private GPoint last;
	private GObject gobj;
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	
	private int brickCounter = 100; // 100 total number of bricks
	
}
