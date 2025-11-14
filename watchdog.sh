#!/bin/bash
# Auto-shutdown watchdog for Minecraft server
# Shuts down server when no players are online for IDLE_MINUTES

IDLE_MINUTES=${IDLE_MINUTES:-30}
CHECK_INTERVAL=${CHECK_INTERVAL:-60}  # Check every 60 seconds

echo "Watchdog started. Will shutdown after $IDLE_MINUTES minutes of no players."
idle_time=0

while true; do
    sleep $CHECK_INTERVAL

    # Check if server is running
    if ! pgrep -f "java.*server.jar" > /dev/null; then
        echo "Server not running, exiting watchdog"
        exit 0
    fi

    # Get online player count from server logs
    player_count=$(tail -100 /minecraft/logs/latest.log 2>/dev/null | grep -oP 'There are \K\d+(?=/\d+ players online)' | tail -1)

    # If we can't determine player count, assume server is starting up
    if [ -z "$player_count" ]; then
        idle_time=0
        continue
    fi

    if [ "$player_count" -eq 0 ]; then
        idle_time=$((idle_time + CHECK_INTERVAL))
        echo "No players online. Idle for $((idle_time / 60)) minutes."

        if [ $idle_time -ge $((IDLE_MINUTES * 60)) ]; then
            echo "Idle timeout reached. Shutting down server to save resources..."
            pkill -f "java.*server.jar"
            exit 0
        fi
    else
        echo "$player_count players online. Resetting idle timer."
        idle_time=0
    fi
done
