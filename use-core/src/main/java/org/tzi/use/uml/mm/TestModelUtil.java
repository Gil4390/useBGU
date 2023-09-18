/*
 * USE - UML based specification environment
 * Copyright (C) 1999-2004 Mark Richters, University of Bremen
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package org.tzi.use.uml.mm;

import java.util.ArrayList;
import java.util.List;

import org.tzi.use.api.UseApiException;
import org.tzi.use.api.UseModelApi;

/**
 * The class <code>TestModelUtil</code> offers methods for creating
 * different USE-models.
 *
 * @author <a href="mailto:hanna@tzi.de">Hanna Bauerdick</a>
 * @author <a href="mailto:gutsche@tzi.de">Fabian Gutsche</a>
 */
public class TestModelUtil {
    private static TestModelUtil util = null;
        
    private TestModelUtil() { }

    /**
     * This method is for creating an instance of this class. It
     * guarantees that only one instance of this class exists.
     */
    public static TestModelUtil getInstance() {
        if ( util == null ) {
            util = new TestModelUtil();
        }
        return util;
    }

    /**
     * This method creates an empty model.
     */
    public MModel createEmptyModel() {
        UseModelApi api = new UseModelApi( "PersonCompany" );
        return api.getModel();
    }

    /**
     * This method creates a model with an enumeration.
     */
    public MModel createModelWithEnum() {
        try {
        	UseModelApi api = new UseModelApi("Color");
        	api.createEnumeration("colors", "blau", "gelb", "rot", "gruen");
        	        	
            List<String> literals = new ArrayList<String>();
            literals.add( "blau" );
            literals.add( "gelb" );
            literals.add( "rot" );
            literals.add( "gruen" );
            api.createEnumeration("colors2", literals);
            
            return api.getModel();
            
        } catch ( UseApiException e ) {
            throw new Error( e );
        }
    }

    /**
     * This method creates a model with some classes, including
     * attributes with an ObjectType.
     */
    public MModel createModelWithObjectTypes() {
        try{
        	UseModelApi api = new UseModelApi("TestObjectType");
        	api.createClass( "A", false );
        	api.createClass( "B", false );
            api.createAttribute("A", "name", "B");
            return api.getModel();
        } catch ( UseApiException e ) {
            throw new Error( e );
        }
    }


    public MModel createModelWithCollectionTypes() {
        try {
            UseModelApi api = new UseModelApi("TestObjectType");
            api.createClass( "A", false ); 
            api.createClass( "B", false );
            api.createAttribute("A", "name","B");
            return api.getModel();
        } catch ( UseApiException e ) {
            throw new Error( e );
        }
        
    }

    /**
     * This method creates a model with two classes (Person and Company).
     */
    public MModel createModelWithClasses() {
        try {
            UseModelApi api = new UseModelApi( "PersonCompany" );
            api.createClass( "Person", false );
            api.createClass( "Company", false );
            return api.getModel();
        } catch ( UseApiException e ) {
            throw new Error( e );
        }
    }

    /**
     * This method creates a model with two classes (Person and Company).
     * @param modelName The model is created with the given name
     */
    public MModel createModelWithClasses(String modelName) {
        try {
            UseModelApi api = new UseModelApi( modelName );
            api.createClass( "Person", false );
            api.createClass( "Company", false );
            return api.getModel();
        } catch ( UseApiException e ) {
            throw new Error( e );
        }
    }


    /**
     * This method creates a model with two classes (Person and Company)
     * and two associations (Job and isBoss).
     */
    public MModel createModelWithClassAndAssocs() {
        try {
            UseModelApi api = new UseModelApi( "PersonCompany" );
            api.createClass( "Person", false );
            api.createClass( "Company", false );
            api.createAssociation("Job", 
            		              "Person", "employee", "0..1", MAggregationKind.NONE, 
            		              "Company", "company", "0..1", MAggregationKind.NONE);
            
            api.createAssociation("isBoss", 
            		              "Person", "boss", "0..1", MAggregationKind.NONE, 
            		              "Person", "worker", "0..1", MAggregationKind.NONE);
            
            return api.getModel();
        } catch ( UseApiException e ) {
            //e.printStackTrace();
            throw new Error( e );
        }
    }

    /**
     * This method creates a model with two classes (Person and Company)
     * and two associations (Job and isBoss).
     * @param modelName The model is created with the given name
     */
    public MModel createModelWithClassAndAssocs(String modelName) {
        try {
            UseModelApi api = new UseModelApi( modelName );
            api.createClass( "Person", false );
            api.createClass( "Company", false );
            api.createAssociation("Job",
                    "Person", "employee", "0..1", MAggregationKind.NONE,
                    "Company", "company", "0..1", MAggregationKind.NONE);

            api.createAssociation("isBoss",
                    "Person", "boss", "0..1", MAggregationKind.NONE,
                    "Person", "worker", "0..1", MAggregationKind.NONE);

            return api.getModel();
        } catch ( UseApiException e ) {
            //e.printStackTrace();
            throw new Error( e );
        }
    }

    /**
     * This method creates a model with two classes (Person and Company)
     * and one association (Job). It contains higher multiplicities.
     */
    public MModel createModelWithClassAndAssocs2() {
        try {
            UseModelApi api = new UseModelApi("PersonCompany");
            api.createClass("Person", false);
            api.createClass("Company", false);
            api.createAttribute("Company", "name", "String");
            api.createAssociation("Job", 
            		              "Person", "employee", "0..1", MAggregationKind.NONE, 
            		              "Company", "company", "0..*", MAggregationKind.NONE);

            return api.getModel();
        } catch ( UseApiException e ) {
            throw new Error( e );
        }
    }

    /**
     * This method creates a model with two classes (Person and Company)
     * and one association (Job). It contains higher multiplicities.
     * @param modelName The model is created with the given name
     */
    public MModel createModelWithClassAndAssocs2(String modelName) {
        try {
            UseModelApi api = new UseModelApi(modelName);
            api.createClass("Person", false);
            api.createClass("Company", false);
            api.createAttribute("Company", "name", "String");
            api.createAssociation("Job",
                    "Person", "employee", "0..1", MAggregationKind.NONE,
                    "Company", "company", "0..*", MAggregationKind.NONE);

            return api.getModel();
        } catch ( UseApiException e ) {
            throw new Error( e );
        }
    }

    /**
     * This method creates a model with two classes (Person and Company)
     * and an association class (Job).
     */
    public MModel createModelWithClassAndAssocClass() {
        try {
            UseModelApi api = new UseModelApi("PersonCompany");
            api.createClass("Person", false);
            api.createClass("Company", false);
            api.createAttribute("Company", "name", "String");

            api.createAssociationClass("Job", false,
            		                   "Person" , "person" , "0..1", MAggregationKind.NONE, 
		                               "Company", "company", "0..1", MAggregationKind.NONE);

            api.createAttribute( "Job", "salary", "Integer" );
            return api.getModel();
        } catch ( UseApiException e ) {
            throw new Error( e );
        }
    }

    /**
     * This method creates a model with two classes (Person and Company)
     * and an association class (Job).
     * @param modelName The model is created with the given name
     */
    public MModel createModelWithClassAndAssocClass(String modelName) {
        try {
            UseModelApi api = new UseModelApi(modelName);
            api.createClass("Person", false);
            api.createClass("Company", false);
            api.createAttribute("Company", "name", "String");

            api.createAssociationClass("Job", false,
                    "Person" , "person" , "0..1", MAggregationKind.NONE,
                    "Company", "company", "0..1", MAggregationKind.NONE);

            api.createAttribute( "Job", "salary", "Integer" );
            return api.getModel();
        } catch ( UseApiException e ) {
            throw new Error( e );
        }
    }

    /**
     * This method creates a model with one class (Person)
     * and one associationclass (Job).
     */
    public MModel createModelWithOneClassAndOneAssocClass() {
        try {
        	UseModelApi api = new UseModelApi( "PersonCompany" );
            api.createClass( "Person", false );
            api.createAssociationClass( "Job", false,
            		                    "Person", "boss", "0..1", MAggregationKind.NONE,
            		                    "Person", "worker", "0..1", MAggregationKind.NONE);

            return api.getModel();
        } catch ( UseApiException e ) {
            throw new Error( e );
        }
    }

    /**
     * This method creates a model with one class (Person)
     * and one associationclass (Job).
     * @param modelName The model is created with the given name
     */
    public MModel createModelWithOneClassAndOneAssocClass(String modelName) {
        try {
            UseModelApi api = new UseModelApi( modelName );
            api.createClass( "Person", false );
            api.createAssociationClass( "Job", false,
                    "Person", "boss", "0..1", MAggregationKind.NONE,
                    "Person", "worker", "0..1", MAggregationKind.NONE);

            return api.getModel();
        } catch ( UseApiException e ) {
            throw new Error( e );
        }
    }

    /**
     * This method creates a model with three classes (Person, Salary and Company)
     * and an associationclass (Job).
     */
    public MModel createModelWithClassAndTenaryAssocClass() {
        try {
        	UseModelApi api = new UseModelApi( "PersonCompany" );
            api.createClass( "Person", false );
            api.createClass( "Company", false );
            api.createClass( "Salary", false );
            
            api.createAttribute( "Company", "name", "String" );
            
            api.createAssociationClass("Job", false, 
            		                   new String[] {"Person", "Company", "Salary"},
            						   new String[] {"person", "company", "salary"},
            						   new String[] {"0..1",   "0..1",    "0..1"},
            						   new int[]    {MAggregationKind.NONE, MAggregationKind.NONE, MAggregationKind.NONE});

            return api.getModel();
        } catch ( UseApiException e ) {
            throw new Error( e );
        }
    }

    /**
     * This method creates a model with three classes (Person, Salary and Company)
     * and an associationclass (Job).
     * @param modelName The model is created with the given name
     */
    public MModel createModelWithClassAndTenaryAssocClass(String modelName) {
        try {
            UseModelApi api = new UseModelApi( modelName );
            api.createClass( "Person", false );
            api.createClass( "Company", false );
            api.createClass( "Salary", false );

            api.createAttribute( "Company", "name", "String" );

            api.createAssociationClass("Job", false,
                    new String[] {"Person", "Company", "Salary"},
                    new String[] {"person", "company", "salary"},
                    new String[] {"0..1",   "0..1",    "0..1"},
                    new int[]    {MAggregationKind.NONE, MAggregationKind.NONE, MAggregationKind.NONE});

            return api.getModel();
        } catch ( UseApiException e ) {
            throw new Error( e );
        }
    }


    /**
     * This method creates a model with two classes (Bank and Person)
     * and one qualified association (Account).
     */
    public MModel createModelWithClassAndQualifiedAssoc() {
        try {
        	UseModelApi api = new UseModelApi( "BankModel" );
            api.createClass( "Person", false );
            api.createClass( "Bank", false );
            
            api.createAssociation("Account", 
            		              new String[] {"Bank", "Person"},
            		              new String[] {"bank", "account"},
            		              new String[] {"0..*", "0..1"},
            		              new int[] {MAggregationKind.NONE, MAggregationKind.NONE},
            		              new boolean[] {false, false},
            		              new String[][][] {
            							new String[][]{new String[] {"accountNr", "String"}},
            							new String[][]{}}
            		              );

            return api.getModel();
        } catch ( UseApiException e ) {
            throw new Error( e );
        }
    }

    /**
     * This method creates a model with two classes (Bank and Person)
     * and one qualified association (Account).
     * @param modelName The model is created with the given name
     */
    public MModel createModelWithClassAndQualifiedAssoc(String modelName) {
        try {
            UseModelApi api = new UseModelApi( modelName );
            api.createClass( "Person", false );
            api.createClass( "Bank", false );

            api.createAssociation("Account",
                    new String[] {"Bank", "Person"},
                    new String[] {"bank", "account"},
                    new String[] {"0..*", "0..1"},
                    new int[] {MAggregationKind.NONE, MAggregationKind.NONE},
                    new boolean[] {false, false},
                    new String[][][] {
                            new String[][]{new String[] {"accountNr", "String"}},
                            new String[][]{}}
            );

            return api.getModel();
        } catch ( UseApiException e ) {
            throw new Error( e );
        }
    }
    
    /**
     * This method creates a model with two classes (Person and Company),
     * an associationclass (Job) and an association (isBoss).
     */
    public MModel createComplexModel() {
        try {
        	UseModelApi api = new UseModelApi( "PersonCompany" );
            // adds two classes named Person and Company
            api.createClass( "Person", false );
            api.createClass( "Company", false );
            
            api.createAttribute( "Company", "name", "String" );
            
            // adds an associationclass between Person and Company named Job
            api.createAssociationClass("Job", false, 
            						   "Person", "employee", "0..1", MAggregationKind.NONE, 
            						   "Company", "company", "0..1", MAggregationKind.NONE);

            // adds an association between Person itself named isBoss
            api.createAssociation( "isBoss", 
            		               "Person", "worker", "0..1", MAggregationKind.NONE,
            		               "Person", "boss"  , "0..1", MAggregationKind.NONE);

            return api.getModel();
        } catch ( UseApiException e ) {
            throw new Error( e );
        }
    }

    /**
     * This method creates a model with two classes (Person and Company),
     * an associationclass (Job) and an association (isBoss).
     * @param modelName The model is created with the given name
     */
    public MModel createComplexModel(String modelName) {
        try {
            UseModelApi api = new UseModelApi( modelName );
            // adds two classes named Person and Company
            api.createClass( "Person", false );
            api.createClass( "Company", false );

            api.createAttribute( "Company", "name", "String" );

            // adds an associationclass between Person and Company named Job
            api.createAssociationClass("Job", false,
                    "Person", "employee", "0..1", MAggregationKind.NONE,
                    "Company", "company", "0..1", MAggregationKind.NONE);

            // adds an association between Person itself named isBoss
            api.createAssociation( "isBoss",
                    "Person", "worker", "0..1", MAggregationKind.NONE,
                    "Person", "boss"  , "0..1", MAggregationKind.NONE);

            return api.getModel();
        } catch ( UseApiException e ) {
            throw new Error( e );
        }
    }


    /**
     * This method creates a model with two classes (Person 
     * and Employee) and a generalization structure between them.
     */
    public MModel createModelWithGen() {
        try {
            UseModelApi api = new UseModelApi( "PersonEmployee" );
            api.createClass( "Person", false );
            api.createClass( "Employee", false );
            
            api.createGeneralization( "Employee", "Person" );
            
            return api.getModel();
        } catch ( UseApiException e ) {
            throw new Error( e );
        }
    }

    /**
     * This method creates a model with Three classes (Person, Adult
     * and Employee), a generalization structure between Person and Adult
     * and one association (Job) between Person and Company
     * @param modelName The model is created with the given name
     */
    public MModel createModelWithGeneralization(String modelName) {
        try {
            UseModelApi api1 = new UseModelApi(modelName);
            api1.createClass("Person", false );
            api1.createClass("Adult", false );
            api1.createClass("Company", false );

            api1.createGeneralization("Adult","Person");
            api1.createAssociation("Job","Person","employee","*",
                    MAggregationKind.NONE,"Company","workplace","0..1",MAggregationKind.NONE);

            return api1.getModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }

    public MModel createModelWithOperation() {
        try{
        	UseModelApi api = new UseModelApi( "Person" );
            api.createClass( "Person", false );
            
            // adds an attribute
            api.createAttribute("Person", "fName", "String" );
            
            // adds an operation
            api.createOperation( "Person", "equalsName", new String[][] {new String[] {"name", "String"}}, "Boolean" ); 
            
            // adds a void operation
            api.createOperation( "Person", "init", new String[][] {new String[] {"name", "String"}}, null );
            
            return api.getModel();
        } catch ( UseApiException e ) {
            throw new Error( e ) ;
        }
    }
        
    public MModel createModelWithInvariant() {
        try{
        	UseModelApi api = new UseModelApi( "Person" );
            api.createClass( "Person", false );
            api.createAttribute("Person", "fName", "String" );
            api.createInvariant("testInv", "Person", "true", false);
            
            return api.getModel();
        } catch ( UseApiException e ) {
            throw new Error( e );
        }
    }

    /**
     * This method creates a model with two classes (Person and Company),
     * two associations (Job and isBoss) and one invariant
     * @param modelName The model is created with the given name
     */
    public MModel createModelWithSimpleInvariant(String modelName) {
        try {
            UseModelApi api = new UseModelApi(modelName);
            api.createClass("Person", false);
            api.createClass("Company", false);

            api.createAttribute( "Person", "salary", "Integer" );

            api.createAssociation("Job",
                    "Person", "employee", "0..1", MAggregationKind.NONE,
                    "Company", "company", "0..1", MAggregationKind.NONE);

            api.createAssociation("isBoss",
                    "Person", "boss", "0..1", MAggregationKind.NONE,
                    "Person", "worker", "0..1", MAggregationKind.NONE);

            api.createInvariant("minimumSalary", "Person", "self.salary >= 5000", false);

            return api.getModel();
        } catch (UseApiException e) {
            //e.printStackTrace();
            throw new Error(e);
        }
    }


    /**
     * Model with 3 classes and 4 invariants.
     *
     * based on the example from <a href="https://github.com/useocl/use/blob/master/manual/main.md">https://github.com/useocl/use/blob/master/manual/main.md</a>
     *
     */
    public MModel createComplexModelWithConstraints(String modelName) {
        try {
            UseModelApi api = new UseModelApi(modelName);
            api.createClass("Employee", false);
            api.createClass("Department", false);
            api.createClass("Project", false);

            api.createAttribute( "Employee", "name", "String" );
            api.createAttribute( "Employee", "salary", "Integer" );

            api.createAttribute( "Department", "name", "String" );
            api.createAttribute( "Department", "location", "String" );
            api.createAttribute( "Department", "budget", "Integer" );

            api.createAttribute( "Project", "name", "String" );
            api.createAttribute( "Project", "budget", "Integer" );


            api.createAssociation("WorksIn",
                    "Employee", "employee", "*", MAggregationKind.NONE,
                    "Department", "department", "1..*", MAggregationKind.NONE);

            api.createAssociation("WorksOn",
                    "Employee", "employee", "*", MAggregationKind.NONE,
                    "Project", "project", "*", MAggregationKind.NONE);

            api.createAssociation("Controls",
                    "Department", "department", "1", MAggregationKind.NONE,
                    "Project", "project", "*", MAggregationKind.NONE);


            api.createInvariant("MoreEmployeesThanProjects",
                    "Department",
                    "self.employee->size >= self.project->size",
                    false);

            api.createInvariant("MoreProjectsHigherSalary",
                    "Employee",
                    "Employee.allInstances->forAll(e1, e2 | \n" +
                            "      e1.project->size > e2.project->size \n" +
                            "        implies e1.salary > e2.salary)",
                    false);

            api.createInvariant("BudgetWithinDepartmentBudget",
                    "Project",
                    "self.budget <= self.department.budget",
                    false);

            api.createInvariant("EmployeesInControllingDepartment",
                    "Project",
                    "self.department.employee->includesAll(self.employee)",
                    false);

            return api.getModel();
        } catch (UseApiException e) {
            //e.printStackTrace();
            throw new Error(e);
        }
    }

}
