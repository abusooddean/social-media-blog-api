package Service;

import Model.Message;
import DAO.SocialMediaDAO;

public class MessageService {

    SocialMediaDAO socialMediaDAO;

    public MessageService(){
        this.socialMediaDAO = new SocialMediaDAO();
    }

    public MessageService(SocialMediaDAO socialMediaDAO){
        this.socialMediaDAO = socialMediaDAO;
    }

    public Message insertMessage(Message message){
        return socialMediaDAO.insertMessage(message);
    }
    
}
