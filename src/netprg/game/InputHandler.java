package netprg.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {

	
    public InputHandler(Game game) {
        game.addKeyListener(this);
    }

    public class Key {
        private int numTimesPressed = 0;
        private boolean pressed = false;

        public int getNumTimesPressed() {
            return numTimesPressed;
        }

        public boolean isPressed() {
            return pressed;
        }

        public void toggle(boolean isPressed) {
            pressed = isPressed;
            if (isPressed) numTimesPressed++;
        }
    }

    public Key up = new Key();
    public Key down = new Key();
    public Key left = new Key();
    public Key right = new Key();
    public Key shoot = new Key();

    @Override
	public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) {
            up.toggle(true);
        }
        if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) {
            down.toggle(true);
        }
        if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) {
            left.toggle(true);
        }
        if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) {
            right.toggle(true);
        }
        if (keyCode == KeyEvent.VK_SPACE) {
        	//if(!canShoot) {
        		Game.game.player.setGameStart(true);
        	//} 

        }
    }

    @Override
	public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) {
            up.toggle(false);
        }
        if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) {
            down.toggle(false);
        }
        if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) {
            left.toggle(false);
        }
        if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) {
            right.toggle(false);
        }
    }

    @Override
	public void keyTyped(KeyEvent e) {
    }

}
