from fabric.api import env
from fabric.api import run

env.hosts = []
global_ports = []
global_names = []

def buildList (*servers):
    for server in servers:
        env.hosts.append(server)

def buildPorts(*ports):
    for port in ports:
        global_ports.append(port)

def buildNames(*names):
    for name in names:
        global_names.append(name)

def deploy():
    global global_ports
    global global_names
    run("java -jar ms3-server.jar "+global_ports[0]+" ERROR &")    
    global_ports = global_ports[1:]
    global_names = global_names[1:]
