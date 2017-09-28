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

public class Deppy {
    public static void main(final String[] args) throws Exception {
        System.setProperty(MavenCli.MULTIMODULE_PROJECT_DIRECTORY, ".");
        System.setProperty("maven.ext.class.path", DeppyEventSpy.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        final MavenCli cli = new MavenCli();
        cli.doMain(new String[]{"dependency:resolve"}, ".", System.out, System.err);
        // to make it thread safe DeppyEventSpy needs to be plucked out of cli.eventSpyDispatcher
    }
}
