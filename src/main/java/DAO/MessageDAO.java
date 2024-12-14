package DAO;

import Model.Message;
import Util.ConnectionUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {
    public Message insertMessage(Message message) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, message.getPosted_by());
            preparedStatement.setString(2, message.getMessage_text());
            preparedStatement.setLong(3, message.getTime_posted_epoch());

            preparedStatement.executeUpdate();

            ResultSet pkeyResultSet = preparedStatement.getGeneratedKeys();

            if (pkeyResultSet.next()) {
                int generated_author_id = (int) pkeyResultSet.getLong(1);

                return new Message(generated_author_id, message.getPosted_by(), message.getMessage_text(), message.getTime_posted_epoch());
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public List<Message> getAllMessages() {
        Connection connection = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();

        try {
            String sql = "SELECT * FROM message";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()){
                Message message = new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getInt("time_posted_epoch")
                );

                messages.add(message);
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        return messages;
    }

    public Message getMessageById(int id) {
        Connection connection = ConnectionUtil.getConnection();
    
        try {
            String sql = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()){
                Message message = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("posted_by"),
                    rs.getString("message_text"),
                    rs.getLong("time_posted_epoch")
                );

                return message;
            }
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }

        return null;
    }

    public Boolean deleteMessageById(int id) {
        Connection connection = ConnectionUtil.getConnection();
    
        try {
            String sql = "DELETE FROM message WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate(); 
            
            return rowsAffected > 0;
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }

        return false;
    }

    public Boolean updateMessageById(int id, String updatedMessage, Integer postedBy, Long postedTime) {
        Connection connection = ConnectionUtil.getConnection();
        StringBuilder query = new StringBuilder("UPDATE message SET message_text = ?");

        if (postedBy != null) { 
            query.append(", posted_by = ?"); 
        } 
        
        if (postedTime != null) { 
            query.append(", posted_time = ?");
        }

        query.append(" WHERE message_id = ?");

        try {
            
            PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
            int index = 1;
            preparedStatement.setString(index++, updatedMessage);

            if (null != postedBy) { 
                preparedStatement.setInt(index++, postedBy); 
            } 
            
            if (null != postedTime) { 
                preparedStatement.setLong(index++, postedTime); 
            }

            preparedStatement.setInt(index, id);
            int rowsAffected = preparedStatement.executeUpdate(); 
            
            return rowsAffected > 0;
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }

        return false;
    }

    public List<Message> getAllMessagesByAccountId(int id) {
        Connection connection = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();

        try {
            String sql = "SELECT * FROM message where message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()){
                Message message = new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getInt("time_posted_epoch")
                );

                messages.add(message);
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        return messages;
    }
}
