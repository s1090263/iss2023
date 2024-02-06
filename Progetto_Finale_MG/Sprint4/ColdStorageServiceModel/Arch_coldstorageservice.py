### conda install diagrams
from diagrams import Cluster, Diagram, Edge
from diagrams.custom import Custom
import os
os.environ['PATH'] += os.pathsep + 'C:/Program Files/Graphviz/bin/'

graphattr = {     #https://www.graphviz.org/doc/info/attrs.html
    'fontsize': '22',
}

nodeattr = {   
    'fontsize': '22',
    'bgcolor': 'lightyellow'
}

eventedgeattr = {
    'color': 'red',
    'style': 'dotted'
}
with Diagram('coldstorageserviceArch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
### see https://renenyffenegger.ch/notes/tools/Graphviz/attributes/label/HTML-like/index
     with Cluster('ctxcoldstorageservice', graph_attr=nodeattr):
          transporttrolley=Custom('transporttrolley','./qakicons/symActorSmall.png')
          fridgeservice=Custom('fridgeservice','./qakicons/symActorSmall.png')
     f=Custom('f','./qakicons/server.png')
     with Cluster('ctxbasicrobot', graph_attr=nodeattr):
          basicrobot=Custom('basicrobot(ext)','./qakicons/externalQActor.png')
     f=Custom('f','./qakicons/server.png')
     fridgeservice >> Edge( label='alarm', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     fridgeservice >> Edge(color='magenta', style='solid', decorate='true', label='<takecharge &nbsp; >',  fontcolor='magenta') >> transporttrolley
     transporttrolley >> Edge(color='magenta', style='solid', decorate='true', label='<engage &nbsp; moverobot &nbsp; >',  fontcolor='magenta') >> basicrobot
     f >> Edge(color='blue', style='solid', decorate='true', label='< &harr; >',  fontcolor='blue') >> fridgeservice
     fridgeservice >> Edge(color='blue', style='solid',  decorate='true', label='<stoptrolley &nbsp; resumetrolley &nbsp; >',  fontcolor='blue') >> transporttrolley
     transporttrolley >> Edge(color='blue', style='solid',  decorate='true', label='<setrobotstate &nbsp; setdirection &nbsp; disengage &nbsp; >',  fontcolor='blue') >> basicrobot
     transporttrolley >> Edge(color='blue', style='solid',  decorate='true', label='<tryagain &nbsp; >',  fontcolor='blue') >> transporttrolley
diag
