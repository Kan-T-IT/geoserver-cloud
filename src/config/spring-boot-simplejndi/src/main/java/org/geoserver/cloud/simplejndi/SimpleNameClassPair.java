/* (c) 2022 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.cloud.simplejndi;

import java.io.Serial;
import java.util.Objects;

/**
 * {@link javax.naming.NameClassPair} subclass that adds value-based {@link #equals(Object)} and {@link #hashCode()},
 * making instances usable in collections and assertions during enumeration of {@link SimpleNamingContext} bindings.
 *
 * @since 1.0
 */
class SimpleNameClassPair extends javax.naming.NameClassPair {

    @Serial
    private static final long serialVersionUID = 1L;

    public SimpleNameClassPair(String name, String className) {
        super(name, className);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof javax.naming.NameClassPair p) {
            return Objects.equals(getName(), p.getName()) && Objects.equals(getClassName(), p.getClassName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getClassName());
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
