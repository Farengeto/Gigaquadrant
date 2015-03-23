//import java.applet.Applet;
//import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.*;
//import java.applet.*;
//import java.net.*;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Main extends JPanel{
	
	public Star[] stars     = new Star[200];
	public Color[] colors   =  {Color.WHITE, new Color(63,63,255),new Color(63,255,63),new Color(255,63,63),new Color(127,127,63),new Color(127,63,127),new Color(63,127,127),new Color(127,127,127),new Color(255,127,63)};
	public int[][] color    = {{255,255,255},{63,63,255},         {63,255,63},         {255,63,63},         {127,127,63},         {127,63,127},         {63,127,127},         {127,127,127},         {255,127,63}}; 
	public int[] capital    = new int[9];
	public int[] fleets     =  {0,  0,  0,  0,  0,  0,  0,  0,  0  };
	public int[] planets    =  {192,1,  1,  1,  1,  1,  1,  1,  1  };
	public int[] population =  {0,  0,  0,  0,  0,  0,  0,  0,  0  };
	public int[] score      =  {-1, 0,  0,  0,  0,  0,  0,  0,  0  };
	public int[] rankings   =  {0,1,2,3,4,5,6,7,8};
	public int[][] diplomacy  = {{1,1,1,1,1,1,1,1,1},{1,1,0,0,0,0,0,0,0},{1,0,1,0,0,0,0,0,0},{1,0,0,1,0,0,0,0,0},{1,0,0,0,1,0,0,0,0},{1,0,0,0,0,1,0,0,0},{1,0,0,0,0,0,1,0,0},{1,0,0,0,0,0,0,1,0},{1,0,0,0,0,0,0,0,1}};
	public int[][] news     = new int[10][3];
	public int[] target = new int[1000];
	public int[] inRange = new int[200];
	public int[] queue = new int[200];
	public int[] sent = new int[200];
	public int[] available = new int[200];
	public String[][] starNames = new String[200][8];
	public int factions = 8;
	public int r = 10;
	public int sr = 2;
	public Ship[] ships = new Ship[1000];
	private BufferedImage background;
	public int click = -1;
	public static boolean paused = false;
	public int zoom = 1;
	public int xOff = 0;
	public int yOff = 0;
	public double speed = 1;
	public boolean grid = false;
	public boolean player = true;
	public int faction = 1;
	private static int height = 768;
	private static int width = (int)(Math.ceil(height*16/9));
	public int count = 0;
	
	public Main(){
		paused = true;
		
		//set screen size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int)screenSize.getWidth();
		height = (int)screenSize.getHeight();
		
		//initialize star and ship arrays
		int starCount = 200;
		stars = new Star[starCount];
		ships = new Ship[starCount*5];
		target = new int[starCount*5];
		inRange = new int[starCount];
		queue = new int[starCount];
		sent = new int[starCount];
		available = new int[starCount];
		
		//initialize faction arrays
		factions = 8;
		colors = new Color[factions+1];
		color = new int[factions+1][3];
		capital = new int[factions+1];
		fleets = new int[factions+1];
		planets = new int[factions+1];
		population = new int[factions+1];
		score = new int[factions+1];
		rankings = new int[factions+1];
		diplomacy = new int[factions+1][factions+1];
		starNames = new String[starCount][factions+1];
		
		//create starfield
		for(int i = 0; i < stars.length; i++){
			int j = (int)(Math.random()*(996)+2);
			int k = (int)(Math.random()*(996)+2);
			stars[i] = new Star(j,k,0,0);
			inRange[i] = 0;
			queue[i] = 0;
			sent[i] = 0;
			available[i] = 0;
		}
		
		//initialize ships
		for(int i = 0; i < ships.length; i++){
			ships[i] = new Ship(this,stars[0],stars[0],0,0);
			ships[i].flying = false;
			target[i] = -1;
		}
		
		//initialize factions
		colors[0] = new Color(255,255,255);
		color[0][0] = 255;
		color[0][1] = 255;
		color[0][1] = 255;
		fleets[0] = 0;
		planets[0] = starCount-factions;
		population[0] = 0;
		score[0] = -1;
		rankings[0] = 0;
		for(int i = 0; i < factions+1; i++){
			diplomacy[0][i] = 1;
		}
		for(int i = 1; i <= factions; i++){
			int r = (int)(Math.random()*201)+25;
			int g = (int)(Math.random()*201)+25;
			int b = (int)(Math.random()*201)+25;
			colors[i] = new Color(r,g,b);
			color[i][0] = r;
			color[i][1] = g;
			color[i][2] = b;
			int j;
			//ensure planets is within range of at least one star at base range
			boolean iso = false;
			do{
				j = (int)(Math.random()*stars.length);
				iso = false;
				for(int k = 0; k < starCount; k++){
						iso = iso || (dist(stars[j],stars[k]) <= 100);
				}
			}while(stars[j].owner != 0 || !iso || stars[j].x > 1000-125 || stars[j].x < 125 || stars[j].y > 1000-125 || stars[j].y < 125);
			stars[j].owner = i;
			stars[j].colony = 50;
			capital[i] = j;
			fleets[i] = 0;
			planets[i] = 1;
			population[i] = 100;
			score[i] = 0;
			rankings[i] = i;
			diplomacy[i][0] = 1;
			for(int k = 1; k <= factions; k++){
				if(i != k)
					diplomacy[i][k] = 0;
				else
					diplomacy[i][k] = 1;
			}
		}
		
		//import resources
		try {                
			background = ImageIO.read(getClass().getResource("/Starfield Large.png"));
		} catch (IOException ex) {}
		
		//generate blank news list
		for(int i = 0; i < 10; i++){
			for(int j = 0; j < 3; j++){
				news[i][j] = 0;
			}
		}
		
		//generate planet name lists
		//source file for letter pairs
		//File file = new File("res/Space.txt");
		//nameGenerator generator = new nameGenerator(file);
		for(int i = 0; i < starCount; i++){
			//name for uninhabited planets
			starNames[i][0] = "RS-" + (stars[i].x*1000 + stars[i].y);
			//name for planet by each faction
			for(int j = 1; j <= factions; j++){
				String name = "RS-" + (stars[i].x*1000 + stars[i].y);
				/*boolean copy = false;
				String name = "";
				while((name.length() < 4 || name.length() > 20) && !copy){
					name = generator.generate();
					//avoids duplicate names within a faction
					copy = duplicate(i,j,name);
				}*/
				starNames[i][j] = name;
			}
		}
		
		//initialize view for player
		if(player){
			zoom = 4;
			xOff = stars[capital[faction]].x*zoom-500;
			yOff = stars[capital[faction]].y*zoom-500;
			click = capital[faction];
			edges();
		}
		
		addMouseListener(new MouseListener() {
		      @Override
		      public void mouseClicked(MouseEvent e) {
		        int x = e.getX();
		        int y = e.getY();
		        //click star t select it
		        if(x<=height && y <=height){
		        	x = (x*1000/height+xOff)/zoom;
			        y = (y*1000/height+yOff)/zoom;
		        	click(x,y);
		        }
		        //click on star icon to move view to it
		        else if(Math.pow(x-(height+110),2) + Math.pow(y-(height-150),2) < 2500){
		        	xOff = stars[click].x*zoom-500;
					yOff = stars[click].y*zoom-500;
					edges();
		        }
		        //click on minimap to move view to location
		        else if(x >= (height+width)/4+410 && x <= (height+width)/4+410+height-820 && y >= 600 && y<= 600+height-820){
		        	x = (x-((height+width)/4+410));
		        	y = (y-600);
		        	x = x*1000/(height-820);
		        	y = y*1000/(height-820);
		        	xOff = x*zoom-500;
		        	yOff = y*zoom-500;
		        	edges();
		        }
		        //exit button
		        else if(x >= width-25 && y <= 25){
		        	System.exit(0);
		        }
		        //add send command
		        else if(player && inRange[click] > 0 && diplomacy[faction][stars[click].owner] != 0 && x >= height+((width-height)/2-250)/2 && x <= height+((width-height)/2-250)/2+250 && y >= height-80 && y <= height-55){
		        	target();
		        }
		        //remove send command
		        else if(player && queue[click] > 0 && x >= height+((width-height)/2-250)/2 && x <= height+((width-height)/2-250)/2+250 && y >= height-40 && y <= height-15){
		        	remove();
		        }
		      }
		      
		      @Override
		      public void mouseReleased(MouseEvent e) {
		      }
		      
		      @Override
		      public void mousePressed(MouseEvent e) {
		      }
		      
		      @Override
		      public void mouseExited(MouseEvent e) {
		      }
		      
		      @Override
		      public void mouseEntered(MouseEvent e) {
		      }
		    });
		addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				//pause/unpause
				if (e.getKeyCode() == KeyEvent.VK_P){
					paused = !paused;
				}
				//zoom in
				else if (e.getKeyCode() == KeyEvent.VK_Z && zoom < 4){
					xOff = ((xOff-500*(zoom-1))*(zoom+1)/zoom)+500*(zoom-1);
					yOff = ((yOff-500*(zoom-1))*(zoom+1)/zoom)+500*(zoom-1);
					zoom++;
					xOff+=500;
					yOff+=500;
				}
				//zoom out
				else if (e.getKeyCode() == KeyEvent.VK_X && zoom-1 > 0){
					xOff = ((xOff-500*(zoom-1))*(zoom-1)/zoom)+500*(zoom-1);
					yOff = ((yOff-500*(zoom-1))*(zoom-1)/zoom)+500*(zoom-1);
					zoom--;
					xOff-=500;
					yOff-=500;
					edges();
				}
				//scroll up
				else if (e.getKeyCode() == KeyEvent.VK_UP && yOff-10 >= 0){
					yOff -= 10;
				}
				//scroll down
				else if (e.getKeyCode() == KeyEvent.VK_DOWN && (yOff+1000 <= 1000*zoom-10)){
					yOff += 10;
				}
				//scroll left
				else if (e.getKeyCode() == KeyEvent.VK_LEFT && xOff-10 >= 0){
					xOff -= 10;
				}
				//scroll right
				else if (e.getKeyCode() == KeyEvent.VK_RIGHT && (xOff+1000 <= 1000*zoom-10)){
					xOff += 10;
				}
				//increase game speed
				else if (e.getKeyCode() == KeyEvent.VK_COMMA && speed > 1){
					speed--;
				}
				//decrease game speed
				else if (e.getKeyCode() == KeyEvent.VK_PERIOD && speed < 5){
					speed++;
				}
				//show/hide grid overlay
				else if (e.getKeyCode() == KeyEvent.VK_G){
					grid = !grid;
				}
				//move view to faction homeworld
				else if (e.getKeyCode() == KeyEvent.VK_H){
					click = capital[faction];
					xOff = stars[click].x*zoom-500;
					yOff = stars[click].y*zoom-500;
					edges();
				}
				//move view to currently selected planet
				else if (e.getKeyCode() == KeyEvent.VK_V){
					xOff = stars[click].x*zoom-500;
					yOff = stars[click].y*zoom-500;
					edges();
				}
				//move to faction 1 capital
				else if (e.getKeyCode() == KeyEvent.VK_1){
					click = capital[1];
					xOff = stars[click].x*zoom-500;
					yOff = stars[click].y*zoom-500;
					edges();
				}
				//move to faction 2 capital
				else if (e.getKeyCode() == KeyEvent.VK_2){
					click = capital[2];
					xOff = stars[click].x*zoom-500;
					yOff = stars[click].y*zoom-500;
					edges();
				}
				//move to faction 3 capital
				else if (e.getKeyCode() == KeyEvent.VK_3){
					click = capital[3];
					xOff = stars[click].x*zoom-500;
					yOff = stars[click].y*zoom-500;
					edges();
				}
				//move to faction 4 capital
				else if (e.getKeyCode() == KeyEvent.VK_4){
					click = capital[4];
					xOff = stars[click].x*zoom-500;
					yOff = stars[click].y*zoom-500;
					edges();
				}
				//move to faction 5 capital
				else if (e.getKeyCode() == KeyEvent.VK_5){
					click = capital[5];
					xOff = stars[click].x*zoom-500;
					yOff = stars[click].y*zoom-500;
					edges();
				}
				//move to faction 6 capital
				else if (e.getKeyCode() == KeyEvent.VK_6){
					click = capital[6];
					xOff = stars[click].x*zoom-500;
					yOff = stars[click].y*zoom-500;
					edges();
				}
				//move to faction 7 capital
				else if (e.getKeyCode() == KeyEvent.VK_7){
					click = capital[7];
					xOff = stars[click].x*zoom-500;
					yOff = stars[click].y*zoom-500;
					edges();
				}
				//move to faction 8 capital
				else if (e.getKeyCode() == KeyEvent.VK_8){
					click = capital[8];
					xOff = stars[click].x*zoom-500;
					yOff = stars[click].y*zoom-500;
					edges();
				}
				//add send command
				else if((e.getKeyCode() == KeyEvent.VK_PLUS || e.getKeyCode() == KeyEvent.VK_EQUALS) && player && inRange[click] > 0 && diplomacy[faction][stars[click].owner] != 0){
		        	target();
		        }
				//remove send command
		        else if((e.getKeyCode() == KeyEvent.VK_MINUS || e.getKeyCode() == KeyEvent.VK_UNDERSCORE) && player && queue[click] > 0){
		        	remove();
		        }
			}
		});
		setFocusable(true);
	}
	
	public static void main(String [] args) throws InterruptedException {
		JFrame frame = new JFrame("Gigaquadrant");
		Main game = new Main();
		frame.add(game);
		frame.setSize(width, height);
		frame.setUndecorated(true);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.count();
		while(true){
			if(!paused){
				game.queue();
				game.shipGen();
				game.count();
				game.diplomacy();
				//game.assimilation();
			}
			game.repaint();
			Thread.sleep(10);
		}
	}
	
	//Keep view within map area at all times when adjusting view
	public void edges(){
		if(xOff+1000 > 1000*zoom){
			xOff = 1000*(zoom-1);
		}
		if(xOff < 0){
			xOff = 0;
		}
		if(yOff+1000 > 1000*zoom){
			yOff = 1000*(zoom-1);
		}
		if(yOff < 0){
			yOff = 0;
		}
	}
	
	//Calculate statistics and scores
	public void count(){
		for(int i = 1; i <= factions; i++){
			population[i] = 0;
		}
		for(int i = 0; i < stars.length; i++){
			int p = stars[i].owner;
			population[p] += stars[i].colony;
		}
		for(int i = 1; i <= factions; i++){
			score[i] = fleets[i] + population[i]/10 + planets[i]*5;
		}
		for(int i = 1; i <= factions; i++){
			int s = capital[i];
			int t = stars[s].owner;
			score[t] += 125;
		}
		
		//Create ranked list of factions by score
		boolean swapped = true;
	    int j = 0;
	    int tmp;
	    int[] rankScore = new int[factions+1];
	    for(int i = 0; i < rankings.length; i++){
	    	rankScore[i] = score[i];
	    	rankings[i] = i;
	    }
	    while (swapped) {
	        swapped = false;
	        j++;
	        for (int i = 0; i < rankScore.length - j; i++) {
	            if (rankScore[i] < rankScore[i + 1]) {
	                tmp = rankScore[i];
	                rankScore[i] = rankScore[i + 1];
	                rankScore[i + 1] = tmp;
	                tmp = rankings[i];
	                rankings[i] = rankings[i + 1];
	                rankings[i + 1] = tmp;
	                swapped = true;
	            }
	        }
	    }	
	}
	
	//Calculate the distance between two stars
	public int dist(Star a,Star b){
		int x = a.x - b.x;
		int y = a.y - b.y;
		int d = (int)Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
		return d;
	}
	
	//Generate AI fleets
	//Randomly sends each valid ship to a random valid planet
	public void shipGen(){
		for(int i = 0; i < stars.length; i++){
			int a = stars[i].owner;
			if(!(player && a == faction)){
				int b = (int)Math.floor(stars[i].colony/20);
				//Override to allow capitals to always send ships,
				//prevents running out of ships at start
				if(i == capital[a]){
					b = Math.max(b, 1);
				}
				for(int j = 0; j < b && stars[i].owner != 0; j++){
					int c = i*5+j;
					//Don't overwrite ships in flight
					if(ships[c].flying == false){
						int d;
						int e;
						//Target planet must be in range and either friendly or at war
						do{
							d = (int)(Math.random()*(stars.length));
							e = stars[d].owner;
						}while((d == i || (dist(stars[i],stars[d])>(100+planets[a]*2))) || diplomacy[a][e] == 0);
						ships[c] = new Ship(this,stars[i],stars[d],stars[i].owner,d);
						if(!(i == capital[a] && stars[i].colony <= 20)){
							stars[i].colony--;
						}
					}
				}
			}
		}
	}
	
	//generate player ships using established routes and remove outdated orders
	public void queue(){
		for(int i = 0; i < ships.length; i++){
			if(target[i] != -1 && ships[i].flying == false){
				if(stars[i/5].owner == faction && (i%5 < stars[i/5].colony/20 || (i%5 == 0 && (i/5) == capital[faction])) && diplomacy[faction][stars[target[i]].owner] == 1){
					ships[i] = new Ship(this,stars[i/5],stars[target[i]],faction,target[i]);
					if(!(i/5 == capital[faction] && stars[i/5].colony <= 20)){
						stars[i/5].colony--;
					}
				}
				else{
					queue[target[i]]--;
					target[i] = -1;
				}
			}
		}
	}
	
	//remove current ship orders
	public void remove(){
		int s = -1;
		double d = 0;
		boolean check = false;
		for(int i = 0; i < stars.length; i++){
			double v = dist(stars[i],stars[click]);
			if(stars[i].owner == faction && i != click){
				for(int j = 0; j < stars[i].colony/20 && !check; j++){
					int u = i*5+j;
					if(target[u] == click && v > d){
						d = v;
						s = u;
					}
				}
			}
		}
		if(s != -1){
			target[s] = -1;
			queue[click]--;
		}
	}
	
	//Find nearest available ship and set a route to move to
	public void target(){
		int s = -1;
		double d = 2000;
		boolean check = false;
		for(int i = 0; i < stars.length; i++){
			double v = dist(stars[i],stars[click]);
			if(stars[i].owner == faction && i != click && (v <= (100+planets[faction]*2))){
				for(int j = 0; j < stars[i].colony/20 && !check; j++){
					int u = i*5+j;
					if(target[u] == -1 && v < d){
						d = v;
						s = u;
					}
				}
			}
		}
		if(s != -1){
			target[s] = click;
			queue[click]++;
		}
	}
	
	//Inter-faction diplomacy
	//Randomly makes peace or declares war between two factions
	public void diplomacy(){
		int a = (int)(Math.random()*1000/speed*8/factions);
		if(a==0){
			//Check remaining factions, aborts if only 1 faction left
			int f = 0;
			for(int i = 1; i <= factions; i++){
				if(planets[i] > 0){
					f++;
				}
			}
			if(f > 1){
				int b;
				do{
					b= (int) Math.ceil(Math.random()*factions);
				}while(planets[b] <= 0);
				int c;
				do{
					c = (int) Math.ceil(Math.random()*factions);
				}while(b == c || planets[c] <= 0);
				//Inverts war/peace state of randomly generated pairs
				//if(diplomacy[b][c] == 1 && planets[b] < stars.length/2 && planets[c] < stars.length/2){
				if(diplomacy[b][c] == 1){
					diplomacy[b][c] = 0;
					diplomacy[c][b] = 0;
					news(b,2,c);
				}
				else{
					diplomacy[b][c] = 1;
					diplomacy[c][b] = 1;
					news(b,1,c);
				}
			}
		}
	}
	
	//function to slowly assimilate weaker factions into larger ones
	public void assimilation(){
		count++;
		if(count >= 100){
			count = 0;
			for(int i = 1; i <= factions; i++){
				int a = stars[capital[i]].owner;
				if(a != i){
					assimilate(a,i);
				}
				for(a = 1; a <= factions; a++){
					//if(planets[i] >= dist(stars[capital[i]],stars[capital[a]]) && planets[i] > planets[a] && diplomacy[i][a] != 1){
					if(score[i] > 2 * score[a] && diplomacy[i][a] != 1 && planets[a] > 0){
						assimilate(i,a);
					}
				}
			}
		}
	}
	
	//assimilates a weaker faction
	public void assimilate(int a,int i){
		boolean assimilated = true;
		if(color[a][0] > color[i][0]){
			color[i][0]++;
			assimilated = false;
		}
		else if(color[a][0] < color[i][0]){
			color[i][0]--;
			assimilated = false;
		}
		if(color[a][1] > color[i][1]){
			color[i][1]++;
			assimilated = false;
		}
		else if(color[a][1] < color[i][1]){
			color[i][1]--;
			assimilated = false;
		}
		if(color[a][2] > color[i][2]){
			color[i][2]++;
			assimilated = false;
		}
		else if(color[a][2] < color[i][2]){
			color[i][2]--;
			assimilated = false;
		}
		if(assimilated == false){
			int remainder = Math.abs(color[a][0]-color[i][0]) + Math.abs(color[a][1]-color[i][1]) + Math.abs(color[a][2]-color[i][2]);
			System.out.println(a + " is assimilating " + i + " (" + remainder + "left )");
		}
		colors[i] = new Color(color[i][0],color[i][1],color[i][2]);
		if(assimilated && planets[i] > 0){
			System.out.println(a + " has assimilated " + i);
			for(int s = 0; s < stars.length; s++){
				if(stars[s].owner == i){
					stars[s].owner = a;
				}
			}
			planets[a] += planets[i];
			planets[i] = 0;
		}
	}
	
	//Updated news list
	//Format: Faction A, Event type, Faction B
	public void news(int a, int b, int c){
		for(int i = 0; i < 9; i++){
			news[i][0] = news [i+1][0];
			news[i][1] = news [i+1][1];
			news[i][2] = news [i+1][2];
		}
		news[9][0] = a;
		news[9][1] = b;
		news[9][2] = c;
	}
	
	//Find closest star on screen
	public void click(int x, int y){
		int c = 0;
		int xi = x-stars[0].x;
		int yi = y-stars[0].y;
		double s = Math.sqrt(Math.pow(xi, 2) + Math.pow(yi, 2));
		for(int i = 1; i < stars.length; i++){
			xi = x-stars[i].x;
			yi = y-stars[i].y;
			double d = Math.sqrt(Math.pow(xi, 2) + Math.pow(yi, 2));
			if(d == Math.min(s,d)){
				c=i;
				s=d;
			}
		}
		if(s*zoom<=20){
			if(click == c){
				xOff = stars[click].x*zoom-500;
				yOff = stars[click].y*zoom-500;
				edges();
			}
			else{
				click = c;
			}
		}
		else{
			click = -1;
		}
	}
	
	//check for duplicate star names
	public boolean duplicate(int j, int k, String name){
		boolean copy = false;
		for(int i = 0; i < j; i++){
			if(starNames[i][k].equals(name)){
				copy = true;
			}
		}
		return copy;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		int d = (width-height)/2;
		
		//Create the background
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		g.drawImage(background,-xOff*height/1000,-yOff*height/1000,height*zoom,height*zoom,null);
		//Draw a grid if enabled
		if(grid){
			for(int i = 0; i <= 10; i++){
				g.setColor(new Color(255,255,255,63));
				g.drawLine(i*height/10,0,i*height/10,height);
				g.drawLine(0,i*height/10,height,i*height/10);
			}
		}
		
		//Draw the stars
		for(int i = 0; i < stars.length; i++){
			int t = stars[i].owner;
			Color c = new Color(color[t][0],color[t][1],color[t][2],Math.max(stars[i].colony,10));
			double x = (((stars[i].x)*zoom - xOff)*height/1000)-sr*zoom;
			double y = (((stars[i].y)*zoom - yOff)*height/1000)-sr*zoom;
			//Check if in range and add "Sphere of Influence" visual effect
			if(x < (1000+zoom*50)*height/1000 && y < (1000+zoom*50)*height/1000 && x > (-zoom*50)*height/1000 && y > (-zoom*50)*height/1000){
				g.setColor(Color.WHITE);
				g.fillOval((int)(x), (int)(y), 2*sr*zoom, 2*sr*zoom);
				g.setColor(c);
				int s = stars[i].colony*zoom/2;
				x = ((stars[i].x)*zoom - xOff)*height/1000-sr*zoom-s;
				y = ((stars[i].y)*zoom - yOff)*height/1000-sr*zoom-s;
				int w = 2*(sr)*zoom+2*s;
				g.fillOval((int)(x), (int)(y), w, w);
			}
		}
		
		//Draw the ships
		for(int i = 0; i < ships.length; i++){
			if(!paused)
				ships[i].move();
			if(ships[i].flying){;
				int t = ships[i].owner;
				Color c = new Color(color[t][0],color[t][1],color[t][2],127);
				g.setColor(c);
				g.drawLine((int)((ships[i].xi*zoom-xOff)*height/1000), (int)((ships[i].yi*zoom-yOff)*height/1000), (int)((ships[i].x*zoom-xOff)*height/1000), (int)((ships[i].y*zoom-yOff)*height/1000));
			}
		}
		
		//Label the capitals
		g.setFont(new Font("Verdana", Font.PLAIN, 20));
		for(int i = 1; i <= factions; i++){
			g.setColor(colors[i]);
			int x = (((stars[capital[i]].x)*zoom - xOff)*height/1000)-sr*zoom;
			int y = (((stars[capital[i]].y)*zoom - yOff)*height/1000)-sr*zoom;
			g.fillOval(x-zoom, y-zoom, 2*sr*zoom+2*zoom, 2*sr*zoom+2*zoom);
			//g.drawOval(x-planets[i]*height/1000+sr*zoom,y-planets[i]*height/1000+sr*zoom,2*planets[i]*height/1000,2*planets[i]*height/1000);
		}
		
		//Display ship range and planets in range for players
		if(player && click != -1){
			g.setColor(colors[stars[click].owner]);
			int c = (100+planets[faction]*2)*height/1000;
			g.drawOval((((stars[click].x)*zoom - xOff)*height/1000)-c*zoom,(((stars[click].y)*zoom - yOff)*height/1000)-c*zoom,c*2*zoom,c*2*zoom);
			g.setColor(new Color(color[stars[click].owner][0],color[stars[click].owner][1],color[stars[click].owner][2],5));
			g.fillOval((((stars[click].x)*zoom - xOff)*height/1000)-c*zoom,(((stars[click].y)*zoom - yOff)*height/1000)-c*zoom,c*2*zoom,c*2*zoom);
			g.setColor(new Color(255,191,0,50));
			c = 100+planets[faction]*2;
			if(stars[click].owner == faction){
				for(int i = 0; i < stars.length; i++){
					if(i != click && dist(stars[i],stars[click]) <= c && diplomacy[faction][stars[i].owner] != 0){
						int x = (((stars[i].x)*zoom - xOff)*height/1000)-sr*zoom;
						int y = (((stars[i].y)*zoom - yOff)*height/1000)-sr*zoom;
						g.fillOval(x-zoom-2, y-zoom-2, 2*sr*zoom+2*zoom+4, 2*sr*zoom+2*zoom+4);
					}
				}
			}
			else{
				for(int i = 0; i < stars.length; i++){
					if(i != click && stars[i].owner == faction && dist(stars[i],stars[click]) <= c){
						int x = (((stars[i].x)*zoom - xOff)*height/1000)-sr*zoom;
						int y = (((stars[i].y)*zoom - yOff)*height/1000)-sr*zoom;
						g.fillOval(x-zoom-2, y-zoom-2, 2*sr*zoom+2*zoom+4, 2*sr*zoom+2*zoom+4);
					}
				}
			}
		}
		
		//Pause icon
		if(paused){
			g.setColor(Color.RED);
			g.fillRect(25, 25, 25, 75);
			g.fillRect(65, 25, 25, 75);
			g.drawString("PAUSED", 16,125);
		}
		
		//Speed Marker
		g.setColor(Color.GREEN);
		for(int i = 1; i <= speed; i++){
			int[] xPoints = {height-160+20*i,height-120+20*i,height-160+20*i};
			int[] yPoints = {20,40,60};
			g.fillPolygon(xPoints, yPoints, 3);
		}
		
		//Display information on factions in stats section
		g.setColor(Color.BLACK);
		g.fillRect(height+1, 0, width, height);
		g.setColor(Color.WHITE);
		g.drawString("Systems", height+20,30);
		g.drawString("Size",height+120,30);
		g.drawString("Score",height+230,30);
		for(int f = 0; f < Math.min(factions,8); f++){
			int i = rankings[f];
			g.setColor(colors[i]);
			int t = stars[capital[i]].owner;
			if(planets[i] !=0){
				g.drawString(""+(planets[i]), height+20,(30*(f+2)));
				g.drawString(""+(population[i]+fleets[i]), height+120,(30*(f+2)));
				g.drawString(""+(score[i]),height+230,(30*(f+2)));
			}
			else{
				//g.setColor(colors[t]);
				g.drawString("EXTINCT", height+20,(30*(f+2)));
			}
			//Mark wars
			g.setColor(colors[t]);
			g.fillOval(height+200,(30*(f+2))-20, 20, 20);
			int war = 0;
			for(int n = 1; n < factions; n++){
				if(planets[i] > 0 && planets[n] > 0 && diplomacy[i][n] == 1 && i != n){
					g.setColor(colors[n]);
					g.fillOval(height+290+(10*war),(30*(f+2))-20, 20, 20);
					war++;
				}
			}
		}
		
		//News display
		for(int i = 0; i < 10; i++){
			if(news[i][0] != 0){
				g.setColor(colors[news[i][0]]);
				g.fillOval(height+(d-250)/2, 290+30*i, 20, 20);
				g.setColor(Color.WHITE);
				switch(news[i][1]){
				case 1:
					g.drawString("declared war on",height+(d-250)/2+30,310+30*i);
					break;
				case 2:
					g.drawString("made peace with",height+(d-250)/2+30,310+30*i);
					break;
				default:
					break;
				}
				g.setColor(colors[news[i][2]]);
				g.fillOval(height+230+(d-250)/2, 290+30*i, 20, 20);
			}
		}
		
		//Mini-map
		int s = height-820;
		for(int i = 0; i < stars.length; i++){
			g.setColor(colors[stars[i].owner]);
			g.fillOval(height+(d-s)/2+stars[i].x*s/1000, 600+stars[i].y*s/1000, 1, 1);
		}
		g.setColor(Color.WHITE);
		g.drawRect(height+(d-s)/2-1, 599, s+1, s+1);
		g.setColor(new Color(255,255,255,63));
		g.drawRect(height+(d-s)/2-1+(xOff*s/1000/zoom), 599+(yOff*s/1000/zoom), s/zoom+1, s/zoom+1);
		
		//Planet selection panel
		if(click != -1){
			int t = stars[click].owner;
			g.setColor(colors[t]);
			g.fillOval(height+60, height-200, 100, 100);
			g.setColor(Color.WHITE);
			//Display current name for planet
			g.drawString(starNames[click][t],height+200,height-180);
			//Display Population if colonized
			if(stars[click].owner == 0){
				g.drawString("Uninhabited",height+200,height-160);
			}
			else{
				g.drawString("Population: " + Math.min(stars[click].colony,100),height+200,height-160);
				//Display available ships
				if(stars[click].colony < 20){
					g.drawString("No Shipyard",height+200,height-140);
				}
				else{
					g.drawString("Level " + (stars[click].colony/20) + " Shipyard",height+200,height-120);
				}
			}
				int l = 0;
				//Check fleets in range
				if(player){
					inRange[click] = 0;
					available[click] = 0;
					int c = 100+planets[faction]*2;
					for(int i = 0; i < stars.length; i++){
						if(i != click && stars[i].owner == faction && dist(stars[click],stars[i]) <= c){
							for(int j = 0; j < stars[i].colony/20; j++){
								inRange[click]++;
								if(target[i*5+j] == -1){
									available[click]++;
								}
							}
						}
					}
					g.drawString(available[click] + " fleets in range",height+200,height-140+20*l);
					l++;
				}
				//Check if capital
				for(int n = 1; n <= factions; n++){
					if(click == capital[n]){
						g.drawString("Capital Planet",height+200,height-120+25*l);
						if(n != stars[capital[n]].owner){
							g.setColor(colors[n]);
							g.fillOval(height+110, height-150, 50, 50);
							g.setColor(Color.WHITE);
							g.drawOval(height+110, height-150, 50, 50);
						}
						l++;
					}
				}
			//queue command buttons
			g.setColor(Color.GRAY);
			g.fillRect(height+(d-250)/2, height-80, 250, 25);
			g.fillRect(height+(d-250)/2, height-40, 250, 25);
			if(player && available[click] > 0 && diplomacy[faction][stars[click].owner] != 0){
				g.setColor(Color.WHITE);
			}
			else{
				g.setColor(new Color(100,100,100));
			}
			g.drawString(" Add to queue ",height+(d-250)/2, height-60);
			
			if(player && queue[click] > 0 && diplomacy[faction][stars[click].owner] != 0){
				g.setColor(Color.WHITE);
			}
			else{
				g.setColor(new Color(100,100,100));
			}
			g.drawString(" Remove from queue (" + queue[click] + ")",height+(d-250)/2, height-20);
		}
		//Exit button
		g.setColor(Color.RED);
		g.fillRect(width-25, 0, 25, 25);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Verdana", Font.PLAIN, 35));
		g.drawString("X",width-25,25);
		g.drawLine(height, 0, height, height);
		//g.drawLine(height+d, 0, height+d, height);
	}
}
