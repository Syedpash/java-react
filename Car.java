public class Car {
    String brand;
    int speed;
    String color;

    public Car(String brand, int speed, String color) {
        this.brand = brand;
        this.speed = speed;
        this.color = color;
    }

    public void describe() {
        System.out.println("Brand: " + brand + ", Speed: " + speed + ", Color: " + color);
    }

    public int accelerate(int a) {
        return speed += a;
    }

    public static void main(String[] args) {
        Car car1 = new Car("BMW", 100, "red");
        Car car2 = new Car("Audi", 120, "black");
        car1.describe();
        car2.accelerate(20);
        car2.describe();
    }
}
