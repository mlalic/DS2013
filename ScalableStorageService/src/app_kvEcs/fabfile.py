from fabric.api import env
from fabric.api import run

env.hosts = []
server_map = {}

def buildList(*servers):
    """
    Build a List of Servers on which 
    KVServer must be started. 
    Server List Input Format : name/ip/port,name/ip/port/
    """
    for server in servers:
        server_list = server.split(",")
        server_details = server.split("/")
        if server_map.has_key(server_details[1]):
            server_map[server_details[1]][0].append(
                server_details[0])
            server_map[server_details[1]][1].append(
                server_details[2])
        else:
            server_map[server_details[1]] = ([server_details[0]], [server_details[2]])
            env.hosts.append(server_details[1])
 

def deploy():
    """
    Execute the Run Server command on the specified list of 
    Servers
    """
    global server_map
    for server in server_map.get(env.host_string)[0]:
        print server
        port = server_map.get(env.host_string)[1][server_map.get(env.host_string)[0].index(server)]
        run("java -jar ms3-server.jar " + port + " " +
            server + "&")
