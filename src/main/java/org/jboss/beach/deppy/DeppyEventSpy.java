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

import org.apache.maven.RepositoryUtils;
import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.model.Model;
import org.apache.maven.repository.internal.ArtifactDescriptorReaderDelegate;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.impl.ArtifactDescriptorReader;
import org.eclipse.aether.repository.ArtifactRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;

import java.util.*;

/**
 * Catch and hold the MavenExecutionResult.
 *
 * Note that it is used through CurrentEventSpy, so this class does not represent a Plexus Component.
 */
public class DeppyEventSpy extends AbstractEventSpy {
    private static ThreadLocal<Boolean> SILENCED = ThreadLocal.withInitial(() -> false);
    private PlexusContainer container;
    private ArtifactDescriptorReader artifactDescriptorReader;
    private RepositorySystemSession repositorySystemSession;
    private Optional<MavenExecutionResult> mavenExecutionResult = Optional.empty();
    private final Map<org.apache.maven.artifact.Artifact, Model> artifacts = new HashMap<>();

    public Optional<MavenExecutionResult> getMavenExecutionResult() {
        return mavenExecutionResult;
    }

    PlexusContainer container() {
        return container;
    }

    @Override
    public void init(Context context) throws Exception {
        super.init(context);
        this.container = Objects.requireNonNull((PlexusContainer) context.getData().get("plexus"));
    }

    Model modelOf(final org.apache.maven.artifact.Artifact artifact) {
        return artifacts.get(artifact);
    }

    @Override
    public void onEvent(final Object event) throws Exception {
        super.onEvent(event);
//        System.out.println(event.getClass().getName() + ": " + event);
//        if (event instanceof DefaultDependencyResolutionRequest) new Exception("here").printStackTrace();
        if (event instanceof RepositoryEvent) onRepositoryEvent((RepositoryEvent) event);
        else if (event instanceof ExecutionEvent) onExecutionEvent((ExecutionEvent) event);
        else if (event instanceof MavenExecutionRequest) onMavenExecutionRequest((MavenExecutionRequest) event);
        else if (event instanceof MavenExecutionResult) {
            onMavenExecutionResult((MavenExecutionResult) event);
        }
    }

    private void onExecutionEvent(ExecutionEvent event) {
//        System.out.println("onExecutionEvent: " + event.getType());
        if (event.getType() == ExecutionEvent.Type.ProjectDiscoveryStarted) {
            try {
                this.artifactDescriptorReader = Objects.requireNonNull(container.lookup(ArtifactDescriptorReader.class), "no artifact descriptor reader");
            } catch (ComponentLookupException e) {
                throw new RuntimeException("failed to find artifact descriptor reader", e);
            }
            // we'll see more execution events, but we only want the first one to obtain the repository system session
            //if (this.repositorySystemSession != null) return;
            assert this.repositorySystemSession == null;

            this.repositorySystemSession = Objects.requireNonNull(event.getSession().getRepositorySession(), "no repository session");
            System.out.println(event.getSession().getRepositorySession());

            // dangerous!
            ((DefaultRepositorySystemSession) this.repositorySystemSession).setConfigProperty(ArtifactDescriptorReaderDelegate.class.getName(), new DeppyArtifactDescriptorReaderDelegate());
        }
    }

    private void onMavenExecutionRequest(MavenExecutionRequest event) {
//        System.out.println("onMavenExecutionRequest");
    }

    private void onMavenExecutionResult(final MavenExecutionResult result) {
        mavenExecutionResult = Optional.of(result);
    }

    private void onRepositoryEvent(RepositoryEvent event) {
        //new Exception("onRepositoryEvent").printStackTrace();
        if (event.getType() == RepositoryEvent.EventType.ARTIFACT_RESOLVED) {
            // no handling of events when we ask for resolution ourselves
            if (SILENCED.get()) return;
            final Artifact artifact = event.getArtifact();
            final ArtifactRepository repository = event.getRepository();
            final List<RemoteRepository> remoteRepositories;
            if (repository != null && repository instanceof RemoteRepository) remoteRepositories = Arrays.asList((RemoteRepository) event.getRepository());
            else remoteRepositories = Collections.EMPTY_LIST;
            final ArtifactDescriptorRequest request = new ArtifactDescriptorRequest(artifact, remoteRepositories, null);
            try {
                SILENCED.set(true);
                final ArtifactDescriptorResult result = artifactDescriptorReader.readArtifactDescriptor(repositorySystemSession, request);
                final Model model = (Model) result.getProperties().get(Model.class.getName());
                artifacts.put(RepositoryUtils.toArtifact(artifact), model);
            } catch (ArtifactDescriptorException e) {
                throw new RuntimeException(e);
            } finally {
                SILENCED.set(false);
            }
        }
    }
}
