FROM openjdk:17-slim

# Install necessary packages
RUN apt-get update && \
    apt-get install -y wget curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /minecraft

# Environment variables with defaults
ENV MINECRAFT_VERSION=1.20.1
ENV NEOFORGE_VERSION=47.1.106
ENV MEMORY=4G
ENV SERVER_PORT=25565

# Download NeoForge installer
RUN wget -O neoforge-installer.jar "https://maven.neoforged.net/releases/net/neoforged/forge/${MINECRAFT_VERSION}-${NEOFORGE_VERSION}/forge-${MINECRAFT_VERSION}-${NEOFORGE_VERSION}-installer.jar"

# Install NeoForge server
RUN java -jar neoforge-installer.jar --installServer

# Clean up installer
RUN rm neoforge-installer.jar

# Accept EULA
RUN echo "eula=true" > eula.txt

# Create server.properties with default values
RUN echo "server-port=${SERVER_PORT}" > server.properties && \
    echo "online-mode=true" >> server.properties && \
    echo "motd=Mirda Boss Mod Server - Prepare for Battle!" >> server.properties && \
    echo "difficulty=hard" >> server.properties && \
    echo "max-players=20" >> server.properties && \
    echo "view-distance=10" >> server.properties && \
    echo "enable-command-block=true" >> server.properties && \
    echo "spawn-protection=0" >> server.properties && \
    echo "max-world-size=29999984" >> server.properties

# Copy the built mod JAR from the build artifacts
COPY build/libs/*.jar /minecraft/mods/

# Create mods directory if it doesn't exist
RUN mkdir -p /minecraft/mods

# Expose Minecraft server port
EXPOSE ${SERVER_PORT}

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=120s --retries=3 \
    CMD pgrep -f "java.*server.jar" || exit 1

# Start the server
CMD java -Xmx${MEMORY} -Xms2G \
    -XX:+UseG1GC \
    -XX:+ParallelRefProcEnabled \
    -XX:MaxGCPauseMillis=200 \
    -XX:+UnlockExperimentalVMOptions \
    -XX:+DisableExplicitGC \
    -XX:+AlwaysPreTouch \
    -XX:G1NewSizePercent=30 \
    -XX:G1MaxNewSizePercent=40 \
    -XX:G1HeapRegionSize=8M \
    -XX:G1ReservePercent=20 \
    -XX:G1HeapWastePercent=5 \
    -XX:G1MixedGCCountTarget=4 \
    -XX:InitiatingHeapOccupancyPercent=15 \
    -XX:G1MixedGCLiveThresholdPercent=90 \
    -XX:G1RSetUpdatingPauseTimePercent=5 \
    -XX:SurvivorRatio=32 \
    -XX:+PerfDisableSharedMem \
    -XX:MaxTenuringThreshold=1 \
    -Dusing.aikars.flags=https://mcflags.emc.gs \
    -Daikars.new.flags=true \
    -jar server.jar nogui
