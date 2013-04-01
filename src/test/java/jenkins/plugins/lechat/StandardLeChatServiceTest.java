package jenkins.plugins.lechat;

import jenkins.plugins.lechat.StandardLeChatService;
import org.junit.Before;
import org.junit.Test;

public class StandardLeChatServiceTest {

    /**
     * Publish should generally not rethrow exceptions, or it will cause a build job to fail at end.
     */
    @Test
    public void publishWithBadHostShouldNotRethrowExceptions() {
        StandardLeChatService service = new StandardLeChatService("room", "from");
        service.setHost("hostvaluethatwillcausepublishtofail");
        service.publish("message");
    }
}
