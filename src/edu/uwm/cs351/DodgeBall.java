package edu.uwm.cs351;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * The Class DodgeBall.
 */
public class DodgeBall implements Runnable{
	
	//Constants
	static final int FPS = 60;
	static final Dimension PANEL_OFFSET = new Dimension(5,51);
	static final int CLOCK_HEIGHT = 22;
	static final int COUNTDOWN = 5, LEVEL_DURATION = 12;
	
	////////////////////////////////////////////////////
	//         TWEAK THESE TO TEST SCENARIOS          //
	////////////////////////////////////////////////////
	static final Dimension BOUNDS = new Dimension(600,600);
	static final int PLAYER_RADIUS = 15;
	static final int INITIAL_BALL_COUNT = 5;
	static final double MIN_SPEED = 4.5, MAX_SPEED = 6.5;
	static final boolean MORTAL = true;
	/////////////////////////////////////////////////////
	
	//Fields
	private JFrame frame;
	private DodgeBallPanel panel;
	private JLabel displayClock;
	private Player player;
	private BallCollection balls;
	private Ball deathBall;
	private int level;
	private int ballCount;
	private boolean suspended, alive;
	private Instant startTime, stopTime;
	
	/**
	 * Instantiates a new DodgeBall game.
	 */
	public DodgeBall(){
		SwingUtilities.invokeLater( ()->createGUI() );
		newGame();
	}
	
	//Create and set all GUI components
	private void createGUI(){
		//create frame, panel, and clock
		displayClock = new JLabel("", SwingConstants.CENTER);
		displayClock.setFont(new Font(displayClock.getFont().getName(), Font.BOLD, 16));
		frame = new JFrame("DodgeBall v0.9");
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel = new DodgeBallPanel();
		frame.add(panel);
		frame.add(displayClock, BorderLayout.SOUTH);
		
		frame.setSize(new Dimension(BOUNDS.width+PANEL_OFFSET.width, BOUNDS.height+PANEL_OFFSET.height+CLOCK_HEIGHT));
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		
		//create menu bar
		JMenuBar menuBar = new JMenuBar();
		JMenuItem newGameButton = new JMenuItem("New Game");
		newGameButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				newGame();
			}
		});
		menuBar.add(newGameButton);
		frame.setJMenuBar(menuBar);
		frame.setVisible(true);
	}
	
	//Reset all game elements
	private void newGame(){
		player = new Player(new Point(0,0));
		player.setImg(happyImg);
		level = 0;
		ballCount = INITIAL_BALL_COUNT;
		alive=true;
		nextLevel();
	}
	
	//Suspend game, create next level
	private void nextLevel(){
		suspended = true;
		level++;
		balls = new BallCollection();
		for (int i=0; i<ballCount; i++)
			createBall(i);
		startTime = Instant.now().plus(Duration.ofSeconds(COUNTDOWN));
		stopTime = startTime.plus(Duration.ofSeconds(LEVEL_DURATION));
	}
	
	
	private static Point randomLoc(){
		int locX = (int) ((DodgeBall.BOUNDS.getWidth() - 2*Ball.DEFAULT_RADIUS)* Math.random())+Ball.DEFAULT_RADIUS;
		int locY = (int) ((DodgeBall.BOUNDS.getHeight() -2*Ball.DEFAULT_RADIUS) * Math.random())+Ball.DEFAULT_RADIUS;
		return new Point(locX,locY);}
	
	private static Vector randomVec(){
		double theta = (Math.random() * Math.PI / 2) + Math.PI;
		double magnitude = DodgeBall.MIN_SPEED + Math.random() * (DodgeBall.MAX_SPEED - DodgeBall.MIN_SPEED);
		return new Vector(theta).scale(magnitude);}
	
	private static Color randomColor(){
		return new Color((int)(Math.random() * 200),
						 (int)(Math.random() * 200),
						 (int)(Math.random() * 200));}

	private static Ball randomBall() {
		return new Ball(randomLoc(),randomVec(),randomColor());}
	
	//Create a ball, using helper method to make sure no balls overlap
	private void createBall(int index){
		Ball newBall;
		while (!validLocation(newBall = randomBall())) {}
		balls.add(newBall);
	}
	
	private boolean validLocation(Ball newBall){
		
		for (Iterator<Ball> it = balls.iterator(); it.hasNext();) {
			Ball b = it.next();
			if (b == null) continue;
			if (b.isColliding(newBall)) return false;
			}
		return true;
	}
	
	/*
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run(){
		Timer timer = new Timer(1000/FPS, (ae)->update());
		SwingUtilities.invokeLater(()->timer.start());
	}
	
	//Update all game elements
	private void update(){
		if (!alive) return;
		//update time
		updateTime();
		//update player
		player.update(getAdjustedMouseLoc());
		//update balls
		for (Iterator<Ball> it = balls.iterator(); it.hasNext();)
		{
			Ball b = it.next();
			b.step();
		}
		if (!suspended){
			checkCollisions();
			checkGameOver();}
	}
	
	//Update times, queue start of rounds and transitions between rounds
	private void updateTime(){
		if (Instant.now().isBefore(startTime))
			displayClock.setText(""+(Duration.between(Instant.now(), startTime).getSeconds()+1));
		else if (Instant.now().isBefore(stopTime)){
			if (suspended){
				suspended = false;

				for (Iterator<Ball> it = balls.iterator(); it.hasNext();)
				{
					Ball b = it.next();
					b.launch();
				}
			}
			displayClock.setText(""+(Duration.between(Instant.now(), stopTime).getSeconds()+1));}
		else{
			makeHarder();
			nextLevel();}
	}
	
	//Make game harder as rounds progress
	//Feel free to add your own!
	private void makeHarder(){
		switch ((int) (Math.random() * 2)){
			case 0: {ballCount++;System.out.println("Added ball.");break;}
			default: {player.grow();System.out.println("Increased player size.");break;}
		}
	}

	//Get mouse location relative to upper left corner of panel
	//We chose to use this instead of a MouseMotionListener to enable
	//player movement whether or not the cursor is within the panel.
	private Point getAdjustedMouseLoc(){
		java.awt.Point location = MouseInfo.getPointerInfo().getLocation();
		return new Point((int) location.getX()-frame.getX()-PANEL_OFFSET.width,
						 (int) location.getY()-frame.getY()-PANEL_OFFSET.height);}
	
	//Check ball-on-ball and ball-on-wall collisions
	private void checkCollisions(){
		
		for (Iterator<Ball> it0 = balls.iterator(); it0.hasNext();)
		{
			Ball a = it0.next();
			for (Iterator<Ball> it1 = balls.iterator(); it1.hasNext();)
			{
				Ball b = it1.next();
				if (a != b && a.isColliding(b)) 
					a.bounce(b);
			}
			a.bounceWalls(DodgeBall.BOUNDS);
		}
	}
	
	//Check if the player was hit by any balls
	private void checkGameOver(){
		
		for (Iterator<Ball> it = balls.iterator(); it.hasNext();)
		{
			Ball b = it.next();
			if(player.isColliding(b))
			{
				deathBall = b;
				gameover();
			}
		}
	}
	
	private void gameover(){
		alive=false;
		suspended=true;
		player.setImg(sadImg);
		panel.repaint();
	}
	
	@SuppressWarnings("serial")
	private class DodgeBallPanel extends JPanel{
		public DodgeBallPanel(){
			setDoubleBuffered(true);
			addMouseListener(new MouseListener(){
				@Override
				public void mouseClicked(MouseEvent arg0) {}
				@Override
				public void mouseEntered(MouseEvent arg0) {}
				@Override
				public void mouseExited(MouseEvent arg0) {}
				@Override
				public void mousePressed(MouseEvent arg0) {if (alive)player.setImg(winkImg);}
				@Override
				public void mouseReleased(MouseEvent arg0) {if (alive)player.setImg(happyImg);}});
		}
		
		/*
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 */
		public void paintComponent(Graphics g) 
		{
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			//clean panel
			super.repaint();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, BOUNDS.width, BOUNDS.height);
			
			//draw game entities
			
			for (Iterator<Ball> it = balls.iterator(); it.hasNext();)
			{
				Ball b = it.next();
				b.draw(g);
			}
			player.draw(g);
			
			//draw round info/game over if animation suspended
			if (suspended){
				//draw translucent mask
				g.setColor(new Color(0,0,0,100));
				g.fillRect(0, 0, BOUNDS.width+PANEL_OFFSET.width, BOUNDS.height+PANEL_OFFSET.height);
				g.setFont(new Font("Verdana", Font.BOLD, 24));
				if (alive){
					//draw round info
					g.setColor(Color.WHITE);
					g.drawString("Round "+level + "..", BOUNDS.width/2 - 62, BOUNDS.height/2);}
				else{
					//draw game over
					g.setColor(new Color(255,255,255,50));
					g.fillOval(deathBall.getLoc().intX()-50, deathBall.getLoc().intY()-50, 100, 100);
					deathBall.draw(g);
					player.draw(g);
					g.setColor(Color.WHITE);
					g.drawString("Game Over!", BOUNDS.width/2 - 70, BOUNDS.height/2);
					g.drawString("You made it", BOUNDS.width/2 - 72, BOUNDS.height/2+50);
					g.drawString(level+" rounds.", BOUNDS.width/2 - 52, BOUNDS.height/2+80);}
			}
			
		}
	}
	
	//Images
	private static BufferedImage happyImg, winkImg, sadImg;
	static{
		try {happyImg = ImageIO.read(new File("images/happy_face.png"));
			 winkImg = ImageIO.read(new File("images/wink_face.png"));
			 sadImg = ImageIO.read(new File("images/sad_face.png"));}
		catch (Exception e){System.out.println("Error loading player image.");}
	}
	
	/**
	 * The main method. Creates a new DodgeBall game and runs it.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args){
		new DodgeBall().run();}
}
