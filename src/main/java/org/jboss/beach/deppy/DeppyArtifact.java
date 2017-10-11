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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Model;

public class DeppyArtifact implements Comparable<DeppyArtifact> {
    private final Artifact artifact;
    private final Model model;

    DeppyArtifact(final Artifact artifact, final Model model) {
        this.artifact = artifact;
        this.model = model;
    }

    @Override
    public int compareTo(final DeppyArtifact other) {
        return artifact.compareTo(other.artifact);
    }

    Artifact getArtifact() {
        return artifact;
    }

    Model getModel() {
        return model;
    }

    @Override
    public String toString() {
        return "DeppyArtifact{" +
                "artifact=" + artifact +
                ", model=" + model +
                '}';
    }
}
