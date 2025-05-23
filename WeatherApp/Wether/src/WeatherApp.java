import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherApp {
    public static JSONObject getWeatherData(String locationName) {
        JSONArray locatioData = getLocationData(locationName);

        JSONObject location = (JSONObject) locatioData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

//        String urlString = "https://api.open-meteo.com/v1/forecast?" +
//                "latitude=" + latitude + "&longitude=" + longitude +
//                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m";

        String urlString = "https://api.open-meteo.com/v1/forecast?" +
        "latitude=" + latitude + "&longitude=" + longitude +
        "&hourly=temperature_2m,relative_humidity_2m,dew_point_2m,apparent_temperature,weather_code,surface_pressure,wind_speed_10m";


        try {
            HttpURLConnection conn = fetchApiResponse(urlString);

            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect tp API");
                return null;
            }

            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine());
            }

            scanner.close();

            conn.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject restJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            JSONObject hourly = (JSONObject) restJsonObj.get("hourly");

            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOFCurrentTime(time);

            //temperature
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);
            //weather
            JSONArray weathercode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = "1";
            weatherCondition = convertWeatherCode((long) weathercode.get(index));
            //relative
            JSONArray relativeHimidity = (JSONArray) hourly.get("relative_humidity_2m");
            long himidity = (long) relativeHimidity.get(index);
            //windspeed
            JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windspeed = (double) windspeedData.get(index);

            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", himidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static JSONArray getLocationData(String locationName) {
        locationName = locationName.replace(" ", "+");
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try {
            HttpURLConnection conn = fetchApiResponse(urlString);

            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to the API");
                return null;
            }else {
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                while(scanner.hasNext()){
                    resultJson.append(scanner.nextLine());
                }
                scanner.close();
                conn.disconnect();
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObject = (JSONObject) parser.parse(String.valueOf(resultJson));
                JSONArray locationData = (JSONArray) resultsJsonObject.get("results");
                return locationData;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private static HttpURLConnection fetchApiResponse(String urlString){
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            conn.connect();
            return conn;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private static int findIndexOFCurrentTime (JSONArray timeList){
        String currentTime = getCurrentTime();

        for (int i = 0; i < timeList.size(); i++) {
            String time = (String) timeList.get(i);
            if (time.equals(currentTime)) {
                return i;
            }
        }

        return 0;
    }
    public static String getCurrentTime(){
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;
    }

    private static String convertWeatherCode(long weatherCode){
        String weatherCondition = "";
        if (weatherCode ==0L){
            weatherCondition = "Clear";
        } else if (weatherCode >=3L && weatherCode <=0L)  {
            weatherCondition = "Cloudy";
        } else if (weatherCode >=51L && weatherCode <=67L || weatherCode >= 80L && weatherCode <=99L)  {
            weatherCondition = "Rain";
        } else if (weatherCode >= 71L && weatherCode <=77L) {
            weatherCondition = "Snow";
        }
        return weatherCondition;
    }
}
