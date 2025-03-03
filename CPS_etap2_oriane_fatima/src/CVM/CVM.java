package CVM;

import java.lang.reflect.InvocationTargetException;

import Components.Client;
import Components.Front;
import Components.Node;
import Endpoints.DHTServices_Simple_endpoint;
import Endpoints.Composite_Endpoint;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.cps.mapreduce.utils.IntInterval;

public class CVM extends AbstractCVM implements RequiredCI{

	public CVM() throws Exception {
		super();
		
	}
	
	public void	deploy() throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, Exception {
	
		
		/**intervalle choisi**/
		int BigInter= 99;
				
		/***création des sous-intervalles de chaque noeud***/
	    IntInterval interval1= new IntInterval(0,19);
		IntInterval interval2= new IntInterval(19,39);
		IntInterval interval3= new IntInterval(39,59);
		IntInterval interval4= new IntInterval(59,79);
		IntInterval interval5= new IntInterval(79,99);

		/**edp qui relie le client au front**/
		DHTServices_Simple_endpoint Client_to_Front_edp = new DHTServices_Simple_endpoint();
		
		/**Composite edp qui relie le front au premier noeud**/
		Composite_Endpoint Front_to_Node_edp=new Composite_Endpoint();
		
		/** Composite edp qui relie les neoud entre eux **/
		Composite_Endpoint Node1_to_Node2_edp= new Composite_Endpoint();		
		Composite_Endpoint lastToFirst_Node_edp= new Composite_Endpoint();
		Composite_Endpoint Node2toNode3_edp= new Composite_Endpoint();
		Composite_Endpoint Node3toNode4_edp= new Composite_Endpoint();
		Composite_Endpoint Node4toNode5_edp= new Composite_Endpoint();

		
		/*                         *
		 * Creation des composants *
		 *                         */
		
		String UriClient= AbstractComponent.createComponent(Client.class.getCanonicalName(),new Object[] {Client_to_Front_edp.copyWithSharable()});
		assert	this.isDeployedComponent(UriClient);
		
		String UriFront= AbstractComponent.createComponent(Front.class.getCanonicalName(),new Object[] {Client_to_Front_edp.copyWithSharable(),
				Front_to_Node_edp.copyWithSharable()});
		assert	this.isDeployedComponent(UriFront);

		
		String UriNode1= AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[] {BigInter,interval1,Front_to_Node_edp.copyWithSharable(),
				lastToFirst_Node_edp.copyWithSharable(),Node1_to_Node2_edp.copyWithSharable()});
		assert	this.isDeployedComponent(UriNode1);

		String UriNode2= AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[] {BigInter,interval2,
				Node1_to_Node2_edp.copyWithSharable(),Node2toNode3_edp.copyWithSharable()});
		assert	this.isDeployedComponent(UriNode2);
		
		
		String UriNode3= AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[] {BigInter,interval3,
				Node2toNode3_edp.copyWithSharable(),Node3toNode4_edp.copyWithSharable()});
		assert	this.isDeployedComponent(UriNode3);
		
		String UriNode4= AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[] {BigInter,interval4,
				Node3toNode4_edp.copyWithSharable(),Node4toNode5_edp.copyWithSharable()});
		assert	this.isDeployedComponent(UriNode4);
		
		
		String UriNode5= AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[] {BigInter,interval5,
				Node4toNode5_edp.copyWithSharable(),lastToFirst_Node_edp.copyWithSharable()});
		assert	this.isDeployedComponent(UriNode5);
		
		super.deploy();
		assert	this.deploymentDone();
		}
	
	
	
	public static void		main(String[] args)
	{
		try {
			
			CVM a = new CVM();
			
			a.startStandardLifeCycle(2000L);
			
			Thread.sleep(100L);
			
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
