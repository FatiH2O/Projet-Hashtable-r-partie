package FrontEnd;

import java.io.Serializable;


import Endpoints.DHTServices_Simple_endpoint;
import Endpoints.AccessSync_edp;
import Endpoints.Composite_Endpoint;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.exceptions.ConnectionException;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessSyncCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.frontend.DHTServicesCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceSyncCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;
import fr.sorbonne_u.cps.mapreduce.utils.URIGenerator;

@OfferedInterfaces(offered= {DHTServicesCI.class})
@RequiredInterfaces(required= {MapReduceSyncCI.class,ContentAccessSyncCI.class})


public class Front extends AbstractComponent{
	
	
	/**endpoint qui relie le Client au FRont**/
	DHTServices_Simple_endpoint Client_Front_edp;
	
	/**edp qui relie front au client**/
	Composite_Endpoint Front_to_Access_edp;

	
	protected Front(DHTServices_Simple_endpoint Client_Front_edp,Composite_Endpoint  Front_to_Access_edp) throws ConnectionException {
		super(1, 0);
		this.Client_Front_edp=Client_Front_edp;
		this.Front_to_Access_edp=Front_to_Access_edp;
		this.Client_Front_edp.initialiseServerSide(this);
	}

@Override
	public void			start() throws ComponentStartException
	{
		this.logMessage("starting consumer component.") ;
		super.start() ;

		
		try {
			
			this.Front_to_Access_edp.initialiseClientSide(this);
		} catch (ConnectionException e) {
			
			throw new ComponentStartException(e);
		};
	}
	
	public ContentDataI get(ContentKeyI key) throws Exception {
		
		return this.Front_to_Access_edp.getAccessSync_edp().getClientSideReference().getSync(URIGenerator.generateURI(), key);
	}

	
	public ContentDataI put(ContentKeyI key, ContentDataI value) throws Exception {
		
		return this.Front_to_Access_edp.getAccessSync_edp().getClientSideReference().putSync(URIGenerator.generateURI(), key, value);
	}

	
	public ContentDataI remove(ContentKeyI key) throws Exception {
		
		return this.Front_to_Access_edp.getAccessSync_edp().getClientSideReference().removeSync(URIGenerator.generateURI(), key);
	}

	
	public <R extends Serializable, A extends Serializable> A mapReduce(SelectorI selector, ProcessorI<R> processor,
			ReductorI<A, R> reductor, CombinatorI<A> combinator, A initialAcc) throws Exception {
		String Uri= URIGenerator.generateURI();
		this.Front_to_Access_edp.getMapreduce_edp().getClientSideReference().mapSync(Uri, selector, processor);
		return this.Front_to_Access_edp.getMapreduce_edp().getClientSideReference().reduceSync(Uri, reductor, combinator, initialAcc);
	}
	
	@Override
	public synchronized void finalise() throws Exception {
		assert !Front_to_Access_edp.serverSideClean();
		this.Front_to_Access_edp.cleanUpClientSide();
		super.finalise();
		
	}
	
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		this.Client_Front_edp.cleanUpServerSide();
		super.shutdown();
	}

}
