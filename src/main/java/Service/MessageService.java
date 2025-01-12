package Service;

import Model.Message;

import java.util.List;

import DAO.SocialMediaDAO;

public class MessageService {

    SocialMediaDAO socialMediaDAO;

    public MessageService(){
        this.socialMediaDAO = new SocialMediaDAO();
    }

    public MessageService(SocialMediaDAO socialMediaDAO){
        this.socialMediaDAO = socialMediaDAO;
    }

    //## 3: Our API should be able to process the creation of new messages.
    public Message insertMessage(Message message){
        return socialMediaDAO.insertMessage(message);
    }
    //## 4: Our API should be able to retrieve all messages.
    public List<Message> getAllMessages(){
        return socialMediaDAO.getAllMessages();
    }
    //## 5: Our API should be able to retrieve a message by its ID.
    public Message getUserMessageByID(int message_id){
        return socialMediaDAO.getUserMessageByID(message_id);
    }
    //## 6: Our API should be able to delete a message identified by a message ID.
    public Message deleteUserMessageByID(int message_id){
        return socialMediaDAO.deleteUserMessageByID(message_id);
    }
    //## 7: Our API should be able to update a message text identified by a message ID.
    public Message updateUserMessageByID(String new_message_text, int message_id){
        return socialMediaDAO.updateUserMessageByID(new_message_text, message_id);
    }
    //## 8: Our API should be able to retrieve all messages written by a particular user.
    public List<Message> getAllMessagesByUser(int account_id){
        return socialMediaDAO.getAllMessagesByUser(account_id);
    }
    
}
