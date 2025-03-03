package Components;

import java.io.Serializable;

import fr.sorbonne_u.exceptions.PostconditionException;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.stream.Stream;

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
	
	/**
	 * @param Front_to_Node_edp           Composte edp qui relie front au Noeud, apeler par le front
	 * 
	 * @param Client_side                 Composite edp pour qui relie le noeud courant au noeud client avant moi
	 * 
	 * @param Server_side                 Composite edp qui relie le noeud courant au prochain noeud server
	 * 
	 * @param BigInterval                 Interval qu'aucune hashtable ne doit dépasser
	 * 
	 * @param monInterval                 Interval assigné au noeud et qui appartient à [0,BigInterval]
	 * 
	 * @param tab						  hashtable du noeud
	 */

	
	private Composite_Endpoint Front_to_Node_edp;
	private Composite_Endpoint Client_side;
	private Composite_Endpoint Server_side;
	
	private int BigInterval;
	private IntInterval monInterval;
	private Hashtable<ContentKeyI, ContentDataI> tab;
	

	protected Node(int BigInterval,IntInterval monInterval,Composite_Endpoint Front_to_Node_edp,Composite_Endpoint Client_side,Composite_Endpoint Server_side)
			throws ConnectionException {
		super(1, 0);
		tab = new Hashtable<ContentKeyI, ContentDataI>();
		
		this.BigInterval=BigInterval;
		this.monInterval=monInterval;
		
		this.Front_to_Node_edp=Front_to_Node_edp;
		assert	Front_to_Node_edp != null :
			new PostconditionException("Front_to_Client_edp is null");
		
		this.Server_side=Server_side;
		assert	Server_side != null :
			new PostconditionException("Server_side is null");
		
		this.Client_side=Client_side;
		assert	Client_side != null :
			new PostconditionException("Client_side is null");
		
		this.Front_to_Node_edp.initialiseServerSide(this);
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
		assert	Server_side != null :
			new PostconditionException("Server_side is null");
		
		this.Client_side=Client_side;
		assert	Client_side != null :
			new PostconditionException("Client_side is null");
		
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
	
	
	
	
	/**recuperer une donnée de la hashtable **/
	
	public ContentDataI getSync(String computationURI, ContentKeyI key) throws Exception {
		int h = key.hashCode()%BigInterval;
		if(monInterval.in(h)) {
			if(tab.get(key) != null) {
				System.out.println("Key " + key.toString() + " with hash " + h + " well get from node with interval [" + 
			monInterval.first() + ", " + monInterval.last() + "]"+ " value is: "+ tab.get(key).toString());
			}
			else {
				System.out.println("Warning : key " + key.toString() + " not in table !");
			}
			return tab.get(key);
		}
		else {
			
            return Server_side.getAccessSync_edp().getClientSideReference().getSync(computationURI, key);

		}
	}

	
	/**mettre une donnéé dans la hashtable**/
	public ContentDataI putSync(String computationURI, ContentKeyI key, ContentDataI value) throws Exception {
		int h = key.hashCode()%BigInterval;
		if(monInterval.in(h)) {
			System.out.println("Key " + key.toString() + " with hash " + h + " well put in node with interval [" + monInterval.first() + ", " + monInterval.last() + "]");
			return tab.put(key, value);
		}
		else {
		
            return 	Server_side.getAccessSync_edp().getClientSideReference().putSync(computationURI, key, value);
		}
	}

	
	/**enlever une donnée de a hashtable**/
	public ContentDataI removeSync(String computationURI, ContentKeyI key) throws Exception {
		int h = key.hashCode()%BigInterval;
		if(monInterval.in(h)) {
			if(tab.get(key) != null) {
				System.out.println("Key " + key.toString() + " with hash " + h + " removed from node with interval [" + monInterval.first() + ", " + monInterval.last() + "]");
			}
			else {
				System.out.println("Warning : key " + key.toString() + " cannot be removed from table !");
			}
			return tab.remove(key);
		}
		else { 
            return 	this.Server_side.getAccessSync_edp().getClientSideReference().removeSync(computationURI, key);

		}
	}

	/**pas utiliser pour le moment**/
	public void clearComputation(String computationURI) throws Exception {
		this.Server_side.getAccessSync_edp().getClientSideReference().clearComputation(computationURI);
	}

	/**Attribut qui nous permet de stocker les resultats intermédiaires renvoyés par mapSync**/
	private HashMap<String, Stream<Serializable>> resTempMap= new HashMap<String, Stream<Serializable>>();	
	
	
	
	@SuppressWarnings("unchecked")
	public <R extends Serializable> void mapSync(String computationURI, SelectorI selector, ProcessorI<R> processor)
			throws Exception {
		
		
		 Stream<R> TempData=  (Stream<R>) tab.values() .stream()
								  							.filter(selector)
								  							.map(processor);
					
		resTempMap.put(computationURI, (Stream<Serializable>) TempData);
				
		//afficher le contenu de TempData pour debuger 
		//resTempMap.get(computationURI).forEach(n ->System.out.println( n));
		
		if(this.monInterval.last()<BigInterval-1) {
			//this.logMessage("au prochain noeud" ); // pour debugger 
			Server_side.getMapreduce_edp().getClientSideReference().mapSync(computationURI, selector, processor);
		}
	}

	
	public <A extends Serializable, R> A reduceSync(String computationURI, ReductorI<A, R> reductor,
			CombinatorI<A> combinator, A currentAcc) throws Exception {
		
		/**appliquer le reduce sur les resultats temporaires**/
		@SuppressWarnings("unchecked")
		A resTempReduce= resTempMap.get(computationURI)
										   .map(v -> (R) v)
										   .reduce((A) currentAcc,  reductor, combinator);
				
		System.out.println("resultat du mapreduce Avant le combinator : " + resTempReduce );
				
		/** Si ce n'est pas le dernier noeud, appliquer combiner avec le prochain noeud**/
		if(this.monInterval.last()<BigInterval-1) resTempReduce=combinator.apply( resTempReduce,
				 																Server_side.getMapreduce_edp()
																							.getClientSideReference()
																							.reduceSync(computationURI, reductor, combinator, currentAcc));
		
		
		System.out.println("resultat du mapreduce Apres le combinator : " + resTempReduce );
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
		try {
		
		if( this.Front_to_Node_edp !=null) this.Front_to_Node_edp.cleanUpServerSide();
		
		}catch (Exception e) {
		throw new ComponentShutdownException(e);
		}
		
		try {
			
		this.Client_side.cleanUpServerSide();
		
		}catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		
		super.shutdown();
		}
	
	
}
