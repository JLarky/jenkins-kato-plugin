package jenkins.plugins.kato;

import hudson.Util;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

@SuppressWarnings({"unchecked"})
public class KatoNotifier extends Notifier {
    // settings
    private String room;
    private String sendAs;
    // notification settings
    private boolean startNotification;
    private boolean notifyAborted;
    private boolean notifyFailure;
    private boolean notifyNotBuilt;
    private boolean notifySuccess;
    private boolean notifyUnstable;

    private static final Logger logger = Logger.getLogger(KatoNotifier.class.getName());

    // getters for project configuration..
    // Configured room name should be null unless different from descriptor/global values
    public String getConfiguredRoomName() {
        if ( getRoom().equals(room) ) {
            return null;
        } else {
            return room;
        }
    }

    // getters for config.jelly
    public boolean getStartNotification() {
        return startNotification;
    }
    public boolean getNotifyAborted() {
        return notifyAborted;
    }
    public boolean getNotifyFailure() {
        return notifyFailure;
    }
    public boolean getNotifyNotBuilt() {
        return notifyNotBuilt;
    }
    public boolean getNotifySuccess() {
        return notifySuccess;
    }
    public boolean getNotifyUnstable() {
        return notifyUnstable;
    }

    public String getRoom() {
        return DESCRIPTOR.getRoom();
    }

    public String getSendAs() {
        return DESCRIPTOR.getSendAs();
    }

    // getters for kato
    private String getInstanceRoom() {
        return (room == null ? getRoom() : room);
    }
    private String getInstanceSendAs() {
        String sendAs = Util.fixEmpty(getSendAs());
        return (sendAs == null ? "Build Server" : sendAs);
    }

    @DataBoundConstructor
    public KatoNotifier(final String room, final String sendAs, boolean startNotification, boolean notifyAborted, boolean notifyFailure, boolean notifyNotBuilt, boolean notifySuccess, boolean notifyUnstable) {
        super();
        // instance variables
        this.room                = room;
        this.sendAs              = sendAs;
        this.startNotification   = startNotification;
        this.notifyAborted       = notifyAborted;
        this.notifyFailure       = notifyFailure;
        this.notifyNotBuilt      = notifyNotBuilt;
        this.notifySuccess       = notifySuccess;
        this.notifyUnstable      = notifyUnstable;
        // validation
        String katoRoom = getInstanceRoom();
        if (katoRoom == null || katoRoom.trim().length() == 0) {
            throw new RuntimeException("room must be not empty");
        }
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    public KatoService newKatoService() {
        return new StandardKatoService(getInstanceRoom(), getInstanceSendAs());
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        return true;
    }

    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        // settings
        private String room;
        private String sendAs;

        // getters for global.jelly
        public String getRoom() {
            return room;
        }

        public String getSendAs() {
            return sendAs;
        }

        public DescriptorImpl() {
            super(KatoNotifier.class);
            load();
        }

        protected DescriptorImpl(Class<? extends Publisher> clazz) {
            super(clazz);
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        /**
         * @see hudson.model.Descriptor#newInstance(org.kohsuke.stapler.StaplerRequest)
         */
        @Override
        public KatoNotifier newInstance(StaplerRequest sr) throws FormException { // handles config.jelly
            // instance only settings
            boolean startNotification = sr.getParameter("startNotification") != null;
            boolean notifyAborted     = sr.getParameter("notifyAborted") != null;
            boolean notifyFailure     = sr.getParameter("notifyFailure") != null;
            boolean notifyNotBuilt    = sr.getParameter("notifyNotBuilt") != null;
            boolean notifySuccess     = sr.getParameter("notifySuccess") != null;
            boolean notifyUnstable    = sr.getParameter("notifyUnstable") != null;
            // override global settings if room isn't empty
            String projectRoom = sr.getParameter("katoProjectRoom");
            if ( projectRoom == null || projectRoom.trim().length() == 0 ) {
                projectRoom = sr.getParameter("katoRoom");
            }
            // use global value
            sendAs = sr.getParameter("katoSendAs");
            try {
                return new KatoNotifier(projectRoom, sendAs, startNotification, notifyAborted, notifyFailure, notifyNotBuilt, notifySuccess, notifyUnstable);
            } catch (Exception e) {
                String message = "Failed to initialize kato notifier - check your campfire notifier configuration settings: " + e.getMessage();
                logger.warning(message);
                throw new FormException(message, e, "");
            }
        }

        @Override
        public boolean configure(StaplerRequest sr, JSONObject formData) throws FormException { // handles global.jelly
            // apply new global settings
            room = sr.getParameter("katoRoom");
            sendAs = sr.getParameter("katoSendAs");
            // validate settings
            try {
                new KatoNotifier(room, sendAs, false, false, false, false, false, false);
            } catch (Exception e) {
                String message = "Failed to initialize kato notifier - check your campfire notifier configuration settings: " + e.getMessage();
                logger.warning(message);
                throw new FormException(message, e, "");
            }
            save();
            return super.configure(sr, formData);
        }

        @Override
        public String getDisplayName() {
            return "Kato Notifications";
        }
    }
}
