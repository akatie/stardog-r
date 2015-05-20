// Copyright (c) 2010 - 2015 , Clark & Parsia, LLC. <http://www.clarkparsia.com>
// For more information about licensing and copyright of this software, please contact
// inquiries@clarkparsia.com or visit http://stardog.com

package com.complexible.stardog.plan.aggregates;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.complexible.common.openrdf.query.TupleQueryResult;
import com.complexible.common.rdf.model.Namespaces;
import com.complexible.stardog.StardogException;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.SelectQuery;
import com.complexible.stardog.plan.SortType;
import com.complexible.stardog.plan.filter.AbstractExpression;
import com.complexible.stardog.plan.filter.Expression;
import com.complexible.stardog.plan.filter.ExpressionEvaluationException;
import com.complexible.stardog.plan.filter.ExpressionVisitor;
import com.complexible.stardog.plan.filter.ValueSolution;
import com.google.common.base.Preconditions;

import org.apache.shiro.UnavailableSecurityManagerException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.QueryEvaluationException;
import org.rosuda.REngine.*;

/**
 * <p>Prediction custom aggregate through the R interface</p>
 *
 * @author  Albert Meroño-Peñuela
 * @since   3.0
 * @version 3.0
 */
public final class Predict extends AbstractExpression implements Aggregate {	
	protected REngine eng = null;
	protected List<Value> rCurr = null; // Values for the current dimension
	protected List<URI> rDims = null; // Dimensions in the DSD
	protected HashMap<URI,List<Value>> rVals = null; // Map with Array<Value> for each dimension
	
	public Predict() {
		super();
	}

	protected Predict(final Predict theAgg) {
		super(theAgg);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setArgs(final List<Expression> theArgs) {
		Preconditions.checkArgument(theArgs.size() == 1, "Predict aggregate function takes one argument, %d found", theArgs.size());
		super.setArgs(theArgs);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Predict copy() {
		return new Predict(this);
	}

	@Override
	public String getName() {
		return Namespaces.STARDOG + "predict";
	}

	@Override
	public List<String> getNames() {
		return new ArrayList<String>(Arrays.asList(getName()));
	}

	@Override
	public void accept(ExpressionVisitor theVisitor) {
		theVisitor.visit(this);
	}

	@Override
	public Value evaluate(ValueSolution theSolution) throws ExpressionEvaluationException {
		return null;
	}

	@Override
	public Value get() throws ExpressionEvaluationException {
		return ValueFactoryImpl.getInstance().createLiteral(0.0);
	}

	@Override
	public boolean isDistinct() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setDistinct(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sortType(SortType arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialize() {
		rDims = new ArrayList<URI>();
		rCurr = new ArrayList<Value>();
		rVals = new HashMap<URI,List<Value>>();
		
		// System.out.println("INITIALIZING Predict MODULE...");
		// To infer a model we use the dimension data in the DSD
		// Using SNARL API to open connection to the db
		Connection aConn = null;
		try {
			// System.out.println("Connecting to server...");
		    aConn = ConnectionConfiguration
		    		.to("testRAggregates")
		    		.credentials("admin", "admin")
		    		.connect();
		    // System.out.println("Connected. Sending DSD query...");
		    SelectQuery dsdQuery = aConn.select("PREFIX qb: <http://purl.org/linked-data/cube#> "
		    		+ "SELECT ?dim WHERE { "
		    		+ "?dsd a qb:DataStructureDefinition . "
		    		+ "?dsd qb:component [ qb:dimension ?dim ] .}");
		    TupleQueryResult aResult = dsdQuery.execute();
		    // System.out.println("Query sent to the server.");
		    try {
		    	System.out.println("Retrieving dimensions from the DSD...");
		    	while (aResult.hasNext()) {
		    		URI dim = ValueFactoryImpl.getInstance().createURI(aResult.next().getValue("dim").toString());
		    		// Retrieve values for each dimension
				    SelectQuery dataQuery = aConn.select("PREFIX qb: <http://purl.org/linked-data/cube#> "
				    		+ "SELECT ?val WHERE { "
				    		+ "?obs a qb:Observation . "
				    		+ "?obs ?dim ?val .}");
				    dataQuery.parameter("dim", dim);
				    TupleQueryResult dataResult = dataQuery.execute();
				    ArrayList<Value> cValues = new ArrayList<Value>();
				    while (dataResult.hasNext()) {
				    	Value cVal = dataResult.next().getValue("val");
				    	cValues.add(cVal);
				    }
		    		rVals.put(dim, cValues);
		    		dataResult.close();
		    	}
		    	System.out.println(Arrays.toString(rVals.entrySet().toArray()));
		    	aResult.close();	
		    } catch (QueryEvaluationException e) {
		    	System.err.println(e.getMessage());
		    }
		    aConn.close();
		} catch (StardogException e) {
		    System.err.println(e.getMessage());
		}
		
		// Initialize R engine -- convert Java HashMap to R data.frame
		String args[] = {"--vanilla"};
		try {
			eng = REngine.getLastEngine();
			if (eng == null) {
				eng = REngine.engineForClass("org.rosuda.REngine.JRI.JRIEngine", args, new REngineStdOutput(), false);
			}
			RList l = new RList();
			l.put("a",new REXPInteger(new int[] { 0,1,2,3}));
			l.put("b",new REXPDouble(new double[] { 0.5,1.2,2.3,3.0}));
			System.out.println("  assign x=pairlist");
			eng.assign("x", new REXPList(l));
			System.out.println("  assign y=vector");
			eng.assign("y", new REXPGenericVector(l));
			System.out.println("  assign z=data.frame");
			eng.assign("z", REXP.createDataFrame(l));
			System.out.println("  pull all three back to Java");

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (REngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (REXPMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		// Iterate all the HashMap
		Iterator it = rVals.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        String colName = (String) pair.getKey();
	        List<Value> colVals = (List<Value>) pair.getValue();
	        double dColVals[] = new double[colVals.size()];
	        String sColVals[] = new String[colVals.size()];
	        for (int i = 0; i < colVals.size(); i++) {
	        	try {
	        		dColVals[i] = Double.parseDouble(((Value) colVals).stringValue());
	        		
		    	} catch (NumberFormatException e) {
		    		sColVals[i] = ((Value) colVals).stringValue();
		    	}	        		
	        }
	        it.remove(); // avoids a ConcurrentModificationException
	    }
		
	}
}
