/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2017, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
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
