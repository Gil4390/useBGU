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


    public MMultiModel createMultiModelTwoModelsAssociationClass() {
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

    public MMultiModel createMultiModelTwoModelsAssociationClassWithAttribute() {
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

    public MMultiModel createMultiModelWithCircularAssoc() {
        try {
            UseMultiModelApi multiApi = new UseMultiModelApi("Multi");
            MModel model = TestModelUtil.getInstance().createModelWithCircularAssoc("model1");
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

            UseModelApi api1 = new UseModelApi("PersonCompany1");
            api1.createClass("Person", false );
            multiApi.addModel(api1.getModel());

            UseModelApi api2 = new UseModelApi("PersonCompany2");
            api2.createClass("Company", false );
            multiApi.addModel(api2.getModel());

            multiApi.createInterAssociation("Job", "PersonCompany1", "PersonCompany2",
                    "Person" , "person" , "0..1", MAggregationKind.NONE,
                    "Company", "company", "0..1", MAggregationKind.NONE);
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

            multiApi.createInterAssociation("Job", "model1", "model2",
                    "Person" , "person" , "0..1", MAggregationKind.NONE,
                    "Company", "company", "0..1", MAggregationKind.NONE);
            multiApi.createInterAssociation("Studies", "model1", "model3",
                    "Person" , "graduate" , "0..1", MAggregationKind.NONE,
                    "School", "studiedAt", "0..1", MAggregationKind.NONE);
            multiApi.createInterAssociation("Students", "model2", "model3",
                    "Company" , "worksAt" , "0..1", MAggregationKind.NONE,
                    "School", "interns", "0..1", MAggregationKind.NONE);
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

            multiApi.createInterAssociation("Job", "model1", "model2",
                    "Person" , "person" , "0..1", MAggregationKind.NONE,
                    "Company", "company", "*", MAggregationKind.NONE);
            multiApi.createInterAssociation("Study", "model2", "model1",
                    "Company" , "graduate" , "0..1", MAggregationKind.NONE,
                    "Person", "studiedAt", "0..1", MAggregationKind.NONE);
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

            multiApi.createInterAssociation("Job", "model2", "model1",
                    "Student" , "student" , "0..1", MAggregationKind.NONE,
                    "Employee", "employee", "*", MAggregationKind.NONE);
            multiApi.createInterInvariant("SalaryInv", "model2" ,"Student",
                    "self.employee->forAll(e | e.salary > self.salary)",
                    false);
            multiApi.createInterInvariant("SizeInv", "model2" ,"Student",
                    "self.employee->size() >= 2",
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

            multiApi.createInterAssociation("Interns", "model3", "model2",
                    "Company" , "internAt" , "0..1", MAggregationKind.NONE,
                    "Student", "interns", "*", MAggregationKind.NONE);
            multiApi.createInterAssociation("Supervise", "model2", "model1",
                    "Student" , "supervising" , "*", MAggregationKind.NONE,
                    "Employee", "supervisors", "*", MAggregationKind.NONE);
            multiApi.createInterAssociation("Workers", "model3", "model1",
                    "Company" , "worksAt" , "0..1", MAggregationKind.NONE,
                    "Employee", "employers", "*", MAggregationKind.NONE);
            multiApi.createInterInvariant("ValidSupervisors", "model3" ,"Company",
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
            api1.createAttribute("Employee", "age", "Integer");

            multiApi.addModel(api1.getModel());

            multiApi.createInterAssociation("WorksFor", "model1", "model1",
                    "Employee" , "supervisor" , "0..1", MAggregationKind.NONE,
                    "Employee", "supervising", "*", MAggregationKind.NONE);
            multiApi.createInterInvariant("SalaryLowerThanSupervisor", "model1" ,"Employee",
                    "self.supervising->forAll(e1 | e1.salary < self.salary)", false);

            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }

    public MMultiModel createMultiModelInterAssocitionWithGeneralization() {
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


            multiApi.createInterAssociation("Job", "model1", "model2",
                    "Person" , "employees" , "*", MAggregationKind.NONE,
                    "Company", "workplace", "0..1", MAggregationKind.NONE);
            multiApi.createInterInvariant("AdultEmployees", "model2" ,"Company",
                    "self.employees->forAll(e1 | e1.oclIsTypeOf(model1.Adult))", false);

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
            api2.createAttribute("School", "start_", "Integer");
            api2.createAttribute("School", "end_", "Integer");

            api2.createAssociation("Studies",
                    "Student" , "students" , "*", MAggregationKind.NONE,
                    "School", "school", "0..1", MAggregationKind.NONE);
            api2.createAssociation("meets",
                    "Student", "std", "1..3", MAggregationKind.NONE,
                    "Meeting" , "mt" , "0..*", MAggregationKind.NONE);
            api2.createInvariant("validGrade", "Student", "self.grade >= 0 and self.grade <= 100", false);

            multiApi.addModel(api2.getModel());


            multiApi.createInterAssociation("supervising", "model1", "model2",
                    "Employee", "supervisor", "1..2", MAggregationKind.NONE,
                    "Student" , "students" , "*", MAggregationKind.NONE);
            multiApi.createInterAssociation("study", "model1", "model2",
                    "Employee", "graduates", "*", MAggregationKind.NONE,
                    "School" , "studiedAt" , "0..1", MAggregationKind.NONE);
            multiApi.createInterAssociation("empMeets", "model1", "model2",
                    "Employee", "emp", "1..2", MAggregationKind.NONE,
                    "Meeting" , "mt" , "0..*", MAggregationKind.NONE);

            // an employee salary must be larger than any student salary [attributes]
            multiApi.createInterInvariant("empLargerSalary", "model1" ,"Employee",
                    "self.students->forAll(s1 | self.salary > s1.salary)", false);
            // an employee can supervise only students from his school
            multiApi.createInterInvariant("empStudentSameSchool", "model1" ,"Employee",
                    "self.students->forAll(s1 | s1.school = self.studiedAt)", false);
            // a student must have least one supervisor who is a manager
            multiApi.createInterInvariant("atLeastOneManager", "model2" ,"Student",
                    "self.supervisor->exists(oclIsTypeOf(model1.Manager))", false);
            // a student must attend with a manager of level 'A' who is not his supervisor
            multiApi.createInterInvariant("meeting", "model2" ,"Student",
                    "self.mt->exists(m |\tm.emp->exists(em| em.oclIsTypeOf(model1.Manager) and\n" +
                            "not self.supervisor->includes(em) and \n" +
                            "em.oclAsType(model1.Manager).level='A'))", false);

            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }

    private static UseModelApi helper1() throws UseApiException {
        UseModelApi api1 = new UseModelApi("model1");
        api1.createClass("A", false );

        api1.createClass("B", false);
        api1.createGeneralization("B", "A");

        api1.createClass("C", false);
        api1.createGeneralization("C", "B");


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


            multiApi.createInterAssociation("interAssoc1", "model1", "model2",
                    "A", "a1", "*", MAggregationKind.NONE,
                    "Bar" , "a2" , "*", MAggregationKind.NONE);

            multiApi.createInterInvariant("inv2", "model2" , "Bar",
                    "self.a1->exists(oclIsTypeOf(model1.B))", false);


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


            multiApi.createInterAssociation("interAssoc1", "model1", "model2",
                    "A", "a1", "*", MAggregationKind.NONE,
                    "Bar" , "a2" , "*", MAggregationKind.NONE);

            multiApi.createInterInvariant("inv2", "model2" , "Bar",
                    "self.a1->exists(oclIsKindOf(model1.B))", false);


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


            multiApi.createInterAssociation("interAssoc1", "model1", "model2",
                    "A", "a1", "*", MAggregationKind.NONE,
                    "Bar" , "a2" , "*", MAggregationKind.NONE);

            multiApi.createInterInvariant("inv2", "model2" , "Bar",
                    "self.a1->selectByType(model1.B)->size() >= 1", false);


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


            multiApi.createInterAssociation("interAssoc1", "model1", "model2",
                    "A", "a1", "*", MAggregationKind.NONE,
                    "Bar" , "a2" , "*", MAggregationKind.NONE);

            multiApi.createInterInvariant("inv2", "model2" , "Bar",
                    "self.a1->selectByKind(model1.B)->size() >= 1", false);


            return multiApi.getMultiModel();
        } catch (Exception e ) {
            throw new Error( e );
        }
    }

    //TODO:
    // oclAsType

}
