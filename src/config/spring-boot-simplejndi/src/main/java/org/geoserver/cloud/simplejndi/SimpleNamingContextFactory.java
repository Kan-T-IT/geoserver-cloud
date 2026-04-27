/* (c) 2022 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.cloud.simplejndi;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

/**
 * Simple {@link InitialContextFactory} that always returns the same shared {@link SimpleNamingContext} instance,
 * ignoring the supplied environment.
 *
 * @see SimpleNamingContext
 */
class SimpleNamingContextFactory implements InitialContextFactory {

    private SimpleNamingContext initialContext = new SimpleNamingContext();

    /** {@inheritDoc} Returns the singleton {@link SimpleNamingContext} held by this factory. */
    @Override
    public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
        return initialContext;
    }
}
