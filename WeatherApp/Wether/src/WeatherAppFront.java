import org.json.simple.JSONObject;
import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppFront extends JFrame {

    private ImageIcon icon;
    private JSONObject weatherData;

    public WeatherAppFront() {
        super("Weather App");

        setBackground(Color.BLUE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setSize(450, 650);

        setLocationRelativeTo(null);

        setLayout(null);

        setResizable(false);

        addGuiComponents();
    }


    private void addGuiComponents() {


        JTextField searchTextField = createTransparentTextField();
        searchTextField.setBounds(15,15,351,45);

        add(searchTextField);


        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        JLabel teperatureText = new JLabel("10 C");
        teperatureText.setBounds(0, 350, 450, 54);
        teperatureText.setFont(new Font("Dialog", Font.PLAIN, 48));


        teperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(teperatureText);

        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);


        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(0, 500, 74, 66);
        add(humidityImage);

        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        JLabel windspeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windspeedImage.setBounds(220, 500, 74, 66);
        add(windspeedImage);

        JLabel windspeedtext = new JLabel("<html><b>Windspeed</b> 15km/h</html>");
        windspeedtext.setBounds(310, 500, 85, 55);
        windspeedtext.setFont(new Font("Dialog", Font.PLAIN, 15));
        add(windspeedtext);

        JButton searchButton = new JButton(loadImage("src/assets/search.png"));

        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userInput = searchTextField.getText();

                if (userInput.replaceAll("\\s", "").length() <= 0) {
                    return;
                }
                weatherData = WeatherApp.getWeatherData(userInput);

                String weatherCondition = (String) weatherData.get("weather_condition");

                switch (weatherCondition) {
                    case "Clean":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/Rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
                        break;
                }
                double temperature = (double) weatherData.get("temperature");
                teperatureText.setText(temperature + " C");

                weatherConditionDesc.setText(weatherCondition);

                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity +  " /h</html>");

                double windspeed = (double) weatherData.get("windspeed");
                windspeedtext.setText("<html>Windspeed<b> " + windspeed + "</b> km/h " + "</html>");

            }
        });
        add(searchButton);
/*
        JLabel weatherAppBackground = new JLabel(loadImage("src/assets/dayBackground.jpg"));
        weatherAppBackground.setBounds(0, 0, 650, 400);
        add(weatherAppBackground);
*/

    }

    private ImageIcon loadImage(String resourcePath) {
        try {
            BufferedImage image = ImageIO.read(new File(resourcePath));
            return new ImageIcon(image);

        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("Could not find resourse");
        return null;
    }


    private static JTextField createTransparentTextField() {
        JTextField textField = new JTextField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                if (isOpaque()) {
                    Graphics2D g2d = (Graphics2D) g;
                    // Устанавливаем полупрозрачный белый фон
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); // 50% прозрачность
                    g2d.setColor(Color.WHITE); // Белый цвет
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 50); // Рисуем скругленные прямоугольники
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); // Восстанавливаем непрозрачность
                }
                super.paintComponent(g); // Рисуем обычный текст
            }
        };

        textField.setPreferredSize(new Dimension(300, 30)); // Размер текстового поля
        return textField;
    }

}