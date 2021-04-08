package server;

import java.sql.*;

public class AuthService {
    private static Connection connection; // характеризует соединение с какой-то базой данных
    private static Statement statement; //запрос, из которого получаем результирующее значение (установка соединения с БД)

    public static void connect(){
        try {
            Class.forName("org.sqlite.JDBC"); // резервируем драйвер для работы с базой sql lite
            connection = DriverManager.getConnection("jdbc:sqlite:main.db");
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    public static String getNicknameByLoginAndPassword(String login, String password) {
        //формируем запрос:
        String query = String.format("select nickname from users where login='%s' and password='%s'", login, password);
        //отправляем запрос:
        try {
            ResultSet rs = statement.executeQuery(query); // возвращ выборку через селект
            if (rs.next()){
                return rs.getString("nickname");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public static void disconnect(){
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

}
