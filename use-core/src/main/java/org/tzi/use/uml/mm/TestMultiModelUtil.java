package org.tzi.use.uml.mm;

import org.tzi.use.api.UseApiException;
import org.tzi.use.api.UseModelApi;
import org.tzi.use.api.UseMultiModelApi;

import java.util.ArrayList;
import java.util.List;

public class TestMultiModelUtil {

    private static TestMultiModelUtil util = null;

    private TestMultiModelUtil() { }

    /**
     * This method is for creating an instance of this class. It
     * guarantees that only one instance of this class exists.
     */
    public static TestMultiModelUtil getInstance() {
        if ( util == null ) {
            util = new TestMultiModelUtil();
        }
        return util;
    }

    /**
     * This method creates an empty multi-model.
     */
    public MMultiModel createEmptyMultiModel() {
        UseMultiModelApi api = new UseMultiModelApi( "PersonCompany" );
        return api.getMultiModel();
    }


    public MMultiModel createMultiModelSingleModel() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi");

            UseModelApi api = new UseModelApi("PersonCompany");
            api.createClass("Person", false );
            api.createClass("Company", false );

            multiApi.addModel(api.getModel());

            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }

    public MMultiModel createMultiModelTwoModels() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi" );

            UseModelApi api1 = new UseModelApi("PersonCompany1");
            api1.createClass("Person", false );
            api1.createClass("Company", false );
            api1.createAssociationClass("Job", false,
                    "Person" , "person" , "0..1", MAggregationKind.NONE,
                    "Company", "company", "0..1", MAggregationKind.NONE);
            multiApi.addModel(api1.getModel());

            UseModelApi api2 = new UseModelApi("PersonCompany2");
            api2.createClass("Person", false );
            api2.createClass("Company", false );
            multiApi.addModel(api2.getModel());

            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }

    public MMultiModel createMultiModelTwoModelsDifferentClassNames() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi" );

            UseModelApi api1 = new UseModelApi("PersonCompany1");
            api1.createClass("Student", false );
            api1.createClass("School", false );

            multiApi.addModel(api1.getModel());

            UseModelApi api2 = new UseModelApi("PersonCompany2");
            api2.createClass("Person", false );
            api2.createClass("Company", false );
            api2.createAssociationClass("Job", false,
                    "Person" , "person" , "0..1", MAggregationKind.NONE,
                    "Company", "company", "0..1", MAggregationKind.NONE);
            multiApi.addModel(api2.getModel());

            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }


    public MMultiModel createMultiModelTwoModels_SameNameFail() throws Exception {
        UseMultiModelApi multiApi = new UseMultiModelApi("Multi" );

        UseModelApi api1 = new UseModelApi("PersonCompany1");
        api1.createClass("Person", false );
        api1.createClass("Company", false );
        multiApi.addModel(api1.getModel());

        UseModelApi api2 = new UseModelApi("PersonCompany1");
        api2.createClass("Person", false );
        api2.createClass("Company", false );
        multiApi.addModel(api2.getModel());

        return multiApi.getMultiModel();
    }


    public MMultiModel createMultiModelThreeModels() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi" );

            UseModelApi api1 = new UseModelApi("PersonCompany1");
            api1.createClass("Person", false);
            api1.createClass("Company", false);
            api1.createAttribute("Company", "name", "String");

            api1.createAssociationClass("Job", false,
                    "Person" , "person" , "0..1", MAggregationKind.NONE,
                    "Company", "company", "0..1", MAggregationKind.NONE);

            api1.createAttribute( "Job", "salary", "Integer" );
            multiApi.addModel(api1.getModel());

            UseModelApi api2 = new UseModelApi("PersonCompany2");
            api2.createClass("Person", false );
            api2.createClass("Company", false );
            multiApi.addModel(api2.getModel());

            UseModelApi api3 = new UseModelApi("PersonCompany3");
            api3.createClass("Person", false );
            api3.createClass("Company", false );
            multiApi.addModel(api3.getModel());

            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }


    public MMultiModel createMultiModelTwoModelsInvSimple() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi");

            UseModelApi api1 = new UseModelApi("model1");
            api1.createClass("Employee", false );
            api1.createAttribute("Employee", "name", "String");
            api1.createAttribute("Employee", "salary", "Integer");
            api1.createInvariant("PositiveSalary", "Employee", "self.salary > 0", false);
            multiApi.addModel(api1.getModel());

            UseModelApi api2 = new UseModelApi("model2");
            api2.createClass("Student", false );
            api2.createAttribute("Student", "name", "String");
            api2.createAttribute("Student", "grade", "Integer");
            api2.createInvariant("ValidGrade", "Student", "self.grade >= 0 and self.grade <= 100", false);
            multiApi.addModel(api2.getModel());

            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }

    public MMultiModel createMultiModelTwoModelsInvSameName() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi");

            UseModelApi api1 = new UseModelApi("model1");
            api1.createClass("Foo", false );
            api1.createInvariant("i", "Foo", "true", false);
            multiApi.addModel(api1.getModel());

            UseModelApi api2 = new UseModelApi("model2");
            api2.createClass("Bar", false );
            api2.createInvariant("i", "Bar", "true", false);
            multiApi.addModel(api2.getModel());

            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }

    public MMultiModel createMultiModelTwoModelsWithEnum() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi");

            UseModelApi api1 = new UseModelApi("model1");
            List<String> literals1 = new ArrayList<String>();
            literals1.add( "foo" );
            api1.createEnumeration("A", literals1);
            multiApi.addModel(api1.getModel());

            UseModelApi api2 = new UseModelApi("model2");
            List<String> literals2 = new ArrayList<String>();
            literals2.add( "bar" );
            api2.createEnumeration("A", literals2);
            multiApi.addModel(api2.getModel());

            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }


    public MMultiModel createMultiModelTwoModelsWithClassAndEnumSameName() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi");

            UseModelApi api1 = new UseModelApi("model1");
            List<String> literals1 = new ArrayList<String>();
            literals1.add( "foo" );
            api1.createEnumeration("A", literals1);
            multiApi.addModel(api1.getModel());

            UseModelApi api2 = new UseModelApi("model2");
            api2.createClass("A", false );
            multiApi.addModel(api2.getModel());

            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }
}
