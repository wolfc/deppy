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
package org.jboss.beach.maven.eventspy;

import org.apache.maven.eventspy.EventSpy;
import org.codehaus.plexus.component.annotations.Component;

/**
 * Allow an EventSpy to be specified in current invocation context.
 */
@Component(role = EventSpy.class)
public class CurrentEventSpy implements EventSpy {
    private static final ThreadLocal<EventSpy> current = new ThreadLocal<>();

    @Override
    public void close() throws Exception {
        invoke(EventSpy::close);
    }

    @Override
    public void init(final Context context) throws Exception {
        invoke(spy -> { spy.init(context); });
    }

    protected void invoke(final ThrowingConsumer<EventSpy, Exception> call) throws Exception {
        final EventSpy spy = current.get();
        if (spy != null) {
            try {
                call.accept(spy);
            } catch (Exception e) {
                // TODO: only when maven is in debug mode
                e.printStackTrace();
                throw e;
            }
        }
        else throw new IllegalStateException("No current EventSpy");
    }

    @Override
    public void onEvent(final Object event) throws Exception {
        invoke(spy -> { spy.onEvent(event); });

    }

    public static void remove() {
        current.remove();
    }

    public static void set(final EventSpy spy) {
        current.set(spy);
    }
}
