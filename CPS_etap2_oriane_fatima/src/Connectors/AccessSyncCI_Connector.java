package Connectors;

import java.io.Serializable;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.frontend.DHTServicesCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessSyncCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceSyncCI;




public class AccessSyncCI_Connector  extends AbstractConnector implements ContentAccessSyncCI {

	@Override
	public ContentDataI getSync(String computationURI, ContentKeyI key) throws Exception {
		
		return ((ContentAccessSyncCI)this.offering).getSync(computationURI, key);
	}

	@Override
	public ContentDataI putSync(String computationURI, ContentKeyI key, ContentDataI value) throws Exception {
		
		return ((ContentAccessSyncCI)this.offering).putSync(computationURI, key, value);
	}

	@Override
	public ContentDataI removeSync(String computationURI, ContentKeyI key) throws Exception {
	
		return ((ContentAccessSyncCI)this.offering).removeSync(computationURI, key);
	}

	@Override
	public void clearComputation(String computationURI) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	

}
