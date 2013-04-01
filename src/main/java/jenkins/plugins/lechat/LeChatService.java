package jenkins.plugins.lechat;

public interface LeChatService {
    void publish(String message);

    void publish(String message, String color);
}
