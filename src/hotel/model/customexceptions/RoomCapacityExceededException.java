package hotel.model.customexceptions;

public class RoomCapacityExceededException extends Exception
{
    public RoomCapacityExceededException (String message)
    {
        super(message);
    }
}
