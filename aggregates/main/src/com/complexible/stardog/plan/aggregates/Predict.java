// Copyright (c) 2010 - 2015 , Clark & Parsia, LLC. <http://www.clarkparsia.com>
// For more information about licensing and copyright of this software, please contact
// inquiries@clarkparsia.com or visit http://stardog.com

package com.complexible.stardog.plan.aggregates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.complexible.common.openrdf.query.TupleQueryResult;
import com.complexible.common.rdf.model.Namespaces;
import com.complexible.common.rdf.query.resultio.TextTableQueryResultWriter;
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

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.resultio.QueryResultIO;
import org.rosuda.JRI.Rengine;

import static com.complexible.common.rdf.model.Values.literal;

/**
 * <p>Prediction custom aggregate through the R interface</p>
 *
 * @author  Albert Meroño-Peñuela
 * @since   3.0
 * @version 3.0
 */
public final class Predict extends AbstractExpression implements Aggregate {	
	protected Rengine re = null;
	protected List<Double> rCurr = null;
	
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
		System.out.println("INITIALIZING Predict MODULE...");
		// To infer a model we use the dimension data in the DSD
		// Using SNARL API to open connection to the db
		Connection aConn = null;
		try {
			System.out.println("Connecting to server...");
		    aConn = ConnectionConfiguration
		    		.to("testRAggregates")
		    		.credentials("admin", "admin")
		    		.server("snarl://localhost:5820")
		    		.connect();
		    System.out.println("Connected. Sending query...");
		    SelectQuery dsdQuery = aConn.select("PREFIX qb: <http://purl.org/linked-data/cube#> "
		    		+ "SELECT ?dim WHERE { "
		    		+ "?dsd a qb:DataStructureDefinition . "
		    		+ "?dsd qb:component [ qb:dimension ?dim ] .}");
		    TupleQueryResult aResult = dsdQuery.execute();
		    System.out.println("Query sent to the server.");
		    try {
		    	System.out.println("Retrieving dimensions from the DSD...");
		    	QueryResultIO.write(aResult, TextTableQueryResultWriter.FORMAT, System.out);
		    	aResult.close();
		    } catch (IOException e) {
		    	System.err.println(e.getMessage());
		    } catch (TupleQueryResultHandlerException e) {
		    	System.err.println(e.getMessage());
		    } catch (QueryEvaluationException e) {
		    	System.err.println(e.getMessage());
		    }
		} catch (StardogException e) {
		    System.err.println(e.getMessage());
		}
		
		// Initialize R engine
		re = Rengine.getMainEngine();
		if (re == null) {
			re = new Rengine(new String[] {"--vanilla"}, false, null);
		}
	}
}
