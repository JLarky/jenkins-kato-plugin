package jenkins.plugins.kato;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import java.util.logging.Level;
import java.util.logging.Logger;

public class StandardKatoService implements KatoService {

    private static final Logger logger = Logger.getLogger(StandardKatoService.class.getName());

    private String apiUrl = "https://api.kato.im";
    private String[] roomIds;
    private String from;

    public StandardKatoService(String roomId, String from) {
        super();
        this.roomIds = roomId.split(",");
        this.from = from;
        logger.info("StandardKatoService = " + from + " = " + roomId);
    }

    public void publish(String message) {
        publish(message, "yellow");
    }

    public void publish(String message, String color) {
        for (String roomId : roomIds) {
            HttpClient client = new HttpClient();
            String url = apiUrl + "/rooms/" + roomId + "/jenkins";
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
                logger.log(Level.WARNING, "Error posting to Kato", e);
            } finally {
                post.releaseConnection();
            }
        }
    }

    private String shouldNotify(String color) {
        return color.equalsIgnoreCase("green") ? "0" : "1";
    }

    void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
}
