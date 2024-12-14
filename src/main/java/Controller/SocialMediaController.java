package Controller;

import Service.AccountService;
import Service.MessageService;
import Model.Account;
import Model.Message;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    private AccountService accountService;
    private MessageService messageService;

    public SocialMediaController() {
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }

    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.get("example-endpoint", this::exampleHandler);
        app.post("/register", this::postAccountRegister);
        app.post("/login", this::postAccountLogin);
        app.post("/messages", this::postMessage);
        app.get("/messages", this::getMessages);
        app.get("/messages/{message_id}", this::getMessageById);
        app.delete("/messages/{message_id}", this::deleteMessageById);
        app.patch("/messages/{message_id}", this::updateMessageById);
        app.get("/accounts/{account_id}/messages", this::getAllMessagesByAccountId);

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("sample text");
    }

    private void postAccountRegister(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(context.body(), Account.class);

        if (
            "" == account.getUsername() 
            || 4 >= account.getPassword().length() 
            || null != this.accountService.getAccountByUsername(account.getUsername())
        ) {
            context.status(400);

            return;
        }

        Account registerAccount = this.accountService.registerAccount(account);
        
        if (null != registerAccount) {
            context.json(mapper.writeValueAsString(registerAccount)).status(200);
        } else {
            context.status(400);
        }
    }

    private void postAccountLogin(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(context.body(), Account.class);
        Account loginAccount = this.accountService.loginAccount(account.getUsername(), account.getPassword());

        if (null != loginAccount) {
            context.json(mapper.writeValueAsString(loginAccount)).status(200);
        } else {
            context.status(401);
        }
    }

    private void postMessage(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(context.body(), Message.class);

        if (
            "" == message.getMessage_text() 
            || 255 <= message.getMessage_text().length()
        ) {
            context.status(400);

            return;
        }

        Message addMessage = this.messageService.addMessage(message);
        
        if (null != addMessage) {
            context.json(mapper.writeValueAsString(addMessage)).status(200);
        } else {
            context.status(400);
        }        
    }

    private void getMessages(Context context) throws JsonProcessingException {
        List<Message> messages = this.messageService.getMessages();
        context.json(messages);
    }

    private void getMessageById(Context context) throws JsonProcessingException {
        String messageId = context.pathParam("message_id");
        Message message = this.messageService.findMessageById(Integer.parseInt(messageId)); 
        
        if (null != message) { 
            context.json(message); 
        } else {
            context.json("");
        }
    }

    private void deleteMessageById(Context context) throws JsonProcessingException {
        String messageId = context.pathParam("message_id");
        Message message = messageService.findMessageById(Integer.parseInt(messageId));
        
        if (null == message) {
            context.json("");
            return;
        }

        Boolean isDeletedMessage = this.messageService.deleteMessageById(message.getMessage_id());
        
        if (isDeletedMessage) { 
            context.json(message); 
        } else {
            context.json("");
        }
    }

    private void updateMessageById(Context context) throws JsonProcessingException {
        String messageId = context.pathParam("message_id");
        Message message = messageService.findMessageById(Integer.parseInt(messageId));
        ObjectMapper mapper = new ObjectMapper(); 
        JsonNode jsonNode = mapper.readTree(context.body());
        String messageText = jsonNode.has("message_text") ? jsonNode.get("message_text").asText() : null;

        if (
            null == message 
            || null == messageText
            || "" == messageText
            || 255 < messageText.length() 
        ) {
            context.status(400);

            return;
        }

        Integer postedBy = jsonNode.has("posted_by") ? jsonNode.get("posted_by").asInt() : null;

        if (null != postedBy) {
            if (!this.accountService.checkAccountIdExist(postedBy)) {
                context.status(400);

                return;
            }
        }

        Long postedTime = jsonNode.has("time_posted_epoch") ? jsonNode.get("time_posted_epoch").asLong() : null;

        Message updatedMessage = this.messageService.updateMessageById(Integer.parseInt(messageId), messageText, postedBy, postedTime);

        if (null != updatedMessage) {
            context.json(updatedMessage);
        } else {
            context.status(400);
        }
    }

    private void getAllMessagesByAccountId(Context context) throws JsonProcessingException {
        String id = context.pathParam("account_id");
        List<Message> messages = this.messageService.findMessagesByAccountId(Integer.parseInt(id));
        context.json(messages);
    }
}
