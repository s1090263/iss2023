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
with Diagram('alarmdeviceArch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
### see https://renenyffenegger.ch/notes/tools/Graphviz/attributes/label/HTML-like/index
     with Cluster('ctxalarmdevice', graph_attr=nodeattr):
          alarmdevice=Custom('alarmdevice','./qakicons/symActorSmall.png')
          warningdevice=Custom('warningdevice','./qakicons/symActorSmall.png')
          sonar=Custom('sonar(coded)','./qakicons/codedQActor.png')
          datacleaner=Custom('datacleaner(coded)','./qakicons/codedQActor.png')
          distancefilter=Custom('distancefilter(coded)','./qakicons/codedQActor.png')
     with Cluster('ctxcoldstorageservice', graph_attr=nodeattr):
          fridgeservice=Custom('fridgeservice(ext)','./qakicons/externalQActor.png')
          transporttrolley=Custom('transporttrolley(ext)','./qakicons/externalQActor.png')
     transporttrolley >> Edge(color='blue', style='solid',  decorate='true', label='<coapUpdate &nbsp; >',  fontcolor='blue') >> warningdevice
     alarmdevice >> Edge(color='blue', style='solid',  decorate='true', label='<stoptrolley &nbsp; resumetrolley &nbsp; >',  fontcolor='blue') >> fridgeservice
     fridgeservice >> Edge(color='blue', style='solid',  decorate='true', label='<coapUpdate &nbsp; >',  fontcolor='blue') >> warningdevice
diag
