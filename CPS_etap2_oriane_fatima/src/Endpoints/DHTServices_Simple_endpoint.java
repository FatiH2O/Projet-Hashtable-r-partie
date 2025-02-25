package Endpoints;

import Connectors.DHTServicesConnector_Connector;

import fr.sorbonne_u.exceptions.PostconditionException;
import Ports.DHTServicesCI_InboundPort;
import Ports.DHTServicesCI_OutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.endpoints.BCMEndPoint;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.frontend.DHTServicesCI;
import fr.sorbonne_u.cps.mapreduce.utils.URIGenerator;

public class DHTServices_Simple_endpoint extends BCMEndPoint<DHTServicesCI>{

	
	public DHTServices_Simple_endpoint() {
		super(DHTServicesCI.class, DHTServicesCI.class,URIGenerator.generateURI());
		
	}
	

	private static final long serialVersionUID = 1L;

	
	@Override
	protected AbstractInboundPort makeInboundPort(AbstractComponent c, String inboundPortURI) throws Exception {
		DHTServicesCI_InboundPort DHTservices_inboundPort= new DHTServicesCI_InboundPort(c,inboundPortURI);
		DHTservices_inboundPort.publishPort();
		return DHTservices_inboundPort;
	}

	@Override
	protected DHTServicesCI makeOutboundPort(AbstractComponent c, String inboundPortURI) throws Exception {
		DHTServicesCI_OutboundPort DHTservices_ountbundPort;
		DHTservices_ountbundPort= new DHTServicesCI_OutboundPort(URIGenerator.generateURI(),c);
		DHTservices_ountbundPort.publishPort();
		c.doPortConnection(DHTservices_ountbundPort.getPortURI(), inboundPortURI, DHTServicesConnector_Connector.class.getCanonicalName());
		return DHTservices_ountbundPort;
	}

}
