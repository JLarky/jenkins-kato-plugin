package jenkins.plugins.kato;

public interface KatoService {
    void publish(String message);

    void publish(String message, String color);
}
