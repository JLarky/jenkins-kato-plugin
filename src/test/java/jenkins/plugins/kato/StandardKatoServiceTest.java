package jenkins.plugins.kato;

import org.junit.Test;

public class StandardKatoServiceTest {

    /**
     * Publish should generally not rethrow exceptions, or it will cause a build job to fail at end.
     */
    @Test
    public void publishWithBadHostShouldNotRethrowExceptions() {
        StandardKatoService service = new StandardKatoService("room", "from");
        service.setApiUrl("hostvaluethatwillcausepublishtofail");
        service.publish("message");
    }
}
