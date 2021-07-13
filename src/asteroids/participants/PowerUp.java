package asteroids.participants;
 
import static asteroids.game.Constants.RANDOM;
import java.awt.Shape;
import java.awt.geom.Path2D;
import asteroids.game.Controller;
import asteroids.game.Participant;
 
//Creates the powerups
public class PowerUp extends Participant
{
    /** The outline of the asteroid */
    private Shape outline;
 
    /** Game controller */
    private Controller controller;
 
    public PowerUp (Controller controller)
    {
        // Create the power up object
        this.controller = controller;
        setPosition(200, 500);
        setRotation(2 * Math.PI * RANDOM.nextDouble());
        setSpeed(1);
 
         Path2D.Double poly = new Path2D.Double();
         poly.moveTo(0, 5);
         poly.lineTo(5, 0);
         poly.lineTo(0, -5);
         poly.lineTo(-5, 0);
         poly.closePath();
         outline = poly;
        
        
 
    }
 
    //return the powerup outline
    @Override
    protected Shape getOutline ()
    {
        // TODO Auto-generated method stub
        return outline;
 
    }
 
    //if something collides with it then it would add extra life
    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof Ship)
        {
            Participant.expire(this);
           // controller.addlives();
        }
 
    }
 
}
