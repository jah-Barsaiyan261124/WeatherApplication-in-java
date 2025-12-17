import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/weather")
public class WeatherServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // 🔑 REPLACE WITH YOUR OPENWEATHER API KEY
    private static final String API_KEY = "096ec795750823f16ce4422dceb0186e";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        String city = request.getParameter("city");

        if (city == null || city.trim().isEmpty()) {
            out.println("<h3>Please enter a city name</h3>");
            return;
        }

        try {
            // 🌐 OpenWeather CURRENT WEATHER API
            String apiUrl =
                    "https://api.openweathermap.org/data/2.5/weather?q="
                            + city + "&units=metric&appid=" + API_KEY;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest apiRequest = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .build();

            HttpResponse<String> apiResponse =
                    client.send(apiRequest, HttpResponse.BodyHandlers.ofString());

            String json = apiResponse.body();

            // 🔍 Extract weather values (simple parsing)
            String temp = json.split("\"temp\":")[1].split(",")[0];
            String feelsLike = json.split("\"feels_like\":")[1].split(",")[0];
            String humidity = json.split("\"humidity\":")[1].split(",")[0];
            String condition = json.split("\"description\":\"")[1].split("\"")[0];
            String wind = json.split("\"speed\":")[1].split(",")[0];
            String country = json.split("\"country\":\"")[1].split("\"")[0];

            // 🕒 Current Date & Time
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
            String currentTime = now.format(formatter);

            // 🌙 DAY / NIGHT DETECTION (FIXED)
            int hour = LocalTime.now().getHour();
            boolean isNight = (hour >= 18 || hour < 6);

            // 🎨 Background selection
            String bgClass;
            String cond = condition.toLowerCase();

            if (isNight) {
                bgClass = "night";
            } else if (cond.contains("clear")) {
                bgClass = "sunny";
            } else if (cond.contains("cloud")) {
                bgClass = "cloudy";
            } else if (cond.contains("rain")) {
                bgClass = "rainy";
            } else if (cond.contains("snow")) {
                bgClass = "snow";
            } else {
                bgClass = "default";
            }

            // 🌈 HTML OUTPUT
            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>Current Weather</title>");
            out.println("<link rel='stylesheet' href='style.css'>");
            out.println("</head>");

            out.println("<body class='" + bgClass + "'>");

            // ☁️ CLOUD BACKGROUND
            out.println("<div class='clouds'>");
            for (int i = 1; i <= 15; i++) {
                out.println("<div class='cloud cloud" + i + "'></div>");
            }
            out.println("</div>");

            // 📦 WEATHER CARD
            out.println("<div class='container weather-box'>");
            out.println("<h2>☁️ Current Weather</h2>");
            out.println("<p><b>City:</b> " + city + ", " + country + "</p>");
            out.println("<p><b>Temperature:</b> " + temp + " °C</p>");
            out.println("<p><b>Feels Like:</b> " + feelsLike + " °C</p>");
            out.println("<p><b>Humidity:</b> " + humidity + " %</p>");
            out.println("<p><b>Wind Speed:</b> " + wind + " m/s</p>");
            out.println("<p><b>Condition:</b> " + condition + "</p>");
            out.println("<p><b>Last Updated:</b> " + currentTime + "</p>");
            out.println("<br><a href='index.jsp'>⬅ Check Another City</a>");
            out.println("</div>");

            out.println("</body></html>");

        } catch (Exception e) {
            out.println("<h3>Error fetching weather data</h3>");
        }
    }
}
