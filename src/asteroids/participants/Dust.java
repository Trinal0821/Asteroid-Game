package asteroids.participants;

import static asteroids.game.Constants.RANDOM;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

//@author Tiffany Ng and Trina Luong

public class Dust extends Participant
{
    //creates the dust
    public Dust (double x, double y, double direction, int speed)
    {
        setPosition(x, y);
        setVelocity(speed, RANDOM.nextDouble() * 2 * Math.PI);
        setRotation(2 * Math.PI * RANDOM.nextDouble());

        new ParticipantCountdownTimer(this, "expired", 500);
    }
    
    
//returns the outline of the dust
    @Override
    protected Shape getOutline ()
    {
        return new Ellipse2D.Double(1, 1, 1, 1);
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
