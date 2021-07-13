package asteroids.participants;

import java.awt.Shape;
import java.awt.geom.Path2D;
import javax.sound.sampled.Clip;
import asteroids.destroyers.AsteroidDestroyer;
import asteroids.destroyers.BulletDestroyer;
import asteroids.destroyers.MediumAlienShipDestroyer;
import asteroids.game.Constants;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;
import asteroids.game.Sound;

//@author Tiffany Ng and Trina Luong

/*
 * a class that is used to create the bullets in order to shoot the asteroids
 * and the alien ship
 */
public class Bullet extends Participant implements AsteroidDestroyer, MediumAlienShipDestroyer
{
    // a variable used to create the outline of the bullet
    private Shape outline;

    /*
     * Creates the outline of the bullet. It will have a certain speed, the position where it is shooting from and where
     * it is being shot from the ship
     */
    public Bullet (double x, double y, double direction, Controller controller)
    {
        // sets the speed, direction and the position of the bullet
        setPosition(x, y);
        setSpeed(Constants.BULLET_SPEED);
        setRotation(direction);
        setVelocity(Constants.BULLET_SPEED, direction);

        // Construct the bullets
        Path2D.Double poly = new Path2D.Double();

        // creates a different type of bullet if it the enhanced version is chosen
        if (controller.isEnhanced)
        { 
            setRotation(Math.PI);
            poly.moveTo(2, 4);
            poly.lineTo(2, 4);
            poly.lineTo(1, 2);
            poly.lineTo(0, 2);
            poly.lineTo(0, -2);
            poly.lineTo(4, -2);
            poly.lineTo(4, 2);
            poly.lineTo(3, 2);
            poly.closePath();

        }
        //creates the classic version of the bullet
        else
        {
            poly.moveTo(1, 1);
            poly.lineTo(1, 1);
            poly.lineTo(-1, 1);
            poly.lineTo(-1, -1);
            poly.lineTo(1, -1);
            poly.closePath();
        }
        
        //creates a timer for the bullet. If the timer has been reached, the it will
        //expire the bullet
        new ParticipantCountdownTimer(this, "expired", Constants.BULLET_DURATION);

        //returns the outline of the bullet
        outline = poly;

    }

    //creates the sound when the bullet is being shot
    public static void bulletSounds ()
    {
        Clip fireClip = new Sound().createClip("/sounds/fire.wav");

        if (fireClip != null)
        {
            if (fireClip.isRunning())
            {
                fireClip.stop();
            }
            fireClip.setFramePosition(0);
            fireClip.start();
        }

    }

    //returns the outline of the bullet
    @Override
    protected Shape getOutline ()
    {
        return outline;
    }

    //if the bullet collided with an object then it will expire the bullet
    @Override
    public void collidedWith (Participant p)
    {

        if (p instanceof BulletDestroyer)
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
        }
    }

}
