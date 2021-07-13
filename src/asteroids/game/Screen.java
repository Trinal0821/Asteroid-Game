package asteroids.game;

import static asteroids.game.Constants.*;
import java.awt.*;
import java.util.Random;
//import java.awt.geom.Path2D;
import javax.swing.*;
import asteroids.participants.Ship;

//@author Tiffany Ng and Trina Luong

/**
 * The area of the display in which the game takes place.
 */
@SuppressWarnings("serial")
public class Screen extends JPanel
{
    /** Legend that is displayed across the screen */
    private String legend;

    // displays the statistics across the screen
    private String asteroidStats;
    private String smallAlienShipStats;
    private String largeAlienShipStats;

    /** Game controller */
    private Controller controller;

    //variable that is used to display the level
    private int level;

    /**
     * Creates an empty screen
     */
    public Screen (Controller controller)
    {
        this.controller = controller;
        legend = "";
        setPreferredSize(new Dimension(SIZE, SIZE));
        setMinimumSize(new Dimension(SIZE, SIZE));
        setBackground(Color.black);
        setForeground(Color.white);
        setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 120));
        setFocusable(true);
    }

    /**
     * Set the legend
     */
    public void setLegend (String legend)
    {
        this.legend = legend;
    }

    // sets the statistics for the asteroids
    public void asteroidStatistics (String asteroidstatistics)
    {
        this.asteroidStats = asteroidstatistics;
    }

    //sets the small alien ship statistics
    public void smallStatistics (String smallalienstatistics)
    {
        this.smallAlienShipStats = smallalienstatistics;

    }

    //set the large alien ship statistics
    public void largeStatistics (String largealienstatistics)
    {
        this.largeAlienShipStats = largealienstatistics;
    }

    /**
     * Paint the participants onto this panel
     */
    @Override
    public void paintComponent (Graphics graphics)
    {

        // Use better resolution
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Do the default painting
        super.paintComponent(g);

        // Draw each participant in its proper place
        for (Participant p : controller)
        {
            p.draw(g);
        }

        //Display the score
        String scoreDisplay = "" + controller.getScore();
        
        //Displays the level
        String levelDisplay = "" + controller.getLevel();
        
        //Displays the highest score if the enhanced version is chosen
        String highestScoreDisplay = "Highest Score: " + controller.getHighestScore();
        
        //gets the next level
        level = controller.getLevel();
        
        // Draw the legend across the middle of the panel
        int size = g.getFontMetrics().stringWidth(legend);
        g.drawString(legend, (SIZE - size) / 2, SIZE / 2);

        //dispalys the score and level 
        if (level >= 1)
        {
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
            g.drawString(scoreDisplay, 65, 50);
            g.drawString(levelDisplay, 700, 50);
            
            //Displays the highest score in the enhanced version
            if (controller.isEnhanced == true)
            {
                g.drawString(highestScoreDisplay, 275, 50);
            }
        }
        
        //displays the asteroid, small alien ship and large alien ship stats
        if (controller.isEnhanced)
        {
            if (asteroidStats != null)
            {

                g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
                g.drawString(asteroidStats, 110, 440);
                g.drawString(smallAlienShipStats, 110, 480);
                g.drawString(largeAlienShipStats, 110, 520);
            }
        }

    }
}
