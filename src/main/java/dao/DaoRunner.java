package dao;

import entity.Car;

public class DaoRunner {
    public static void main(String[] args) {

        //saveTest();
        //deleteTest();

        var carDao = CarDao.getInstance();
        var maybeCar = carDao.findById(2);
        System.out.println(maybeCar);

        maybeCar.ifPresent(car -> {
            car.setPrice(11244);
            carDao.update(car);
        });


    }

    private static void deleteTest() {
        var carDao = CarDao.getInstance();
        var deleteResult = carDao.delete(4);
        System.out.println(deleteResult);
    }


    private static void saveTest() {
        var carDao = CarDao.getInstance();

        var car = new Car();
        car.setCarname("new Lada");
        car.setColor("new Red");
        car.setPrice(5000);
        var savedCar = carDao.save(car);
        System.out.println(savedCar);
    }
}
