<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<head>
    <title>Weather App</title>
    <link rel="stylesheet" href="style.css">
</head>
<body class="default">

<div class="container">
    <h2>🌤 Weather Application</h2>

    <form action="weather" method="get">
        <input type="text" name="city" placeholder="Enter city name" required>
        <br><br>
        <input type="submit" value="Get Weather">
    </form>
</div>

</body>
</html>
