package hotel.model.entities;
import java.io.Serializable;
import java.util.ArrayList; // This is the dynamic array class
import java.util.List;
public class Room implements Serializable
{
    private int roomNumber;
    private int floor;
    private RoomType roomType;
    private List<Amenity> amenities;
    private List<Review> reviews;

    public Room() 
    {
        this.amenities = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }

    public Room( int roomNumber, int floor, RoomType roomType ) 
    {
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.roomType = roomType;
        this.amenities = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public List<Amenity> getAmenities() {
        return amenities;
    }

    public void addAmenity(Amenity amenity) {
        amenities.add(amenity);
    }

    public void removeAmenity(Amenity amenity) {
        amenities.remove(amenity);
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setAmenities(List<Amenity> amenities) {
        this.amenities = amenities;
    }

    public void addReview (Review review) {
        reviews.add(review);
    }

    public double calculateAverageRating() {
        if (reviews.isEmpty()) {
            return 0.0; 
        }
        double totalRating = 0;
        for (Review review : reviews) {
            totalRating += review.getScore();
        }
        return totalRating / reviews.size();
    }

}
