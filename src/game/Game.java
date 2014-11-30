package game;

import javax.swing.JFrame;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;


/**
 * Created by xkazuhira on 2014-11-30.
 */
public class Game extends Canvas implements Runnable {
    private static final long serialVersionUID = 1L;

    // Basic constants
    public static final int WIDTH = 160;
    public static final int HEIGHT = WIDTH / 12*9;
    public static final int SCALE = 3;
    public static final String NAME = "Tokyo_Traffic";

    private JFrame frame;

    public int tickCount = 0;
    public boolean running = false;

    // Creating Image
    private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

    public Game() {
        // Setting the minimum and maximum size of canvas by using dimensions

        setMinimumSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
        setMaximumSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
        setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));

        frame = new JFrame(NAME);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        frame.add(this, BorderLayout.CENTER); // Adding a frame to the canvas
        frame.pack(); // keeps size

        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public synchronized void start() {
        running = true;
        new Thread(this).start();
    }
    public synchronized void stop() {
        running = false;
    }

    public void run() {
        long lastTime = System.nanoTime();
        double nsPerTick = 1000000000D/60D;

        int ticks = 0;
        int frames = 0;

        long lastTimer = System.currentTimeMillis();
        double delta = 0;

        while(running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;
            boolean shouldRender = true;

            while (delta >= 1) {
                ticks++;
                tick();
                delta -= 1;
                shouldRender = true;
            }

            // Small pause to omit system overload
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(shouldRender) {
                frames++;
                render();
            }

            if(System.currentTimeMillis() - lastTimer >= 1000) {
                lastTimer += 1000;
                System.out.println(frames+", "+ticks);
                frames = 0;
                ticks = 0;
            }
        }
    }

    public void tick () {
        tickCount++;

        // just filling the array with random numbers
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = i * tickCount;
        }
    }

    public void render() {
        BufferStrategy bs = getBufferStrategy(); //for organisation
        if (bs == null) {
            createBufferStrategy(3); // reduces image tearing
            return;
        }

        Graphics g = bs.getDrawGraphics();

        // temp., for testing
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);

        g.dispose();
        bs.show();
    }

    public static void main(String[] args) {
        new Game().start();
    }

}
