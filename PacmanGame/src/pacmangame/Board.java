/**
 *
 * @author 5518123002-Edanur Işık
 */
package pacmangame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {  //Board sınıfından JPanel miras alınmaktadır ve ActionListeer'dan kalıtıldı 

    private Dimension d;            //JPanelin genişlik ve yükseklik değerlerini tanımlanacaktır
    private final Font smallFont = new Font("Helvetica", Font.BOLD, 14);

    private Image images;          //Görselleri tanımlamak için değişken tanımlanmaktadır
    private final Color dotColor = new Color(128, 128, 128);     //Noktaların rengi renk kodlarından tanımlama yapılmıştır, (128,128,128)=>gri rengi
    private Color labyrinthColor;          //Labirentin rengi tanımlanacak değişken tanımlanmaktadır

    private boolean inGame = false;           //inGame ve dying ile Pacman Game'in devam etmesi ve durması boolean olarak tanımlanmıştır
    private boolean dying = false;

    private final int BLOCK_SIZE = 24;
    private final int N_BLOCKS = 15;
    private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;
    private final int PAC_ANIM_DELAY = 2;
    private final int PACMAN_ANIM_COUNT = 4;
    private final int MAX_HAYALETLER = 12;
    private final int PACMAN_SPEED = 6;

    private int pacAnimCount = PAC_ANIM_DELAY;
    private int pacAnimDir = 1;
    private int pacmanAnimPos = 0;
    private int N_HAYALET = 6;
    private int pacsLeft, score;
    private int[] dx, dy;
    private int[] hayalet_x, hayalet_y, hayalet_dx, hayalet_dy, hayaletSpeed;

    //Pacmanin dört yönde de(sağ,sol,üst,aşağı)ve hayaletler değişkenleri tanımlanmaktadır.
    private Image hayalet;
    private Image pacman1, pacman2up, pacman2left, pacman2right, pacman2down;
    private Image pacman3up, pacman3down, pacman3left, pacman3right;
    private Image pacman4up, pacman4down, pacman4left, pacman4right;
    
    //Buradaki pacman_x ve pacman_y Pacman oyununun x ve y koordinatlarını oluşturmaktadır.
    //Sondaki diğer iki değişkenlerde delta olarak dikey ve yatay konumlarını ifade etmektedir.
    private int pacman_x, pacman_y, pacmand_x, pacmand_y;
    private int req_dx, req_dy, view_dx, view_dy;

    //Bu sayılar Pacman oyun çerçevesindeki köşeleri ve noktaları oluşturmuş olduğum yer koordinatlarını tutmaktadır.
    //1 numarası sol,2 numarası üst,4 numara sağ ve 8 numara sol köşeleri temsil etmektedir.
    //16 numara ise noktayı ifade etmektedir.
    //Bu bilgiler ışığında pacman oyunun genel çerçevesi belirlenmiş olmaktadır.
    private final short levelData[] = {
        19, 26, 26, 26, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
        21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
        21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
        21, 0, 0, 0, 17, 16, 16, 24, 16, 16, 16, 16, 16, 16, 20,
        17, 18, 18, 18, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 20,
        17, 16, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 16, 24, 20,
        25, 16, 16, 16, 24, 24, 28, 0, 25, 24, 24, 16, 20, 0, 21,
        1, 17, 16, 20, 0, 0, 0, 0, 0, 0, 0, 17, 20, 0, 21,
        1, 17, 16, 16, 18, 18, 22, 0, 19, 18, 18, 16, 20, 0, 21,
        1, 17, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21,
        1, 17, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21,
        1, 17, 16, 16, 16, 16, 16, 18, 16, 16, 16, 16, 20, 0, 21,
        1, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 21,
        1, 25, 24, 24, 24, 24, 24, 24, 24, 24, 16, 16, 16, 18, 20,
        9, 8, 8, 8, 8, 8, 8, 8, 8, 8, 25, 24, 24, 24, 28
    };
    //Örneğin 22 saysısı;
    //Sağ üst köşeyi ifade etmektedir.Şimdi 22 sayısını çözümleyelim.
    //22=(16+4+2)=> 16 noktayı,4 sağ ve 2 de üst bölgeyi ifade etmektedir.

    private final int validSpeeds[] = {1, 2, 3, 4, 6, 8};
    private final int maxSpeed = 6;

    private int currentSpeed = 3;
    private short[] screenData;
    private Timer timer;

    public Board() {

        loadImages();                     //Görselleri yüklemek için loadImages() metodu çağrılmaktadır
        initVariables();
        initBoard();
    }
    
    private void initBoard() {
        
        addKeyListener(new TAdapter());

        setFocusable(true);

        setBackground(Color.black);          //JPanelin arka taban rengi oluşturmaktadır
    }

    private void initVariables() {

        screenData = new short[N_BLOCKS * N_BLOCKS];
        labyrinthColor = new Color(102, 0, 153);     //Labirentteki engellerin renkleri belirlenmektedir, (102,0,153)=>mor
        d = new Dimension(400, 400);             //dimension ile yükseklik ve geniişlik değerleri atandı
        hayalet_x = new int[MAX_HAYALETLER];
        hayalet_dx = new int[MAX_HAYALETLER];
        hayalet_y = new int[MAX_HAYALETLER];
        hayalet_dy = new int[MAX_HAYALETLER];
        hayaletSpeed = new int[MAX_HAYALETLER];
        dx = new int[4];
        dy = new int[4];
        
        timer = new Timer(40, this);               //Zamanlayıcı tanımlanmaktadır
        timer.start();                             //Zamanlayıcı başlatılmaktadır
    }

    @Override
    public void addNotify() {
        super.addNotify();

        initGame();
    }
    //Pacmanin görüntülerini çizdirir.
    private void doAnim() {

        pacAnimCount--;

        if (pacAnimCount <= 0) {
            //pac_anim_delay ile pacmanin ağız hareketlerini yavaşlatmış oluyoruz.
            pacAnimCount = PAC_ANIM_DELAY;
            pacmanAnimPos = pacmanAnimPos + pacAnimDir;

            if (pacmanAnimPos == (PACMAN_ANIM_COUNT - 1) || pacmanAnimPos == 0) {
                pacAnimDir = -pacAnimDir;
            }
        }
    }

    private void playGame(Graphics2D g2d) {         //Pacman Game'in ana kontrollerinin sağlandığı metot

        if (dying) {

            death();

        } else {

            movePacman();
            drawPacman(g2d);
            moveHayaletler(g2d);
            checkLabyrinth();
        }
    }

    private void showIntroScreen(Graphics2D g2d) {

        g2d.setColor(new Color(0, 32, 48));
        g2d.fillRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50);
        g2d.setColor(Color.white);
        g2d.drawRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50);

        String s = "Press s to start.";          //Oyuncu "s" tuşuna basarak oyuna başlamaktadır
        Font small = new Font("Helvetica", Font.BOLD, 14);     //Yazı tipi ve puntosu ayarlanmaktadır
        FontMetrics metr = this.getFontMetrics(small);

        g2d.setColor(Color.white);
        g2d.setFont(small);
        g2d.drawString(s, (SCREEN_SIZE - metr.stringWidth(s)) / 2, SCREEN_SIZE / 2);
    }

    private void drawScore(Graphics2D g) {     //Score hesaplayan metot

        int i;
        String s;

        g.setFont(smallFont);
        g.setColor(new Color(96, 128, 255));
        s = "Score: " + score;
        g.drawString(s, SCREEN_SIZE / 2 + 96, SCREEN_SIZE + 16);

        for (i = 0; i < pacsLeft; i++) {
            g.drawImage(pacman3left, i * 28 + 8, SCREEN_SIZE + 1, this);
        }
    }

    private void checkLabyrinth() {

        short i = 0;
        //Pacmanin yemesi gereken noktaların olup olmadığını kontrol etmektedir.
        //levelData[] dizisinde belirttiğim gibi 16 noktayı ifade etmektedir.
        //Tüm canlarını kaybedince oyun tekrar başlamaktadır.
        boolean finished = true;

        while (i < N_BLOCKS * N_BLOCKS && finished) {

            if ((screenData[i] & 48) != 0) {
                finished = false;
            }

            i++;
        }

        if (finished) {

            score += 50;

            if (N_HAYALET < MAX_HAYALETLER) {
                N_HAYALET++;
            }

            if (currentSpeed < maxSpeed) {
                currentSpeed++;
            }

            initLevel();
        }
    }

    private void death() {

        pacsLeft--;

        if (pacsLeft == 0) {
            inGame = false;
        }

        continueLevel();
    }

    private void moveHayaletler(Graphics2D g2d) {

        short i;
        int pos;
        int count;

        for (i = 0; i < N_HAYALET; i++) {
            //Hayaletlerin yönlerini bu koşul ile gerçekleştiriyoruz.
            if (hayalet_x[i] % BLOCK_SIZE == 0 && hayalet_y[i] % BLOCK_SIZE == 0) { //Hayaletler bir kare boyunca hareket etmektedir.
                //Pacmanin x ve y koordinatlarından yola çıkarak hayaletlerin konumunu belirlemektedir.
                //Hayaletler ve pacman oluşturulmuş duvarların üzerinden geçişleri olmamaktadır.
                pos = hayalet_x[i] / BLOCK_SIZE + N_BLOCKS * (int) (hayalet_y[i] / BLOCK_SIZE);

                count = 0;
                //Hayaletlerin hareketleri hemen hemen rastgele şekilde olmaktadır.Düz yol boyunca haraketleri 
                //devam edememektedir.
                if ((screenData[pos] & 1) == 0 && hayalet_dx[i] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 2) == 0 && hayalet_dy[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((screenData[pos] & 4) == 0 && hayalet_dx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 8) == 0 && hayalet_dy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {

                    if ((screenData[pos] & 15) == 15) {
                        hayalet_dx[i] = 0;
                        hayalet_dy[i] = 0;
                    } else {
                        hayalet_dx[i] = -hayalet_dx[i];
                        hayalet_dy[i] = -hayalet_dy[i];
                    }

                } else {

                    count = (int) (Math.random() * count);

                    if (count > 3) {
                        count = 3;
                    }

                    hayalet_dx[i] = dx[count];
                    hayalet_dy[i] = dy[count];
                }

            }

            hayalet_x[i] = hayalet_x[i] + (hayalet_dx[i] * hayaletSpeed[i]);
            hayalet_y[i] = hayalet_y[i] + (hayalet_dy[i] * hayaletSpeed[i]);
            drawHayalet(g2d, hayalet_x[i] + 1, hayalet_y[i] + 1);
            
            //Bu if bloğunda ise pacman ve hayaletler ile igilidir.
            //Eğer pacman, hayalet ile karşı karşıya kalırsa(çarparsa, Pacman can kaybeder. 
            if (pacman_x > (hayalet_x[i] - 12) && pacman_x < (hayalet_x[i] + 12)
                    && pacman_y > (hayalet_y[i] - 12) && pacman_y < (hayalet_y[i] + 12)
                    && inGame) {

                dying = true;
            }
        }
    }

    private void drawHayalet(Graphics2D g2d, int x, int y) {

        g2d.drawImage(hayalet, x, y, this);
    }

    private void movePacman() {    //Pacman kontrol yapısı

        int pos;
        short ch;

        if (req_dx == -pacmand_x && req_dy == -pacmand_y) {   //Bu yöntemdeki if bloğu imleç tuşları ile kontrol etmeyi sağlıyor.
            pacmand_x = req_dx;
            pacmand_y = req_dy;
            view_dx = pacmand_x;
            view_dy = pacmand_y;
        }

        if (pacman_x % BLOCK_SIZE == 0 && pacman_y % BLOCK_SIZE == 0) {
            pos = pacman_x / BLOCK_SIZE + N_BLOCKS * (int) (pacman_y / BLOCK_SIZE);
            ch = screenData[pos];

            //Pacman hareketi boyunca noktaları yerse, scor tablosuna puan eklenir.
            if ((ch & 16) != 0) {
                screenData[pos] = (short) (ch & 15);
                score++;
            }

            if (req_dx != 0 || req_dy != 0) {
                if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)
                        || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)
                        || (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
                        || (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
                    pacmand_x = req_dx;
                    pacmand_y = req_dy;
                    view_dx = pacmand_x;
                    view_dy = pacmand_y;
                }
            }

            //Pacman imleç tuşları ile hareket edilmezse olduğu yerde durmaktadır.
            if ((pacmand_x == -1 && pacmand_y == 0 && (ch & 1) != 0)
                    || (pacmand_x == 1 && pacmand_y == 0 && (ch & 4) != 0)
                    || (pacmand_x == 0 && pacmand_y == -1 && (ch & 2) != 0)
                    || (pacmand_x == 0 && pacmand_y == 1 && (ch & 8) != 0)) {
                pacmand_x = 0;
                pacmand_y = 0;
            }
        }
        pacman_x = pacman_x + PACMAN_SPEED * pacmand_x;
        pacman_y = pacman_y + PACMAN_SPEED * pacmand_y;
    }

    private void drawPacman(Graphics2D g2d) {     //Pacman yönlerine göre metotlara göndererek ve çizdirerek metot oluşturulur

        if (view_dx == -1) {
            //Pacmanin sol yön için ağzını açıp kapatmasını canlandırmak için kullanılmaktadır.
            drawPacnanLeft(g2d);
        } else if (view_dx == 1) {
            //Pacmanin sağ yön için ağzını açıp kapatmasını canlandırmak için kullanılmaktadır.
            drawPacmanRight(g2d);
        } else if (view_dy == -1) {
            //Pacmanin yukarı yön için ağzını açıp kapatmasını canlandırmak için kullanılmaktadır.
            drawPacmanUp(g2d);
        } else {
            //Pacmanin aşağı yön için ağzını açıp kapatmasını canlandırmak için kullanılmaktadır.
            drawPacmanDown(g2d);
        }
    }

    private void drawPacmanUp(Graphics2D g2d) {         //Pacman yukarı yönde olduğu zaman ekranda gözükücek görsellerin kontrolu

        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacman2up, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3up, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4up, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }

    private void drawPacmanDown(Graphics2D g2d) {        //Pacman aşağı yönde olduğu zaman ekranda gözükücek görsellerin kontrolu

        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacman2down, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3down, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4down, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }

    private void drawPacnanLeft(Graphics2D g2d) {             //Pacman sol yönde olduğu zaman ekranda gözükücek görsellerin kontrolu

        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacman2left, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3left, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4left, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }

    private void drawPacmanRight(Graphics2D g2d) {               //Pacman sağ yönde olduğu zaman ekranda gözükücek görsellerin kontrolu

        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacman2right, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3right, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4right, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }

    private void drawLabyrinth(Graphics2D g2d) {     //Labirentin kontrol yapısı

        short i = 0;
        int x, y;

        for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
            for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {

                g2d.setColor(labyrinthColor);
                g2d.setStroke(new BasicStroke(2));

                if ((screenData[i] & 1) != 0) { 
                    g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 2) != 0) { 
                    g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
                }

                if ((screenData[i] & 4) != 0) { 
                    g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 8) != 0) { 
                    g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 16) != 0) { 
                    g2d.setColor(dotColor);
                    g2d.fillRect(x + 11, y + 11, 2, 2);
                }

                i++;
            }
        }
    }

    private void initGame() {

        pacsLeft = 4;   //Pacman'in canı 4'tür
        score = 0;
        initLevel();
        N_HAYALET = 8;  //Hayalet sayısı 8 olarak tanımlanmıştır
        currentSpeed = 3;
    }

    private void initLevel() {

        int i;
        for (i = 0; i < N_BLOCKS * N_BLOCKS; i++) {
            screenData[i] = levelData[i];
        }

        continueLevel();
    }

    private void continueLevel() {

        short i;
        int dx = 1;
        int random;

        for (i = 0; i < N_HAYALET; i++) {

            hayalet_y[i] = 4 * BLOCK_SIZE;
            hayalet_x[i] = 4 * BLOCK_SIZE;
            hayalet_dy[i] = 0;
            hayalet_dx[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (currentSpeed + 1));

            if (random > currentSpeed) {
                random = currentSpeed;
            }

            hayaletSpeed[i] = validSpeeds[random];
        }

        pacman_x = 7 * BLOCK_SIZE;
        pacman_y = 11 * BLOCK_SIZE;
        pacmand_x = 0;
        pacmand_y = 0;
        req_dx = 0;
        req_dy = 0;
        view_dx = -1;
        view_dy = 0;
        dying = false;
    }

    private void loadImages() {
        
        //images klasöründen görüntüler yüklenmektedir.
        hayalet = new ImageIcon("images/hayaletler.png").getImage();
        pacman1 = new ImageIcon("images/pacman.png").getImage();
        pacman2up = new ImageIcon("images/up1.png").getImage();
        pacman3up = new ImageIcon("images/up2.png").getImage();
        pacman4up = new ImageIcon("images/up3.png").getImage();
        pacman2down = new ImageIcon("images/down1.png").getImage();
        pacman3down = new ImageIcon("images/down2.png").getImage();
        pacman4down = new ImageIcon("images/down3.png").getImage();
        pacman2left = new ImageIcon("images/left1.png").getImage();
        pacman3left = new ImageIcon("images/left2.png").getImage();
        pacman4left = new ImageIcon("images/left3.png").getImage();
        pacman2right = new ImageIcon("images/right1.png").getImage();
        pacman3right = new ImageIcon("images/right2.png").getImage();
        pacman4right = new ImageIcon("images/right3.png").getImage();

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);            //doDrawing() metoduyla  ekranda gözükecek görsel ve metinlerin basılması gerçekleştirildi
    }

    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, d.width, d.height);

        drawLabyrinth(g2d);
        drawScore(g2d);
        doAnim();

        if (inGame) {
            playGame(g2d);
        } else {
            showIntroScreen(g2d);
        }

        g2d.drawImage(images, 5, 5, this);
        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }

    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {        //Klavye kontrolü ile oyunu durdurma ve bitirme tanımlandı

            int key = e.getKeyCode();

            if (inGame) {
                if (key == KeyEvent.VK_LEFT) {
                    req_dx = -1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_RIGHT) {
                    req_dx = 1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_UP) {
                    req_dx = 0;
                    req_dy = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                    req_dx = 0;
                    req_dy = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                    inGame = false;
                } else if (key == KeyEvent.VK_PAUSE) {
                    if (timer.isRunning()) {
                        timer.stop();
                    } else {
                        timer.start();
                    }
                }
            } else {
                if (key == 's' || key == 'S') {
                    inGame = true;
                    initGame();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

            int key = e.getKeyCode();

            if (key == Event.LEFT || key == Event.RIGHT
                    || key == Event.UP || key == Event.DOWN) {
                req_dx = 0;
                req_dy = 0;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        repaint();
    }
}