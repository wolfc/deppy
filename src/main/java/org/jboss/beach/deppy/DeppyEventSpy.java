package org.jboss.beach.deppy;

import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.eventspy.EventSpy;
import org.apache.maven.execution.MavenExecutionResult;
import org.codehaus.plexus.component.annotations.Component;

@Component(role = EventSpy.class)
public class DeppyEventSpy extends AbstractEventSpy {
    @Override
    public void onEvent(final Object event) throws Exception {
        super.onEvent(event);
        //System.err.println(" ** " + event);
        if (event instanceof MavenExecutionResult) {
            onMavenExecutionResult((MavenExecutionResult) event);
        }
    }

    private void onMavenExecutionResult(final MavenExecutionResult result) {
        //System.out.println(result.getDependencyResolutionResult().getDependencies());
        //System.out.println(result.getProject().getArtifacts());
        result.getProject().getArtifacts().stream().forEach(a -> {
            System.out.println("> " + a);
        });
    }
}
