package Endpoints;


import Connectors.AccessSyncCI_Connector;
import Ports.ContentAccessSyncCI_InboundPort;
import Ports.ContentAccessSyncCI_OutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.endpoints.BCMEndPoint;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessSyncCI;
import fr.sorbonne_u.cps.mapreduce.utils.URIGenerator;



public class AccessSync_edp extends BCMEndPoint<ContentAccessSyncCI> {
	
	private static final long serialVersionUID = 1L;

	public AccessSync_edp() {
		super(ContentAccessSyncCI.class, ContentAccessSyncCI.class,URIGenerator.generateURI());

	}

	@Override
	protected AbstractInboundPort makeInboundPort(AbstractComponent c, String inboundPortURI) throws Exception {
		ContentAccessSyncCI_InboundPort ContentAccessSyncCI_inboundPort= new ContentAccessSyncCI_InboundPort(inboundPortURI,c);
		ContentAccessSyncCI_inboundPort.publishPort();
		
		assert ContentAccessSyncCI_inboundPort.isPublished();
		
		return ContentAccessSyncCI_inboundPort;
	}

	

	@Override
	protected ContentAccessSyncCI makeOutboundPort(AbstractComponent c, String inboundPortURI) throws Exception {
		ContentAccessSyncCI_OutboundPort ContentAccessSyncCI_outboundPort=new ContentAccessSyncCI_OutboundPort(c,URIGenerator.generateURI());
		
		ContentAccessSyncCI_outboundPort.publishPort();
		assert ContentAccessSyncCI_outboundPort.isPublished();
		c.doPortConnection(ContentAccessSyncCI_outboundPort.getPortURI(), inboundPortURI, AccessSyncCI_Connector.class.getCanonicalName());
		return ContentAccessSyncCI_outboundPort;
	}


}
