package Ports;



import Components.Node;

import fr.sorbonne_u.components.ComponentI;

import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessSyncCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;


public class ContentAccessSyncCI_InboundPort extends AbstractInboundPort implements ContentAccessSyncCI{


	public ContentAccessSyncCI_InboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ContentAccessSyncCI.class, owner);
		
	}

	private static final long serialVersionUID = 1L;

	

	@Override
	public ContentDataI getSync(String computationURI, ContentKeyI key) throws Exception {
		
		return this.getOwner().handleRequest(owner-> ((Node)owner).getSync(computationURI, key));
	}

	@Override
	public ContentDataI putSync(String computationURI, ContentKeyI key, ContentDataI value) throws Exception {
		
		return this.getOwner().handleRequest(owner-> ((Node)owner).putSync(computationURI, key, value));
	}

	@Override
	public ContentDataI removeSync(String computationURI, ContentKeyI key) throws Exception {
		
		return this.getOwner().handleRequest(owner-> ((Node)owner).removeSync(computationURI, key));
	}

	@Override
	public void clearComputation(String computationURI) throws Exception {
		//
		
	}

	

}