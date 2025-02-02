package com.example.pantrypal;

/**
 * The Store class represents a store and its associated information.
 *
 * It currently used as a way to display nearby stores in the StoreLocatorActivity as
 * cards in a RecyclerView.
 * @author Adrian Boziloff
 */
public class Store {
    String name;
    String address;
    String rating;
    String distance;
    String status;
    String directionsLink;

    /**
     * Constructor for the store object
     * @param name store name
     * @param address store address
     * @param rating store rating out of five stars
     * @param distance store distance from user in miles
     * @param status store open/closed status
     * @param directionsLink link to Google maps with directions to the store
     */
    public Store(String name, String address, String rating, String distance, String status, String directionsLink) {
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.distance = distance;
        this.status = status;
        this.directionsLink = directionsLink;
    }

    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getRating() { return rating; }
    public String getDistance() { return distance; }
    public String getStatus() { return status; }
    public String getDirectionsLink() { return directionsLink; }

}
