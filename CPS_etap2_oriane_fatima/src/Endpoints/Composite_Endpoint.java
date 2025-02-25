package Endpoints;

import fr.sorbonne_u.components.endpoints.BCMCompositeEndPoint;
import fr.sorbonne_u.components.endpoints.EndPointI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessSyncCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceSyncCI;

public class Composite_Endpoint extends BCMCompositeEndPoint{

	private static final long serialVersionUID = 1L;
	protected static final int	NUMBER_OF_ENDPOINTS = 2;
	
	public Composite_Endpoint() {
		super(NUMBER_OF_ENDPOINTS);
		
		AccessSync_edp AccessSync_edp= new AccessSync_edp();
		this.addEndPoint(AccessSync_edp);
		
		MapSync_edp MapReduce_edp= new MapSync_edp();
		this.addEndPoint(MapReduce_edp);
		
		
	}
	
	public EndPointI<ContentAccessSyncCI> getAccessSync_edp() {
		return this.getEndPoint(ContentAccessSyncCI.class);
		
	}
	
	
	public EndPointI<MapReduceSyncCI> getMapreduce_edp() {
		return this.getEndPoint(MapReduceSyncCI.class);
		
	}

}
