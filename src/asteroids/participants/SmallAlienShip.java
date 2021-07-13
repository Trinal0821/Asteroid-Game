package asteroids.participants;

import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;
import javax.swing.Timer;
import asteroids.destroyers.BulletDestroyer;
import asteroids.destroyers.MediumAlienShipDestroyer;
import asteroids.destroyers.ShipDestroyer;
import asteroids.destroyers.SmallAlienShipDestroyer;
import asteroids.game.Constants;
import asteroids.game.Controller;

import asteroids.game.Participant;

//@author Tiffany Ng and Trina Luong

/**
 * Represents small alien ships
 */
public class SmallAlienShip extends Participant implements ShipDestroyer, BulletDestroyer
{
    /** The outline of the ship */
    private Shape outline;

    /** The game controller */
    private Controller controller;

    //creates the small alien ship
    public SmallAlienShip (double x, double y, double direction,boolean leftRight, Controller controller)
    {
        this.controller = controller;
        setPosition(x, y);
        setRotation(direction);
        setVelocity(0, direction);
        setSpeed(5);
        
        // Set the zig zag path and direction
        int delay = 200; 
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) 
            {
                if (leftRight == true)
                {
                    setDirection(Constants.RIGHT_ZIG_ZAG[Constants.RANDOM.nextInt(3)]); 
                }
                else
                {
                    setDirection(Constants.LEFT_ZIG_ZAG[Constants.RANDOM.nextInt(3)]);
                }
            }
        };
        new Timer(delay, taskPerformer).start();

        // Constructs alien ship
        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(15, 0);
        poly.curveTo(15, 0, 0, -10, -15, 0);
        poly.closePath();
        poly.moveTo(15, 0);
        poly.curveTo(15, 0, 0, 20, -15, 0);
        poly.closePath();
        poly.moveTo(5, -5);
        poly.curveTo(5, -5, 0, -25, -5, -5);
        poly.closePath();
        outline = poly;
    }

    //returns the outline of the small alien ship
    @Override
    protected Shape getOutline ()
    {
        return outline;
    }
    
//if the small is hit, then it will expire
    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof SmallAlienShipDestroyer)
        {
            //counts the number of alien that have been destroyed
            controller.smallAlienShipCounter++;
            
            //switches the direction of the alien ship
            controller.AlienShipSwitchDirection ();
            
            //stop playing the alien ship sound when it is hit
            controller.smallSaucerClip.stop();
            
            //plays the alien crash sound
            controller.alienShipBang();
            
            //expire the alien ship 
            Participant.expire(this);
            
            //creates the debris of the ship
            controller.addParticipant(new Debris(p.getX(), p.getY(), 0, 5));
            controller.addParticipant(new Debris(p.getX(), p.getY(), Math.PI / 2, 5));
            controller.addParticipant(new Debris(p.getX(), p.getY(), Math.PI, 5));

            //adds the score when the alien ship have been destroyed
            controller.score(1000);
        }

    }

}
