package Ports;

import java.io.Serializable;

import FrontEnd.Front;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.frontend.DHTServicesCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;


public class DHTServicesCI_InboundPort extends AbstractInboundPort implements DHTServicesCI{

	private static final long serialVersionUID = 1L;
	
	
	public DHTServicesCI_InboundPort(ComponentI owner, String uri)
			throws Exception {
		super(uri,DHTServicesCI.class, owner);
		
		assert owner instanceof Front;
	}

	@Override
	public ContentDataI get(ContentKeyI key) throws Exception {
		
		return  this.getOwner().handleRequest(owner-> ((Front)owner).get(key));
	}

	@Override
	public ContentDataI put(ContentKeyI key, ContentDataI value) throws Exception {
		
		return this.getOwner().handleRequest(owner-> ((Front)owner).put(key, value));
	}

	@Override
	public ContentDataI remove(ContentKeyI key) throws Exception {
		// TODO Auto-generated method stub
		return this.getOwner().handleRequest(owner-> ((Front)owner).remove(key));
	}

	@Override
	public <R extends Serializable, A extends Serializable> A mapReduce(SelectorI selector, ProcessorI<R> processor,
			ReductorI<A, R> reductor, CombinatorI<A> combinator, A initialAcc) throws Exception {
		return 	this.getOwner().handleRequest(owner-> ((Front)owner).mapReduce(selector, processor, reductor, combinator, initialAcc));

	}

}
