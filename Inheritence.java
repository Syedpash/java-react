public class Inheritence {
    public static void main(String[] args) {
        Boat boat = new Boat("Speed Craft", 50);
        boat.sail();

    }
}

/**
 * Sailabale
 */
interface Sailabale {

    public void sail();
}

class Vehicle {
    String brand;
    int speed;

    public Vehicle(String brand, int speed) {
        this.brand = brand;
        this.speed = speed;
    }

    public void move() {
        System.out.println("Vehicle is moving");
    }
}

class Car extends Vehicle {
    String color;

    public Car(String brand, int speed, String color) {
        super(brand, speed);
        this.color = color;
    }

    @Override
    public void move() {
        System.out.println("Car is moving");
    }
}

class Boat extends Vehicle implements Sailabale {
    public Boat(String brand, int speed) {
        super(brand, speed);
    }

    public void sail() {
        System.out.printf("Boat %s is sailing with speed %d%n", brand, speed);
    }
}
