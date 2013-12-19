# This script assumes that the ms3-server.jar binary is already
# found in the remote server and is hosted in the home directory
# of the user which runs it.
ssh -n $1@$2 "nohup java -jar ms3-server.jar $3 $4 >/dev/null 2>&1 &"
