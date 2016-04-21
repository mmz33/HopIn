package aub.hopin;

public class Vehicle {
    public int capacity;
    public String make;
    public String color;
    public String ownerEmail;

    public Vehicle(int capacity, String make, String color, String ownerEmail) {
        this.capacity = capacity;
        this.make = make;
        this.color = color;
        this.ownerEmail = ownerEmail;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getMake() {
        return make;
    }

   public String getColor() {
       return color;
   }
}
