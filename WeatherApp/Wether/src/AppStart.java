import javax.swing.*;

public class AppStart {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new WeatherAppFront().setVisible(true);
                
            }
        });
    }
}
