package Controller;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    AccountService accountService;
    MessageService messageService;

    public SocialMediaController(){
        accountService = new AccountService();
        messageService = new MessageService();

    }
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::postRegisterHandler);
        app.post("/login", this::postLoginHandler);
        app.post("/messages", this::postMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getUserMessageByIDHandler);
        app.delete("/messages/{message_id}", this::deleteMessageByIDHandler);
        app.patch("/messages/{message_id}", this::updateMessageByIDHandler);
        app.get("/accounts/{account_id}/messages", this::getAllMessagesByUserHandler);


        return app;
    }

    //## 1: Our API should be able to process new User registrations.
    private void postRegisterHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);
        Account addedUser = accountService.registerUser(account);
        if(addedUser==null){
            ctx.status(400);
        }else{
            ctx.json(mapper.writeValueAsString(addedUser));
        }
    }
    //## 2: Our API should be able to process User logins.
    private void postLoginHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);
        Account user = accountService.loginUser(account);
        if(user==null){
            ctx.status(401);
        }else{
            ctx.json(mapper.writeValueAsString(user));
        }
    }
    //## 3: Our API should be able to process the creation of new messages.
    private void postMessageHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(ctx.body(), Message.class);
        Message newMessage = messageService.insertMessage(message);
        if(newMessage==null){
            ctx.status(400);
        }else{
            ctx.json(mapper.writeValueAsString(newMessage));
        }
    }
    //## 4: Our API should be able to retrieve all messages.
    private void getAllMessagesHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<Message> allMessages = messageService.getAllMessages();
        if(allMessages==null){
            ctx.status(200);
        }else{
            ctx.json(mapper.writeValueAsString(allMessages));
        }
    }
    //## 5: Our API should be able to retrieve a message by its ID.
    private void getUserMessageByIDHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        Message userMessage = messageService.getUserMessageByID(messageId);
        if(userMessage==null){
            ctx.status(200);
        }else{
            ctx.json(mapper.writeValueAsString(userMessage));
        }
    }
    //## 6: Our API should be able to delete a message identified by a message ID.
    private void deleteMessageByIDHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        Message userMessage = messageService.deleteUserMessageByID(messageId);
        if(userMessage==null){
            ctx.status(200);
        }else{
            ctx.json(mapper.writeValueAsString(userMessage));
        }
    }
    //## 7: Our API should be able to update a message text identified by a message ID.
    private void updateMessageByIDHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        //https://www.baeldung.com/jackson-object-mapper-tutorial#5-creating-java-map-from-json-string
        Map<String, String> bodyMap = mapper.readValue(ctx.body(), Map.class);
        String new_message_text = bodyMap.get("message_text");
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));

        Message userMessage = messageService.updateUserMessageByID(new_message_text, messageId);
        if(userMessage==null){
            ctx.status(400);
        }else{
            ctx.status(200);
            ctx.json(mapper.writeValueAsString(userMessage));
        }
    }
    //## 8: Our API should be able to retrieve all messages written by a particular user.
    private void getAllMessagesByUserHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        int account_id = Integer.parseInt(ctx.pathParam("account_id"));
        List<Message> userMessages = messageService.getAllMessagesByUser(account_id);
        if(userMessages==null){
            ctx.status(200);
        }else{
            ctx.json(mapper.writeValueAsString(userMessages));
        }
    }

}