package Components;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.stream.Stream;

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
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceSyncCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;
import fr.sorbonne_u.cps.mapreduce.utils.IntInterval;



@OfferedInterfaces(offered= {MapReduceSyncCI.class,ContentAccessSyncCI.class})
@RequiredInterfaces(required= {MapReduceSyncCI.class,ContentAccessSyncCI.class})


public class Node extends AbstractComponent {

	/**edp qui relie front au client**/
	Composite_Endpoint Front_to_Access_edp;
	
	/**edpt pour le neuds cliend avant moi**/
	Composite_Endpoint Client_side;
	
	/**edp qui relie mon noeud au prochain noeud server**/
	Composite_Endpoint Server_side;
	
	int BigInterval;
	private IntInterval monInterval;
	private Hashtable<ContentKeyI, ContentDataI> tab;
	

	protected Node(int BigInterval,IntInterval monInterval,Composite_Endpoint Front_to_Access_edp,Composite_Endpoint Client_side,Composite_Endpoint Server_side)
			throws ConnectionException {
		super(1, 0);
		tab = new Hashtable<ContentKeyI, ContentDataI>();
		this.BigInterval=BigInterval;
		this.monInterval=monInterval;
		this.Front_to_Access_edp=Front_to_Access_edp;
		this.Server_side=Server_side;
		this.Client_side=Client_side;
		this.Front_to_Access_edp.initialiseServerSide(this);
		this.Client_side.initialiseServerSide(this);
		this.toggleLogging();
		this.toggleTracing();
		
	}
	
	protected Node(int BigInterval,IntInterval monInterval,Composite_Endpoint Client_side,Composite_Endpoint Server_side) throws ConnectionException {
		super(1, 0);
		tab = new Hashtable<ContentKeyI, ContentDataI>();
		this.BigInterval=BigInterval;
		this.monInterval=monInterval;
		this.Server_side=Server_side;
		this.Client_side=Client_side;
		this.Client_side.initialiseServerSide(this);
		this.toggleLogging();
		this.toggleTracing();
		
		
	}

	@Override
	public void			start() throws ComponentStartException
	{
		this.logMessage("starting Node component.") ;
		super.start() ;

		
		try {
			
			
			
			this.Server_side.initialiseClientSide(this);
		} catch (ConnectionException e) {
			
			throw new ComponentStartException(e);
		};
	}
	
	public ContentDataI getSync(String computationURI, ContentKeyI key) throws Exception {
		int h = key.hashCode()%BigInterval;
		if(monInterval.in(h)) {
			if(tab.get(key) != null) {
				this.logMessage("Key " + key.toString() + " with hash " + h + " well get from node with interval [" + monInterval.first() + ", " + monInterval.last() + "]");
			}
			else {
				this.logMessage("Warning : key " + key.toString() + " not in table !");
			}
			return tab.get(key);
		}
		else {
			
		
			
            return Server_side.getAccessSync_edp().getClientSideReference().getSync(computationURI, key);

		}
	}

	

	public ContentDataI putSync(String computationURI, ContentKeyI key, ContentDataI value) throws Exception {
		int h = key.hashCode()%BigInterval;
		if(monInterval.in(h)) {
			this.logMessage("Key " + key.toString() + " with hash " + h + " well put in node with interval [" + monInterval.first() + ", " + monInterval.last() + "]");
			return tab.put(key, value);
		}
		else {
		
            return 	Server_side.getAccessSync_edp().getClientSideReference().putSync(computationURI, key, value);

		}
	}

	public ContentDataI removeSync(String computationURI, ContentKeyI key) throws Exception {
		int h = key.hashCode()%BigInterval;
		if(monInterval.in(h)) {
			if(tab.get(key) != null) {
				this.logMessage("Key " + key.toString() + " with hash " + h + " removed from node with interval [" + monInterval.first() + ", " + monInterval.last() + "]");
			}
			else {
				this.logMessage("Warning : key " + key.toString() + " cannot be removed from table !");
			}
			return tab.remove(key);
		}
		else { 
            return 	this.Server_side.getAccessSync_edp().getClientSideReference().removeSync(computationURI, key);

		}
	}

	public void clearComputation(String computationURI) throws Exception {
		this.Server_side.getAccessSync_edp().getClientSideReference().clearComputation(computationURI);
	}

	private HashMap<String, Stream<Serializable>> resTempMap= new HashMap<String, Stream<Serializable>>();	
	@SuppressWarnings("unchecked")
	public <R extends Serializable> void mapSync(String computationURI, SelectorI selector, ProcessorI<R> processor)
			throws Exception {
		
		
		 Stream<R> TempData=  (Stream<R>) tab.values() .stream()
								  							.filter(selector)
								  							.map(processor);
					
		resTempMap.put(computationURI, (Stream<Serializable>) TempData);
				
		//afficher le contenu de TempData
		//resTempMap.get(computationURI).forEach(n ->this.logMessage( n));
		
		if(this.monInterval.last()<BigInterval-1) {
			this.logMessage("au prochain noeud" );
			Server_side.getMapreduce_edp().getClientSideReference().mapSync(computationURI, selector, processor);
		}
	}

	
	public <A extends Serializable, R> A reduceSync(String computationURI, ReductorI<A, R> reductor,
			CombinatorI<A> combinator, A currentAcc) throws Exception {
		
		@SuppressWarnings("unchecked")
		A resTempReduce= resTempMap.get(computationURI)
										   .map(v -> (R) v)
										   .reduce((A) currentAcc,  reductor, combinator);
				
		this.logMessage("resultat du mapreduce Avvvv : " + resTempReduce );
				
		if(this.monInterval.last()<BigInterval-1) resTempReduce=combinator.apply( resTempReduce,Server_side.getMapreduce_edp().getClientSideReference()
				.reduceSync(computationURI, reductor, combinator, currentAcc));
		this.logMessage("resultat du mapreduce AP : " + resTempReduce );
			
		return resTempReduce ;
	}

	public void clearMapReduceComputation(String computationURI) throws Exception {
		Server_side.getMapreduce_edp().getClientSideReference().clearMapReduceComputation(computationURI);
	}
	

	@Override
	public synchronized void finalise() throws Exception {
		this.Server_side.cleanUpClientSide();
		super.finalise();
		
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		assert Front_to_Access_edp.clientSideClean();
		
		this.Front_to_Access_edp.cleanUpServerSide();
		this.Client_side.cleanUpServerSide();
		super.shutdown();
	}
	
	
}
