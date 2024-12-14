package Service;

import java.util.List;

import DAO.MessageDAO;
import Model.Message;

public class MessageService {
    private MessageDAO messageDAO;

    public MessageService() {
        messageDAO = new MessageDAO();
    }

    public MessageService(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }

    public Message addMessage(Message message) {
        return this.messageDAO.insertMessage(message);
    }

    public List<Message> getMessages() {
        return this.messageDAO.getAllMessages();
    }

    public Message findMessageById(int id) {
        return this.messageDAO.getMessageById(id);
    }

    public Boolean deleteMessageById(int id) {
        return this.messageDAO.deleteMessageById(id);
    }

    public Message updateMessageById(int id, String updatedMessage, Integer postedBy, Long postedTime) {
        if (this.messageDAO.updateMessageById(id, updatedMessage, postedBy, postedTime)) {
            return this.messageDAO.getMessageById(id);
        }

        return null;
    }

    public List<Message> findMessagesByAccountId(int id) {
        return this.messageDAO.getAllMessagesByAccountId(id);
    }
}
