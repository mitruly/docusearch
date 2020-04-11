#!/usr/bin/env bash

# Fetch docusearch process IDs
PID=$(docker ps -a | grep -e "docusearch" | awk '{print $1}')

# Kill containers
echo "killing docusearch containers..."
echo $(echo "$PID" | xargs -I {} docker kill {})
echo

# Remove process
echo "removing docusearch containers..."
echo $(echo "$PID" | xargs -I {} docker rm -v {})
echo
