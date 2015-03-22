
public class Ship {
	double x;
	double y;
	int xi;
	int yi;
	int vx;
	int vy;
	double d;
	double t=0;
	boolean flying;
	int owner;
	int i;
	Main game;
	
	public Ship(Main game, Star A, Star B, int owner, int i){
		this.game = game;
		xi = A.x;
		yi = A.y;
		x = xi;
		y = yi;
		vx = B.x - A.x;
		vy = B.y - A.y;
		flying = true;
		d = Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2));
		this.owner = owner;
		this.i = i;
		game.fleets[owner]++;
	}
	
	public void move(){
		//increase distance travelled, adjusted for time acceleration
		t+=game.speed;
		//ship moving but not at destination
		if(t<d && flying==true){
			double p = (double)(t)/d;
			x = xi + (vx*p);
			y = yi + (vy*p);
		}
		//ship reached destination
		else if(flying==true){
			flying  = false;
			game.fleets[owner]--;
			if(owner != 0){	
				if(game.stars[i].owner == owner){
					if(game.stars[i].colony < 100)
						game.stars[i].colony += 2;
				}
				else if(game.stars[i].owner == 0){
					game.planets[0]--;
					game.stars[i].owner = owner;
					game.planets[owner]++;
					game.stars[i].colony = 2;
				}
				else{
					game.stars[i].colony -= 2 + (game.planets[owner]/25);
					if(game.stars[i].colony<=0){
						game.planets[game.stars[i].owner]--;
						game.stars[i].owner = owner;
						game.planets[owner]++;
						game.stars[i].colony = 1;
					}
				}
			}
				
		}
		//ship not in use
		else{}
	}
	
}
