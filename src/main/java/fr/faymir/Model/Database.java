package fr.faymir.Model;

import java.sql.*;
import java.util.Vector;

public class Database /*implements Observer*/ {
    public static final String FIRSTIME = "firstTime";
    public static final String NEWUSER = "newUser";
    private static final String host = "localhost";
    private static final String port = "3306";
    private static final String defaultDbName = "Clavardage";
    private static final String username = "root";
    private static final String password = "";
    private static Database instance = null;
    private static String url = "";
    //private static String uname = "";
    private static String privateKey = "";
    private static String publicKey = "";

    private Database(){
        setDbName(defaultDbName);
        if(!tableExist("users")) {
            createUserTable();
        }
    }

    private Connection connect() {
//        System.out.println("Loading driver...");
//
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            System.out.println("Driver loaded!");
//        } catch (ClassNotFoundException e) {
//            throw new IllegalStateException("Cannot find the driver in the classpath!", e);
//        }

        System.out.println("Connecting database...");

        Connection conn = null;
        try{
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected!");
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
        return conn;
    }

    public static Database getInstance(){
        if(instance == null){
            instance = new Database();
        }
        return instance;
    }
    public static void createNewDatabase() {

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    public static void createFriendsTable() {
//
//        // SQL statement for creating a new table
//        String sql =
//                "CREATE TABLE IF NOT EXISTS friends (\n"
//                + "	name VARCHAR(50) PRIMARY KEY,\n"
//                + "	ip VARCHAR(50) NOT NULL,\n"
//                + "	messages BLOB,\n"
//                + " last_message_index int not null default 0\n"
//                + ");";
//        executeQuery(sql);
//    }

    private boolean tableExist(String name){
        String sql = "SELECT 1 FROM " + name + " LIMIT 1";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private void createUserTable(){
        String sql =
                "CREATE TABLE IF NOT EXISTS users (\n"
                        + "	uniqueId VARCHAR(50) PRIMARY KEY,\n"
                        + "	name VARCHAR(50),\n"
                        + "	ip VARCHAR(50) NOT NULL,\n"
                        + "	lastSeen INT(20) NOT NULL\n"
                        + ");";
        executeQuery(sql);
        System.out.println("Users Table created");
    }

//    private void seedUserTable(){
//        String sql = "INSERT INTO user(name, privateKey, publicKey) VALUES(?,?,?)";
//        try (Connection conn = this.connect();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, uname);
//            pstmt.setString(2, rsa.getPrivateKey());
//            pstmt.setString(3, rsa.getPublicKey());
//            pstmt.executeUpdate();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            System.out.println(e.getMessage());
//        }
//    }

//    private void selectKeys(){
//        String sql = "SELECT privateKey, publicKey FROM user where name = ?";
//
//        try (Connection conn = this.connect();
//             PreparedStatement pstmt = conn.prepareStatement(sql)){
//            pstmt.setString(1, uname);
//            ResultSet rs    = pstmt.executeQuery();
//            // loop through the result set
//            while (rs.next()) {
//                privateKey = rs.getString("privateKey");
//                publicKey = rs.getString("publicKey");
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

//    public void initUser(String username){
//        uname = username;
//        if(!checkExist(username, "user")){
//            rsa = new Rsa(NEWUSER);
//            rsa.addObserver(this);
//            new Thread(rsa).start();
//        }
//        else
//            selectKeys();
//    }

    private static void executeQuery(String sql){
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
            System.out.println("Query ok");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Vector<ServerUser> selectAll(){
        String sql = "SELECT uniqueId, name, ip, lastSeen FROM users";
        Vector<ServerUser> users = new Vector<>();
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                String  uniqueId = rs.getString("uniqueId");
                String  name = rs.getString("name");
                String  ip = rs.getString("ip");
                long lastSeen = rs.getLong("lastSeen");

                users.add(new ServerUser(ip, false, name, uniqueId, lastSeen));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return users;
    }

    public ServerUser getUser(String name){
        String sql = "SELECT uniqueId, ip, lastSeen "
                + "FROM users WHERE name = ?";

        ServerUser u = null;
        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)){

            // set the value
            pstmt.setString(1,name);
            //
            ResultSet rs  = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                String  uniqueId = rs.getString("uniqueId");
                String  ip = rs.getString("ip");
                long lastSeen = rs.getLong("lastSeen");
                u = new ServerUser(ip, false, name, uniqueId, lastSeen);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return u;
    }

    public Object getbyName(String username, String column){
        String sql = "SELECT " + column
                + " FROM users WHERE name = ?";

        Object result = null;
        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)){

            // set the value
            pstmt.setString(1,username);
            //
            ResultSet rs  = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                System.out.println("username = [" + username + "], column = [" + column + "]");
                if (column.equalsIgnoreCase("ip"))
                    result = rs.getString("ip");
                else if (column.equalsIgnoreCase("lastSeen"))
                    result = rs.getLong("lastSeen");
                else if (column.equalsIgnoreCase("uniqueId")){
                    result = rs.getString("uniqueId");
                }

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public boolean checkExist(String name, String table){
        String sql = "SELECT ROWID from " + table + " where name = ?";

        Object result = null;
        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)){

            // set the value
            pstmt.setString(1,name);
            //
            ResultSet rs  = pstmt.executeQuery();
            if (rs.next())
                return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }


    public boolean checkUserExist(String uniquId){
        String sql = "SELECT 1 as 'exist' FROM users WHERE uniqueId = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uniquId);
            ResultSet rs  = pstmt.executeQuery();
            if (rs.next()){
                if(rs.getInt("exist") == 1)
                    return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public void insert(ServerUser u) {
        String sql = "INSERT INTO users(uniqueId, name, ip, lastSeen) VALUES(?,?,?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            fillStmtWithUser(u, pstmt);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public static void setDbName(String name) {
        Database.url = "jdbc:mysql://" + host + ":" + port + "/" + name;
    }

    public void update(String username, ServerUser user) {
        String sql = "UPDATE users SET name = ?, ip = ?, lastSeen = ? WHERE name = ? ";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getIp());
            pstmt.setLong(3, user.getLastSeen());
            pstmt.setString(4, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private void fillStmtWithUser(ServerUser user, PreparedStatement pstmt) throws SQLException {
        pstmt.setString(1, user.getUniqueId());
        pstmt.setString(2, user.getUsername());
        pstmt.setString(3, user.getIp());
        pstmt.setLong(4, user.getLastSeen());
    }

    public static String getPrivateKey() {
        return privateKey;
    }

    public static String getPublicKey() {
        return publicKey;
    }

//    @Override
//    public void update(Observable observable, Object o) {
//        if(observable.getClass() == Rsa.class){
//            String type =  (String) o;
//            if(type.equals(NEWUSER)){
//                seedUserTable();
//            }
//
//            selectKeys();
//
//        }
//    }
}