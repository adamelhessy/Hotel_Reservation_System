package hotel.GUI.controllers;

import hotel.model.entities.Room;
import javafx.scene.image.Image;
import java.io.InputStream;
import hotel.model.entities.Amenity;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

public class RoomViewController {
    @FXML private ImageView roomImage;
    @FXML private Label ratingLabel;
    @FXML private Label roomTitleLabel;
    @FXML private Label priceLabel;
    @FXML private Label descriptionLabel;
    @FXML private FlowPane amenitiesBox;
    @FXML private Button bookNowBtn;

    private Room currentRoom;

    public void setRoomData(Room room) {
        this.currentRoom = room;
        String typeName = room.getRoomType().getTypeName();

        roomTitleLabel.setText(typeName);
        priceLabel.setText("$" + (int)room.getRoomType().getEffectivePrice());
        if (descriptionLabel != null) {
            descriptionLabel.setText(room.getRoomType().getDescription());
        }
        ratingLabel.setText("⭐ " + String.format("%.1f", room.calculateAverageRating()));

        try {
            String imagePath = "/hotel/GUI/assets/rooms/" + typeName + ".jpg";
            InputStream imageStream = getClass().getResourceAsStream(imagePath);

            if (imageStream != null) {
                Image image = new Image(imageStream);
                roomImage.setImage(image);
            } else {
                InputStream defaultStream = getClass().getResourceAsStream("/hotel/GUI/assets/rooms/default.jpg");
                if (defaultStream != null) {
                    roomImage.setImage(new Image(defaultStream));
                }
                System.out.println("The room photo is not available " + typeName);
            }
        } catch (Exception e) {
            System.out.println("Problem in loading photo " + e.getMessage());
        }

        amenitiesBox.getChildren().clear();
        room.getAmenities().forEach(a -> {
            Label chip = new Label(a.getAmenityName());
            chip.getStyleClass().add("amenity-chip");
            amenitiesBox.getChildren().add(chip);
        });
    }

    @FXML
    private void onBookNow() {
        System.out.println("Booking room: " + currentRoom.getRoomNumber());
    }
}