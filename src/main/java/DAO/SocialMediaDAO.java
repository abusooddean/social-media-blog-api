package DAO;

import Model.Account;
import Model.Message;
import Util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SocialMediaDAO {

    //HELPER METHODS FOR EARLY USERNAME EXISTS CHECK
    public boolean usernameExists(String username){
        Connection connection = ConnectionUtil.getConnection();
        try{
            String sql = "SELECT * FROM account WHERE username=?";

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
            String sql = "SELECT * FROM account WHERE account_id=?";

            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, userID);

            ResultSet rs = preparedStatement.executeQuery();
            return rs.next(); //returns boolean
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
    }
    public boolean messageIDExists(int messageID){
        Connection connection = ConnectionUtil.getConnection();
        try{
            String sql = "SELECT * FROM message WHERE message_id=?";

            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, messageID);

            ResultSet rs = preparedStatement.executeQuery();
            return rs.next(); //returns boolean
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
    }
    
    //## 1: Our API should be able to process new User registrations.
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
    //## 2: Our API should be able to process User logins.
    public Account loginUser(Account account){
        if(!usernameExists(account.getUsername()) || account.getPassword().length() < 4){ //check if username doesnt exist + short password check
            return null;
        }

        Connection connection = ConnectionUtil.getConnection();

        try{
            String sql = "SELECT * FROM account WHERE username=? AND password=?";
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
    //## 3: Our API should be able to process the creation of new messages.
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

    //## 4: Our API should be able to retrieve all messages.
    public List<Message> getAllMessages(){
        List<Message> allMessages = new ArrayList<>();

        Connection connection = ConnectionUtil.getConnection();

        try {
            String sql = "SELECT * FROM message";

            PreparedStatement ps = connection.prepareStatement(sql);

            ResultSet rs =  ps.executeQuery();
            if(rs.next()){
               Message message = new Message(rs.getInt("message_id"),rs.getInt("posted_by"), rs.getString("message_text"), rs.getLong("time_posted_epoch"));
                allMessages.add(message);
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return allMessages;
    }

    //## 5: Our API should be able to retrieve a message by its ID.
    public Message getUserMessageByID(int message_id){
        Connection connection = ConnectionUtil.getConnection();

        try {
            String sql = "SELECT * FROM message WHERE message_id=?";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, message_id);

            ResultSet rs =  ps.executeQuery();
            if(rs.next()){
               return new Message(rs.getInt("message_id"), rs.getInt("posted_by"), rs.getString("message_text"), rs.getLong("time_posted_epoch"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return null;
    }

    //## 6: Our API should be able to delete a message identified by a message ID.
    public Message deleteUserMessageByID(int message_id){
        Connection connection = ConnectionUtil.getConnection();

        try {
            //store message before deletion to return later
            String sql = "SELECT * FROM message WHERE message_id=?";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, message_id);

            ResultSet rs =  ps.executeQuery();
            if(rs.next()){
                String deleteMessageSql = "DELETE FROM message WHERE message_id=?";
                ps = connection.prepareStatement(deleteMessageSql);
                ps.setInt(1, message_id);
                ps.executeUpdate();

            return new Message(rs.getInt("message_id"), rs.getInt("posted_by"), rs.getString("message_text"), rs.getLong("time_posted_epoch"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    //## 7: Our API should be able to update a message text identified by a message ID.
    public Message updateUserMessageByID(String new_message_text, int message_id){
        if(new_message_text == null|| new_message_text.trim().isEmpty() || new_message_text.length() > 255 || !messageIDExists(message_id)){
            return null;
        }

        Connection connection = ConnectionUtil.getConnection();

        try {
            String selectSql = "SELECT * FROM message WHERE message_id=?";

            PreparedStatement ps = connection.prepareStatement(selectSql);
            ps.setInt(1, message_id);

            ResultSet rs =  ps.executeQuery();
            if(!rs.next()) return null; //does not exist
            
            //UPDATE
            String updateMessageSql = "UPDATE message SET message_text=? WHERE message_id=?";
                ps = connection.prepareStatement(updateMessageSql);
                ps.setString(1, new_message_text);
                ps.setInt(2, message_id);
                ps.executeUpdate();

                //SELECT AGAIN TO RETURN MESSAGE
                ps = connection.prepareStatement(selectSql);
                ps.setInt(1, message_id);
                rs = ps.executeQuery();

            if(rs.next()){
                return new Message(rs.getInt("message_id"), rs.getInt("posted_by"), new_message_text, rs.getLong("time_posted_epoch"));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    //## 8: Our API should be able to retrieve all messages written by a particular user.
    public List<Message> getAllMessagesByUser(int account_id){
        List<Message> allMessages = new ArrayList<>();
        Connection connection = ConnectionUtil.getConnection();

        try {
            String sql = "SELECT * FROM message WHERE posted_by=?";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, account_id);

            ResultSet rs =  ps.executeQuery();
            if(rs.next()){
               Message message = new Message(rs.getInt("message_id"), rs.getInt("posted_by"), rs.getString("message_text"), rs.getLong("time_posted_epoch"));
               allMessages.add(message);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return allMessages;
    }


}
