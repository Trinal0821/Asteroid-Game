package asteroids.participants;

import static asteroids.game.Constants.*;
import java.awt.Shape;
import java.awt.geom.*;
import javax.sound.sampled.Clip;
import asteroids.destroyers.*;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;
import asteroids.game.Sound;

//@author Tiffany Ng and Trina Luong

/**
 * Represents ships
 */
public class Ship extends Participant implements AsteroidDestroyer
{
    /** The outline of the ship */
    private Shape outline;

    //the outline of the flame
    private Shape flame;

    //checks if the ship is accelerating
    private boolean acc;

    /** Game controller */
    private Controller controller;

    /**
     * Constructs a ship at the specified coordinates that is pointed in the given direction.
     */
    public Ship (int x, int y, double direction, Controller controller)
    {
        this.controller = controller;
        setPosition(x, y);
        setRotation(direction);
        setVelocity(0, direction);

        // Constructs a ship
        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(21, 0);
        poly.lineTo(-21, 12);
        poly.lineTo(-14, 10);
        poly.lineTo(-14, -10);
        poly.lineTo(-21, -12);
        poly.closePath();
        outline = poly;

        // Constructs a ship's with flame
        Path2D.Double poly2 = new Path2D.Double();
        poly2.moveTo(21, 0);
        poly2.lineTo(-21, 12);
        poly2.lineTo(-14, 10);
        poly2.lineTo(-21, 0);
        poly2.lineTo(-14, -10);
        poly2.lineTo(-14, 10);
        poly2.lineTo(-14, -10);
        poly2.lineTo(-21, -12);
        poly2.closePath();
        flame = poly2;

        // Schedule an acceleration in two seconds
         new ParticipantCountdownTimer(this, "move", 2000);
    }

    //creates the sound when the ship dies
    public static void shipCrashSounds ()
    {
        Clip shipClip = new Sound().createClip("/sounds/bangShip.wav");

        if (shipClip != null)
        {
            if (shipClip.isRunning())
            {
                shipClip.stop();
            }
            shipClip.setFramePosition(0);
            shipClip.start();
        }
    }

    //creates the sound when the ship is accelerating
    public static void thrustSounds ()
    {
        Clip thrustClip = new Sound().createClip("/sounds/thrust.wav");

        if (thrustClip != null)
        {
            if (thrustClip.isRunning())
            {
                thrustClip.stop();
            }
            thrustClip.setFramePosition(0);
            thrustClip.start();
        }
    }

    /**
     * Returns the x-coordinate of the point on the screen where the ship's nose is located.
     */
    public double getXNose ()
    {
        Point2D.Double point = new Point2D.Double(20, 0);
        transformPoint(point);
        return point.getX();
    }

    /**
     * Returns the y-coordinate of the point on the screen where the ship's nose is located.
     */
    public double getYNose ()
    {
        Point2D.Double point = new Point2D.Double(20, 0);
        transformPoint(point);
        return point.getY();
    }

    /**
     * Returns ship with flame if accelerate key is pressed, otherwise returns just the ship
     */
    @Override
    protected Shape getOutline ()
    {
        if (acc == true)
        {
            return flame;
        }
        else
            return outline;
    }

    /**
     * Customizes the base move method by imposing friction
     */
    @Override
    public void move ()
    {
        applyFriction(SHIP_FRICTION);
        super.move();
    }

    /**
     * Turns right by Pi/16 radians
     */
    public void turnRight ()
    {
        rotate(Math.PI / 16);
    }

    /**
     * Turns left by -Pi/16 radians
     */
    public void turnLeft ()
    {
        rotate(-Math.PI / 16);
    }

    /**
     * Accelerates by SHIP_ACCELERATION
     */
    public void accelerate ()
    {
        accelerate(SHIP_ACCELERATION);

    }

    /**
     * When a Ship collides with a ShipDestroyer, it expires
     */
    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof ShipDestroyer)
        {
            controller.updateShipLives();

            // Expire the ship from the game
            Participant.expire(this);

            //
            shipCrashSounds();

            // Debris appears briefly when the ship is hit
            controller.addParticipant(new Debris(p.getX(), p.getY(), 0, 5));
            controller.addParticipant(new Debris(p.getX(), p.getY(), Math.PI / 2, 5));
            controller.addParticipant(new Debris(p.getX(), p.getY(), Math.PI, 5));

            // Tell the controller the ship was destroyed
            controller.shipDestroyed();
            controller.updateShipLives();
        }
    }

    /**
     * This method is invoked when a ParticipantCountdownTimer completes its countdown.
     */
    @Override
    public void countdownComplete (Object payload)
    {
        // Give a burst of acceleration, then schedule another
        // burst for 200 msecs from now.
        if (payload.equals("move"))
        {
            accelerate();
        }
    }

    // flame flickers while accelerate key is pressed
    public void flame ()
    {
        if (acc == true)
        {
            acc = false;
        }
        else
        {
            acc = true;
        }
    }

    // when accelerate key is released flame, update acc variable to disappear
    public void flameOff ()
    {
        acc = false;
    }

}
