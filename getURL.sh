#!/bin/bash
curl -s localhost:4040/api/tunnels | jq -r .tunnels[0].public_url