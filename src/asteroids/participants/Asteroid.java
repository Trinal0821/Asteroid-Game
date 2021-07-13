package asteroids.participants;

import static asteroids.game.Constants.*;
import java.awt.Shape;
import java.awt.geom.*;
import javax.sound.sampled.Clip;
import asteroids.destroyers.AsteroidDestroyer;
import asteroids.destroyers.BulletDestroyer;
import asteroids.destroyers.MediumAlienShipDestroyer;
import asteroids.destroyers.ShipDestroyer;
import asteroids.destroyers.SmallAlienShipDestroyer;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.Sound;

//@author Tiffany Ng and Trina Luong

/**
 * Represents asteroids
 */
public class Asteroid extends Participant
        implements ShipDestroyer, BulletDestroyer, MediumAlienShipDestroyer, SmallAlienShipDestroyer
{
    /** The size of the asteroid (0 = small, 1 = medium, 2 = large) */
    private int size;

    /** The outline of the asteroid */
    private Shape outline;

    /** The game controller */
    private Controller controller;

    /**
     * Throws an IllegalArgumentException if size or variety is out of range.
     * 
     * Creates an asteroid of the specified variety (0 through 3) and size (0 = small, 1 = medium, 2 = large) and
     * positions it at the provided coordinates with a random rotation. Its velocity has the given speed but is in a
     * random direction.
     */
    public Asteroid (int variety, int size, double x, double y, int speed, Controller controller)
    {
        // Make sure size and variety are valid
        if (size < 0 || size > 2)
        {
            throw new IllegalArgumentException("Invalid asteroid size: " + size);
        }
        else if (variety < 0 || variety > 3)
        {
            throw new IllegalArgumentException();
        }

        // Create the asteroid
        this.controller = controller;
        this.size = size;
        setPosition(x, y);
        setVelocity(speed, RANDOM.nextDouble() * 2 * Math.PI);
        setRotation(2 * Math.PI * RANDOM.nextDouble());
        createAsteroidOutline(variety, size);
    }

    //returns the outline of the asteroid
    @Override
    protected Shape getOutline ()
    {
        return outline;
    }

    /**
     * Creates the outline of the asteroid based on its variety and size.
     */
    private void createAsteroidOutline (int variety, int size)
    {
        // This will contain the outline
        Path2D.Double poly = new Path2D.Double();

        // Fill out according to variety
        if (variety == 0)
        {
            poly.moveTo(0, -30);
            poly.lineTo(28, -15);
            poly.lineTo(20, 20);
            poly.lineTo(4, 8);
            poly.lineTo(-1, 30);
            poly.lineTo(-12, 15);
            poly.lineTo(-5, 2);
            poly.lineTo(-25, 7);
            poly.lineTo(-10, -25);
            poly.closePath();
        }
        else if (variety == 1)
        {
            poly.moveTo(10, -28);
            poly.lineTo(7, -16);
            poly.lineTo(30, -9);
            poly.lineTo(30, 9);
            poly.lineTo(10, 13);
            poly.lineTo(5, 30);
            poly.lineTo(-8, 28);
            poly.lineTo(-6, 6);
            poly.lineTo(-27, 12);
            poly.lineTo(-30, -11);
            poly.lineTo(-6, -15);
            poly.lineTo(-6, -28);
            poly.closePath();
        }
        else if (variety == 2)
        {
            poly.moveTo(10, -30);
            poly.lineTo(30, 0);
            poly.lineTo(15, 30);
            poly.lineTo(0, 15);
            poly.lineTo(-15, 30);
            poly.lineTo(-30, 0);
            poly.lineTo(-10, -30);
            poly.closePath();
        }
        else
        {
            poly.moveTo(30, -18);
            poly.lineTo(5, 5);
            poly.lineTo(30, 15);
            poly.lineTo(15, 30);
            poly.lineTo(0, 25);
            poly.lineTo(-15, 30);
            poly.lineTo(-25, 8);
            poly.lineTo(-10, -25);
            poly.lineTo(0, -30);
            poly.lineTo(10, -30);
            poly.closePath();
        }

        // Scale to the desired size
        double scale = ASTEROID_SCALE[size];
        poly.transform(AffineTransform.getScaleInstance(scale, scale));

        // Save the outline
        outline = poly;
    }

    /**
     * Returns the size of the asteroid
     */
    public int getSize ()
    {
        return size;
    }

    //creates a sound when a small asteroid is destroyed
    public static void smallBangSounds ()
    {
        Clip smallBangClip = new Sound().createClip("/sounds/bangSmall.wav");

        if (smallBangClip != null)
        {
            if (smallBangClip.isRunning())
            {
                smallBangClip.stop();
            }
            smallBangClip.setFramePosition(0);
            smallBangClip.start();
        }
    }

    //creates a sound when a medium asteroid is destroyed
    public static void mediumBangSounds ()
    {
        Clip mediumBangClip = new Sound().createClip("/sounds/bangMedium.wav");

        if (mediumBangClip != null)
        {
            if (mediumBangClip.isRunning())
            {
                mediumBangClip.stop();
            }
            mediumBangClip.setFramePosition(0);
            mediumBangClip.start();
        }
    }

    //creates a sound when a large asteroid is destroyed
    public static void largeBangSounds ()
    {
        Clip largeBangClip = new Sound().createClip("/sounds/bangLarge.wav");

        if (largeBangClip != null)
        {
            if (largeBangClip.isRunning())
            {
                largeBangClip.stop();
            }
            largeBangClip.setFramePosition(0);
            largeBangClip.start();
        }
    }

    /**
     * When a large asteroid collides with a bullet or a ship, the asteroid splits into two medium asteroids. When a
     * medium asteroid collides, it splits into two small asteroids. When a small asteroid collides, it disappears.
     */
    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof AsteroidDestroyer)
        {
            if (getSize() == 0)
            {
                //expire the asteroid
                Participant.expire(this);
                
                //adds the score for the small asteroid
                controller.score(100);
                
              //adds the debris for the for the small asteroid
                controller.addParticipant(new Dust(p.getX(), p.getY(), 0, 5));
                controller.addParticipant(new Dust(p.getX(), p.getY(), 0, 5));
                controller.addParticipant(new Dust(p.getX(), p.getY(), 0, 5));
                controller.addParticipant(new Dust(p.getX(), p.getY(), 0, 5));
                
                //tell the controller that the small asteroid has been destroyed
                controller.asteroidDestroyed();
            }
            else if (getSize() == 1)
            {
              //expire the asteroid
                Participant.expire(this);
              //adds the score for the medium asteroid
                controller.score(50);
                
              //adds the debris for the for the medium asteroid
                controller.addParticipant(new Dust(p.getX(), p.getY(), 0, 5));
                controller.addParticipant(new Dust(p.getX(), p.getY(), 0, 5));
                controller.addParticipant(new Dust(p.getX(), p.getY(), 0, 5));
                controller.addParticipant(new Dust(p.getX(), p.getY(), 0, 5));
                
                //add two small asteroids
                controller.addParticipant(new Asteroid(1, 0, p.getX(), p.getY(),
                        RANDOM.nextInt(MAXIMUM_SMALL_ASTEROID_SPEED) + MAXIMUM_LARGE_ASTEROID_SPEED, controller));
                controller.addParticipant(new Asteroid(0, 0, p.getX(), p.getY(),
                        RANDOM.nextInt(MAXIMUM_SMALL_ASTEROID_SPEED) + MAXIMUM_LARGE_ASTEROID_SPEED, controller));
                
              //tell the controller that the medium asteroid has been destroyed
                controller.asteroidDestroyed();

            }
            else if (getSize() == 2)
            {
              //expire the asteroid
                Participant.expire(this);
                
              //adds the score for the large asteroid
                controller.score(20);
                
                //adds the debris for the for the large asteroid
                controller.addParticipant(new Dust(p.getX(), p.getY(), 0, 5));
                controller.addParticipant(new Dust(p.getX(), p.getY(), 0, 5));
                controller.addParticipant(new Dust(p.getX(), p.getY(), 0, 5));
                controller.addParticipant(new Dust(p.getX(), p.getY(), 0, 5));
                
                //add two medium asteroids
                controller.addParticipant(new Asteroid(2, 1, p.getX(), p.getY(),
                        RANDOM.nextInt(MAXIMUM_MEDIUM_ASTEROID_SPEED) + MAXIMUM_LARGE_ASTEROID_SPEED, controller));
                controller.addParticipant(new Asteroid(1, 1, p.getX(), p.getY(),
                        RANDOM.nextInt(MAXIMUM_MEDIUM_ASTEROID_SPEED) + MAXIMUM_LARGE_ASTEROID_SPEED, controller));
                
              //tell the controller that the large asteroid has been destroyed
                controller.asteroidDestroyed();
            }

        }

    }
}
