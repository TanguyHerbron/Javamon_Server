package fr.ensim.lemeeherbron;

import java.sql.*;

public class SQLiteHandler {
    private String bddPath;
    private Connection bdd;
    public SQLiteHandler(String bddPath){
        this.bddPath = bddPath;
        connect();
        init();
    }

    private void connect() {
        try {
            String url = "jdbc:sqlite:" + bddPath;
            // create a connection to the database
            bdd = DriverManager.getConnection(url);
            if(bdd!=null)
            {
                System.out.println("Connection to SQLite has been established.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void close()
    {
        try {
            if (bdd != null) {
                bdd.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void init()
    {
        String sqlRequest = "CREATE TABLE IF NOT EXISTS user (Login VARVHAR(50) PRIMARY KEY, password VARVHAR(50) NOT NULL)";
        try {
            Statement stmt = bdd.createStatement();
            stmt.execute(sqlRequest);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public synchronized void addUser(String login, String password)
    {
        String sql = "INSERT INTO user(login,password) VALUES(?,?)";

        try {
            PreparedStatement pstmt = bdd.prepareStatement(sql);
            pstmt.setString(1, login);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
        } catch (SQLException e) {
        e.printStackTrace();
        }
    }

    public synchronized boolean verifyUser(String login, String password)
    {
        boolean isValid = false;
        ResultSet rs;

        String sql = "SELECT * FROM user WHERE login=?";

        try {
            PreparedStatement pstmt = bdd.prepareStatement(sql);
            pstmt.setString(1, login);
            rs = pstmt.executeQuery();

            if(!rs.isClosed()) {
                if (password.equals(rs.getString("password"))) {
                    isValid = true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isValid;
    }
}
