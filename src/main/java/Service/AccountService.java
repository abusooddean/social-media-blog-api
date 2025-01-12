package Service;

import Model.Account;
import DAO.SocialMediaDAO;

public class AccountService {

    SocialMediaDAO socialMediaDAO;

    public AccountService(){
        this.socialMediaDAO = new SocialMediaDAO();
    }

    public AccountService(SocialMediaDAO socialMediaDAO){
        this.socialMediaDAO = socialMediaDAO;
    }

    //## 1: Our API should be able to process new User registrations.
    public Account registerUser(Account account){
        return socialMediaDAO.insertUser(account);
    }
    //## 2: Our API should be able to process User logins.
    public Account loginUser(Account account){
        return socialMediaDAO.loginUser(account);
    }
    
}
