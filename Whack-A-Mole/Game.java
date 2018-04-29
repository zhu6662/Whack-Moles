/**
 * Name: Matthew Hyman
 *
 * Program Plays a Whack-a-Mole style game via a GUI. Relies upon the Creature sub-class of JButton to facilitate the interaction between the player and 
 * the moles, called creatures in the code for re-usability. The game offers the user the option to play again or end the program after each attempt to beat the game. 
 * 
 */

import javax.swing.*;
import java.awt.*; 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.*;


public class Game extends JFrame implements ActionListener{
    private static final int MAX_CREATURES = 4; //stores the max number of active creatures 
    private static final String SCORE_PREFIX = "Score: "; 
    private static final String TIME_PREFIX = "Time: "; 
    private static final double LENGTH_OF_GAME = .25*40000; //stores the length of the game in milliseconds
    private static final int TARGET_SCORE = 250; //stores the score the player must get to beat the level 
    private static Random rand = new Random(); 
    private static  int numOfCreatures = 9; //stores the number of creatures, varies with the level the player is currently on 
    static int score; //store the numerical score of the game 
    static JLabel scoreLabel; //displays the score of the game
    static JLabel timeLabel; //displays the time left in the game
    public static int creaturesAlive; //stores the number of creatures that are currently alive
    
    Creature[] creatures; //stores the creatures in the game 
    
    
    
    
    public static void main(String[] args) {
        //create an instance of the game and display start message 
        Game this_game = new Game(); 
        JOptionPane.showMessageDialog(this_game, "Instructions: Match the vocab word with the definition shown. \nEach match is worth 10 points.\n  Good luck.");
        
        //loop to enable the user the continuously replay the game, the loop ends when the player chooses to not replay the game at the end of the game
        while(true) {
            
            
            //announce level number and number of creatures to warn player before timer starts 
            JOptionPane.showMessageDialog(this_game, "Time: 10 secs \nTarget Score: " + TARGET_SCORE   + "\n Press OK to begin.");
            
            //play the level 
            this_game.playGame(); 
            
            //if target score is not reached end the game 
            if(score < TARGET_SCORE) {
                JOptionPane.showMessageDialog(this_game,  " Score: " + score + "\n Did not get to " + TARGET_SCORE + " points.  Game Over");
            }
                
            
            //if player has beat the last level display success message 
            if(score > TARGET_SCORE)
                JOptionPane.showMessageDialog(this_game, "Congratulations, you have won the game!");
            
            //display ending screen where the player decides to play again or not 
            int response = JOptionPane.showConfirmDialog(this_game, "Thank you for playing!\n Do you want to play again?", "Play Again?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            
            //if the player decides not to play again or closes the box exit the loop to begin closing the game, otherwise let the game re-set itself
            if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION) 
                break; 
            
            //to facilitate playing the game again reset the appropriate variables to the way they were at the start of the game and re-instantiate the game to reflect the changes
            //resetLevel(); 
            this_game.dispose();
            this_game = new Game();
            
        }
        
        //dispose of game hence ending the program 
        this_game.dispose(); 
        System.exit(0);
        
        
    }
    //updates the time left in the game setting the value 
    //displayed by the time label equal to the time remaining 
    //variable 
    public static void update_time(double timeRemaining) {
        //update the time label 
        timeLabel.setText(TIME_PREFIX + NumberFormat.getInstance().format(timeRemaining/1000));   
    }
    
    //increments the score variable while updating the score label
    public static void update_score() {
        score += 10; 
        scoreLabel.setText(SCORE_PREFIX + score);   
    }
    
    public Game() {
        //set JFrames size, layout, close operation, and title 
        setSize(500, 500); 
        setLayout(new BorderLayout()); 
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Whack-a-Mole"); 
        
        //set score and creatures alive to 0 for the current game/level 
        score = 0; 
        creaturesAlive = 0; 
        
        //Initialize and add top of the screen 
        JPanel top = intitialize_top(); 
        add(top, BorderLayout.NORTH);
        
        //Initialize and add the play field 
        JPanel field = intitialize_field(); 
        add(field, BorderLayout.CENTER);
        
        //set as being visible
        this.setVisible(true); 
        
        
    }
    
    //Initializes the play field with all the creatures and sets their action listeners to the game 
    private JPanel intitialize_field() {
        //create field panel and set up grid layout 
        JPanel field = new JPanel(); 
        field.setLayout(new GridLayout(4,3, 5, 5)); 
        //Initialize creatures array 
        creatures = new Creature[numOfCreatures]; 
        for(int x = 0; x < creatures.length; x++) {
            if(x == 4){
                field.add(new JButton("asdf"));
                creatures[x] = new Creature();
            }else{
                creatures[x] = new Creature();
                creatures[x].addActionListener(this); 
                field.add(creatures[x]);  
            }
        }
        return field; 
        
    }
    
    //Initializes the top of the game screen with the score label
    private JPanel intitialize_top() { 
        
        //Initialize top JPanel 
        JPanel top = new JPanel(); 
        top.setLayout(new GridLayout(1,2)); 
        
        //Initialize and add score and time label to JPanel 
        scoreLabel = new JLabel(); 
        timeLabel = new JLabel(); 
        scoreLabel.setText(SCORE_PREFIX);
        timeLabel.setText(TIME_PREFIX);
        top.add(scoreLabel); 
        top.add(timeLabel); 
        
        return top; 
    }
    
    //play the game with its current level and creature information 
    private void playGame() {
        double startTime = new Date().getTime(); //stores the start of the game   
        double currentTime = startTime; //stores the current time each frame is rendered to allow comparison to start time 
        double timeRemaining = LENGTH_OF_GAME; //stores the time left in the game   
        
        //play game until the length of the game as been reached  
        while(( LENGTH_OF_GAME - timeRemaining) < LENGTH_OF_GAME) {
            
            // Measure the current time in an effort to keep up a consistent
            // frame rate
            long time = System.currentTimeMillis();
            
            reviveCreatures(); 
            updateCreatures(); 
            
            // Sleep until it's time to draw the next frame 
            // (i.e. 32 ms after this frame started processing)
            try{
                long delay = Math.max(0, 32-(System.currentTimeMillis()-time));
                
                Thread.sleep(delay);
            }
            catch(InterruptedException e)
            {
                
            }
            
            //update time variable  
            currentTime = new Date().getTime(); 
            
            //calculate how much time is left in the game 
            timeRemaining = LENGTH_OF_GAME - (currentTime - startTime);
            
            //update time label 
            update_time(timeRemaining); 
            
            
        }
        
        
    }
    
    //updates all the creatures in the game by incrementing their life count if necessary 
    //and killing them if their life is up
    private void updateCreatures() {
        for(int x = 0; x < creatures.length; x++)
            creatures[x].update(); 
    }
    
    //if the max number of creatures are not currently alive attempts to revive a creature
    //note: is intended to accidently revive already alive creature for randomness purposes 
    private void reviveCreatures() {
        if (creaturesAlive < MAX_CREATURES) {
            //no +1 because of array indexes being length-1 
            int randomCreature = rand.nextInt(numOfCreatures); 
            //if creature is not alive revive it 
            if(!creatures[randomCreature].getIsAlive()) {
                creatures[randomCreature].revive(); 
                creaturesAlive++; 
            }
        }
    }
    
    
    //if the player clicks a creature and it is alive kill it and add points to score
    public void actionPerformed(ActionEvent event) {
        Creature clickedCreature = (Creature) event.getSource();
        //if the clicked creature is alive kill it and call update score function 
        if(clickedCreature.getIsAlive()) {
            clickedCreature.kill();
            update_score(); 
        }
        
    }
    
}
