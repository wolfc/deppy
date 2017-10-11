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

import org.apache.maven.cli.MavenCli;
import org.apache.maven.model.Model;
import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;
import org.jboss.beach.maven.eventspy.CurrentEventSpy;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

public class Deppy {
    public static void main(final String[] args) throws Exception {
        final String workingDirectory = args != null && args.length > 0 ? args[0] : ".";
        getDependencies(workingDirectory).sorted().forEach(a -> {
            //System.out.println("> " + a + ": " + tagOf(a));
            final Optional<Model> model = ofNullable(a.getModel());
            final Optional<Scm> scm = model.map(Model::getScm);
            System.out.println("> " + a.getArtifact() + ": " + scm.map(Scm::getUrl).orElse("<no url>") + " @" + scm.map(Scm::getTag).orElse("<unknown>"));
        });
    }

    public static Stream<DeppyArtifact> getDependencies(final String mavenProjectLocation) {
        System.setProperty(MavenCli.MULTIMODULE_PROJECT_DIRECTORY, ".");
        final DeppyEventSpy eventSpy = new DeppyEventSpy();
        CurrentEventSpy.set(eventSpy);
        try {
            final MavenCli cli = new MavenCli();
            cli.doMain(new String[]{"dependency:resolve"}, mavenProjectLocation, System.out, System.err);
            return Stream.of(eventSpy.getMavenExecutionResult().orElseThrow(IllegalStateException::new).getProject()).flatMap(p -> Stream.concat(Stream.of(p), p.getCollectedProjects().stream())).map(MavenProject::getArtifacts).flatMap(Set::stream).collect(Collectors.toSet()).stream().map(a -> new DeppyArtifact(a, eventSpy.modelOf(a)));
        } finally {
            CurrentEventSpy.remove();
        }
    }
}
