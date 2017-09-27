package org.jboss.beach.deppy;

import org.apache.maven.cli.MavenCli;

public class Deppy {
    public static void main(final String[] args) throws Exception {
        System.setProperty(MavenCli.MULTIMODULE_PROJECT_DIRECTORY, ".");
        System.setProperty("maven.ext.class.path", DeppyEventSpy.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        final MavenCli cli = new MavenCli();
        cli.doMain(new String[]{"dependency:resolve"}, ".", System.out, System.err);
        // to make it thread safe DeppyEventSpy needs to be plucked out of cli.eventSpyDispatcher
    }
}
