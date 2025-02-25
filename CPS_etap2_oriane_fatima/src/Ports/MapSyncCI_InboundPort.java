package Ports;

import java.io.Serializable;
import Components.Node;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ComponentI.ComponentService;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceSyncCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;

public class MapSyncCI_InboundPort extends  AbstractInboundPort implements MapReduceSyncCI{

	private static final long serialVersionUID = 1L;

	public MapSyncCI_InboundPort(String uri, ComponentI owner) throws Exception {
		super(uri,MapReduceSyncCI.class, owner);
		assert owner instanceof Node;
		
	}

	@Override
	public <R extends Serializable> void mapSync(String computationURI, SelectorI selector, ProcessorI<R> processor)
			throws Exception {
		this.getOwner().handleRequest((ComponentService<Node>)owner-> (owner).mapSync(computationURI, selector, processor));
		
	}

	@Override
	public <A extends Serializable, R> A reduceSync(String computationURI, ReductorI<A, R> reductor,
			CombinatorI<A> combinator, A currentAcc) throws Exception {
		
		return this.getOwner().handleRequest(owner->((Node)owner).reduceSync(computationURI, reductor, combinator, currentAcc));
				
	}

	@Override
	public void clearMapReduceComputation(String computationURI) throws Exception {
	    this.clearMapReduceComputation(computationURI);
	}

}
