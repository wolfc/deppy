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
import org.apache.maven.model.Model;
import org.eclipse.aether.artifact.Artifact;

public class DeppyArtifact implements Comparable<DeppyArtifact> {
    private final DeppyEventSpy eventSpy;
    private final Artifact artifact;
    private final Model model;

    DeppyArtifact(final DeppyEventSpy eventSpy, final Artifact artifact, final Model model) {
        this.eventSpy = eventSpy;
        this.artifact = artifact;
        this.model = model;
    }

    @Override
    public int compareTo(final DeppyArtifact other) {
        return AetherArtifactComparator.INSTANCE.compare(artifact, other.artifact);
    }

    Artifact getArtifact() {
        return artifact;
    }

    public String getArtifactId() {
        return artifact.getArtifactId();
    }

    public String getGroupId() {
        return artifact.getGroupId();
    }

    Model getModel() {
        return model;
    }

    DeppyArtifact getParent() {
        final Artifact artifact = AetherArtifactHelper.toArtifact(model.getParent());
        final Model model = eventSpy.modelOf(RepositoryUtils.toArtifact(artifact));
        return new DeppyArtifact(this.eventSpy, artifact, model);
    }

    public String getVersion() {
        return artifact.getVersion();
    }

    @Override
    public String toString() {
        return "DeppyArtifact{" +
                "artifact=" + artifact +
                ", model=" + model +
                '}';
    }
}
