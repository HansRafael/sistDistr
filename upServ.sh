#!/bin/bash
PORT=12345
ngrok tcp $PORT
echo $PORT