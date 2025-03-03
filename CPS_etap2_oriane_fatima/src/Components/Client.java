package Components;

import Data.Author;

import Data.Book;
import Endpoints.DHTServices_Simple_endpoint;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.exceptions.ConnectionException;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.frontend.DHTServicesCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;



@RequiredInterfaces(required= {DHTServicesCI.class})
public class Client extends  AbstractComponent {
	
	
	/** Endpoint qui permet de connecter le client au Front **/
	DHTServices_Simple_endpoint endpoint;
	
	protected Client(DHTServices_Simple_endpoint endpoint) throws Exception {
		super(1, 0);
		this.endpoint=endpoint;
		this.toggleLogging();
		this.toggleTracing();
		
		
	}
	
	
	/*****************************************************/
	/**         Cycle de vie du composant              **/
	/****************************************************/

	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;
		this.logMessage("je suis dans la methode start du client");

		try {
			this.endpoint.initialiseClientSide(this);
			this.logMessage("parti client INITIALISÉ");

		} catch (ConnectionException e) {
			
			throw new ComponentStartException();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws Exception {
		super.execute();
		this.logMessage("je suis dans la methode execute du client");
		
	   /** Creer les données**/
		
		Author auth1 = new Author("A1");
		Author auth2 = new Author("A2");
		Author auth3 = new Author("A3");
		Author auth4 = new Author("A4");
		Author auth7 = new Author("A7");
		Author auth9 = new Author("A9");
		Author auth10 = new Author("A10");
		Author auth23 = new Author("A23");
		Author auth11 = new Author("A11");
		Author auth12 = new Author("A12");
		Author auth13 = new Author("A13");
		Author auth14 = new Author("A14");
		Author auth19 = new Author("A19");
		
		Book book1 = new Book("B1");
		Book book2 = new Book("B2");
		Book book3 = new Book("B3");
		Book book4 = new Book("B4");
		Book book9 = new Book("B9");
		Book book10 = new Book("S10");
		Book booka = new Book("S1");
		Book bookd = new Book("S2");
		Book books = new Book("S3");
		Book bookk = new Book("S4");
		Book booko = new Book("S9");
		
		
		/**Mettre les données dans la table de hashage**/
		System.out.println("------------------------------------------------------------------------");
		System.out.println("-----------------Mettre les données dans la hashtable-------------------");
		System.out.println("------------------------------------------------------------------------");
		
		
		 this.endpoint.getClientSideReference().put(auth1, book1);
		 this.endpoint.getClientSideReference().put(auth2, book2);
		 this.endpoint.getClientSideReference().put(auth3, book3);
		 this.endpoint.getClientSideReference().put(auth4, book4);
		 this.endpoint.getClientSideReference().put(auth9, book9);
		 this.endpoint.getClientSideReference().put(auth10, book10);
		 this.endpoint.getClientSideReference().put(auth11, booka);
		 this.endpoint.getClientSideReference().put(auth12, bookd);
		 this.endpoint.getClientSideReference().put(auth13, books);
		 this.endpoint.getClientSideReference().put(auth14, bookk);
		 this.endpoint.getClientSideReference().put(auth19, booko);

	    /**test de différentes méthodes**/
		 System.out.println("------------------------------------------------------------------------");
		 System.out.println("----------Récuperer des clé qui ne sont Pas dans la hashtable-----------");
		 System.out.println("------------------------------------------------------------------------");
		
		this.endpoint.getClientSideReference().get(auth7); //n'a pas été ajoutée à la hashtable
		this.endpoint.getClientSideReference().get(auth23);//celle ci non plus
		
		
		 System.out.println("------------------------------------------------------------------------");
		 System.out.println("-------------Récuperer des clé qui Sont dans la hashtable---------------");
		 System.out.println("------------------------------------------------------------------------");
		
		this.endpoint.getClientSideReference().get(auth14);
		this.endpoint.getClientSideReference().get(auth10);
		this.endpoint.getClientSideReference().get(auth1);
		this.endpoint.getClientSideReference().get(auth11);
		this.endpoint.getClientSideReference().get(auth2);
		this.endpoint.getClientSideReference().get(auth13);
		this.endpoint.getClientSideReference().get(auth19);
		
		
		System.out.println("------------------------------------------------------------------------");
		System.out.println("-----------Retirer des cles de la hashtable puis la recuperer------------");
		System.out.println("------------------------------------------------------------------------");
		
		this.endpoint.getClientSideReference().remove(auth14);
		this.endpoint.getClientSideReference().remove(auth9);
		this.endpoint.getClientSideReference().get(auth14);
		this.endpoint.getClientSideReference().get(auth9);
		 
		 /** récuperer les livre dont le titre commence par S et additionner la longeur de leurs titre**/
		
		System.out.println("------------------------------------------------------------------------");
		System.out.println("-MapReduce: récuperer les titre qui commence par S et somme des longueurss-");
		System.out.println("------------------------------------------------------------------------");
		
		 
		SelectorI MyPred = s->((Book) s).getBook().startsWith("S");
		ProcessorI myProcess= s-> ((Book) s).getBook().length();
		ReductorI myRed= (acc,x) -> (int)acc+ (int)x;
		CombinatorI myCombi= (res1,res2)-> (int)res1 + (int)res2;
			
		endpoint.getClientSideReference().mapReduce(MyPred, myProcess, myRed, myCombi,0);
			
	}
	
	
	
	@Override
	public synchronized void finalise() throws Exception {
		this.endpoint.cleanUpClientSide();
		super.finalise();
		
	}

	
	
}
