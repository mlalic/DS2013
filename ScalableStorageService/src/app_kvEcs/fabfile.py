from fabric.api import env
from fabric.api import run

env.hosts = []
global_ports = []

def buildList (*servers):
    for server in servers:
        env.hosts.append(server)

def buildPorts(*ports):
    for port in ports:
        global_ports.append(port)

def deploy():
    run("java -jar ms3-server.jar "+global_ports[0]+" ERROR &")    
    global_ports = global_port[1:]
