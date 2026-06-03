/**
 * Interface for GPS services.
 */
public interface ServiceGPS {
   
    /** Returns the current position. */
    Coordinate getPosition();


    /** Computes an itinerary to the given destination. */
    Itinerary computeItinerary(Coordinate destination);


    /** Displays the path of the given itinerary. */
    void displayPath(Itinerary itinerary);
}