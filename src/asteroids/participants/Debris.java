package asteroids.participants;

import static asteroids.game.Constants.RANDOM;
import java.awt.Shape;
import java.awt.geom.Path2D;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

//@author Tiffany Ng and Trina Luong

public class Debris extends Participant
{
    //sets the debris ready to create the outline for it
    private Shape outline;
    
    //creates the debris in a certain location according to the given x and y coordinate
    public Debris (double x, double y, double direction, int speed)
    {
        setPosition(x, y);
        setVelocity(speed, RANDOM.nextDouble() * 2 * Math.PI);
        setRotation(2 * Math.PI * RANDOM.nextDouble());
        
     // Constructs debris
        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(7, 7);
        poly.lineTo(-21, 12);
        poly.closePath();
        outline = poly;

        new ParticipantCountdownTimer(this, "expired", 500);

    }

    //returns the outline of the dust
    @Override
    protected Shape getOutline ()
    {
        return outline;
    }

    @Override
    public void collidedWith (Participant p)
    {
    }

    /**
     * This method is invoked when a ParticipantCountdownTimer completes its countdown.
     */
    @Override
    public void countdownComplete (Object payload)
    {
        //
        if (payload.equals("expired"))
        {
            Participant.expire(this);

        }
    }

}
