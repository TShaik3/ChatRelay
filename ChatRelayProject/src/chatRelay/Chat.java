package chatRelay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Chat {
    private static int count = 0; // probably make atomic
    private String id;
    private List<AbstractUser> chatters = new ArrayList<>();
    private List<Message> messages = new ArrayList<>(); 
    private AbstractUser owner;
    private String roomName;
    private Boolean isPrivate;

    // constructor for new chats, doesnt take in an ID (needs to create unique id)
    // used for when users make new chats
    //TODO : consider using a setter to add chatters to prevent having to do 2nd loop?
    public Chat(AbstractUser chatOwner, String name, List<AbstractUser> chatters, boolean isPrivate) {
        this.id =  String.valueOf(++count);
        this.owner = chatOwner;
        this.roomName = name;
        this.chatters = chatters;
        this.isPrivate = isPrivate;
        
//        this.chatters.add(chatOwner);
    }

    // when loading in data from the .txt file (reads in an ID)
    //TODO : consider using a setter to add chatters to prevent having to do 2nd loop?
    public Chat(AbstractUser chatOwner, String name, String id, List<AbstractUser> chatters, boolean isPrivate) {
    	++count; // needed to keep count synced
    	
    	this.owner = chatOwner;
    	this.roomName = name;
        this.id = id;
        this.chatters = chatters;
        this.isPrivate = isPrivate;
    }
    
    // add chatter
    public void addChatter(AbstractUser user) {
        if (!chatters.contains(user)) {
            chatters.add(user);
        }
    }
    
    // remove chatter
    public void removeChatter(AbstractUser user) {
        if (chatters.size() <= 1) {
            return; // cannot remove only user
        }
        
        chatters.remove(user);
    }
    
    // add message
    public void addMessage(Message msg) {
        messages.add(msg);
		Collections.sort(messages, (m1, m2) -> (int) m1.getCreatedAt() - (int) m2.getCreatedAt());
    }
    
    // change privacy
    // if private, only owner can add users
    // if public, anyone can add users
    public void changePrivacy(Boolean newState) {
        this.isPrivate = newState;
    }
    
   
    // get as text format for the DB .txt file
    public String toString() {
    	List<String> chatterIds = new ArrayList<>();
    	
    	for (AbstractUser chatter : chatters) {
    		chatterIds.add(chatter.getId());
    	}
    	
    	return id + "/" + owner.getId() + "/" + roomName + "/" + String.valueOf(isPrivate) + "/" + String.join(",", chatterIds);
    }
    
    // getters
    public String getId() {
        return id;
    }
    
    public String getRoomName() {
        return roomName;
    }
    
    public AbstractUser getOwner() {
        return owner;
    }
    
    public List<Message> getMessages() {
        return messages;
    }
    
    public List<AbstractUser> getChatters() {
        return chatters;
    }
    
    public ArrayList<String> getChattersIds(){
    	ArrayList<String> chattersIds = new ArrayList<>();
    	for (AbstractUser chatter : chatters) {
    		chattersIds.add(chatter.getId());
    	}
    	
    	return chattersIds;
    }
    
    public void setRoomName(String newname) {
    	this.roomName = newname;
    }
    
    public Boolean isPrivate() {
        return isPrivate;
    }
}
