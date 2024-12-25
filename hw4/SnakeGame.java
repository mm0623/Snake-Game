package hw4;

import javax.swing.JFrame;

public class SnakeGame {
    public static void main(String arg[]){
        
        JFrame game = new JFrame("Snake Game"); //create a JFrame with name “Snake Game”
        game.setResizable(false); //set the frame to unresizable
        game.add(new SnakeGamePanel()); //add the custom SnakeGamePanel to the frame
        game.pack(); //make the frame size fit the preferred size and layouts of its subcomponents
        game.setLocationRelativeTo(null);//place the frame in center of screen
        game.setVisible(true); //set the frame visible
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //exit on close
        
    }
}
