package jenkins.plugins.lechat;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import java.util.logging.Level;
import java.util.logging.Logger;

public class StandardLeChatService implements LeChatService {

    private static final Logger logger = Logger.getLogger(StandardLeChatService.class.getName());

    private String host = "api.lechat.im";
    private String[] roomIds;
    private String from;

    public StandardLeChatService(String roomId, String from) {
        super();
        this.roomIds = roomId.split(",");
        this.from = from;
    }

    public void publish(String message) {
        publish(message, "yellow");
    }

    public void publish(String message, String color) {
        for (String roomId : roomIds) {
            HttpClient client = new HttpClient();
            String url = "https://" + host + "/rooms/" + roomId + "/jenkins";
            logger.info("Posting: " + from + " to " + url + ": " + message + " " + color);
            PostMethod post = new PostMethod(url);

            try {
                post.addParameter("from", from);
                post.addParameter("room_id", roomId);
                post.addParameter("message", message);
                post.addParameter("color", color);
                post.addParameter("notify", shouldNotify(color));
                post.getParams().setContentCharset("UTF-8");
                client.executeMethod(post);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error posting to LeChat", e);
            } finally {
                post.releaseConnection();
            }
        }
    }

    private String shouldNotify(String color) {
        return color.equalsIgnoreCase("green") ? "0" : "1";
    }

    void setHost(String host) {
        this.host = host;
    }
}
