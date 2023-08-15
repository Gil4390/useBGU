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

package org.tzi.use.uml.sys;

import org.tzi.use.api.UseApiException;
import org.tzi.use.api.UseMultiSystemApi;
import org.tzi.use.api.UseSystemApi;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MMultiModel;
import org.tzi.use.uml.mm.TestModelUtil;
import org.tzi.use.uml.mm.TestMultiModelUtil;


/**
 * ObjectCreation is needed for creating instances of a specific
 * model. This class is only needed for the tests.
 *
 * @author <a href="mailto:hanna@tzi.de">Hanna Bauerdick</a>
 * @author <a href="mailto:gutsche@tzi.de">Fabian Gutsche</a>
 */
public class ObjectCreation {
    private static ObjectCreation instance = null;

    private ObjectCreation() {
    }

    public static ObjectCreation getInstance() {
        if ( instance == null ) {
            instance = new ObjectCreation();
        }
        return instance;
    }

    /**
     * Creates a model with two classes. It creates instances of those as well.
     * The model is created with the given name
     * @return returns the actual System.
     */
    public MSystem createModelWithOnlyObjects(String modelName) {
        try {
            // creation of the system
            MModel model = TestModelUtil.getInstance()
                    .createModelWithClasses(modelName);
            MSystem system = new MSystem( model );

            UseSystemApi systemApi = UseSystemApi.create(system, false);

            // creation of an object (p1) of the class Person
            systemApi.createObjects("Person", "p1");

            // creation of an object (c1) of the class Company
            systemApi.createObjects("Company", "c1");

            return system;
        } catch ( Exception e ) {
            throw ( new Error( e ) );
        }
    }

    /**
     * Creates a model with two classes and an association class. It creates
     * instances of those as well.
     *
     * @return returns the actual System.
     */
    public MSystem createModelWithObjects() {
        try {
            // creation of the system
            MModel model = TestModelUtil.getInstance()
                    .createModelWithClassAndAssocs();
            MSystem system = new MSystem( model );
            
            UseSystemApi systemApi = UseSystemApi.create(system, false);
            
            // creation of an object (p1) of the class Person
            systemApi.createObjects("Person", "p1");
            
            // creation of an object (c1) of the class Company
            systemApi.createObjects("Company", "c1");
            
            // creation of a link between p1 and c1 of an association
            systemApi.createLink("Job", "p1", "c1");
            
            return system;
        } catch ( Exception e ) {
            throw ( new Error( e ) );
        }
    }

    /**
     * Creates a model with two classes and an association class. It creates
     * instances of those as well.
     * The model is created with the given name
     * @return returns the actual System.
     */
    public MSystem createModelWithObjects(String modelName) {
        try {
            // creation of the system
            MModel model = TestModelUtil.getInstance().createModelWithClassAndOneAssoc(modelName);
            MSystem system = new MSystem( model );

            UseSystemApi systemApi = UseSystemApi.create(system, false);

            // creation of an object (p1) of the class Person
            systemApi.createObjects("Person", "p1");

            // creation of an object (c1) of the class Company
            systemApi.createObjects("Company", "c1");

            // creation of a link between p1 and c1 of an association
            systemApi.createLink("Job", "p1", "c1");

            return system;
        } catch ( Exception e ) {
            throw ( new Error( e ) );
        }
    }

    /**
     * Creates a model with two classes and an association class. It creates
     * instances of those as well.
     *
     * @return returns the actual System.
     */
    public MSystem createModelWithManyObjects() {
        try {
            // creation of the system
            MModel model = TestModelUtil.getInstance()
                    .createModelWithClassAndAssocs2();
            MSystem system = new MSystem( model );
            
            UseSystemApi systemApi = UseSystemApi.create(system, false);
            
            // creation of an object (p1) of the class Person
            systemApi.createObjects("Person", "p1");
            
            // creation of four objects (c1-c4) of the class Company
            systemApi.createObjects(
            		"Company", 
            		"c1", "c2", "c3", "c4");
             
            // creation of a link between p1 and c1 of an association
            systemApi.createLink("Job", "p1", "c1");
            systemApi.createLink("Job", "p1", "c2");
            systemApi.createLink("Job", "p1", "c3");
            systemApi.createLink("Job", "p1", "c4");
            
            // set an attribute value in c1
            systemApi.setAttributeValue("c1", "name", "'IBM'");
                   
            return system;
        } catch ( UseApiException e ) {
            throw ( new Error( e ) );
        }
    }

    /**
     * Creates a model with two classes and an association class. It creates
     * instances of those as well.
     * The model is created with the given name
     * @return returns the actual System.
     */
    public MSystem createModelWithManyObjects(String modelName) {
        try {
            // creation of the system
            MModel model = TestModelUtil.getInstance()
                    .createModelWithClassAndAssocs2(modelName);
            MSystem system = new MSystem( model );

            UseSystemApi systemApi = UseSystemApi.create(system, false);

            // creation of an object (p1) of the class Person
            systemApi.createObjects("Person", "p1");

            // creation of four objects (c1-c4) of the class Company
            systemApi.createObjects(
                    "Company",
                    "c1", "c2", "c3", "c4");

            // creation of a link between p1 and c1 of an association
            systemApi.createLink("Job", "p1", "c1");
            systemApi.createLink("Job", "p1", "c2");
            systemApi.createLink("Job", "p1", "c3");
            systemApi.createLink("Job", "p1", "c4");

            // set an attribute value in c1
            systemApi.setAttributeValue("c1", "name", "'IBM'");

            return system;
        } catch ( UseApiException e ) {
            throw ( new Error( e ) );
        }
    }

    /**
     * Creates a model with two classes and an association class. It creates
     * instances of those as well.
     *
     * @return returns the actual System.
     */
    public MSystem createModelWithObjectsAndLinkObject() {
        try {
            // creation of the system
            MModel model = TestModelUtil.getInstance()
                    .createModelWithClassAndAssocClass();
            MSystem system = new MSystem( model );
            
            UseSystemApi systemApi = UseSystemApi.create(system, false);
            
            // creation of an object (p1) of the class Person
            systemApi.createObjects("Person", "p1");
            
            // creation of an object (c1) of the class Company
            systemApi.createObjects("Company", "c1");
            
            // creation of a link object (j1) of class Job between p1 and c1
            systemApi.createLinkObject(
            		"Job", 
            		"j1",
            		"p1", "c1");
            
            // set an attribute value in c1
            systemApi.setAttributeValue("c1", "name", "'IBM'");
            
            return system;
        } catch ( UseApiException e ) {
            throw ( new Error( e ) );
        }
    }

    /**
     * Creates a model with two classes and an association class. It creates
     * instances of those as well.
     * The model is created with the given name
     * @return returns the actual System.
     */
    public MSystem createModelWithObjectsAndLinkObject(String modelName) {
        try {
            // creation of the system
            MModel model = TestModelUtil.getInstance()
                    .createModelWithClassAndAssocClass(modelName);
            MSystem system = new MSystem( model );

            UseSystemApi systemApi = UseSystemApi.create(system, false);

            // creation of an object (p1) of the class Person
            systemApi.createObjects("Person", "p1");

            // creation of an object (c1) of the class Company
            systemApi.createObjects("Company", "c1");

            // creation of a link object (j1) of class Job between p1 and c1
            systemApi.createLinkObject(
                    "Job",
                    "j1",
                    "p1", "c1");

            // set an attribute value in c1
            systemApi.setAttributeValue("c1", "name", "'IBM'");

            return system;
        } catch ( UseApiException e ) {
            throw ( new Error( e ) );
        }
    }

    /**
     * Creates an instance of a model with one class and one association class. 
     *
     * @return returns the actual System.
     */
    public MSystem createModelWithObjectsOfSameClassAndLinkObject() {
        try {
            // creation of the system
            MModel model = TestModelUtil.getInstance()
                    .createModelWithOneClassAndOneAssocClass();
            MSystem system = new MSystem( model );
            
            UseSystemApi systemApi = UseSystemApi.create(system, false);
            
            // creation of an objects (p1,p2) of the class Person
            systemApi.createObjects("Person", "p1", "p2");
            
            // creation of a link object (j1) of class Job between p1 and p2
            systemApi.createLinkObject(
            		"Job", 
            		"j1", 
            		"p1", "p2");
            
            return system;
        } catch ( UseApiException e ) {
            throw ( new Error( e ) );
        }
    }

    /**
     * Creates an instance of a model with one class and one association class.
     * The model is created with the given name
     * @return returns the actual System.
     */
    public MSystem createModelWithObjectsOfSameClassAndLinkObject(String modelName) {
        try {
            // creation of the system
            MModel model = TestModelUtil.getInstance()
                    .createModelWithOneClassAndOneAssocClass(modelName);
            MSystem system = new MSystem( model );

            UseSystemApi systemApi = UseSystemApi.create(system, false);

            // creation of an objects (p1,p2) of the class Person
            systemApi.createObjects("Person", "p1", "p2");

            // creation of a link object (j1) of class Job between p1 and p2
            systemApi.createLinkObject(
                    "Job",
                    "j1",
                    "p1", "p2");

            return system;
        } catch ( UseApiException e ) {
            throw ( new Error( e ) );
        }
    }

    /**
     * Creates a model with two classes and an association class. It creates
     * instances of those as well.
     *
     * @return returns the actual System.
     */
    public MSystem createModelWithObjectsAndTenaryLinkObject() {
        try {
            // creation of the system
            MModel model = TestModelUtil.getInstance()
                    .createModelWithClassAndTenaryAssocClass();
            MSystem system = new MSystem( model );
            
            UseSystemApi systemApi = UseSystemApi.create(system, false);
            
            // creation of an object (p1) of the class Person
            systemApi.createObjects("Person", "p1");
            
            // creation of an object (c1) of the class Company
            systemApi.createObjects("Company", "c1");
            
            // creation of an object (s1) of the class Salary
            systemApi.createObjects("Salary", "s1");
            
            // creation of a link object (j1) of class Job between p1, s1 and c1
            systemApi.createLinkObject(
            		"Job",
            		"j1", 
            		"p1", "c1", "s1");

            // set an attribute value in c1
            systemApi.setAttributeValue("c1", "name", "'IBM'");

            return system;
        } catch ( UseApiException e ) {
            throw ( new Error( e ) );
        }
    }

    /**
     * Creates a model with two classes and an association class. It creates
     * instances of those as well.
     * The model is created with the given name
     * @return returns the actual System.
     */
    public MSystem createModelWithObjectsAndTenaryLinkObject(String modelName) {
        try {
            // creation of the system
            MModel model = TestModelUtil.getInstance()
                    .createModelWithClassAndTenaryAssocClass(modelName);
            MSystem system = new MSystem( model );

            UseSystemApi systemApi = UseSystemApi.create(system, false);

            // creation of an object (p1) of the class Person
            systemApi.createObjects("Person", "p1");

            // creation of an object (c1) of the class Company
            systemApi.createObjects("Company", "c1");

            // creation of an object (s1) of the class Salary
            systemApi.createObjects("Salary", "s1");

            // creation of a link object (j1) of class Job between p1, s1 and c1
            systemApi.createLinkObject(
                    "Job",
                    "j1",
                    "p1", "c1", "s1");

            // set an attribute value in c1
            systemApi.setAttributeValue("c1", "name", "'IBM'");

            return system;
        } catch ( UseApiException e ) {
            throw ( new Error( e ) );
        }
    }


    /**
     * Creates a model with two classes, an association class and an association.
     * It creates instances of those as well.
     *
     * @return returns the actual System.
     */
    public MSystem createModelWithObjectsAndLinkObject2() {
        try {
            // creation of the system
            MModel model = TestModelUtil.getInstance()
                    .createComplexModel();
            MSystem system = new MSystem( model );
            
            UseSystemApi systemApi = UseSystemApi.create(system, false);
            
            // creation of two objects (p1, p2) of the class Person
            systemApi.createObjects("Person", "p1", "p2");
            
            // creation of a link between p1 and p2 (p1 is boss of p2)
            systemApi.createLink("isBoss", "p1", "p2");
                 
            // creation of two objects (c1, c2) of the class Company
            systemApi.createObjects("Company", "c1", "c2");
            
            // creation of a link object (j1) of class Job between p1 and c1
            systemApi.createLinkObject(
            		"Job", 
            		"j1",
            		"p1", "c1");
            
            // creation of a link object (j2) of class Job between p2 and c1
            systemApi.createLinkObject(
            		"Job", 
            		"j2",
            		"p2", "c1");                      
            
            // set an attribute value in c1
            systemApi.setAttributeValue("c1", "name", "'IBM'");
            
            // set an attribute value in c2
            systemApi.setAttributeValue("c2", "name", "'SUN'");

            return system;
        } catch ( UseApiException e ) {
            throw ( new Error( e ) );
        }
    }

    /**
     * Creates a model with two classes, an association class and an association.
     * It creates instances of those as well.
     * The model is created with the given name
     * @return returns the actual System.
     */
    public MSystem createModelWithObjectsAndLinkObject2(String modelName) {
        try {
            // creation of the system
            MModel model = TestModelUtil.getInstance()
                    .createComplexModel(modelName);
            MSystem system = new MSystem( model );

            UseSystemApi systemApi = UseSystemApi.create(system, false);

            // creation of two objects (p1, p2) of the class Person
            systemApi.createObjects("Person", "p1", "p2");

            // creation of a link between p1 and p2 (p1 is boss of p2)
            systemApi.createLink("isBoss", "p1", "p2");

            // creation of two objects (c1, c2) of the class Company
            systemApi.createObjects("Company", "c1", "c2");

            // creation of a link object (j1) of class Job between p1 and c1
            systemApi.createLinkObject(
                    "Job",
                    "j1",
                    "p1", "c1");

            // creation of a link object (j2) of class Job between p2 and c1
            systemApi.createLinkObject(
                    "Job",
                    "j2",
                    "p2", "c1");

            // set an attribute value in c1
            systemApi.setAttributeValue("c1", "name", "'IBM'");

            // set an attribute value in c2
            systemApi.setAttributeValue("c2", "name", "'SUN'");

            return system;
        } catch ( UseApiException e ) {
            throw ( new Error( e ) );
        }
    }



    /**
     * Creates a model with two classes and a simple invariant.
     * It creates an instance of the Person class with an attribute that doesn't satisfy the invariant
     * The model is created with the given name
     * @return returns the actual System.
     */
    public MSystem createModelWithObjectsAndConstraintsSimpleUnsatisfactory(String modelName) {
        try {
            // creation of the system
            MModel model = TestModelUtil.getInstance().createModelWithSimpleInvariant(modelName);
            MSystem system = new MSystem( model );

            UseSystemApi systemApi = UseSystemApi.create(system, false);

            // creation of an object (p1) of the class Person
            systemApi.createObjects("Person", "p1");

            systemApi.setAttributeValue("p1", "salary", "1000");


            return system;
        } catch ( Exception e ) {
            throw ( new Error( e ) );
        }
    }

    /**
     * Creates a model with two classes and a simple invariant.
     * It creates an instance of the Person class with an attribute that satisfies the invariant
     * The model is created with the given name
     * @return returns the actual System.
     */
    public MSystem createModelWithObjectsAndConstraintsSimpleSatisfactory(String modelName) {
        try {
            // creation of the system
            MModel model = TestModelUtil.getInstance().createModelWithSimpleInvariant(modelName);
            MSystem system = new MSystem( model );

            UseSystemApi systemApi = UseSystemApi.create(system, false);

            // creation of an object (p1) of the class Person
            systemApi.createObjects("Person", "p1");

            systemApi.setAttributeValue("p1", "salary", "6000");


            return system;
        } catch ( Exception e ) {
            throw ( new Error( e ) );
        }
    }

    /**
     * Creates a model with 3 classes and 4 invariants. only 3 of the 4 invariants hold
     * based on the example from <a href="https://useocl.sourceforge.net/w/index.php/Quick_Tour">https://useocl.sourceforge.net/w/index.php/Quick_Tour</a>
     *
     * It creates instances of those as well.
     * The model is created with the given name
     * @return returns the actual System.
     */
    public MSystem createComplexModelWithConstraintsUnSatisfactory(String modelName) {
        try {
            // creation of the system
            MModel model = TestModelUtil.getInstance().createComplexModelWithConstraints(modelName);
            MSystem system = new MSystem( model );

            UseSystemApi systemApi = UseSystemApi.create(system, false);

            systemApi.createObjects("Department", "cs");
            systemApi.setAttributeValue("cs", "name", "'Computer Science'");
            systemApi.setAttributeValue("cs", "location", "'Bermen'");
            systemApi.setAttributeValue("cs", "budget", "10000");

            systemApi.createObjects("Employee", "john");
            systemApi.setAttributeValue("john", "name", "'John'");
            systemApi.setAttributeValue("john", "salary", "4000");

            systemApi.createObjects("Employee", "frank");
            systemApi.setAttributeValue("frank", "name", "'Frank'");
            systemApi.setAttributeValue("frank", "salary", "4500");

            systemApi.createLink("WorksIn", "john", "cs");
            systemApi.createLink("WorksIn", "frank", "cs");

            systemApi.createObjects("Project", "research");
            systemApi.setAttributeValue("research", "name", "'Research'");
            systemApi.setAttributeValue("research", "budget", "12000");

            systemApi.createObjects("Project", "teaching");
            systemApi.setAttributeValue("teaching", "name", "'Validating UML'");
            systemApi.setAttributeValue("teaching", "budget", "3000");

            systemApi.createLink("Controls", "cs", "research");
            systemApi.createLink("Controls", "cs", "teaching");

            systemApi.createLink("WorksOn", "frank", "research");
            systemApi.createLink("WorksOn", "frank", "teaching");
            systemApi.createLink("WorksOn", "john", "research");


            return system;
        } catch ( Exception e ) {
            throw ( new Error( e ) );
        }
    }



    /**
     * Creates a model with 3 classes and 4 invariants. all 4 invariants hold
     * based on the example from <a href="https://useocl.sourceforge.net/w/index.php/Quick_Tour">https://useocl.sourceforge.net/w/index.php/Quick_Tour</a>
     *
     * It creates instances of those as well.
     * The model is created with the given name
     * @return returns the actual System.
     */
    public MSystem createComplexModelWithConstraintsSatisfactory(String modelName) {
        try {
            // creation of the system
            MModel model = TestModelUtil.getInstance().createComplexModelWithConstraints(modelName);
            MSystem system = new MSystem( model );

            UseSystemApi systemApi = UseSystemApi.create(system, false);

            systemApi.createObjects("Department", "cs");
            systemApi.setAttributeValue("cs", "name", "'Computer Science'");
            systemApi.setAttributeValue("cs", "location", "'Bermen'");
            systemApi.setAttributeValue("cs", "budget", "13000"); // <-- large enough budget

            systemApi.createObjects("Employee", "john");
            systemApi.setAttributeValue("john", "name", "'John'");
            systemApi.setAttributeValue("john", "salary", "4000");

            systemApi.createObjects("Employee", "frank");
            systemApi.setAttributeValue("frank", "name", "'Frank'");
            systemApi.setAttributeValue("frank", "salary", "4500");

            systemApi.createLink("WorksIn", "john", "cs");
            systemApi.createLink("WorksIn", "frank", "cs");

            systemApi.createObjects("Project", "research");
            systemApi.setAttributeValue("research", "name", "'Research'");
            systemApi.setAttributeValue("research", "budget", "12000");

            systemApi.createObjects("Project", "teaching");
            systemApi.setAttributeValue("teaching", "name", "'Validating UML'");
            systemApi.setAttributeValue("teaching", "budget", "3000");

            systemApi.createLink("Controls", "cs", "research");
            systemApi.createLink("Controls", "cs", "teaching");

            systemApi.createLink("WorksOn", "frank", "research");
            systemApi.createLink("WorksOn", "frank", "teaching");
            systemApi.createLink("WorksOn", "john", "research");


            return system;
        } catch ( Exception e ) {
            throw ( new Error( e ) );
        }
    }

    //=============================== inter =========================================

}
