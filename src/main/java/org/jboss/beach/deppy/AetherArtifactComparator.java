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

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.ArtifactProperties;

import java.util.Comparator;

class AetherArtifactComparator implements Comparator<Artifact> {
    static final Comparator<Artifact> INSTANCE = new AetherArtifactComparator();

    @Override
    public int compare(final Artifact orig, final Artifact a) {
        // see org.apache.maven.artifact.DefaultArtifact#compareTo
        final String groupId = orig.getGroupId();
        int result = groupId.compareTo( a.getGroupId() );
        if ( result == 0 )
        {
            final String artifactId = orig.getArtifactId();
            result = artifactId.compareTo( a.getArtifactId() );
            if ( result == 0 )
            {
                final String type = typeOf(orig);
                result = type.compareTo( typeOf(a) );
                if ( result == 0 )
                {
                    final String classifier = orig.getClassifier();
                    if ( classifier == null )
                    {
                        if ( a.getClassifier() != null )
                        {
                            result = 1;
                        }
                    }
                    else
                    {
                        if ( a.getClassifier() != null )
                        {
                            result = classifier.compareTo( a.getClassifier() );
                        }
                        else
                        {
                            result = -1;
                        }
                    }
                    if ( result == 0 )
                    {
                        final String version = orig.getVersion();
                        // We don't consider the version range in the comparison, just the resolved version
                        result = new DefaultArtifactVersion( version ).compareTo(
                                new DefaultArtifactVersion( a.getVersion() ) );
                    }
                }
            }
        }
        return result;
    }

    private static String typeOf(final Artifact a) {
        return a.getProperty(ArtifactProperties.TYPE, a.getExtension());
    }
}
