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

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DeppyTestCase {
    private static Deppy instance;

    @BeforeClass
    public static void beforeClass() {
        instance = new Deppy(".");
    }

    @Test
    public void testArtifacts() {
        assertNotNull(instance.getArtifacts());
    }

    @Test
    public void testDeppy() throws Exception {
        Deppy.main(new String[]{});
    }

    @Test
    public void testProjectArtifact() {
        final DeppyArtifact project = instance.getProjectArtifact();
        assertNotNull(project);
        assertEquals("org.jboss.beach", project.getGroupId());
        assertEquals("jboss-beach-deppy", project.getArtifactId());
        assertNotNull(project.getVersion()); // semantics of version are not important here
    }
}
