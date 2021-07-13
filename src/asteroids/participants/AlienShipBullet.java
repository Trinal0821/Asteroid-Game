
package asteroids.participants;

import java.awt.Shape;
import java.awt.geom.Path2D;
import asteroids.destroyers.AlienShipBulletDestroyer;
import asteroids.destroyers.ShipDestroyer;
import asteroids.game.Constants;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

//@author Tiffany Ng and Trina Luong

// creates the alien ship bullet
public class AlienShipBullet extends Participant implements ShipDestroyer
{
    //creates the outline 
    private Shape outline;

    /** The game controller */
    private Controller controller;

    //creates the outline of the alien ship bullet
    public AlienShipBullet (double x, double y, double direction, Controller controller)
    {
        ///creates the position, veolcity and controller for the bullet, so the alien ship bullet can shoot
        this.controller = controller;
        setPosition(x, y);
        // setSpeed(Constants.BULLET_SPEED);
        setVelocity(Constants.BULLET_SPEED, direction);

        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(0, 5);
        poly.lineTo(5, 0);
        poly.lineTo(0, -5);
        poly.lineTo(-5, 0);
        poly.closePath();
        new ParticipantCountdownTimer(this, "expired", Constants.BULLET_DURATION);

        outline = poly;
    }

    //returns the outline of the alien ship bullet
    @Override
    protected Shape getOutline ()
    {
        return outline;
    }

    //expire the bullet if it hits the ship or misses
    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof AlienShipBulletDestroyer)
        {
            // Expire the ship from the game
            Participant.expire(this);
        }

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
            controller.alienShipBulletAppearTimer.restart();
        }

    }

}
