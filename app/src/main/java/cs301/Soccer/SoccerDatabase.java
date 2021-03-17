package cs301.Soccer;

import android.util.Log;
import cs301.Soccer.soccerPlayer.SoccerPlayer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Soccer player database -- presently, all dummied up
 *
 * @author Connor Morgan
 * @version *** put date of completion here ***
 *
 */
public class SoccerDatabase implements SoccerDB {

    // dummied up variable; you will need to change this
    private Hashtable<String, SoccerPlayer> database = new Hashtable<String, SoccerPlayer>();

    /**
     * add a player
     *
     * @see SoccerDB#addPlayer(String, String, int, String)
     */
    @Override
    public boolean addPlayer(String firstName, String lastName,
                             int uniformNumber, String teamName) {
        String key = firstName + "#" + lastName;
        if(database.containsKey(key)){
            return false;
        }
        database.put(key,new SoccerPlayer(firstName,lastName,uniformNumber,teamName));
        return true;
    }

    /**
     * remove a player
     *
     * @see SoccerDB#removePlayer(String, String)
     */
    @Override
    public boolean removePlayer(String firstName, String lastName) {

        String key = firstName + "#" + lastName;

        if(database.get(key) != null){
            database.remove(key);
            return true;
        }
        return false;
    }

    /**
     * look up a player
     *
     * @see SoccerDB#getPlayer(String, String)
     */
    @Override
    public SoccerPlayer getPlayer(String firstName, String lastName) {

        String key = firstName + "#" + lastName;
        return  database.get(key);


    }

    /**
     * increment a player's goals
     *
     * @see SoccerDB#bumpGoals(String, String)
     */
    @Override
    public boolean bumpGoals(String firstName, String lastName) {

        if(getPlayer(firstName,lastName) != null) {
            getPlayer(firstName, lastName).bumpGoals();
            return true;
        }
        return false;
    }

    /**
     * increment a player's yellow cards
     *
     * @see SoccerDB#bumpYellowCards(String, String)
     */
    @Override
    public boolean bumpYellowCards(String firstName, String lastName) {

        if(getPlayer(firstName,lastName) != null) {
            getPlayer(firstName, lastName).bumpYellowCards();
            return true;
        }

        return false;
    }

    /**
     * increment a player's red cards
     *
     * @see SoccerDB#bumpRedCards(String, String)
     */
    @Override
    public boolean bumpRedCards(String firstName, String lastName) {

        if(getPlayer(firstName,lastName) != null) {
            getPlayer(firstName, lastName).bumpRedCards();
            return true;
        }
        return false;
    }

    /**
     * tells the number of players on a given team
     *
     * @see SoccerDB#numPlayers(String)
     */
    @Override
    // report number of players on a given team (or all players, if null)
    public int numPlayers(String teamName) {
        int c = 0;
        if(teamName == null){
            c = database.size();
        }
        else{
            Enumeration<SoccerPlayer> enu = database.elements();
            while(enu.hasMoreElements())
                if(enu.nextElement().getTeamName().equals(teamName))
                    c++;
        }
        return c;
    }

    /**
     * gives the nth player on a the given team
     *
     * @see SoccerDB#playerIndex(int, String)
     */
    // get the nTH player
    @Override
    public SoccerPlayer playerIndex(int idx, String teamName) {
        int c = 0;
        Enumeration<SoccerPlayer> enu = database.elements();
        if(idx >= database.size()){
            return null;
        }
        else{
            SoccerPlayer player;
            while(enu.hasMoreElements()){
                if(teamName == null){
                    if(c == idx)
                        return enu.nextElement();
                    enu.nextElement();
                    c++;
                }
                else{
                    player = enu.nextElement();
                    if(player.getTeamName().equals(teamName)){
                        if(c == idx)
                            return player;
                        c++;
                    }

                }
            }
            //ran out of soccer players to check;
            return null;
        }
    }

    /**
     * reads database data from a file
     *
     * @see SoccerDB#readData(java.io.File)
     */
    // read data from file
    @Override
    public boolean readData(File file) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        while(scanner.hasNextLine()){

            SoccerPlayer player = new SoccerPlayer(scanner.nextLine(),scanner.nextLine(),
                    Integer.parseInt(scanner.nextLine()),scanner.nextLine());

            database.put(player.getFirstName() + "#" + player.getLastName(),player);

            for(int i = 0; i < Integer.parseInt(scanner.nextLine()); i++){
                bumpRedCards(player.getFirstName(),player.getLastName());
            }
            for(int i = 0; i < Integer.parseInt(scanner.nextLine()); i++){
                bumpYellowCards(player.getFirstName(),player.getLastName());
            }
            for(int i = 0; i < Integer.parseInt(scanner.nextLine()); i++){
                bumpGoals(player.getFirstName(),player.getLastName());
            }

        }

        scanner.close();
        return true;
    }

    /**
     * write database data to a file
     *
     * @see SoccerDB#writeData(java.io.File)
     */
    // write data to file
    @Override
    public boolean writeData(File file) {

        //open file
        FileOutputStream outputStream = null;
        try{
            outputStream = new FileOutputStream(file);

        }
        catch (FileNotFoundException fnfe){
            System.out.println("Error opening the file: " + file.getName());
            return false;
        }

        try {
            Enumeration<SoccerPlayer> enu = database.elements();
            SoccerPlayer p = null;
            while(enu.hasMoreElements()){
                p = enu.nextElement();
                outputStream.write(logString(p.getFirstName() + "\n").getBytes());
                outputStream.write(logString(p.getLastName() + "\n").getBytes());
                outputStream.write(logString(p.getUniform() + "\n").getBytes());
                outputStream.write(logString(p.getTeamName() + "\n").getBytes());
                outputStream.write(logString(p.getRedCards() + "\n").getBytes());
                outputStream.write(logString(p.getYellowCards() + "\n").getBytes());
                outputStream.write(logString(p.getGoals() + "\n").getBytes());


            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * helper method that logcat-logs a string, and then returns the string.
     * @param s the string to log
     * @return the string s, unchanged
     */
    private String logString(String s) {
        Log.i("write string", s);
        return s;
    }

    /**
     * returns the list of team names in the database
     *
     * @see cs301.Soccer.SoccerDB#getTeams()
     */
    // return list of teams
    @Override
    public HashSet<String> getTeams() {

        HashSet<String> teams= new HashSet<String>();

        Enumeration<SoccerPlayer> enu = database.elements();
        while(enu.hasMoreElements()){
            String teamname = enu.nextElement().getTeamName();
            if(!teams.contains(teamname))
                teams.add(teamname);
        }
        return new HashSet<String>();
    }

    /**
     * Helper method to empty the database and the list of teams in the spinner;
     * this is faster than restarting the app
     */
    public boolean clear() {
        if(database != null) {
            database.clear();
            return true;
        }
        return false;
    }
}
