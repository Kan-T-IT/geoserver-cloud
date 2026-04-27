FROM eclipse-temurin:25-jre

LABEL maintainer="GeoServer PSC <geoserver-users@lists.sourceforge.net>"

# default JVM parameters https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/envvars002.html
# to add JVM parameters use the JAVA_OPTS env variable preferrably
ENV DEFAULT_JAVA_TOOL_OPTIONS="\
--enable-preview \
--add-opens=java.base/java.lang=ALL-UNNAMED \
--add-opens=java.base/java.util=ALL-UNNAMED \
--add-opens=java.base/java.lang.reflect=ALL-UNNAMED \
--add-opens=java.base/java.text=ALL-UNNAMED \
--add-opens=java.desktop/java.awt.font=ALL-UNNAMED \
--add-opens=java.desktop/sun.awt.image=ALL-UNNAMED \
--add-opens=java.desktop/sun.java2d.pipe=ALL-UNNAMED \
--add-opens=java.naming/com.sun.jndi.ldap=ALL-UNNAMED \
--add-opens=java.net.http/jdk.internal.net.http=ALL-UNNAMED \
--enable-native-access=ALL-UNNAMED \
-Djava.awt.headless=true \
-XX:MaxRAMPercentage=80 \
-XX:InitialRAMPercentage=30 \
-XX:+UseCompressedOops \
-XX:+UseCompactObjectHeaders \
-XX:+UseStringDeduplication \
-XX:+IgnoreUnrecognizedVMOptions \
-XX:UseSVE=0 \
-XX:UseAVX=2"

# -XX:UseSVE=0 disables Scalable Vector Extension (SVE)
# It is currently the primary workaround for a critical compatibility bug affecting Apple M4 chips running Docker on macOS 15.2+
#
# -XX:UseAVX=2 caps x86_64 vector instructions to AVX2 (Haswell, 2013+).
# This is required so the AOT cache baked into images at build time is portable
# across amd64 hosts with different CPU feature sets (e.g., GitHub Actions runners
# rotate across Azure VMs with varying AVX-512 availability, causing SIGILL when
# an AVX-512-trained cache runs on an AVX2-only host).

ENV JAVA_TOOL_OPTIONS="${DEFAULT_JAVA_TOOL_OPTIONS}"
ENV JAVA_OPTS=

# Install the system CA certificates for the JVM :wqnow that we're root
RUN USE_SYSTEM_CA_CERTS=true /__cacert_entrypoint.sh
