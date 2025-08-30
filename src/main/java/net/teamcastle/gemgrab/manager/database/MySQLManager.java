package net.teamcastle.gemgrab.manager.database;

import java.sql.*;
import java.util.UUID;

public class MySQLManager {

    private final String host, database, username, password;
    private final int port;
    private Connection connection;

    public MySQLManager(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public void connect() throws SQLException {
        if (connection != null && !connection.isClosed()) return;
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false";
        connection = DriverManager.getConnection(url, username, password);
        createTable();
    }

    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS player_stats (" +
                "uuid VARCHAR(36) PRIMARY KEY," +
                "kills INT DEFAULT 0," +
                "deaths INT DEFAULT 0," +
                "wins INT DEFAULT 0)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }

    public void createPlayer(UUID uuid) throws SQLException {
        String check = "SELECT * FROM player_stats WHERE uuid=?";
        try (PreparedStatement ps = connection.prepareStatement(check)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                String insert = "INSERT INTO player_stats (uuid) VALUES (?)";
                try (PreparedStatement insertPs = connection.prepareStatement(insert)) {
                    insertPs.setString(1, uuid.toString());
                    insertPs.executeUpdate();
                }
            }
        }
    }

    public void addKill(UUID uuid) throws SQLException {
        updateStat(uuid, "kills");
    }

    public void addDeath(UUID uuid) throws SQLException {
        updateStat(uuid, "deaths");
    }

    public void addWin(UUID uuid) throws SQLException {
        updateStat(uuid, "wins");
    }

    private void updateStat(UUID uuid, String column) throws SQLException {
        createPlayer(uuid);
        String sql = "UPDATE player_stats SET " + column + " = " + column + " + 1 WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        }
    }

    public int getStat(UUID uuid, String column) throws SQLException {
        createPlayer(uuid);
        String sql = "SELECT " + column + " FROM player_stats WHERE uuid=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(column);
            }
        }
        return 0;
    }
}
