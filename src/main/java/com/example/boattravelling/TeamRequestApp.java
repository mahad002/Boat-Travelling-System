package com.example.boattravelling;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.VBox;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class TeamRequestApp extends Application {
    private ComboBox<String> teamComboBox;
    private TextField locationField;
    private ComboBox<String> workTypeComboBox;
    private Slider timeEstimateSlider;
    private DatePicker datePicker;
    private CheckBox craneCheckBox;
    private CheckBox roughWeatherCheckBox;
    private Stage primaryStage;
    private Scene teamScene;

    // Email configuration
    private String host = "smtp.gmail.com";
    private  String port = "587";
    private String username = "";
    private String password = "";
    private String fromEmail = "";
    private String toEmail = "";

    private List<RequestDetails> requestList; // List to store the request details

    // File path and name for storing the serialized data
    private final String DATA_FILE_PATH = "src/main/java/com/example/boattravelling/requests.ser";


    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Team Request App");

        requestList = new ArrayList<>(); // Initialize the request list
        requestList= loadRequestData();
        // First Screen - Team Selection
        Label teamLabel = new Label("Select Team:");
        teamComboBox = new ComboBox<>();
        teamComboBox.getStyleClass().add("combo-box");
        teamComboBox.getItems().addAll("Maintenance Team", "Boat Team");
        Button nextButton = new Button("Next");
        nextButton.setOnAction(e -> handleNextButton());

        VBox teamPane = new VBox(10);
        teamPane.getStyleClass().add("container");
        teamPane.getChildren().addAll(teamLabel, teamComboBox, nextButton);
        teamPane.setAlignment(Pos.CENTER);

        teamScene = new Scene(teamPane, 300, 200);
        teamScene.getStylesheets().add("styles.css");

        primaryStage.setScene(teamScene);
        primaryStage.show();
    }

    private void handleNextButton() {
        String selectedTeam = teamComboBox.getValue();
        if (selectedTeam.equals("Maintenance Team")) {
            primaryStage.setScene(getMaintenanceScene());
            primaryStage.setFullScreen(true);
        } else if (selectedTeam.equals("Boat Team")) {
            primaryStage.setScene(getBoatScene());
            primaryStage.setFullScreen(true);
        }
    }
    private Scene getMaintenanceScene() {
        // Maintenance Team Request Screen
        Label locationLabel = new Label("Number of Locations:");
        locationField = new TextField();
        locationField.getStyleClass().add("text-field");
        Label workTypeLabel = new Label("Type of Work:");
        workTypeComboBox = new ComboBox<>();
        workTypeComboBox.getStyleClass().add("combo-box");
        workTypeComboBox.getItems().addAll("PM", "MMT");
        Label timeEstimateLabel = new Label("Time Estimate (1-10 hours):");
        timeEstimateSlider = new Slider(1, 10, 1);
        timeEstimateSlider.getStyleClass().add("slider");
        Label dateLabel = new Label("Date:");
        datePicker = new DatePicker();
        datePicker.getStyleClass().add("date-picker");
        Label craneLabel = new Label("Crane Required:");
        craneCheckBox = new CheckBox();
        craneCheckBox.getStyleClass().add("check-box");
        Label weatherLabel = new Label("Weather Condition (Rough/Not Rough):");
        roughWeatherCheckBox = new CheckBox();
        roughWeatherCheckBox.getStyleClass().add("check-box");
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(teamScene));
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> handleMaintenanceSubmitButton());

        // Logo
        ImageView logoImageView = new ImageView(new Image(getClass().getResourceAsStream("/logo.png")));
        logoImageView.setFitWidth(50);
        logoImageView.setFitHeight(50);

        // Heading
        Label headingLabel = new Label("Maintenance Team Request");
        headingLabel.getStyleClass().add("heading-label");

        // HBox to contain logo and heading
        HBox logoHeadingBox = new HBox(10);
        logoHeadingBox.setAlignment(Pos.CENTER);
        logoHeadingBox.getChildren().addAll(logoImageView, headingLabel);

        GridPane maintenancePane = new GridPane();
        maintenancePane.getStyleClass().add("container");
        maintenancePane.setPadding(new Insets(10));
        maintenancePane.setVgap(10);
        maintenancePane.setHgap(10);
        maintenancePane.addRow(0, logoHeadingBox);
        maintenancePane.addRow(1, locationLabel, locationField);
        maintenancePane.addRow(2, workTypeLabel, workTypeComboBox);
        maintenancePane.addRow(3, timeEstimateLabel, timeEstimateSlider);
        maintenancePane.addRow(4, dateLabel, datePicker);
        maintenancePane.addRow(5, craneLabel, craneCheckBox);
        maintenancePane.addRow(6, weatherLabel, roughWeatherCheckBox);
        maintenancePane.addRow(7, backButton, submitButton);
        maintenancePane.setAlignment(Pos.CENTER);

        // Fetch weather information from WeatherAPI.com
        String weatherCondition = getWeatherConditionFromAPI();

        // Display weather information
        Label weatherDataLabel = new Label("Weather Condition: " + weatherCondition);
        weatherDataLabel.getStyleClass().add("weather-data-label");
        maintenancePane.add(weatherDataLabel, 0, 8, 2, 1);

        Scene maintenanceScene = new Scene(maintenancePane, 400, 300);
        maintenanceScene.getStylesheets().add("styles.css"); // Apply the CSS to the maintenance scene

        return maintenanceScene;
    }



    private Scene getBoatScene() {
        // Boat Team Review Screen
        VBox requestDetailsBox = new VBox(20);
        requestDetailsBox.getStyleClass().add("container");

        // Logo
        ImageView logoImageView = new ImageView(new Image(getClass().getResourceAsStream("/logo.png")));
        logoImageView.setFitWidth(50);
        logoImageView.setFitHeight(50);

        // Heading
        Label headingLabel = new Label("Boat Team Review");
        headingLabel.getStyleClass().add("heading-label");

        // HBox to contain logo and heading
        HBox logoHeadingBox = new HBox(10);
        logoHeadingBox.setAlignment(Pos.CENTER);
        logoHeadingBox.getChildren().addAll(logoImageView, headingLabel);

        requestDetailsBox.getChildren().add(logoHeadingBox);

        ScrollPane scrollPane = new ScrollPane(requestDetailsBox);
        scrollPane.setFitToWidth(true);

        if (requestList.isEmpty()) {
            // No request details yet
            Label reviewDetailsLabel = new Label("No Data Yet");
            reviewDetailsLabel.getStyleClass().add("no-data-label");
            requestDetailsBox.getChildren().add(reviewDetailsLabel);
        } else {
            // Display request details for each object in the list
            for (RequestDetails request : requestList) {
                VBox requestPane = new VBox(10);
                requestPane.getStyleClass().add("request-pane");

                Label locationLabel = new Label("Location:");
                Label locationValueLabel = new Label(request.getLocation());
                HBox locationBox = new HBox(10, locationLabel, locationValueLabel);
                locationBox.setAlignment(Pos.CENTER_LEFT);

                Label workTypeLabel = new Label("Work Type:");
                Label workTypeValueLabel = new Label(request.getWorkType());
                HBox workTypeBox = new HBox(10, workTypeLabel, workTypeValueLabel);
                workTypeBox.setAlignment(Pos.CENTER_LEFT);

                Label timeEstimateLabel = new Label("Time Estimate (hours):");
                Label timeEstimateValueLabel = new Label(String.valueOf(request.getTimeEstimate()));
                HBox timeEstimateBox = new HBox(10, timeEstimateLabel, timeEstimateValueLabel);
                timeEstimateBox.setAlignment(Pos.CENTER_LEFT);

                Label dateLabel = new Label("Date:");
                Label dateValueLabel = new Label(request.getDate());
                HBox dateBox = new HBox(10, dateLabel, dateValueLabel);
                dateBox.setAlignment(Pos.CENTER_LEFT);

                Label craneLabel = new Label("Crane Required:");
                Label craneValueLabel = new Label(request.isCraneRequired() ? "Yes" : "No");
                HBox craneBox = new HBox(10, craneLabel, craneValueLabel);
                craneBox.setAlignment(Pos.CENTER_LEFT);

                Label weatherLabel = new Label("Weather Condition:");
                Label weatherValueLabel = new Label(request.isRoughWeather() ? "Rough" : "Not Rough");
                HBox weatherBox = new HBox(10, weatherLabel, weatherValueLabel);
                weatherBox.setAlignment(Pos.CENTER_LEFT);

                HBox buttonsBox = new HBox(10);
                buttonsBox.setAlignment(Pos.CENTER_RIGHT);

                // Agree and Disagree buttons for each request
                Button agreeButton = new Button("Agree");
                agreeButton.getStyleClass().add("agree-button");
                agreeButton.setOnAction(e -> handleAgreeButton(request));

                Button disagreeButton = new Button("Disagree");
                disagreeButton.getStyleClass().add("disagree-button");
                disagreeButton.setOnAction(e -> handleDisagreeButton(request));

                buttonsBox.getChildren().addAll(agreeButton, disagreeButton);

                requestPane.getChildren().addAll(
                        locationBox, workTypeBox, timeEstimateBox,
                        dateBox, craneBox, weatherBox
                );

                requestPane.getChildren().add(buttonsBox); // Add buttons at the end

                requestDetailsBox.getChildren().add(requestPane);
            }
        }

        Button backButton = new Button("Back");
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(e -> primaryStage.setScene(teamScene));

        VBox boatPane = new VBox(20);
        boatPane.getStyleClass().add("boat-pane");
        boatPane.getChildren().addAll(scrollPane, backButton);

        Scene boatScene = new Scene(boatPane, 600, 400);
        boatScene.getStylesheets().add("styles.css"); // Apply the CSS to the boat scene

        return boatScene;
    }




    private void handleMaintenanceSubmitButton() {
        // Retrieve user input from Maintenance Team Request Screen
        String location = locationField.getText();
        String workType = workTypeComboBox.getValue();
        int timeEstimate = (int) timeEstimateSlider.getValue();
        String date = datePicker.getValue().toString();
        boolean craneRequired = craneCheckBox.isSelected();
        boolean roughWeather = roughWeatherCheckBox.isSelected();

        // Create a RequestDetails object with the submitted data
        RequestDetails request = new RequestDetails(location, workType, timeEstimate, date, craneRequired, roughWeather);

        // Send email to Boat Team with the request details
        sendEmailToBoatTeam(location, workType, timeEstimate, date, craneRequired, roughWeather);

        // Add the request details to the list
        requestList.add(request);

        // Save the updated request data to the file
        saveRequestData(requestList);

        // Show a success message
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Request to Boat Team  sent successfully!");
        alert.showAndWait();
    }



    private void handleAgreeButton(RequestDetails request) {
        // Send email to Maintenance Team for agreement
        sendAgreementEmailToMaintenanceTeam();
        // primaryStage.close();
    }

    private void handleDisagreeButton(RequestDetails request) {
        // Send email to Maintenance Team for disagreement
        sendDisagreementEmailToMaintenanceTeam();

        // Close the application
//        primaryStage.close();
    }
    private void sendEmailToBoatTeam(String location, String workType, int timeEstimate, String date,
                                     boolean craneRequired, boolean roughWeather) {


        // Create properties for the SMTP server
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        // Create a Session object with authentication
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create a MimeMessage object
            MimeMessage message = new MimeMessage(session);

            // Set the From and To addresses
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));

            // Set the email subject and content
            message.setSubject("Boat Team Request");
            String emailContent = "Location: " + location + "\n"
                    + "Work Type: " + workType + "\n"
                    + "Time Estimate: " + timeEstimate + "\n"
                    + "Date: " + date + "\n"
                    + "Crane Required: " + (craneRequired ? "Yes" : "No") + "\n"
                    + "Weather Condition: " + (roughWeather ? "Rough" : "Not Rough");
            message.setText(emailContent);

            // Send the email
            Transport.send(message);

            System.out.println("Boat Team Request email sent successfully.");
        } catch (MessagingException e) {
            System.out.println("Failed to send Boat Team Request email: " + e.getMessage());
        }
    }


    private void sendAgreementEmailToMaintenanceTeam() {


        // Create properties for the SMTP server
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        // Create a Session object with authentication
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create a MimeMessage object
            MimeMessage message = new MimeMessage(session);

            // Set the From and To addresses
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));

            // Set the email subject and content
            message.setSubject("Agreement Confirmation");
            message.setText("Maintenance Team Agreement: Yes");

            // Send the email
            Transport.send(message);

            System.out.println("Agreement email sent to Maintenance Team.");
        } catch (MessagingException e) {
            System.out.println("Failed to send Agreement email to Maintenance Team: " + e.getMessage());
        }
    }

    private void sendDisagreementEmailToMaintenanceTeam() {


        // Create properties for the SMTP server
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        // Create a Session object with authentication
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create a MimeMessage object
            MimeMessage message = new MimeMessage(session);

            // Set the From and To addresses
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));

            // Set the email subject and content
            message.setSubject("Agreement Confirmation");
            message.setText("Maintenance Team Agreement: No");

            // Send the email
            Transport.send(message);

            System.out.println("Disagreement email sent to Maintenance Team.");
        } catch (MessagingException e) {
            System.out.println("Failed to send Disagreement email to Maintenance Team: " + e.getMessage());
        }
    }

// ...

    private String getWeatherConditionFromAPI() {
        String apiKey = "20a5ea82fa4446f98c3182717231107";
        String apiUrl = "https://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q=Rawalpindi";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse the JSON response to extract weather details
                String jsonString = response.toString();

                // Extract the weather condition from the JSON string
                int conditionStartIndex = jsonString.indexOf("\"text\":\"") + "\"text\":\"".length();
                int conditionEndIndex = jsonString.indexOf("\",\"icon\":\"");
                String weatherCondition = jsonString.substring(conditionStartIndex, conditionEndIndex);

                // Extract the temperature from the JSON string
                int tempStartIndex = jsonString.indexOf("\"temp_c\":") + "\"temp_c\":".length();
                int tempEndIndex = jsonString.indexOf(",\"temp_f\":");
                String temperature = jsonString.substring(tempStartIndex, tempEndIndex);

                // Extract the humidity from the JSON string
                int humidityStartIndex = jsonString.indexOf("\"humidity\":") + "\"humidity\":".length();
                int humidityEndIndex = jsonString.indexOf(",\"cloud\":");
                String humidity = jsonString.substring(humidityStartIndex, humidityEndIndex);

                // Return a formatted string with all the weather details
                return "Condition: " + weatherCondition + ", Temperature: " + temperature + "°C, Humidity: " + humidity + "%";
            } else {
                System.out.println("API request failed with response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Unknown";  // Return a default value in case of an error
    }


    public List<RequestDetails> loadRequestData() {
        List<RequestDetails> requestList = new ArrayList<>();
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(DATA_FILE_PATH ))) {
            requestList = (List<RequestDetails>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return requestList;
    }

    public void saveRequestData(List<RequestDetails> requestList) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(DATA_FILE_PATH ))) {
            outputStream.writeObject(requestList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}