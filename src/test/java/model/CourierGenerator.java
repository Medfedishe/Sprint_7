package model;

public class CourierGenerator {

    public static Courier getRandomCourier() {
        long timestamp = System.currentTimeMillis();
        return new Courier("courier_" + timestamp, "P@ssword123", "Egor_" + timestamp);
    }
}
