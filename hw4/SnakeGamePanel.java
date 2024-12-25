package hw4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGamePanel extends JPanel{
    
    //fields
    ArrayList<Sequence> snake = new ArrayList<>(); //the snake body's coordinates as Sequences
    Sequence apple = new Sequence(); //coordinates of the apple
    int delay = 250; //the current delay time (in milliseconds) of firing an ActionEvent
    Timer timer = new Timer(delay, new MovingListener()); //fires MovingListener after every delay time has past
    int currentKey; //the latest valid key pressed (space, up, down, left, right)
    int direction; //the latest direction (up, down, left, right)
    int eaten = 0; //number of apples eaten (decrease th edelay time per 4 apples eaten)
    int space = 0; //number of times space is pressed, resets to 0 everytime game resumes
    final static Random rand = new Random(); //a constant random number generator

    /* SnakeGamePanel(): default constructor
    Sets the blue 320x320 pixels game background panel and starts a new game.
    */
    public SnakeGamePanel(){
        this.setPreferredSize(new Dimension(320,320)); //320x320 pixels panel
        this.setBackground(Color.white); //set panel to green
        this.setFocusable(true); // set panel as focusable
        
        newGame(); //start new game
    }

    /* DirectionListener:
    Listens to KeyEvent of space, up, down, left, and right keys.
    Update the latest valid key pressed and latest valid location respectively when a key is pressed. 
    */
    private class DirectionListener implements KeyListener{
        @Override
        public void keyPressed(KeyEvent e){
            
            //get the key code associated with the KeyEvent
            int key = e.getKeyCode();

            //update currentKey and direction if key pressed a valid direction where snake wouldn't overlap onto itself
            if((key == KeyEvent.VK_UP && currentKey != KeyEvent.VK_DOWN)||
                (key == KeyEvent.VK_DOWN && currentKey != KeyEvent.VK_UP)||
                (key == KeyEvent.VK_LEFT && currentKey != KeyEvent.VK_RIGHT)||
                (key == KeyEvent.VK_RIGHT && currentKey != KeyEvent.VK_LEFT))
            {
                direction = key;
                currentKey = key;
            }

            //update currentKey if key pressed is space
            if(key == KeyEvent.VK_SPACE)
            {
                currentKey = key;
            }
        }
        
        //empty implementations of abstract methods from KeyListener
        @Override
        public void keyTyped(KeyEvent e){}
        @Override
        public void keyReleased(KeyEvent e){}
    }

    /* MovingListener:
    Update the snake coordinates according to the latest key pressed and if an apple was eaten.
    Update the UI only if the game isn't over.
    Pause or restart the timer (the game and the UI) if space is pressed. 
    */
    private class MovingListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            
            Sequence head = snake.get(snake.size()-1); //coordinates of the snake's head
            
            //depending on the direction, move 20 pixels on the x or y axis
            //by adding a Sequence 20 pixels away from the snake's head
            switch(currentKey){
                case KeyEvent.VK_UP:
                    snake.add(new Sequence(head.x,head.y-20)); //add a Sequence 20 pixels up in y-axis
                    break;
                case KeyEvent.VK_DOWN:
                    snake.add(new Sequence(head.x,head.y+20)); //add a Sequence 20 pixels down in y-axis
                    break;
                case KeyEvent.VK_RIGHT:
                    snake.add(new Sequence(head.x+20,head.y)); //add a Sequence 20 pixels right in x-axis
                    break;
                case KeyEvent.VK_LEFT:
                    snake.add(new Sequence(head.x-20,head.y)); //add a Sequence 20 pixels left in x-axis
                    break;
            }

            //if the apple is eaten
            if(head.equals(apple)){
                //don't remove the first snake Sequence
                nextApple(); //generate a new apple
            }

            //remove the first snake Sequence if the apple isn't eaten
            //don't remove the first snake Sequence if the game is paused or just started
            else if(!head.equals(apple) && currentKey!=KeyEvent.VK_0 && currentKey!=KeyEvent.VK_SPACE){
                snake.remove(0);
            }

            //if the game isn't over
            if(!isGameOver()){

                repaint(); //update the panel UI
                
                //pause the game if space is pressed
                if(currentKey == KeyEvent.VK_SPACE){
                    
                    //stop timer (pause actionlistener and subsequent UI change)
                    timer.stop();
                    
                    //show option pane to continue with OK option
                    JOptionPane.showMessageDialog(SnakeGamePanel.this,"Pause, continue?"," ", JOptionPane.OK_OPTION);
                    
                    //reset current key to the current direction
                    currentKey = direction;
                    
                    timer.start(); //start timer again
                }
            }
        }
    }

    /* paintComponent (Graphics g):
    Remove the old game panel and paints the new one with updated apple and snake coordinates.
    Called whenever repaint() is called. 
    */
    @Override
    protected void paintComponent (Graphics g){

        //update the old graphic to prevent overlap
        super.paintComponent(g);

        //repaint the apple UI (red, each block is 20x20) at current coordinates
        g.setColor(Color.red);
        g.fillRect(apple.x,apple.y,20,20);

        //repain the snake UI (blue, each block is 20x20) at current coordinates
        g.setColor(Color.BLUE);
        for(Sequence s:snake){
            g.fillRect(s.x,s.y,20,20);
        }
    }

    /* nextApple():
    Generates random valid coordinates for a new apple that doesn't overlap on snake 
    and are multiples of 20 between [0,300], reducing the delay time if 4 apples are eaten.
    */
    private void nextApple(){

        //keep generating until the apple doesn't overlap on the snake
        do{
            apple.x = rand.nextInt(16)*20;
            apple.y = rand.nextInt(16)*20;
        }
        while(snake.contains(apple));

        eaten+=1; //update number of apples eaten

        //reduce delay time by 20ms per 4 apples eaten until a mininum delay time of 50ms
        if(eaten==4 && delay>50){
            eaten=0; //reset the number of apples eaten
            delay-=20; 
            timer.setDelay(delay); //set the new delay time to the timer
        }
    }

    /* Sequence:
    Stores coordinates that are multiples of 20 as x and y.
    */
    private class Sequence{

        //fields
        public int x;
        public int y;

        /* Sequence(): default constructor
        Calls the class' parameterized constructor to instantiate a Sequence with
        random coordinates that are multiples of 20 between [0,300].
        */
        public Sequence(){
            this(SnakeGamePanel.rand.nextInt(16)*20,SnakeGamePanel.rand.nextInt(16)*20);
        }

        /* Sequence(int x,int y): constructor
        Instantiates a Sequence with x and y given in the parameters.
        */
        public Sequence(int x,int y){
            this.x=x;
            this.y=y;
        }

        /* equals(Object obj):
        Returns true if the x and y coordinates of two Sequences are the same.
        */
        @Override
        public boolean equals(Object obj) {
            Sequence a = (Sequence) obj;
            return this.x == a.x && this.y == a.y;
        }
    }

    /* isGameOver():
    Returns true and ends the game if the snake touches the border or if it runs into itself.
    */
    private boolean isGameOver(){

        //coordinates of the snake's head
        Sequence head = snake.get(snake.size()-1);

        //if snake head goes over the border
        if(head.x<0 || head.x>300 || head.y<0 || head.y>300){
            endGame();
            return true;
        }

        //if snake head intersects with its body
        for(int i=1; i<snake.size()-1;i++){
            //skip evaluating if head == snake.get(0) since they're the same
            if (snake.get(i).equals(head)){
                endGame();
                return true;
            }
        }

        //otherwise, return false
        return false;
    }

    /* endGame():
    Stops the game and displays an option pane asking to start a new game.
    Starts a new game once the option pane closes.
    */
    private void endGame(){
        
        //stop timer (pause actionlistener and subsequent UI change)
        timer.stop();

        //show option pane to play again with an OK option
        JOptionPane.showMessageDialog(this,"New Game?","Game Over", JOptionPane.PLAIN_MESSAGE); 

        //start a new game and start the timer
        newGame();
        repaint();
    }

    /* newGame():
    Resets all game components, adds the custom key listener, and starts a new timer with the custom action listener. 
    */
    private void newGame(){
        delay = 250; //reset the timer delay time
        timer = new Timer(delay, new MovingListener()); //start a new timer
        snake = new ArrayList<Sequence>(); //generate a new snake
        snake.add(new Sequence()); //generate a random first snake Sequence
        currentKey = KeyEvent.VK_0; //put a placeholder as the lastest key pressed
        direction = KeyEvent.VK_0; //put a placeholder as the lastest direction
        nextApple(); //generate a new valid apple
        eaten = 0; //reset apples eaten to 0
        addKeyListener(new DirectionListener()); //add the direction key listener
        timer.start();//start the game once a key is pressed
    }
}