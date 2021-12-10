package com.clementsnelson.smoothriders;

import androidx.annotation.NonNull;
import java.io.Serializable;

// Constructor for Ride objects
// implements Serializable so we can pass Ride objects through intents
public class Ride implements Serializable {

    // Class variables
    public String rideDescription, rideTime, pickupLocation, destinationLocation, rideTip, requesterEmail, driverEmail, rideId;
    private Boolean isAccepted;

    // Default constructor
    public Ride() {

    }

    // Constructor using parameters
    public Ride(String rideDescription, String rideTime, String pickupLocation, String destinationLocation,
                String rideTip, String requesterEmail, String driverEmail, String rideId, Boolean isAccepted) {
        this.rideDescription = rideDescription;
        this.rideTime = rideTime;
        this.pickupLocation = pickupLocation;
        this.destinationLocation = destinationLocation;
        this.rideTip = rideTip;
        this.requesterEmail = requesterEmail;
        this.driverEmail = driverEmail;
        this.rideId = rideId;
        this.isAccepted = isAccepted;
    }

    // Getters for class variables
    public String getRideDescription(){ return rideDescription;}
    public String getRideTime(){return rideTime;}
    public String getPickupLocation(){return pickupLocation;}
    public String getDestinationLocation(){return destinationLocation;}
    public String getRideTip(){return rideTip;}
    public String getRequesterEmail(){return requesterEmail;}
    public String getDriverEmail(){return driverEmail;}
    public String getRideId(){return rideId;}
    public Boolean getIsAccepted(){return isAccepted;}

    // Setter for isAccepted and driverEmail
    public void setIsAccepted(Boolean isAccepted){this.isAccepted = isAccepted;}
    public void setDriverEmail(String driverEmail){this.driverEmail = driverEmail;}


    @NonNull
    @Override
    // Override default toString method for use in ListView adapter
    public String toString() {
        return this.requesterEmail + " is requesting a ride from " + this.pickupLocation + " to " +
                this.destinationLocation + " at " + this.rideTime + " for $" + this.rideTip + " dollars.";
    } // end of toStrong method

} // end of Ride class
