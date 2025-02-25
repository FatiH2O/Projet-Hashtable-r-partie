package Endpoints;

import Connectors.AccessSyncCI_Connector;
import Connectors.MapSync_Connector;
import Ports.MapSyncCI_InboundPort;
import Ports.MapSyncCI_OutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.endpoints.BCMEndPoint;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceSyncCI;
import fr.sorbonne_u.cps.mapreduce.utils.URIGenerator;

public class MapSync_edp extends BCMEndPoint<MapReduceSyncCI>{

	private static final long serialVersionUID = 1L;

	public MapSync_edp() {
		super(MapReduceSyncCI.class, MapReduceSyncCI.class, URIGenerator.generateURI());
		
	}

	@Override
	protected AbstractInboundPort makeInboundPort(AbstractComponent c, String inboundPortURI) throws Exception {
		MapSyncCI_InboundPort MapSync_inboundport= new MapSyncCI_InboundPort(inboundPortURI,c);
		MapSync_inboundport.publishPort();
		
		assert MapSync_inboundport.isPublished();
		
		return MapSync_inboundport;
	}

	@Override
	protected MapReduceSyncCI makeOutboundPort(AbstractComponent c, String inboundPortURI) throws Exception {
		MapSyncCI_OutboundPort MapSync_outboundPort=new MapSyncCI_OutboundPort(URIGenerator.generateURI(),c);
		MapSync_outboundPort.publishPort();
		
		assert MapSync_outboundPort.isPublished();
		
		c.doPortConnection(MapSync_outboundPort.getPortURI(), inboundPortURI, MapSync_Connector.class.getCanonicalName());
		return MapSync_outboundPort;
	}

}
