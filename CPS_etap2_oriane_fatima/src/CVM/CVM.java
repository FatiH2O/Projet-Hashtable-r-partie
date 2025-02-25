package CVM;

import java.lang.reflect.InvocationTargetException;


import Cleint.Client;
import Components.Node;
import Endpoints.DHTServices_Simple_endpoint;
import Endpoints.AccessSync_edp;
import Endpoints.Composite_Endpoint;
import FrontEnd.Front;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.cps.mapreduce.utils.IntInterval;
import fr.sorbonne_u.cps.mapreduce.utils.URIGenerator;

public class CVM extends AbstractCVM implements RequiredCI{

	public CVM() throws Exception {
		super();
		
	}
	
	public void	deploy() throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, Exception {
	
		
		/**intervalle choisie**/
		int BigInter= 99;
				
		/***création des sous-intervalles de chaque noeud***/
	    IntInterval interval1= new IntInterval(0,19);
		IntInterval interval2= new IntInterval(19,39);
		IntInterval interval3= new IntInterval(39,59);
		IntInterval interval4= new IntInterval(59,79);
		IntInterval interval5= new IntInterval(79,99);

		DHTServices_Simple_endpoint Client_to_Front_edp = new DHTServices_Simple_endpoint();
		
		/**edp qui relie le front au premier noeud**/
		Composite_Endpoint Front_to_Access_edp=new Composite_Endpoint();
		
		/** edp qui relie le premier neoud et le deuxieme**/
		Composite_Endpoint Node1_to_Node_edp2= new Composite_Endpoint();		
		Composite_Endpoint last_Node_to_first= new Composite_Endpoint();
		Composite_Endpoint Node2toNode3= new Composite_Endpoint();
		Composite_Endpoint Node3toNode4= new Composite_Endpoint();
		Composite_Endpoint Node4toNode5= new Composite_Endpoint();

		
		
		String UriClient= AbstractComponent.createComponent(Client.class.getCanonicalName(),new Object[] {Client_to_Front_edp.copyWithSharable()});
		assert	this.isDeployedComponent(UriClient);
		
		String UriFront= AbstractComponent.createComponent(Front.class.getCanonicalName(),new Object[] {Client_to_Front_edp.copyWithSharable(),
				Front_to_Access_edp.copyWithSharable()});
		assert	this.isDeployedComponent(UriFront);

		
		String UriNode1= AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[] {BigInter,interval1,Front_to_Access_edp.copyWithSharable(),
				last_Node_to_first.copyWithSharable(),Node1_to_Node_edp2.copyWithSharable()});
		assert	this.isDeployedComponent(UriNode1);

		String UriNode2= AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[] {BigInter,interval2,
				Node1_to_Node_edp2.copyWithSharable(),Node2toNode3.copyWithSharable()});
		assert	this.isDeployedComponent(UriNode2);
		
		
		String UriNode3= AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[] {BigInter,interval3,
				Node2toNode3.copyWithSharable(),Node3toNode4.copyWithSharable()});
		assert	this.isDeployedComponent(UriNode3);
		
		String UriNode4= AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[] {BigInter,interval4,
				Node3toNode4.copyWithSharable(),Node4toNode5.copyWithSharable()});
		assert	this.isDeployedComponent(UriNode4);
		
		
		String UriNode5= AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[] {BigInter,interval5,
				Node4toNode5.copyWithSharable(),last_Node_to_first.copyWithSharable()});
		assert	this.isDeployedComponent(UriNode5);
		
		super.deploy();
		assert	this.deploymentDone();
		}
	
	
	
	public static void		main(String[] args)
	{
		try {
			
			CVM a = new CVM();
			
			a.startStandardLifeCycle(20000L);
			// Give some time to see the traces (convenience).
			Thread.sleep(100L);
			
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
