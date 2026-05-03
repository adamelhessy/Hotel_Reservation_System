package hotel.model.customexceptions;

public class InvalidBookingDatesException extends Exception
{
    public InvalidBookingDatesException ( String message )
    {
        super(message);
    }
}
