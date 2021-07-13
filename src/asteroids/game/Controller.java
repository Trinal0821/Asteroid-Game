package asteroids.game;

import static asteroids.game.Constants.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import javax.sound.sampled.Clip;
import javax.swing.*;
import asteroids.participants.MediumAlienShip;
import asteroids.participants.AlienShipBullet;
import asteroids.participants.Asteroid;
import asteroids.participants.Bullet;
import asteroids.participants.Debris;
import asteroids.participants.Ship;
import asteroids.participants.SmallAlienShip;

//@author Tiffany Ng and Trina Luong

/**
 * Controls a game of Asteroids.
 */
public class Controller implements KeyListener, ActionListener, Iterable<Participant>
{
    /** The state of all the Participants */
    private ParticipantState pstate;

    /** The ship (if one is active) or null (otherwise) */
    private Ship ship;

    /** When this timer goes off, it is time to refresh the animation */
    private Timer refreshTimer;

    /** When this timer goes off, it is time to add a medium alien ship */
    public Timer mediumAlienShipAppearTimer;

    /** When this timer goes off, it is time to add a medium alien ship */
    public Timer smallAlienShipAppearTimer;

    /** When this timer goes off, it is time to add a medium alien ship */
    public Timer alienShipBulletAppearTimer;

    /** The medium alien ship */
    private MediumAlienShip mas;

    /** The small alien ship */
    private SmallAlienShip sas;

    // The ship's lives
    private Ship shipLives1;
    private Ship shipLives2;
    private Ship shipLives3;

    // The state of keyboard input
    private boolean leftKey;
    private boolean rightKey;
    private boolean accKey;
    private boolean bulletKey;
    private boolean leftRight;

    // retains the highest score in the game
    private int highestScore = 0;

    // Count the number of asteroids and alien ship that were killed
    public int asteroidCounter = 0;
    public int mediumAlienShipCounter = 0;
    public int smallAlienShipCounter = 0;

    /**
     * The time at which a transition to a new stage of the game should be made. A transition is scheduled a few seconds
     * in the future to give the user time to see what has happened before doing something like going to a new level or
     * resetting the current level.
     */
    private long transitionTime;

    /** Number of lives */
    private int lives;

    /** score the player got */
    private int score;

    /** level of the game */
    private int level;

    /** The game display */
    private Display display;

    // Creates a timer for the beats in the background
    private Timer time = new Timer(1000, this);

    // Sets the last playedBeat to true, so that that the beat will alternate with each other
    private boolean lastPlayedBeat = true;

    // creates the clips for the big saucer and the small saucer
    public Clip bigSaucerClip;
    public Clip smallSaucerClip;

    // Creates the isEnhanced variable
    public static boolean isEnhanced;
    
    private Ship extraShipLife;
    
    private ArrayList<Ship> liveList;


    /**
     * Constructs a controller to coordinate the game and screen
     */
    public Controller ()
    {

        // Initialize the ParticipantState
        pstate = new ParticipantState();

        // Set up the refresh timer.
        refreshTimer = new Timer(FRAME_INTERVAL, this);

        // Set up the timer for medium alien ship
        mediumAlienShipAppearTimer = new Timer((RANDOM.nextInt(6) + 5) * 1000, this);

        // Set up the timer for small alien ship
        smallAlienShipAppearTimer = new Timer((RANDOM.nextInt(6) + 5) * 1000, this);

        // Set up the timer for alien bullet to appear
        alienShipBulletAppearTimer = new Timer(5000, this);

        // Clear the transitionTime
        transitionTime = Long.MAX_VALUE;

        // Record the display object
        display = new Display(this);

        // Bring up the splash screen and start the refresh timer
        splashScreen();
        display.setVisible(true);
        refreshTimer.start();
        time.start();

    }

    /**
     * Constructs a enhanced controller to coordinate the game and screen
     */
    public Controller (boolean isEnhanced)
    {
        // assign the enhanced variable so it can be used
        // in other classes to only implement certain methods
        // if the enhanced version is chosen
        this.isEnhanced = isEnhanced;

        // Initialize the ParticipantState
        pstate = new ParticipantState();

        // Set up the refresh timer.
        refreshTimer = new Timer(FRAME_INTERVAL, this);

        // Set up the timer for medium alien ship
        mediumAlienShipAppearTimer = new Timer((RANDOM.nextInt(6) + 5) * 1000, this);

        // Set up the timer for small alien ship
        smallAlienShipAppearTimer = new Timer((RANDOM.nextInt(6) + 5) * 1000, this);

        // Set up the timer for alien bullet to appear
        alienShipBulletAppearTimer = new Timer(5000, this);

        // Clear the transitionTime
        transitionTime = Long.MAX_VALUE;

        // Record the display object
        display = new Display(this);

        // Bring up the splash screen and start the refresh timer
        splashScreen();
        display.setVisible(true);
        refreshTimer.start();
        time.start();

    }

    /**
     * This makes it possible to use an enhanced for loop to iterate through the Participants being managed by a
     * Controller.
     */
    @Override
    public Iterator<Participant> iterator ()
    {
        return pstate.iterator();
    }

    /**
     * Returns the ship, or null if there isn't one
     */
    public Ship getShip ()
    {
        return ship;
    }

    /**
     * Configures the game screen to display the splash screen
     */
    private void splashScreen ()
    {
        // Clear the screen, reset the level, and display the legend
        clear();
        display.setLegend("Asteroids");

        // Place four asteroids near the corners of the screen.
        placeAsteroids();

    }

    /**
     * The game is over. Displays a message to that effect.
     */
    private void finalScreen ()
    {
        // displays the legend
        display.setLegend(GAME_OVER);

        // display the statistics that were accounted for
        if (isEnhanced)
        {
            if (display != null)
            {
                display.setAsteroidStatics("Number of asteroids destroyed: " + asteroidCounter);
                display.setSmallShipStatics("Number of medium alien ship destroyed: " + mediumAlienShipCounter);
                display.setLargeShipStatics("Number of small alien ship destroyed: " + smallAlienShipCounter);
            }

        }

        // remove any participants that are active
        display.removeKeyListener(this);
    }

    /**
     * Place a new ship in the center of the screen. Remove any existing ship first.
     */
    private void placeShip ()
    {
        // Place a new ship
        Participant.expire(ship);
        ship = new Ship(SIZE / 2, SIZE / 2, -Math.PI / 2, this);
        addParticipant(ship);
        display.setLegend("");
    }

    /**
     * Places an asteroid near one corner of the screen. Gives it a random velocity and rotation.
     */
    private void placeAsteroids ()
    {
        // Places 4 asteroids on each corners
        addParticipant(new Asteroid(0, 2, EDGE_OFFSET, EDGE_OFFSET, MAXIMUM_LARGE_ASTEROID_SPEED, this));
        addParticipant(new Asteroid(2, 2, EDGE_OFFSET2, EDGE_OFFSET, MAXIMUM_LARGE_ASTEROID_SPEED, this));
        addParticipant(new Asteroid(1, 2, EDGE_OFFSET, EDGE_OFFSET2, MAXIMUM_LARGE_ASTEROID_SPEED, this));
        addParticipant(new Asteroid(0, 2, EDGE_OFFSET2, EDGE_OFFSET2, MAXIMUM_LARGE_ASTEROID_SPEED, this));
    }

    /**
     * Clears the screen so that nothing is displayed
     */
    private void clear ()
    {
        pstate.clear();
        display.setLegend("");
        ship = null;
    }

    /**
     * Sets things up and begins a new game.
     */
    private void initialScreen ()
    {
        // initial game statistics
        lives = 3;
        level = 1;
        score = 0;

        // initial keyboard input
        leftKey = false;
        rightKey = false;
        accKey = false;
        bulletKey = false;
        transitionTime = 0;

        // when leftRight is true, alien ship's direction goes from left to right, otherwise right to left
        leftRight = true;

        // Clear the screen
        clear();

        // Place asteroids
        placeAsteroids();

        // Place the ship
        placeShip();
        


        // Start listening to events (but don't listen twice)
        display.removeKeyListener(this);
        display.addKeyListener(this);

        // Give focus to the game screen
        display.requestFocusInWindow();

        // Display reminding ship lives
        displayShipLives();
    }

    /**
     * Adds a new Participant
     */
    public void addParticipant (Participant p)
    {
        pstate.addParticipant(p);
    }

    /**
     * The ship has been destroyed
     */
    public void shipDestroyed ()
    {
        // Null out the ship
        ship = null;
        leftKey = false;
        rightKey = false;
        accKey = false;
        bulletKey = false;

        // Decrement lives
        lives = lives - 1;

        // Since the ship was destroyed, schedule a transition
        scheduleTransition(END_DELAY);
    }

    /**
     * An asteroid has been destroyed
     */
    public void asteroidDestroyed ()
    {
        // If all the asteroids are gone, schedule a transition
        if (countAsteroids() == 0)
        {
            level = level + 1;
            scheduleTransition(END_DELAY);
        }
        
    }

    /**
     * Bring up the screen according to current level
     */
    public void nextLevel ()
    {
        if (level == 2)
        {
            level2Screen();

        }
        else if (level >= 3)
        {
            level3Screen();
        }
    }

    /**
     * Sets things up for level 2
     */
    private void level2Screen ()
    {
        // Clear the screen
        clear();

        // Place asteroids
        placeAsteroids();

        // Place additional asteroids according to level
        placeAdditionalAsteroids();

        // Start the timer for medium alien ship to show up
        mediumAlienShipAppearTimer.start();

        // Place the ship
        placeShip();

        // Start listening to events (but don't listen twice)
        display.removeKeyListener(this);
        display.addKeyListener(this);

        // Give focus to the game screen
        display.requestFocusInWindow();

        // Display reminding ship lives
        displayShipLives();
    }

    /**
     * After the alien ship die the direction will switch either left-to-right or right-to-left across the screen
     */
    public void AlienShipSwitchDirection ()
    {
        if (leftRight == true)
        {
            leftRight = false;
        }
        else
        {
            leftRight = true;
        }
    }

    /**
     * Places a medium alien ship that follows a zig-zag path
     */
    private void placeMediumAlienShip ()
    {
        if (leftRight == true)
        {
            addParticipant(mas = new MediumAlienShip(0, 200, 0, true, this));
        }
        else
        {
            addParticipant(mas = new MediumAlienShip(749, 200, 0, false, this));
        }

        // Start the timer for alien ship's bullet
        alienShipBulletAppearTimer.start();
    }

    /**
     * Sets things up from 3 to infinity level
     */
    private void level3Screen ()
    {
        // Clear the screen
        clear();

        // Place asteroids
        placeAsteroids();

        // Place additional asteroids according to level
        placeAdditionalAsteroids();

        // Start the timer for medium alien ship to show up
        smallAlienShipAppearTimer.start();

        // Place the ship
        placeShip();

        // Start listening to events (but don't listen twice)
        display.removeKeyListener(this);
        display.addKeyListener(this);

        // Give focus to the game screen
        display.requestFocusInWindow();

        // displays the ship lives
        displayShipLives();

    }

    /**
     * Places a small alien ship
     */
    private void placeSmallAlienShip ()
    {
        if (leftRight == true)
        {
            addParticipant(sas = new SmallAlienShip(0, 200, 0, true, this));
        }
        else
        {
            addParticipant(sas = new SmallAlienShip(749, 200, 0, false, this));
        }

        // Start the timer for alien ship's bullet
        alienShipBulletAppearTimer.start();
    }

    /**
     * Adding additional asteroids according to the current level
     */
    private void placeAdditionalAsteroids ()
    {
        for (int i = 0; i < level - 1; i++)
        {
            addParticipant(new Asteroid(0, 2, Math.random(), Math.random(), MAXIMUM_LARGE_ASTEROID_SPEED, this));
        }
    }

    // creates the small alien ship sound
    public void smallSaucerSounds ()
    {
        smallSaucerClip = new Sound().createClip("/sounds/saucerSmall.wav");

        if (smallSaucerClip != null)
        {
            if (smallSaucerClip.isRunning())
            {
                smallSaucerClip.stop();
            }
            smallSaucerClip.setFramePosition(0);
            smallSaucerClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    // creates the large alien ship sounds
    public void bigSaucerSounds ()
    {
        bigSaucerClip = new Sound().createClip("/sounds/saucerBig.wav");

        if (bigSaucerClip != null)
        {
            if (bigSaucerClip.isRunning())
            {
                bigSaucerClip.stop();
            }
            bigSaucerClip.setFramePosition(0);
            bigSaucerClip.loop(Clip.LOOP_CONTINUOUSLY);
        }

    }

    /**
     * Schedules a transition m msecs in the future
     */
    private void scheduleTransition (int m)
    {
        transitionTime = System.currentTimeMillis() + m;
    }

    // A method that creates the first beat
    public void beat1Sounds ()
    {
        Clip beat1Clip = new Sound().createClip("/sounds/beat1.wav");

        if (beat1Clip != null)
        {
            if (beat1Clip.isRunning())
            {
                beat1Clip.stop();
            }
            beat1Clip.setFramePosition(0);
            beat1Clip.start();
        }
    }

    // A method that creates the second beat
    public void beat2Sounds ()
    {
        Clip beat2Clip = new Sound().createClip("/sounds/beat2.wav");

        if (beat2Clip != null)
        {
            if (beat2Clip.isRunning())
            {
                beat2Clip.stop();
            }
            beat2Clip.setFramePosition(0);
            beat2Clip.start();
        }
    }

    // creates the alien ship crash sound
    public void alienShipBang ()
    {
        Clip alienShipClip = new Sound().createClip("/sounds/bangAlienShip.wav");

        if (alienShipClip != null)
        {
            if (alienShipClip.isRunning())
            {
                alienShipClip.stop();
            }
            alienShipClip.setFramePosition(0);
            alienShipClip.start();
        }
    }

    /**
     * This method will be invoked because of button presses and timer events.
     */
    @Override
    public void actionPerformed (ActionEvent e)
    {
        // The start button has been pressed. Stop whatever we're doing
        // and bring up the initial screen
        if (e.getSource() instanceof JButton)
        {
            initialScreen();
        }

        // Time to refresh the screen
        else if (e.getSource() == refreshTimer)
        {
            // Deal with keyboard input
            if (rightKey == true && ship != null)
            {
                ship.turnRight();
            }
            if (leftKey == true && ship != null)
            {
                ship.turnLeft();
            }
            if (accKey == true && ship != null)
            {
                ship.accelerate();
                ship.flame();
            }
            if (bulletKey == true && countBullet() < BULLET_LIMIT && ship != null)
            {
                addParticipant(new Bullet(ship.getXNose(), ship.getYNose(), ship.getRotation(), this));
            }
        }
        // plays the beat and alternate them
        else if (e.getSource() == time)
        {
            if (lastPlayedBeat == true)
            {
                beat2Sounds();
                // always play the fastest beat
                time.setDelay(Math.max(time.getDelay() - BEAT_DELTA, FASTEST_BEAT));
                lastPlayedBeat = false;
            }
            else
            {
                lastPlayedBeat = true;
                beat1Sounds();
                // always play the fastest beat
                time.setDelay(Math.max(time.getDelay() - BEAT_DELTA, FASTEST_BEAT));
            }
            // restarts the time
            time.restart();
        }

        // Place a medium alien ship after the timer went off
        if (e.getSource() == mediumAlienShipAppearTimer && level == 2 && countMediumAlienShip() == 0 && ship != null)
        {
            placeMediumAlienShip();
            bigSaucerSounds();

        }

        // Place a small alien ship after the timer went off
        if (e.getSource() == smallAlienShipAppearTimer && level >= 3 && countSmallAlienShip() == 0 && ship != null)
        {
            placeSmallAlienShip();
            smallSaucerSounds();

        }

        // Place alien Bullet after the timer went off
        if (e.getSource() == alienShipBulletAppearTimer && ship != null)
        {
            if (countMediumAlienShip() == 1)
            {
                // Firing bullets in random directions
                addParticipant(new AlienShipBullet(mas.getX(), mas.getY(), RANDOM.nextDouble() * Math.PI, this));

            }
            else if (countSmallAlienShip() == 1)
            {
                // Fires bullets toward the ship
                addParticipant(new AlienShipBullet(sas.getX(), sas.getY(),
                        Math.atan2(ship.getY() - sas.getY(), ship.getX() - sas.getX() + 5), this));

            }
        }

        // It may be time to make a game transition
        performTransition();

        // Move the participants to their new locations
        pstate.moveParticipants();

        // Refresh screen
        display.refresh();

        // Update lives
        updateShipLives();
    }

    /**
     * If the transition time has been reached, transition to a new state
     */
    private void performTransition ()
    {
        // Do something only if the time has been reached
        if (transitionTime <= System.currentTimeMillis())
        {
            // Clear the transition time
            transitionTime = Long.MAX_VALUE;

            // If there are no lives left, the game is over. Show the final
            // screen. Otherwise place ship.
            if (lives <= 0)
            {
                finalScreen();
            }
            // if all of the asteroids are destroyed then all of the alien ship
            // sounds are stopped
            else if (countAsteroids() == 0)
            {
                if (mas != null)
                {

                    bigSaucerClip.stop();
                }
                if (sas != null)
                {
                    smallSaucerClip.stop();
                }
                // if all of the asteroids are destroyed then it moves on to
                // the nexte level
                nextLevel();
            }
            // as long as the ship is not null and there are still lives left
            // then it will still place the ship
            else if (ship == null && lives > 0)
            {
                placeShip();
            }
        }
    }

    /**
     * Returns the number of asteroids that are active participants
     */
    private int countAsteroids ()
    {
        int count = 0;
        for (Participant p : this)
        {
            if (p instanceof Asteroid)
            {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the number of bullets that are active participants
     */
    private int countBullet ()
    {
        int count = 0;
        for (Participant p : this)
        {
            if (p instanceof Bullet)
            {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the number of medium alien ship that are active participants
     */
    private int countMediumAlienShip ()
    {
        int count = 0;
        for (Participant p : this)
        {
            if (p instanceof MediumAlienShip)
            {
                count++;
            }
        }
        return count;
    }

    /**
     * Returnsyy the number of medium alien ship that are active participants
     */
    private int countSmallAlienShip ()
    {
        int count = 0;
        for (Participant p : this)
        {
            if (p instanceof SmallAlienShip)
            {
                count++;
            }
        }
        return count;
    }

    /**
     * If a key of interest is pressed, record that it is down.
     */
    @Override
    public void keyPressed (KeyEvent e)
    {
        if (isEnhanced)
        {
            if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D
                    || e.getKeyCode() == KeyEvent.VK_J) && ship != null)
            {
                rightKey = true;
            }
            if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A
                    || e.getKeyCode() == KeyEvent.VK_L) && ship != null)
            {
                leftKey = true;
            }
            if ((e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_I)
                    && ship != null)
            {
                accKey = true;
                // plays the accelerating sound
                Ship.thrustSounds();
            }
            if ((e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_DOWN
                    || e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_K) && ship != null)
            {
                bulletKey = true;
                // play the bullet shooting sound
                Bullet.bulletSounds();
            }
        }
        else
        {
            if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) && ship != null)
            {
                rightKey = true;
            }
            if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) && ship != null)
            {
                leftKey = true;
            }
            if ((e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) && ship != null)
            {
                accKey = true;
                // plays the accelerating sound
                Ship.thrustSounds();
            }
            if ((e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_DOWN
                    || e.getKeyCode() == KeyEvent.VK_S) && ship != null)
            {
                bulletKey = true;
                
                // play the bullet shooting sound
                Bullet.bulletSounds();
            }
        }
    }

    @Override
    public void keyTyped (KeyEvent e)
    {
    }

    /**
     * If a key of interest is released, record that it is up.
     */
    @Override
    public void keyReleased (KeyEvent e)
    {
        if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) && ship != null)
        {
            rightKey = false;
        }
        if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) && ship != null)
        {
            leftKey = false;
        }
        if ((e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) && ship != null)
        {
            accKey = false;
            ship.flameOff();
        }
        if ((e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_DOWN
                || e.getKeyCode() == KeyEvent.VK_S) && ship != null)
        {
            bulletKey = false;
        }
    }

    /**
     * Update the score of the game.
     */
    public void score (int num)
    {
        score = score + num;
        if (score > highestScore)
        {
            highestScore = score;
        }
    }

    // records the highest score
    public int getHighestScore ()
    {
        return highestScore;
    }

    /**
     * Return the score of the game.
     */
    public int getScore ()
    {
        return score;
    }

    /**
     * Return the level of the game.
     */
    public int getLevel ()
    {
        return level;
    }

    /**
     * Display lives of ship at the upper left corner
     */
    public void displayShipLives ()
    {
        // Ships representing lives
        shipLives1 = new Ship(50, 80, Math.PI * 3 / 2, null);
        shipLives2 = new Ship(80, 80, Math.PI * 3 / 2, null);
        shipLives3 = new Ship(110, 80, Math.PI * 3 / 2, null);

        // Collisions with other Participants is ignored
        shipLives1.setInert(true);
        shipLives2.setInert(true);
        shipLives3.setInert(true);

        // Place 3 ships
        addParticipant(shipLives1);
        addParticipant(shipLives2);
        addParticipant(shipLives3);

    }

    /**
     * Update ship at the upper left corner that representing lives
     */
    public void updateShipLives ()
    {

        if (lives == 0)
        {
            Participant.expire(shipLives1);
            Participant.expire(shipLives2);
            Participant.expire(shipLives3);
        }
        else if (lives == 1)
        {
            Participant.expire(shipLives2);
            Participant.expire(shipLives3);
        }
        else if (lives == 2)
        {
            Participant.expire(shipLives3);
        }
    }
    
  //expires the ship and keeps the ship updated for the enhanced version
    public void expShip ()
        {
            Participant.expire(liveList.get(liveList.size() - 1));
            liveList.remove(liveList.size() - 1);
        }
    //adds the lives
        public void addlives ()
        {
     
            lives = lives + 1;
            displayShipLives ();
        }


}
