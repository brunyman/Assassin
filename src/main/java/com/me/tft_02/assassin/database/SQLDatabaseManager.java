package com.me.tft_02.assassin.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.bukkit.Location;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.config.Config;
import com.me.tft_02.assassin.datatypes.ScoreType;
import com.me.tft_02.assassin.datatypes.Status;
import com.me.tft_02.assassin.datatypes.database.DatabaseType;
import com.me.tft_02.assassin.datatypes.database.DatabaseUpdateType;
import com.me.tft_02.assassin.datatypes.database.PlayerStat;
import com.me.tft_02.assassin.datatypes.player.PlayerProfile;
import com.me.tft_02.assassin.runnables.database.SQLReconnectTask;
import com.me.tft_02.assassin.util.Misc;


public final class SQLDatabaseManager implements DatabaseManager {
    private String connectionString;
    private String tablePrefix = Config.getInstance().getMySQLTablePrefix();
    private Connection connection = null;

    // Scale waiting time by this much per failed attempt
    private final double SCALING_FACTOR = 40.0;

    // Minimum wait in nanoseconds (default 500ms)
    private final long MIN_WAIT = 500L * 1000000L;

    // Maximum time to wait between reconnects (default 5 minutes)
    private final long MAX_WAIT = 5L * 60L * 1000L * 1000000L;

    // How long to wait when checking if connection is valid (default 3 seconds)
    private final int VALID_TIMEOUT = 3;

    // When next to try connecting to Database in nanoseconds
    private long nextReconnectTimestamp = 0L;

    // How many connection attempts have failed
    private int reconnectAttempt = 0;

    protected SQLDatabaseManager() {
        checkConnected();
        checkStructure();
    }

    public void purgeOldUsers() {
    }

    public boolean removeUser(String playerName) {
        return true;
    }

    public void saveUser(PlayerProfile profile) {
        checkConnected();
        int userId = readId(profile.getPlayerName());
        if (userId == -1) {
            newUser(profile.getPlayerName());
            userId = readId(profile.getPlayerName());
            if (userId == -1) {
                Assassin.p.getLogger().log(Level.WARNING, "Failed to save user " + profile.getPlayerName());
                return;
            }
        }
    }

    public List<PlayerStat> readLeaderboard(String skillName, int pageNumber, int statsPerPage) {
        List<PlayerStat> stats = new ArrayList<PlayerStat>();

        return stats;
    }

    public Map<String, Integer> readRank(String playerName) {
        Map<String, Integer> skills = new HashMap<String, Integer>();

        return skills;
    }

    public void newUser(String playerName) {
        checkConnected();
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement("INSERT INTO " + tablePrefix + "users (user, lastlogin) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, playerName);
            statement.setLong(2, System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR);
            statement.execute();

            int id = readId(playerName);
            writeMissingRows(id);
        }
        catch (SQLException ex) {
            printErrors(ex);
        }
        finally {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }
    }

    public PlayerProfile loadPlayerProfile(String playerName, boolean create) {
        checkConnected();
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(
                    "SELECT ");
            statement.setString(1, playerName);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                try {
                    PlayerProfile ret = loadFromResult(playerName, result);
                    result.close();
                    return ret;
                }
                catch (SQLException e) {
                }
            }
            result.close();
        }
        catch (SQLException ex) {
            printErrors(ex);
        }
        finally {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }

        // Problem, nothing was returned

        // First, read User Id - this is to check for orphans

        int id = readId(playerName);

        if (id == -1) {
            // There is no such user
            if (create) {
                newUser(playerName);
            }

            return new PlayerProfile(playerName, create);
        }
        // There is such a user
        writeMissingRows(id);
        // Retry, and abort on re-failure
        return loadPlayerProfile(playerName, false);
    }

    public void convertUsers(DatabaseManager destination) {
        checkConnected();
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(
                    "SELECT "
                            + "WHERE u.user = ?");
            List<String> usernames = getStoredUsers();
            ResultSet result = null;
            for (String playerName : usernames) {
                statement.setString(1, playerName);
                try {
                    result = statement.executeQuery();
                    result.next();
                    destination.saveUser(loadFromResult(playerName, result));
                    result.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }
        catch (SQLException e) {
            printErrors(e);
        }
        finally {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }

    }

    /**
     * Check connection status and re-establish if dead or stale.
     * <p/>
     * If the very first immediate attempt fails, further attempts
     * will be made in progressively larger intervals up to MAX_WAIT
     * intervals.
     * <p/>
     * This allows for MySQL to time out idle connections as needed by
     * server operator, without affecting Assassin, while still providing
     * protection against a database outage taking down Bukkit's tick
     * processing loop due to attempting a database connection each
     * time Assassin needs the database.
     *
     * @return the boolean value for whether or not we are connected
     */
    public boolean checkConnected() {
        boolean isClosed = true;
        boolean isValid = false;
        boolean exists = (connection != null);

        // If we're waiting for server to recover then leave early
        if (nextReconnectTimestamp > 0 && nextReconnectTimestamp > System.nanoTime()) {
            return false;
        }

        if (exists) {
            try {
                isClosed = connection.isClosed();
            }
            catch (SQLException e) {
                isClosed = true;
                e.printStackTrace();
                printErrors(e);
            }

            if (!isClosed) {
                try {
                    isValid = connection.isValid(VALID_TIMEOUT);
                }
                catch (SQLException e) {
                    // Don't print stack trace because it's valid to lose idle connections to the server and have to restart them.
                    isValid = false;
                }
            }
        }

        // Leave if all ok
        if (exists && !isClosed && isValid) {
            // Housekeeping
            nextReconnectTimestamp = 0;
            reconnectAttempt = 0;
            return true;
        }

        // Cleanup after ourselves for GC and MySQL's sake
        if (exists && !isClosed) {
            try {
                connection.close();
            }
            catch (SQLException ex) {
                // This is a housekeeping exercise, ignore errors
            }
        }

        // Try to connect again
        connect();

        // Leave if connection is good
        try {
            if (connection != null && !connection.isClosed()) {
                // Schedule a database save if we really had an outage
                if (reconnectAttempt > 1) {
                    new SQLReconnectTask().runTaskLater(Assassin.p, 5);
                }
                nextReconnectTimestamp = 0;
                reconnectAttempt = 0;
                return true;
            }
        }
        catch (SQLException e) {
            // Failed to check isClosed, so presume connection is bad and attempt later
            e.printStackTrace();
            printErrors(e);
        }

        reconnectAttempt++;
        nextReconnectTimestamp = (long) (System.nanoTime() + Math.min(MAX_WAIT, (reconnectAttempt * SCALING_FACTOR * MIN_WAIT)));
        return false;
    }

    public List<String> getStoredUsers() {
        checkConnected();
        ArrayList<String> users = new ArrayList<String>();
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet result = stmt.executeQuery("SELECT user FROM " + tablePrefix + "users");
            while (result.next()) {
                users.add(result.getString("user"));
            }
            result.close();
        }
        catch (SQLException e) {
            printErrors(e);
        }
        finally {
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }
        return users;
    }

    /**
     * Attempt to connect to the mySQL database.
     */
    private void connect() {
        connectionString = "jdbc:mysql://" + Config.getInstance().getMySQLServerName() + ":" + Config.getInstance().getMySQLServerPort() + "/" + Config.getInstance().getMySQLDatabaseName();

        try {
            Assassin.p.getLogger().info("Attempting connection to MySQL...");

            // Force driver to load if not yet loaded
            Class.forName("com.mysql.jdbc.Driver");
            Properties connectionProperties = new Properties();
            connectionProperties.put("user", Config.getInstance().getMySQLUserName());
            connectionProperties.put("password", Config.getInstance().getMySQLUserPassword());
            connectionProperties.put("autoReconnect", "false");
            connectionProperties.put("maxReconnects", "0");
            connection = DriverManager.getConnection(connectionString, connectionProperties);

            Assassin.p.getLogger().info("Connection to MySQL was a success!");
        }
        catch (SQLException ex) {
            connection = null;

            if (reconnectAttempt == 0 || reconnectAttempt >= 11) {
                Assassin.p.getLogger().severe("Connection to MySQL failed!");
                printErrors(ex);
            }
        }
        catch (ClassNotFoundException ex) {
            connection = null;

            if (reconnectAttempt == 0 || reconnectAttempt >= 11) {
                Assassin.p.getLogger().severe("MySQL database driver not found!");
            }
        }
    }

    /**
     * Checks that the database structure is present and correct
     */
    private void checkStructure() {
        for (DatabaseUpdateType updateType : DatabaseUpdateType.values()) {
            checkDatabaseStructure(updateType);
        }
    }

    /**
     * Check database structure for missing values.
     *
     * @param update Type of data to check updates for
     */
    private void checkDatabaseStructure(DatabaseUpdateType update) {
    }

    /**
     * Attempt to write the SQL query.
     *
     * @param sql Query to write.
     *
     * @return true if the query was successfully written, false otherwise.
     */
    private boolean write(String sql) {
        if (!checkConnected()) {
            return false;
        }

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.executeUpdate();
            return true;
        }
        catch (SQLException ex) {
            if (!sql.equalsIgnoreCase("ALTER TABLE `" + tablePrefix + "users` DROP COLUMN `party` ;")) {
                printErrors(ex);
            }
            return false;
        }
        finally {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }
    }

    /**
     * Returns the number of rows affected by either a DELETE or UPDATE query
     *
     * @param sql SQL query to execute
     *
     * @return the number of rows affected
     */
    private int update(String sql) {
        int rows = 0;

        if (checkConnected()) {
            PreparedStatement statement = null;

            try {
                statement = connection.prepareStatement(sql);
                rows = statement.executeUpdate();
            }
            catch (SQLException ex) {
                printErrors(ex);
            }
            finally {
                if (statement != null) {
                    try {
                        statement.close();
                    }
                    catch (SQLException e) {
                        // Ignore
                    }
                }
            }
        }

        return rows;
    }

    /**
     * Read SQL query.
     *
     * @param sql SQL query to read
     *
     * @return the rows in this SQL query
     */
    private HashMap<Integer, ArrayList<String>> read(String sql) {
        HashMap<Integer, ArrayList<String>> rows = new HashMap<Integer, ArrayList<String>>();

        if (checkConnected()) {
            PreparedStatement statement = null;
            ResultSet resultSet;

            try {
                statement = connection.prepareStatement(sql);
                resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    ArrayList<String> column = new ArrayList<String>();

                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        column.add(resultSet.getString(i));
                    }

                    rows.put(resultSet.getRow(), column);
                }
            }
            catch (SQLException ex) {
                printErrors(ex);
            }
            finally {
                if (statement != null) {
                    try {
                        statement.close();
                    }
                    catch (SQLException e) {
                        // Ignore
                    }
                }
            }
        }

        return rows;
    }

    /**
     * Get the Integer. Only return first row / first field.
     *
     * @param statement SQL query to execute
     *
     * @return the value in the first row / first field
     */
    private int readInt(PreparedStatement statement) {
        int result = -1;

        if (checkConnected()) {
            ResultSet resultSet = null;

            try {
                resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    result = resultSet.getInt(1);
                }
            }
            catch (SQLException ex) {
                printErrors(ex);
            }
            finally {
                if (statement != null) {
                    try {
                        statement.close();
                    }
                    catch (SQLException e) {
                        // Ignore
                    }
                }
            }
        }

        return result;
    }

    private void writeMissingRows(int id) {
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement("INSERT IGNORE INTO " + tablePrefix + "experience (user_id) VALUES (?)");
            statement.setInt(1, id);
            statement.execute();
            statement.close();

            statement = connection.prepareStatement("INSERT IGNORE INTO " + tablePrefix + "skills (user_id) VALUES (?)");
            statement.setInt(1, id);
            statement.execute();
            statement.close();

            statement = connection.prepareStatement("INSERT IGNORE INTO " + tablePrefix + "cooldowns (user_id) VALUES (?)");
            statement.setInt(1, id);
            statement.execute();
            statement.close();
        }
        catch (SQLException ex) {
            printErrors(ex);
        }
        finally {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }
    }

    private void saveIntegers(String sql, int... args) {
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(sql);
            int i = 1;

            for (int arg : args) {
                statement.setInt(i++, arg);
            }

            statement.execute();
        }
        catch (SQLException ex) {
            printErrors(ex);
        }
        finally {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }
    }

    private void saveLongs(String sql, int id, long... args) {
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(sql);
            int i = 1;

            for (long arg : args) {
                statement.setLong(i++, arg);
            }

            statement.setInt(i++, id);
            statement.execute();
        }
        catch (SQLException ex) {
            printErrors(ex);
        }
        finally {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }
    }

    /**
     * Retrieve the database id for a player
     *
     * @param playerName The name of the user to retrieve the id for
     *
     * @return the requested id or -1 if not found
     */
    private int readId(String playerName) {
        int id = -1;

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT id FROM " + tablePrefix + "users WHERE user = ?");
            statement.setString(1, playerName);
            id = readInt(statement);
        }
        catch (SQLException ex) {
            printErrors(ex);
        }

        return id;
    }

    private void saveLogin(int id, long login) {
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement("UPDATE " + tablePrefix + "users SET lastlogin = ? WHERE id = ?");
            statement.setLong(1, login);
            statement.setInt(2, id);
            statement.execute();
        }
        catch (SQLException ex) {
            printErrors(ex);
        }
        finally {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }
    }

    private void saveHuds(int userId, String hudType, String mobHealthBar) {
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement("UPDATE " + tablePrefix + "huds SET hudtype = ?, mobhealthbar = ? WHERE user_id = ?");
            statement.setString(1, hudType);
            statement.setString(2, mobHealthBar);
            statement.setInt(3, userId);
            statement.execute();
        }
        catch (SQLException ex) {
            printErrors(ex);
        }
        finally {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }
    }

    private PlayerProfile loadFromResult(String playerName, ResultSet result) throws SQLException {
        Map<ScoreType, Integer> scoreStats = new HashMap<ScoreType, Integer>();

        final int OFFSET_SKILLS = 0; // TODO update these numbers when the query changes (a new skill is added)
        final int OFFSET_XP = 12;
        final int OFFSET_DATS = 24;
        final int OFFSET_OTHER = 36;

        Status status;

        try {
            status = Status.valueOf(result.getString(OFFSET_OTHER + 1));
        }
        catch (Exception e) {
            status = Status.NORMAL; // Shouldn't happen unless database is being tampered with
        }

        int activeTime = Integer.valueOf(result.getString(OFFSET_OTHER + 1));
        int cooldown = Integer.valueOf(result.getString(OFFSET_OTHER + 1));
        Location location = null; //TODO getlocation

        return new PlayerProfile(playerName, scoreStats, status, activeTime, cooldown, location);
    }

    private void printErrors(SQLException ex) {
        Assassin.p.getLogger().severe("SQLException: " + ex.getMessage());
        Assassin.p.getLogger().severe("SQLState: " + ex.getSQLState());
        Assassin.p.getLogger().severe("VendorError: " + ex.getErrorCode());
    }

    public DatabaseType getDatabaseType() {
        return DatabaseType.SQL;
    }
}
