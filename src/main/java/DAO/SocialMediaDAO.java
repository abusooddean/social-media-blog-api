package DAO;

import Model.Account;
import Model.Message;
import Util.ConnectionUtil;

import java.sql.*;

public class SocialMediaDAO {

    //HELPER METHOD FOR EARLY CHECK
    public boolean usernameExists(String username){
        Connection connection = ConnectionUtil.getConnection();
        try{
            String sql = "SELECT * FROM account WHERE username = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, username);

            ResultSet rs = preparedStatement.executeQuery();
            return rs.next(); //returns boolean
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
    }
    public boolean usernameExists(int userID){
        Connection connection = ConnectionUtil.getConnection();
        try{
            String sql = "SELECT * FROM account WHERE account_id = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, userID);

            ResultSet rs = preparedStatement.executeQuery();
            return rs.next(); //returns boolean
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            // return false;
        }
        return false;
    }
    
    //CREATE NEW USER - CHECK INPUT
    public Account insertUser(Account account){
        if(account.getUsername().trim().isEmpty() || account.getPassword().length() < 4 || usernameExists(account.getUsername())){
            return null;
        }

        Connection connection = ConnectionUtil.getConnection();

        try{
            String sql = "INSERT INTO account (username, password) VALUES(?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getPassword());

            preparedStatement.executeUpdate();

            ResultSet pkeyResultSet = preparedStatement.getGeneratedKeys();
            if(pkeyResultSet.next()){
                int generatedAccountID = (int)pkeyResultSet.getLong(1);
                return new Account(generatedAccountID, account.getUsername(), account.getPassword());
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }
    //LOGIN USER - CHECK IF USER EXISTS
    public Account loginUser(Account account){
        if(!usernameExists(account.getUsername()) || account.getPassword().length() < 4){ //check if username doesnt exist + short password check
            return null;
        }

        Connection connection = ConnectionUtil.getConnection();

        try{
            String sql = "SELECT * FROM account WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getPassword());

            ResultSet rs =  preparedStatement.executeQuery();
            if(rs.next()){
                Account acc = new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password"));
                return acc;
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }
    //ADD NEW MESSAGE - CHECK IF USER EXISTS
    public Message insertMessage(Message message){
        //check message length to fit constraints and if userID exists
        if(message.getMessage_text().trim().isEmpty() || message.getMessage_text().length() > 255 || !usernameExists(message.getPosted_by())){ 
            return null;
        }
        Connection connection = ConnectionUtil.getConnection();

        try{
            String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES(?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, message.getPosted_by());
            preparedStatement.setString(2, message.getMessage_text());
            preparedStatement.setLong(3, message.getTime_posted_epoch());

            preparedStatement.executeUpdate();

            ResultSet pkeyResultSet = preparedStatement.getGeneratedKeys();
            if(pkeyResultSet.next()){
                int generatedMessageID = (int)pkeyResultSet.getLong(1);
                return new Message(generatedMessageID, message.getPosted_by(), message.getMessage_text(), message.getTime_posted_epoch());
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

}
