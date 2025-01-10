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

    public Account registerUser(Account account){
        return socialMediaDAO.insertUser(account);
    }

    public Account loginUser(Account account){
        return socialMediaDAO.loginUser(account);
    }
    
}
