package dao;




import entity.Car;
import exception.DaoException;
import util.ConnectionManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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

    private static final String FIND_ALL_SQL = """
            SELECT id,
             carname,
             color,
             price
            FROM car
            """;


    private CarDao() {
    }
// вывод всех записей из таблицы
    public List<Car> FindAll(){
        try (var connection = ConnectionManager.open();
        var prepareStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = prepareStatement.executeQuery();
            List<Car> cars = new ArrayList<>();
            Car car = null;
            while (resultSet.next()){
                car = new Car(resultSet.getInt("id"),
                        resultSet.getString("carname"),
                        resultSet.getString("color"),
                        resultSet.getInt("price"));
                cars.add(car);
            }
            return cars;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }


    //Тут он сказал что можно ломбоом заменить все кэтч блоки надо посмотреть
    // так же этот метод работает в связке с нижним методом !!! ВАЖНО
    public Optional<Car> findById(Integer id) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            prepareStatement.setInt(1, id);

            var resultSet = prepareStatement.executeQuery();
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
             var prepareStatement = connection.prepareStatement(UPDATE_SQL)) {
            prepareStatement.setString(1, car.getCarname());
            prepareStatement.setString(2, car.getColor());
            prepareStatement.setInt(3, car.getPrice());
            prepareStatement.setInt(4, car.getId());

            prepareStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    // вставить в таблицу новый объект
    public Car save(Car car) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            prepareStatement.setString(1, car.getCarname());
            prepareStatement.setString(2, car.getColor());
            prepareStatement.setInt(3, car.getPrice());

            prepareStatement.executeUpdate();

            var generatedKeys = prepareStatement.getGeneratedKeys();
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
             var prepareStatement = connection.prepareStatement(DELETE_SQL)) {
            prepareStatement.setInt(1, id);

            return prepareStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public static CarDao getInstance() {
        return INSTANCE;
    }
}
