### Evolution:

    https://github.com/jgp/hudson_campfire_plugin ->
    https://github.com/jlewallen/jenkins-hipchat-plugin ->
    https://github.com/jenkinsci/kato-plugin


### Installation

This plugin is available from the [Update Center](https://wiki.jenkins-ci.org/display/JENKINS/Plugins#Plugins-Howtoinstallplugins) in your Jenkins installation.


### Building from source:

1. Install maven 3

2. `$ mvn clean`

3. `$ mvn package`

4. target/kato.hpi is the plugin, upload it to plugin management console (http://example.com:8080/pluginManager/advanced). You have to restart Jenkins in order to find the pluing in the installed plugins list.

### Development mode

run `mvn hpi:run -Djetty.port=8090`
for more read [Plugin Tutorial](https://wiki.jenkins-ci.org/display/JENKINS/Plugin+tutorial)
