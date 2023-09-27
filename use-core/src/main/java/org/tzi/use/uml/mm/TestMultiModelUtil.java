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

    public MMultiModel createMultiModelSingleModel2() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi");
            MModel model = multiApi.createModel("PersonCompany");
            multiApi.addModel(model);

            multiApi.createClass("PersonCompany@Person", false);
            multiApi.createClass("PersonCompany@Company", false);

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
            api1.createAssociation("Job",
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

    public MMultiModel createMultiModelTwoModels2() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi");

            MModel model1 = multiApi.createModel("PersonCompany1");
            MModel model2 = multiApi.createModel("PersonCompany2");
            multiApi.addModel(model1);
            multiApi.addModel(model2);

            multiApi.createClass("PersonCompany1@Person", false);
            multiApi.createClass("PersonCompany1@Company", false);
            multiApi.createAssociation("Job",
                    "PersonCompany1@Person" , "person" , "0..1", MAggregationKind.NONE,
                    "PersonCompany1@Company", "company", "0..1", MAggregationKind.NONE);


            multiApi.createClass("PersonCompany2@Person", false);
            multiApi.createClass("PersonCompany2@Company", false);

            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }

    public MMultiModel createMultiModelTwoModelsAssociationClass() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi" );

            UseModelApi api1 = new UseModelApi("PersonCompany1");
            api1.createClass("Person", false );
            api1.createClass("Company", false );
            api1.createAssociationClass("Job", false,
                    "Person" , "person" , "0..1", MAggregationKind.NONE,
                    "Company", "company", "0..1", MAggregationKind.NONE);
            api1.createAttribute( "Job", "salary", "Integer" );
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

    public MMultiModel createMultiModelTwoModelsAssociationClass2() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi");

            MModel model1 = multiApi.createModel("PersonCompany1");
            MModel model2 = multiApi.createModel("PersonCompany2");
            multiApi.addModel(model1);
            multiApi.addModel(model2);

            multiApi.createClass("PersonCompany1@Person", false);
            multiApi.createClass("PersonCompany1@Company", false);
            multiApi.createAssociationClass("PersonCompany1@Job", false,
                    "PersonCompany1@Person" , "person" , "0..1", MAggregationKind.NONE,
                    "PersonCompany1@Company", "company", "0..1", MAggregationKind.NONE);


            multiApi.createClass("PersonCompany2@Person", false);
            multiApi.createClass("PersonCompany2@Company", false);

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

    public MMultiModel createMultiModelThreeModels2() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi" );

            UseModelApi api1 = new UseModelApi("PersonCompany1");
            UseModelApi api2 = new UseModelApi("PersonCompany2");
            UseModelApi api3 = new UseModelApi("PersonCompany3");
            multiApi.addModel(api1.getModel());
            multiApi.addModel(api2.getModel());
            multiApi.addModel(api3.getModel());


            multiApi.createClass("PersonCompany1@Person", false);
            multiApi.createClass("PersonCompany1@Company", false);
            multiApi.createAttribute("PersonCompany1@Company", "name", "String");

            multiApi.createAssociationClass("PersonCompany1@Job", false,
                    "PersonCompany1@Person" , "person" , "0..1", MAggregationKind.NONE,
                    "PersonCompany1@Company", "company", "0..1", MAggregationKind.NONE);

            multiApi.createAttribute( "PersonCompany1@Job", "salary", "Integer" );

            multiApi.createClass("PersonCompany2@Person", false);
            multiApi.createClass("PersonCompany2@Company", false);

            multiApi.createClass("PersonCompany3@Person", false);
            multiApi.createClass("PersonCompany3@Company", false);

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

    public MMultiModel createMultiModelTwoModelsInvSimple2() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi");

            UseModelApi api1 = new UseModelApi("model1");
            UseModelApi api2 = new UseModelApi("model2");
            multiApi.addModel(api1.getModel());
            multiApi.addModel(api2.getModel());

            multiApi.createClass("model1@Employee", false );
            multiApi.createAttribute("model1@Employee", "name", "String");
            multiApi.createAttribute("model1@Employee", "salary", "Integer");
            multiApi.createInvariant("PositiveSalary", "model1@Employee", "self.salary > 0", false);

            multiApi.createClass("model2@Student", false );
            multiApi.createAttribute("model2@Student", "name", "String");
            multiApi.createAttribute("model2@Student", "grade", "Integer");
            multiApi.createInvariant("ValidGrade", "model2@Student", "self.grade >= 0 and self.grade <= 100", false);

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

    public MMultiModel createMultiModelTwoModelsInvSameName2() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi");

            UseModelApi api1 = new UseModelApi("model1");
            UseModelApi api2 = new UseModelApi("model2");
            multiApi.addModel(api1.getModel());
            multiApi.addModel(api2.getModel());

            multiApi.createClass("model1@Foo", false );
            multiApi.createInvariant("i", "model1@Foo", "true", false);

            multiApi.createClass("model2@Bar", false );
            multiApi.createInvariant("i", "model2@Bar", "true", false);

            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }

    public MMultiModel createMultiModelWithInvOclIsType() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi");

            UseModelApi api1 = helper1();
            api1.createInvariant("inv1", "Foo", "self.r1->exists(oclIsTypeOf(B))", false);

            multiApi.addModel(api1.getModel());

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

    public MMultiModel createMultiModelWithCircularAssoc() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi");
            MModel model = TestModelUtil.getInstance().createModelWithClassAndAssocs("model1");
            multiApi.addModel(model);
            return multiApi.getMultiModel();
        } catch(Exception e) {
            throw new Error(e);
        }
    }

    //=============================== inter =========================================

    public MMultiModel createMultiModelTwoModelsInterAssociation() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi" );
            MModel model1 = multiApi.createModel("PersonCompany1");
            multiApi.addModel(model1);
            multiApi.createClass("PersonCompany1@Person", false );

            MModel model2 = multiApi.createModel("PersonCompany2");
            multiApi.addModel(model2);
            multiApi.createClass("PersonCompany2@Company", false );

            multiApi.createAssociation("Job",
                    "PersonCompany1@Person" , "person" , "0..1", MAggregationKind.NONE,
                    "PersonCompany2@Company", "company", "0..1", MAggregationKind.NONE);
            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }

    public MMultiModel createMultiModelMultipleInterAssociation() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi" );

            UseModelApi api1 = new UseModelApi("model1");
            api1.createClass("Person", false );
            multiApi.addModel(api1.getModel());

            UseModelApi api2 = new UseModelApi("model2");
            api2.createClass("Company", false );
            multiApi.addModel(api2.getModel());

            UseModelApi api3 = new UseModelApi("model3");
            api3.createClass("School", false );
            multiApi.addModel(api3.getModel());

            multiApi.createAssociation("Job",
                    "model1@Person" , "person" , "0..1", MAggregationKind.NONE,
                    "model2@Company", "company", "0..1", MAggregationKind.NONE);

            multiApi.createAssociation("Studies",
                    "model1@Person" , "graduate" , "0..1", MAggregationKind.NONE,
                    "model3@School", "studiedAt", "0..1", MAggregationKind.NONE);
            multiApi.createAssociation("Students",
                    "model2@Company" , "worksAt" , "0..1", MAggregationKind.NONE,
                    "model3@School", "interns", "0..1", MAggregationKind.NONE);
            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }

    public MMultiModel createMultiModelTwoInterAssociationSameClass() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi" );

            UseModelApi api1 = new UseModelApi("model1");
            api1.createClass("Person", false );
            multiApi.addModel(api1.getModel());

            UseModelApi api2 = new UseModelApi("model2");
            api2.createClass("Company", false );
            multiApi.addModel(api2.getModel());

            multiApi.createAssociation("Job",
                    "model1@Person" , "person" , "0..1", MAggregationKind.NONE,
                    "model2@Company", "company", "*", MAggregationKind.NONE);
            multiApi.createAssociation("Study",
                    "model2@Company" , "graduate" , "0..1", MAggregationKind.NONE,
                    "model1@Person", "studiedAt", "0..1", MAggregationKind.NONE);
            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }


    public MMultiModel createMultiModelInterConstraintVerySimple() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi");

            UseModelApi api1 = new UseModelApi("model1");
            api1.createClass("Employee", false );
            api1.createAttribute("Employee", "name", "String");
            api1.createAttribute("Employee", "salary", "Integer");

            multiApi.addModel(api1.getModel());

            UseModelApi api2 = new UseModelApi("model2");
            api2.createClass("Student", false );
            api2.createAttribute("Student", "name", "String");
            api2.createAttribute("Student", "grade", "Integer");
            multiApi.addModel(api2.getModel());

            multiApi.createAssociation("Job",
                    "model2@Student" , "student" , "0..1", MAggregationKind.NONE,
                    "model1@Employee", "employee", "*", MAggregationKind.NONE);
            multiApi.createInterInvariant("SalaryInv","model2@Student",
                    "self.grade > 0 and self.grade < 100",
                    false);
            multiApi.createInterInvariant("SizeInv", "model1@Employee",
                    "self.salary > 100",
                    false);

            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }

    public MMultiModel createMultiModelInterConstraintSimple() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi");

            UseModelApi api1 = new UseModelApi("model1");
            api1.createClass("Employee", false );
            api1.createAttribute("Employee", "name", "String");
            api1.createAttribute("Employee", "salary", "Integer");
            api1.createAttribute("Employee", "ident", "Integer");

            multiApi.addModel(api1.getModel());

            UseModelApi api2 = new UseModelApi("model2");
            api2.createClass("Student", false );
            api2.createAttribute("Student", "name", "String");
            api2.createAttribute("Student", "grade", "Integer");
            api2.createAttribute("Student", "salary", "Integer");
            multiApi.addModel(api2.getModel());

            multiApi.createAssociation("Job",
                    "model2@Student" , "student" , "*", MAggregationKind.NONE,
                    "model1@Employee", "employee", "0..1", MAggregationKind.NONE);
            multiApi.createInterInvariant("SalaryInv","model1@Employee",
                    "self.student->forAll(e | e.salary < self.salary)",
                    false);
            multiApi.createInterInvariant("SizeInv", "model1@Employee",
                    "self.student->size() >= 2",
                    false);

            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }

    public MMultiModel createMultiModelInterConstraintComplex() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi");

            UseModelApi api1 = new UseModelApi("model1");
            api1.createClass("Employee", false );
            api1.createAttribute("Employee", "name", "String");
            api1.createAttribute("Employee", "salary", "Integer");
            multiApi.addModel(api1.getModel());

            UseModelApi api2 = new UseModelApi("model2");
            api2.createClass("Student", false );
            api2.createAttribute("Student", "name", "String");
            api2.createAttribute("Student", "grade", "Integer");
            multiApi.addModel(api2.getModel());

            UseModelApi api3 = new UseModelApi("model3");
            api3.createClass("Company", false );
            api3.createAttribute("Company", "name", "String");
            multiApi.addModel(api3.getModel());

            multiApi.createAssociation("Interns",
                    "model3@Company" , "internAt" , "0..1", MAggregationKind.NONE,
                    "model2@Student", "interns", "*", MAggregationKind.NONE);
            multiApi.createAssociation("Supervise",
                    "model2@Student" , "model1@supervising" , "*", MAggregationKind.NONE,
                    "Employee", "supervisors", "*", MAggregationKind.NONE);
            multiApi.createAssociation("Workers",
                    "model3@Company" , "worksAt" , "0..1", MAggregationKind.NONE,
                    "model1@Employee", "employers", "*", MAggregationKind.NONE);
            multiApi.createInterInvariant("ValidSupervisors", "model3@Company",
                    "self.interns->forAll(i1 | i1.supervisors->size() > 0)", false);

            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }

    public MMultiModel createMultiModelInterConstraintSelfAssociation() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi");

            UseModelApi api1 = new UseModelApi("model1");
            api1.createClass("Employee", false );
            api1.createAttribute("Employee", "name", "String");
            api1.createAttribute("Employee", "salary", "Integer");

            multiApi.addModel(api1.getModel());

            multiApi.createAssociation("WorksFor",
                    "model1@Employee" , "supervisor" , "0..1", MAggregationKind.NONE,
                    "model1@Employee", "supervising", "*", MAggregationKind.NONE);
            multiApi.createInterInvariant("SalaryLowerThanSupervisor", "model1@Employee",
                    "self.supervising->forAll(e1 | e1.salary < self.salary)", false);

            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }

    public MMultiModel createMultiModelInterAssociationWithGeneralization() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi");

            UseModelApi api1 = new UseModelApi("model1");
            api1.createClass("Person", false );
            api1.createClass("Adult", false );
            api1.createGeneralization("Adult", "Person");

            multiApi.addModel(api1.getModel());

            UseModelApi api2 = new UseModelApi("model2");
            api2.createClass("Company", false );

            multiApi.addModel(api2.getModel());


            multiApi.createAssociation("Job",
                    "model1@Person" , "employees" , "*", MAggregationKind.NONE,
                    "model2@Company", "workplace", "0..1", MAggregationKind.NONE);
            multiApi.createInterInvariant("AdultEmployees", "model2@Company",
                    "self.employees->forAll(e1 | e1.oclIsTypeOf(model1@Adult))", false);

            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }

    public MMultiModel createMultiModelInterConstraintComplex2() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi");

            UseModelApi api1 = new UseModelApi("model1");
            api1.createClass("Employee", false );
            api1.createAttribute("Employee", "name", "String");
            api1.createAttribute("Employee", "salary", "Integer");

            api1.createClass("Manager", false);
            api1.createGeneralization("Manager", "Employee");
            api1.createAttribute("Manager", "level", "String");

            api1.createClass("Worker", false);
            api1.createGeneralization("Worker", "Employee");

            api1.createClass("Company", false );
            api1.createAttribute("Company", "name", "String");
            api1.createAttribute("Company", "location", "String");

            api1.createAssociation("Works",
                    "Employee" , "workers" , "*", MAggregationKind.NONE,
                    "Company", "company", "0..1", MAggregationKind.NONE);

            api1.createInvariant("PositiveSalary", "Employee", "self.salary > 0", false);

            multiApi.addModel(api1.getModel());

            UseModelApi api2 = new UseModelApi("model2");
            api2.createClass("Student", false );
            api2.createAttribute("Student", "name", "String");
            api2.createAttribute("Student", "grade", "Integer");
            api2.createAttribute("Student", "salary", "Integer");

            api2.createClass("School", false );
            api2.createAttribute("School", "name", "String");
            api2.createAttribute("School", "location", "String");

            api2.createClass("Meeting", false );
            api2.createAttribute("Meeting", "start_", "Integer");
            api2.createAttribute("Meeting", "end_", "Integer");

            api2.createAssociation("Studies",
                    "Student" , "students" , "*", MAggregationKind.NONE,
                    "School", "school", "0..1", MAggregationKind.NONE);
            api2.createAssociation("meets",
                    "Student", "std", "1..3", MAggregationKind.NONE,
                    "Meeting" , "mt" , "0..*", MAggregationKind.NONE);
            api2.createInvariant("validGrade", "Student", "self.grade >= 0 and self.grade <= 100", false);

            multiApi.addModel(api2.getModel());


            multiApi.createAssociation("supervising",
                    "model1@Employee", "supervisor", "1..2", MAggregationKind.NONE,
                    "model2@Student" , "students" , "*", MAggregationKind.NONE);
            multiApi.createAssociation("study",
                    "model1@Employee", "graduates", "*", MAggregationKind.NONE,
                    "model2@School" , "studiedAt" , "0..1", MAggregationKind.NONE);
            multiApi.createAssociation("empMeets",
                    "model1@Employee", "emp", "1..2", MAggregationKind.NONE,
                    "model2@Meeting" , "mt" , "0..*", MAggregationKind.NONE);

            // an employee salary must be larger than any student salary [attributes]
            multiApi.createInterInvariant("empLargerSalary", "model1@Employee",
                    "self.students->forAll(s1 | self.salary > s1.salary)", false);
            // an employee can supervise only students from his school
            multiApi.createInterInvariant("empStudentSameSchool", "model1@Employee",
                    "self.students->forAll(s1 | s1.school = self.studiedAt)", false);
            // a student must have least one supervisor who is a manager
            multiApi.createInterInvariant("atLeastOneManager", "model2@Student",
                    "self.supervisor->exists(oclIsTypeOf(model1@Manager))", false);
            // a student must attend with a manager of level 'A' who is not his supervisor
            multiApi.createInterInvariant("meeting", "model2@Student",
                    "self.mt->exists(m | m.emp->exists(em| em.oclIsTypeOf(model1@Manager) and\n" +
                            "not self.supervisor->includes(em) and \n" +
                            "em.oclAsType(model1@Manager).level='A'))", false);

            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }

    private static UseModelApi helper1() throws UseApiException {
        UseModelApi api1 = new UseModelApi("model1");
        api1.createClass("A", false );
        api1.createAttribute("A", "attrA", "String");

        api1.createClass("B", false);
        api1.createGeneralization("B", "A");
        api1.createAttribute("B", "attrB", "String");

        api1.createClass("C", false);
        api1.createGeneralization("C", "B");
        api1.createAttribute("C", "attrC", "String");


        api1.createClass("Foo", false );
        api1.createClass("Goo", false );

        api1.createAssociation("assoc1",
                "A" , "r1" , "*", MAggregationKind.NONE,
                "Foo", "r2", "*", MAggregationKind.NONE);

        api1.createAssociation("assoc2",
                "Goo" , "g1" , "*", MAggregationKind.NONE,
                "Foo", "g2", "*", MAggregationKind.NONE);


        return api1;
    }

    public MMultiModel createMultiModelWithConstraintOclIsType() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi");

            UseModelApi api1 = helper1();
            api1.createInvariant("inv1", "Foo", "self.r1->exists(oclIsTypeOf(B))", false);

            multiApi.addModel(api1.getModel());

            UseModelApi api2 = new UseModelApi("model2");
            api2.createClass("Bar", false );
            multiApi.addModel(api2.getModel());


            multiApi.createAssociation("interAssoc1",
                    "model1@A", "a1", "*", MAggregationKind.NONE,
                    "model2@Bar" , "a2" , "*", MAggregationKind.NONE);

            multiApi.createInterInvariant("inv2", "model2@Bar",
                    "self.a1->exists(oclIsTypeOf(model1@B))", false);


            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }

    public MMultiModel createMultiModelWithConstraintOclIsKind() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi");

            UseModelApi api1 = helper1();
            api1.createInvariant("inv1", "Foo", "self.r1->exists(oclIsKindOf(B))", false);

            multiApi.addModel(api1.getModel());

            UseModelApi api2 = new UseModelApi("model2");
            api2.createClass("Bar", false );
            multiApi.addModel(api2.getModel());


            multiApi.createAssociation("interAssoc1",
                    "model1@A", "a1", "*", MAggregationKind.NONE,
                    "model2@Bar" , "a2" , "*", MAggregationKind.NONE);

            multiApi.createInterInvariant("inv2", "model2@Bar",
                    "self.a1->exists(oclIsKindOf(model1@B))", false);


            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }

    public MMultiModel createMultiModelWithConstraintSelectByType() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi");

            UseModelApi api1 = helper1();
            api1.createInvariant("inv1", "Foo", "self.r1->selectByType(B)->size() >= 1", false);

            multiApi.addModel(api1.getModel());

            UseModelApi api2 = new UseModelApi("model2");
            api2.createClass("Bar", false );
            multiApi.addModel(api2.getModel());


            multiApi.createAssociation("interAssoc1",
                    "model1@A", "a1", "*", MAggregationKind.NONE,
                    "model2@Bar" , "a2" , "*", MAggregationKind.NONE);

            multiApi.createInterInvariant("inv2", "model2@Bar",
                    "self.a1->selectByType(model1@B)->size() >= 1", false);


            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }

    public MMultiModel createMultiModelWithConstraintSelectByKind() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi");

            UseModelApi api1 = helper1();
            api1.createInvariant("inv1", "Foo", "self.r1->selectByKind(B)->size() >= 1", false);

            multiApi.addModel(api1.getModel());

            UseModelApi api2 = new UseModelApi("model2");
            api2.createClass("Bar", false );
            multiApi.addModel(api2.getModel());


            multiApi.createAssociation("interAssoc1",
                    "model1@A", "a1", "*", MAggregationKind.NONE,
                    "model2@Bar" , "a2" , "*", MAggregationKind.NONE);

            multiApi.createInterInvariant("inv2", "model2@Bar",
                    "self.a1->selectByKind(model1@B)->size() >= 1", false);


            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }

    public MMultiModel createMultiModelWithConstraintOclAsType() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi");

            UseModelApi api1 = new UseModelApi("model1");
            api1.createClass("Person", false);
            api1.createClass("Student", false);
            api1.createClass("Teacher", false);
            api1.createGeneralization("Student", "Person");
            api1.createGeneralization("Teacher", "Person");

            api1.createAttribute("Student", "ID", "String");

            api1.createClass("School", false);

            api1.createAssociation("PartOfSchool",
                    "School", "school", "*", MAggregationKind.NONE,
                    "Person", "people", "*", MAggregationKind.NONE);

            api1.createInvariant("inv1", "School",
                    "self.people->forAll(p | p.oclIsTypeOf(Student) implies p.oclAsType(Student).ID <> '')",
                    false);

            multiApi.addModel(api1.getModel());



            UseModelApi api2 = new UseModelApi("model2");
            api2.createClass("University", false );
            multiApi.addModel(api2.getModel());

            multiApi.createAssociation("PartOfUni",
                    "model2@University", "university", "*", MAggregationKind.NONE,
                    "model1@Person" , "people" , "*", MAggregationKind.NONE);

            multiApi.createInterInvariant("inv2", "model2@University",
                    "self.people->forAll(p | p.oclIsTypeOf(model1@Student) implies p.oclAsType(model1@Student).ID <> '')",
                    false);


            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }


    public MMultiModel createMultiModelWithInterClass() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi");

            UseModelApi api1 = new UseModelApi("model1");
            UseModelApi api2 = new UseModelApi("model2");
            multiApi.addModel(api1.getModel());
            multiApi.addModel(api2.getModel());

            multiApi.createClass("model1@A", false );
            multiApi.createClass("model2@B", false );
            multiApi.createClass("model2@C", false );


            multiApi.createClass("A", false);
            multiApi.createClass("B", false);

            multiApi.createAttribute("model1@A", "number", "Integer");
            multiApi.createAttribute("B", "salary", "Integer");

            multiApi.createAssociation("A1",
                    "A", "a1", "*", MAggregationKind.NONE,
                    "model1@A", "a2", "*", MAggregationKind.NONE);

            multiApi.createAssociation("A2",
                    "A", "b1", "*", MAggregationKind.NONE,
                    "B", "b2", "*", MAggregationKind.NONE);

            multiApi.createAssociation("A3",
                    "A", "aaa1", "*", MAggregationKind.NONE,
                    "A", "aaa2", "*", MAggregationKind.NONE);

            multiApi.createAssociation("A4",
                    "model2@B", "bb1", "*", MAggregationKind.NONE,
                    "model2@C", "cc1", "*", MAggregationKind.NONE);


            multiApi.createInterInvariant("inv1", "A",
                    "self.a2->forAll(a | a.number > 5)", false);

            multiApi.createInterInvariant("inv2", "A",
                    "self.b2->forAll(b | b.salary > 50)", false);

            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }

    public MMultiModel createMultiModelWithInterAssociationClass() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi");

            UseModelApi api1 = new UseModelApi("model1");
            UseModelApi api2 = new UseModelApi("model2");
            multiApi.addModel(api1.getModel());
            multiApi.addModel(api2.getModel());

            multiApi.createClass("model1@A", false );
            multiApi.createClass("model2@B", false );
            multiApi.createClass("model2@C", false );


            multiApi.createClass("A", false);
            multiApi.createClass("B", false);

            multiApi.createAttribute("model1@A", "number", "Integer");
            multiApi.createAttribute("B", "salary", "Integer");

            multiApi.createAssociationClass("Ass1", false,
                    "model1@A", "ma1", "*", MAggregationKind.NONE,
                    "model2@B", "mb1", "*", MAggregationKind.NONE);

            multiApi.createAssociationClass("Ass2", false,
                    "A", "ma2", "*", MAggregationKind.NONE,
                    "model2@B", "mb2", "*", MAggregationKind.NONE);

            multiApi.createAssociationClass("Ass3", false,
                    "A", "ma3", "*", MAggregationKind.NONE,
                    "B", "mb3", "*", MAggregationKind.NONE);

            multiApi.createAssociationClass("Ass4", false,
                    "model2@B", "mb4", "*", MAggregationKind.NONE,
                    "model2@C", "mc4", "*", MAggregationKind.NONE);






            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }

}
