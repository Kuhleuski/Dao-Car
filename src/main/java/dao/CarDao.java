package dao;

import entity.Car;
import exception.DaoException;
import util.ConnectionManager;


import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class CarDao {
    private static final CarDao INSTANCE = new CarDao();
    private static final String DELETE_SQL = """
            DELETE FROM car
            where id = ?
            """;

    private static final String SAVE_SQL = """
            INSERT INTO car(carname, color, price) 
            VALUES (?,?,?);
            """;

    private static final String UPDATE_SQL = """
            UPDATE car
            SET carname = ?,
                color = ?,
                price = ?
                WHERE id = ?
            """;

    private static final String FIND_BY_ID_SQL = """
             SELECT id,
             carname,
             color,
             price
            FROM car
            WHERE id = ?
            """;

    private CarDao() {
    }

    //Тут он сказал что можно ломбоом заменить все кэтч блоки надо посмотреть
    // так же этот метод работает в связке с нижним методом !!! ВАЖНО
    public Optional<Car> findById(Integer id) {
        try (var connection = ConnectionManager.open();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);

            var resultSet = preparedStatement.executeQuery();
            Car car = null;
            if (resultSet.next()) {
                car = new Car(
                        resultSet.getInt("id"),
                        resultSet.getString("carname"),
                        resultSet.getString("color"),
                        resultSet.getInt("price")

                );
            }
            return Optional.ofNullable(car);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    // метод который сверху используется для этого метода в связке
    public void update(Car car) {
        try (var connection = ConnectionManager.open();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, car.getCarname());
            preparedStatement.setString(2, car.getColor());
            preparedStatement.setInt(3, car.getPrice());
            preparedStatement.setInt(4, car.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    // вставить в таблицу новый объект
    public Car save(Car car) {
        try (var connection = ConnectionManager.open();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, car.getCarname());
            preparedStatement.setString(2, car.getColor());
            preparedStatement.setInt(3, car.getPrice());

            preparedStatement.executeUpdate();

            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getInt(1)); // тут проблема была, не принимает название колонки
            }
            return car;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    // удалить из таблицы запись по id
    public boolean delete(Integer id) {
        try (var connection = ConnectionManager.open();
             var preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setInt(1, id);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public static CarDao getInstance() {
        return INSTANCE;
    }
}
