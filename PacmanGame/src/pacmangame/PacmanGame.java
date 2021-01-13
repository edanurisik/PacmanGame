/**
 *
 * @author 5518123002-Edanur Işık
 */
package pacmangame;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class PacmanGame extends JFrame {      //PacmanGame sınıfından JFrame sınıfı miras alınmaktadır

    public PacmanGame() {
        
        initUI();                            //PacmanGame sınıfından nesne tanımladığımızda açılacak olan metot
    }
    
    private void initUI() {                 //Bu açılan matotta JFrame'in özellikleri yer almaktadır
        
        add(new Board());                   //JFrame oluşturulacak metot eklenir
        
        setTitle("Pacman Game");            //Oyunun çerveçevi olan JFrame'e "Pacman Game" ismini vermektedir
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(380, 420);                 //çerçeve boyutu
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {           //Eklenen tüm değişkenlerin eşzamanlı olarak yenilenebilmesi için metot oluşturulmaktadır

            PacmanGame ex = new PacmanGame();    //JFrame'den yeni bir nesne oluşturulmaktadır
            ex.setVisible(true);                 //Animasyonun çalışması ve resimlerin ekranda gözükmesi için setVisible(true) şeklinde metot tanımlanmaktadır
                                                 
        });
    }
}