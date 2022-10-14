package edu.jsu.mcis.cs310;

import java.sql.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class Database {
    
    private final Connection connection;
    
    private final int TERMID_SP22 = 1;
    private ResultSet ResultSet;
    
    /* CONSTRUCTOR */

    public Database(String username, String password, String address) {
        
        this.connection = openConnection(username, password, address);
        
    }
    
    /* PUBLIC METHODS */

    public String getSectionsAsJSON(int termid, String subjectid, String num) {
        
        String result = null;
        
        // INSERT YOUR CODE HERE
        
        
        try{        
        String query = "SELECT * FROM section WHERE termid = ? AND subjectid = ? AND num = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, termid);
        stmt.setString(2, subjectid);
        stmt.setString(3, num);
        ResultSet results = stmt.executeQuery();
        result = getResultSetAsJSON(results);
        
        
        
        
    }
        catch (Exception e) { e.printStackTrace(); }
        return result;
    }
    
    public int register(int studentid, int termid, int crn) {
        
        int result = 0;
        
        // INSERT YOUR CODE HERE
        try{
        String query = "INSERT INTO registration (studentid, termid, crn) VALUES (?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, studentid);
        stmt.setInt(2, termid);
        stmt.setInt(3, crn);
        result = stmt.executeUpdate();
        }
        catch (Exception e) { e.printStackTrace(); }
        return result;
        
    }

    public int drop(int studentid, int termid, int crn) {
        
        int result = 0;
        
        // INSERT YOUR CODE HERE
        try{
        String query = "DELETE FROM registration WHERE studentid = ? AND termid = ? AND crn = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, studentid);
        stmt.setInt(2, termid);
        stmt.setInt(3, crn);
        result = stmt.executeUpdate();
        }
        catch (Exception e) { e.printStackTrace(); }
        return result;
        
    }
    
    public int withdraw(int studentid, int termid) {
        
        int result = 0;
        
        // INSERT YOUR CODE HERE
        try{
            String query = "DELETE FROM registration WHERE studentid = ? AND termid = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, studentid);
            stmt.setInt(2, termid);
            result = stmt.executeUpdate();
        }
        catch (Exception e) { e.printStackTrace(); }
        return result;
        
    }
    
    public String getScheduleAsJSON(int studentid, int termid) {
        
        String result = null;
        
        // INSERT YOUR CODE HERE
        try{
            String query = "SELECT r.studentid, r.termid, s.scheduletypeid, s.instructor, s.num, s.start, s.days, s.section, s.end, s.where, s.crn, s.subjectid FROM registration AS r, section AS s, term AS t WHERE r.studentid = ? AND r.termid = ? AND r.crn = s.crn AND s.termid = t.id";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, studentid);
            stmt.setInt(2, termid);
            ResultSet results = stmt.executeQuery();
            result = getResultSetAsJSON(results);
        }
        catch (Exception e) { e.printStackTrace(); }
            
        return result;
        
    }
    
    public int getStudentId(String username) {
        
        int id = 0;
        
        try {
        
            String query = "SELECT * FROM student WHERE username = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, username);
            
            boolean hasresults = pstmt.execute();
            
            if ( hasresults ) {
                
                ResultSet resultset = pstmt.getResultSet();
                
                if (resultset.next())
                    
                    id = resultset.getInt("id");
                
            }
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return id;
        
    }
    
    public boolean isConnected() {

        boolean result = false;
        
        try {
            
            if ( !(connection == null) )
                
                result = !(connection.isClosed());
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return result;
        
    }
    
    /* PRIVATE METHODS */

    private Connection openConnection(String u, String p, String a) {
        
        Connection c = null;
        
        if (a.equals("") || u.equals("") || p.equals(""))
            
            System.err.println("*** ERROR: MUST SPECIFY ADDRESS/USERNAME/PASSWORD BEFORE OPENING DATABASE CONNECTION ***");
        
        else {
        
            try {

                String url = "jdbc:mysql://" + a + "/jsu_sp22_v1?autoReconnect=true&useSSL=false&zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=America/Chicago";
                // System.err.println("Connecting to " + url + " ...");

                c = DriverManager.getConnection(url, u, p);

            }
            catch (Exception e) { e.printStackTrace(); }
        
        }
        
        return c;
        
    }
    
    private String getResultSetAsJSON(ResultSet resultset) {
        
        String result;
        
        /* Create JSON Containers */
        
        JSONArray json = new JSONArray();
        JSONArray keys = new JSONArray();
        
        try {
            
            /* Get Metadata */
        
            ResultSetMetaData metadata = resultset.getMetaData();
            int columnCount = metadata.getColumnCount();
            
            // INSERT YOUR CODE HERE
        while (resultset.next()) {
            JSONObject obj = new JSONObject();
            for (int i = 0; i < columnCount; i++) {
                obj.put(metadata.getColumnLabel(i+1).toLowerCase(), resultset.getObject(i+1).toString());
            }
            json.add(obj);
        }
        
        }
        catch (Exception e) { e.printStackTrace(); }
        
        /* Encode JSON Data and Return */
        
        result = JSONValue.toJSONString(json);
        return result;
        
    }     
}