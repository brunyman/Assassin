package com.me.tft_02.assassin.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.datatypes.ScoreType;
import com.me.tft_02.assassin.datatypes.Status;
import com.me.tft_02.assassin.datatypes.database.DatabaseType;
import com.me.tft_02.assassin.datatypes.database.PlayerStat;
import com.me.tft_02.assassin.datatypes.player.PlayerProfile;
import com.me.tft_02.assassin.util.LocationData;
import com.me.tft_02.assassin.util.Misc;

public final class FlatfileDatabaseManager implements DatabaseManager {
    private final HashMap<ScoreType, List<PlayerStat>> playerStatHash = new HashMap<ScoreType, List<PlayerStat>>();
    private final List<PlayerStat> powerLevels = new ArrayList<PlayerStat>();
    private long lastUpdate = 0;

    private final long UPDATE_WAIT_TIME = 600000L; // 10 minutes
    private final File usersFile;
    private static final Object fileWritingLock = new Object();

    protected FlatfileDatabaseManager() {
        usersFile = new File(Assassin.getUsersFilePath());
        checkStructure();
        updateLeaderboards();
    }

    public void purgeOldUsers() {
        int removedPlayers = 0;
        long currentTime = System.currentTimeMillis();

        Assassin.p.getLogger().info("Purging old users...");

        BufferedReader in = null;
        FileWriter out = null;
        String usersFilePath = Assassin.getUsersFilePath();

        // This code is O(n) instead of O(n²)
        synchronized (fileWritingLock) {
            try {
                in = new BufferedReader(new FileReader(usersFilePath));
                StringBuilder writer = new StringBuilder();
                String line = "";

                while ((line = in.readLine()) != null) {
                    String[] character = line.split(":");
                    String name = character[0];
                    long lastPlayed = 0;
                    boolean rewrite = false;
                    try {
                        lastPlayed = Long.parseLong(character[11]) * Misc.TIME_CONVERSION_FACTOR;
                    }
                    catch (NumberFormatException e) {
                    }
                    if (lastPlayed == 0) {
                        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
                        lastPlayed = player.getLastPlayed();
                        rewrite = true;
                    }

                    if (currentTime - lastPlayed > PURGE_TIME) {
                        removedPlayers++;
                    }
                    else {
                        if (rewrite) {
                            // Rewrite their data with a valid time
                            character[11] = Long.toString(lastPlayed);
                            String newLine = org.apache.commons.lang.StringUtils.join(character, ":");
                            writer.append(newLine).append("\r\n");
                        }
                        else {
                            writer.append(line).append("\r\n");
                        }
                    }
                }

                // Write the new file
                out = new FileWriter(usersFilePath);
                out.write(writer.toString());
            }
            catch (IOException e) {
                Assassin.p.getLogger().severe("Exception while reading " + usersFilePath + " (Are you sure you formatted it correctly?)" + e.toString());
            }
            finally {
                tryClose(in);
                tryClose(out);
            }
        }

        Assassin.p.getLogger().info("Purged " + removedPlayers + " users from the database.");
    }

    public boolean removeUser(String playerName) {
        boolean worked = false;

        BufferedReader in = null;
        FileWriter out = null;
        String usersFilePath = Assassin.getUsersFilePath();

        synchronized (fileWritingLock) {
            try {
                in = new BufferedReader(new FileReader(usersFilePath));
                StringBuilder writer = new StringBuilder();
                String line = "";

                while ((line = in.readLine()) != null) {
                    // Write out the same file but when we get to the player we want to remove, we skip his line.
                    if (!worked && line.split(":")[0].equalsIgnoreCase(playerName)) {
                        Assassin.p.getLogger().info("User found, removing...");
                        worked = true;
                        continue; // Skip the player
                    }

                    writer.append(line).append("\r\n");
                }

                out = new FileWriter(usersFilePath); // Write out the new file
                out.write(writer.toString());
            }
            catch (Exception e) {
                Assassin.p.getLogger().severe("Exception while reading " + usersFilePath + " (Are you sure you formatted it correctly?)" + e.toString());
            }
            finally {
                tryClose(in);
                tryClose(out);
            }
        }

        return worked;
    }

    public void saveUser(PlayerProfile profile) {
        String playerName = profile.getPlayerName();

        BufferedReader in = null;
        FileWriter out = null;
        String usersFilePath = Assassin.getUsersFilePath();

        synchronized (fileWritingLock) {
            try {
                // Open the file
                in = new BufferedReader(new FileReader(usersFilePath));
                StringBuilder writer = new StringBuilder();
                String line;

                // While not at the end of the file
                while ((line = in.readLine()) != null) {
                    // Read the line in and copy it to the output it's not the player we want to edit
                    if (!line.split(":")[0].equalsIgnoreCase(playerName)) {
                        writer.append(line).append("\r\n");
                    }
                    else {
                        // Otherwise write the new player information
                        // name:kills_current:kills_total:kills_highest:bounty_current:bounty_collected:bounty_highest:status:activetime:cooldown:location:last_played

                        writer.append(playerName).append(":");
                        writer.append(profile.getScore(ScoreType.KILLS_CURRENT)).append(":");
                        writer.append(profile.getScore(ScoreType.KILLS_TOTAL)).append(":");
                        writer.append(profile.getScore(ScoreType.KILLS_HIGHEST)).append(":");
                        writer.append(profile.getScore(ScoreType.BOUNTY_CURRENT)).append(":");
                        writer.append(profile.getScore(ScoreType.BOUNTY_COLLECTED)).append(":");
                        writer.append(profile.getScore(ScoreType.BOUNTY_HIGHEST)).append(":");
                        writer.append(profile.getStatus().toString()).append(":");
                        writer.append(profile.getActiveTime()).append(":");
                        writer.append(profile.getCooldown()).append(":");
                        Location location = profile.getLocation();
                        writer.append(location == null ? "null" : new LocationData(location).convertToString()).append(":");
                        writer.append(System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR).append(":");
                        writer.append("\r\n");
                    }
                }

                // Write the new file
                out = new FileWriter(usersFilePath);
                out.write(writer.toString());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                tryClose(in);
                tryClose(out);
            }
        }
    }

    public List<PlayerStat> readLeaderboard(String skillName, int pageNumber, int statsPerPage) {
//        updateLeaderboards();
//        List<PlayerStat> statsList = skillName.equalsIgnoreCase("all") ? powerLevels : playerStatHash.get(SkillType.getSkill(skillName));
//        int fromIndex = (Math.max(pageNumber, 1) - 1) * statsPerPage;
//
//        return statsList.subList(Math.min(fromIndex, statsList.size()), Math.min(fromIndex + statsPerPage, statsList.size()));
        return null;
    }

    public Map<String, Integer> readRank(String playerName) {
        updateLeaderboards();

        Map<String, Integer> skills = new HashMap<String, Integer>();

//        for (SkillType skill : SkillType.nonChildSkills()) {
//            skills.put(skill.name(), getPlayerRank(playerName, playerStatHash.get(skill)));
//        }

        skills.put("ALL", getPlayerRank(playerName, powerLevels));

        return skills;
    }

    public void newUser(String playerName) {
        BufferedWriter out = null;
        synchronized (fileWritingLock) {
            try {
                // Open the file to write the player
                out = new BufferedWriter(new FileWriter(Assassin.getUsersFilePath(), true));

                // Add the player to the end
                out.append(playerName).append(":");
                out.append("0:"); // KILLS_CURRENT
                out.append("0:"); // KILLS_TOTAL
                out.append("0:"); // KILLS_HIGHEST
                out.append("0:"); // BOUNTY_CURRENT
                out.append("0:"); // BOUNTY_COLLECTED
                out.append("0:"); // BOUNTY_HIGHEST
                out.append("NORMAL").append(":"); // STATUS
                out.append("0:"); // ACTIVE TIME
                out.append("0:"); // COOLDOWN
                out.append("null:"); // LOCATION
                out.append(String.valueOf(System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR)).append(":"); // LastLogin

                // Add more in the same format as the line above

                out.newLine();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                tryClose(out);
            }
        }
    }

    public PlayerProfile loadPlayerProfile(String playerName, boolean create) {
        BufferedReader in = null;
        String usersFilePath = Assassin.getUsersFilePath();

        synchronized (fileWritingLock) {
            try {
                // Open the user file
                in = new BufferedReader(new FileReader(usersFilePath));
                String line;

                while ((line = in.readLine()) != null) {
                    // Find if the line contains the player we want.
                    String[] character = line.split(":");

                    if (!character[0].equalsIgnoreCase(playerName)) {
                        continue;
                    }

                    PlayerProfile p = loadFromLine(character);
                    in.close();
                    return p;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                tryClose(in);
            }
        }

        if (create) {
            newUser(playerName);
            return new PlayerProfile(playerName, true);
        }
        return new PlayerProfile(playerName);
    }

    public void convertUsers(DatabaseManager destination) {
        BufferedReader in = null;
        String usersFilePath = Assassin.getUsersFilePath();

        synchronized (fileWritingLock) {
            try {
                // Open the user file
                in = new BufferedReader(new FileReader(usersFilePath));
                String line;

                while ((line = in.readLine()) != null) {
                    String[] character = line.split(":");

                    try {
                        destination.saveUser(loadFromLine(character));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                tryClose(in);
            }
        }
    }

    public boolean checkConnected() {
        // Not implemented
        return true;
    }

    public List<String> getStoredUsers() {
        ArrayList<String> users = new ArrayList<String>();
        BufferedReader in = null;
        String usersFilePath = Assassin.getUsersFilePath();

        synchronized (fileWritingLock) {
            try {
                // Open the user file
                in = new BufferedReader(new FileReader(usersFilePath));
                String line;

                while ((line = in.readLine()) != null) {
                    String[] character = line.split(":");
                    users.add(character[0]);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                tryClose(in);
            }
        }
        return users;
    }

    /**
     * Update the leader boards.
     */
    private void updateLeaderboards() {
//        // Only update FFS leaderboards every 10 minutes.. this puts a lot of strain on the server (depending on the size of the database) and should not be done frequently
//        if (System.currentTimeMillis() < lastUpdate + UPDATE_WAIT_TIME) {
//            return;
//        }
//
//        String usersFilePath = Assassin.getUsersFilePath();
//        lastUpdate = System.currentTimeMillis(); // Log when the last update was run
//        powerLevels.clear(); // Clear old values from the power levels
//
//        // Initialize lists
//        List<PlayerStat> mining = new ArrayList<PlayerStat>();
//        List<PlayerStat> woodcutting = new ArrayList<PlayerStat>();
//        List<PlayerStat> herbalism = new ArrayList<PlayerStat>();
//        List<PlayerStat> excavation = new ArrayList<PlayerStat>();
//        List<PlayerStat> acrobatics = new ArrayList<PlayerStat>();
//        List<PlayerStat> repair = new ArrayList<PlayerStat>();
//        List<PlayerStat> swords = new ArrayList<PlayerStat>();
//        List<PlayerStat> axes = new ArrayList<PlayerStat>();
//        List<PlayerStat> archery = new ArrayList<PlayerStat>();
//        List<PlayerStat> unarmed = new ArrayList<PlayerStat>();
//        List<PlayerStat> taming = new ArrayList<PlayerStat>();
//        List<PlayerStat> fishing = new ArrayList<PlayerStat>();
//
//        BufferedReader in = null;
//        // Read from the FlatFile database and fill our arrays with information
//        synchronized (fileWritingLock) {
//            try {
//                in = new BufferedReader(new FileReader(usersFilePath));
//                String line = "";
//
//                while ((line = in.readLine()) != null) {
//                    String[] data = line.split(":");
//                    String playerName = data[0];
//                    int powerLevel = 0;
//
//                    Map<SkillType, Integer> skills = getSkillMapFromLine(data);
//
//                    powerLevel += putStat(acrobatics, playerName, skills.get(SkillType.ACROBATICS));
//                    powerLevel += putStat(archery, playerName, skills.get(SkillType.ARCHERY));
//                    powerLevel += putStat(axes, playerName, skills.get(SkillType.AXES));
//                    powerLevel += putStat(excavation, playerName, skills.get(SkillType.EXCAVATION));
//                    powerLevel += putStat(fishing, playerName, skills.get(SkillType.FISHING));
//                    powerLevel += putStat(herbalism, playerName, skills.get(SkillType.HERBALISM));
//                    powerLevel += putStat(mining, playerName, skills.get(SkillType.MINING));
//                    powerLevel += putStat(repair, playerName, skills.get(SkillType.REPAIR));
//                    powerLevel += putStat(swords, playerName, skills.get(SkillType.SWORDS));
//                    powerLevel += putStat(taming, playerName, skills.get(SkillType.TAMING));
//                    powerLevel += putStat(unarmed, playerName, skills.get(SkillType.UNARMED));
//                    powerLevel += putStat(woodcutting, playerName, skills.get(SkillType.WOODCUTTING));
//
//                    putStat(powerLevels, playerName, powerLevel);
//                }
//            }
//            catch (Exception e) {
//                mcMMO.p.getLogger().severe("Exception while reading " + usersFilePath + " (Are you sure you formatted it correctly?)" + e.toString());
//            }
//            finally {
//                tryClose(in);
//            }
//        }
//
//        SkillComparator c = new SkillComparator();
//
//        Collections.sort(mining, c);
//        Collections.sort(woodcutting, c);
//        Collections.sort(repair, c);
//        Collections.sort(unarmed, c);
//        Collections.sort(herbalism, c);
//        Collections.sort(excavation, c);
//        Collections.sort(archery, c);
//        Collections.sort(swords, c);
//        Collections.sort(axes, c);
//        Collections.sort(acrobatics, c);
//        Collections.sort(taming, c);
//        Collections.sort(fishing, c);
//        Collections.sort(powerLevels, c);
//
//        playerStatHash.put(SkillType.MINING, mining);
//        playerStatHash.put(SkillType.WOODCUTTING, woodcutting);
//        playerStatHash.put(SkillType.REPAIR, repair);
//        playerStatHash.put(SkillType.UNARMED, unarmed);
//        playerStatHash.put(SkillType.HERBALISM, herbalism);
//        playerStatHash.put(SkillType.EXCAVATION, excavation);
//        playerStatHash.put(SkillType.ARCHERY, archery);
//        playerStatHash.put(SkillType.SWORDS, swords);
//        playerStatHash.put(SkillType.AXES, axes);
//        playerStatHash.put(SkillType.ACROBATICS, acrobatics);
//        playerStatHash.put(SkillType.TAMING, taming);
//        playerStatHash.put(SkillType.FISHING, fishing);
    }

    /**
     * Checks that the file is present and valid
     */
    private void checkStructure() {
        if (usersFile.exists()) {
            BufferedReader in = null;
            FileWriter out = null;
            String usersFilePath = Assassin.getUsersFilePath();

            synchronized (fileWritingLock) {
                try {
                    in = new BufferedReader(new FileReader(usersFilePath));
                    StringBuilder writer = new StringBuilder();
                    String line = "";
                    HashSet<String> players = new HashSet<String>();

                    while ((line = in.readLine()) != null) {
                        // Length checks depend on last character being ':'
                        if (line.charAt(line.length() - 1) != ':') {
                            line = line + ":";
                        }
                        String[] character = line.split(":");

                        // Prevent the same player from being present multiple times
                        if (!players.add(character[0])) {
                            continue;
                        }

                        // If they're valid, rewrite them to the file.
                        if (character.length > 11) {
                            writer.append(line).append("\r\n");
                        }
                        else if (character.length < 11) {
                            // Before Version 1.0 - Drop
                            Assassin.p.getLogger().warning("Dropping malformed line from database - " + line);
                        }
                    }

                    // Write the new file
                    out = new FileWriter(usersFilePath);
                    out.write(writer.toString());
                }
                catch (IOException e) {
                    Assassin.p.getLogger().severe("Exception while reading " + usersFilePath + " (Are you sure you formatted it correctly?)" + e.toString());
                }
                finally {
                    tryClose(in);
                    tryClose(out);
                }
            }
            return;
        }

        usersFile.getParentFile().mkdir();

        try {
            Assassin.p.debug("Creating assassin.users file...");
            new File(Assassin.getUsersFilePath()).createNewFile();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void tryClose(Closeable c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Integer getPlayerRank(String playerName, List<PlayerStat> statsList) {
        if (statsList == null) {
            return null;
        }

        int currentPos = 1;

        for (PlayerStat stat : statsList) {
            if (stat.name.equalsIgnoreCase(playerName)) {
                return currentPos;
            }

            currentPos++;
        }

        return null;
    }

    private int putStat(List<PlayerStat> statList, String playerName, int statValue) {
        statList.add(new PlayerStat(playerName, statValue));
        return statValue;
    }

    private class SkillComparator implements Comparator<PlayerStat> {
        @Override
        public int compare(PlayerStat o1, PlayerStat o2) {
            return (o2.statVal - o1.statVal);
        }
    }

    private PlayerProfile loadFromLine(String[] character) throws Exception {
        Map<ScoreType, Integer> scoreStats = new HashMap<ScoreType, Integer>();

        scoreStats.put(ScoreType.KILLS_CURRENT, Integer.valueOf(character[1]));
        scoreStats.put(ScoreType.KILLS_TOTAL, Integer.valueOf(character[2]));
        scoreStats.put(ScoreType.KILLS_HIGHEST, Integer.valueOf(character[3]));
        scoreStats.put(ScoreType.BOUNTY_CURRENT, Integer.valueOf(character[4]));
        scoreStats.put(ScoreType.BOUNTY_COLLECTED, Integer.valueOf(character[5]));
        scoreStats.put(ScoreType.BOUNTY_HIGHEST, Integer.valueOf(character[6]));

        Status status;

        try {
            status = Status.valueOf(character[7]);
        }
        catch (Exception e) {
            status = Status.NORMAL; // Shouldn't happen unless database is being tampered with
        }

        int activeTime = Integer.valueOf(character[8]);
        int cooldown = Integer.valueOf(character[9]);


        Location location;
        String locationData = character[10];
        if (locationData.equals("null")) {
            location = null;
        }
        else {
            location = LocationData.convertFromString(locationData).getLocation();
        }

        return new PlayerProfile(character[0], scoreStats, status, activeTime, cooldown, location);
    }

    public DatabaseType getDatabaseType() {
        return DatabaseType.FLATFILE;
    }
}
