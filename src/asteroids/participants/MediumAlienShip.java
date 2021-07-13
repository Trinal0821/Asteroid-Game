package asteroids.participants;

    import java.awt.Shape;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.awt.geom.Path2D;
    import javax.swing.Timer;
    import asteroids.destroyers.AsteroidDestroyer;
    import asteroids.destroyers.BulletDestroyer;
    import asteroids.destroyers.MediumAlienShipDestroyer;
    import asteroids.destroyers.ShipDestroyer;
    import asteroids.game.Participant;
    import asteroids.game.Constants;
    import asteroids.game.Controller;

  //@author Tiffany Ng and Trina Luong

    /**
     * Represents medium alien ships
     */
    public class MediumAlienShip extends Participant implements ShipDestroyer, BulletDestroyer, AsteroidDestroyer
    {
        /** The outline of the ship */
        private Shape outline;

        /** The game controller */
        private Controller controller;

        //creates the medium alien ship
        public MediumAlienShip (double x, double y, double direction, boolean leftRight, Controller controller)
        {

            //sets the position, speed of the alien ship. Assign a controller to it so it can shoot bullets
            this.controller = controller;
            setPosition(x, y);
            setRotation(direction);
            setVelocity(0, direction);
            setSpeed(2);

            // Set the zig zag path and direction
            int delay = 500;
            ActionListener taskPerformer = new ActionListener()
            {
                public void actionPerformed (ActionEvent evt)
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
            poly.moveTo(25, 0);
            poly.curveTo(25, 0, 0, -25, -25, 0);
            poly.closePath();
            poly.moveTo(25, 0);
            poly.curveTo(25, 0, 0, 40, -25, 0);
            poly.closePath();
            poly.moveTo(10, -12);
            poly.curveTo(10, -12, 0, -40, -10, -12);
            poly.closePath();
            outline = poly;

        }
      
//returns the outline of the alien ship
        @Override
        protected Shape getOutline ()
        {
            return outline;
        }

        //if anything collides with the alien ship then it will expire and create debris
        @Override
        public void collidedWith (Participant p)
        {
            if (p instanceof MediumAlienShipDestroyer)
            {
                //incrementing the counter for the alien ship stats
                controller.smallAlienShipCounter++;
                
                // Expire the alien ship from the game and add 200 point to the player
                controller.AlienShipSwitchDirection();
                
                //play the alien ship sound when it crashes
                controller.alienShipBang();
                
                //expire the alien ship
                Participant.expire(this);
                
                //creates the debris
                controller.addParticipant(new Debris(p.getX(), p.getY(), 0, 5));
                controller.addParticipant(new Debris(p.getX(), p.getY(), Math.PI / 2, 5));
                controller.addParticipant(new Debris(p.getX(), p.getY(), Math.PI, 5));

                //add to the score 
                controller.score(200);
                
                //stop playing the alien ship sound
                controller.bigSaucerClip.stop();
            }

        }

    }



