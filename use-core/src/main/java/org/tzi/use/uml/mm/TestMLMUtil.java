package org.tzi.use.uml.mm;

import org.tzi.use.api.UseMLMApi;

public class TestMLMUtil {

    private static TestMLMUtil util = null;
    private final String delimiter = "@";
    private final String mlmName = "TestMLM";
    private final String personCompany1 = "PersonCompany1";
    private final String personCompany2 = "PersonCompany2";
    private final String personClass = "Person";
    private final String companyClass = "Company";
    private final String jobAssociation = "Job";


    private TestMLMUtil() {}

    public static TestMLMUtil getInstance() {
        if(util == null) {
            util = new TestMLMUtil();
        }
        return util;
    }

    public MMultiLevelModel createEmptyMLM() {
        UseMLMApi mlmApi = new UseMLMApi(mlmName);
        return mlmApi.getMultiLevelModel();
    }

    public MMultiLevelModel createMLMSimple() {
        UseMLMApi mlmApi = new UseMLMApi(TestMultiModelUtil.getInstance().createMultiModelTwoModels());
        MMultiLevelModel mlm = mlmApi.getMultiLevelModel();
        return mlm;
    }

    public MMultiLevelModel createMLMWithEmptyMediator() {
        UseMLMApi mlmApi = new UseMLMApi(TestMultiModelUtil.getInstance().createMultiModelTwoModels());
        MMultiLevelModel mlm = mlmApi.getMultiLevelModel();
        try {
            mlmApi.createMediator("Mediator1", personCompany1);
            mlmApi.createMediator("Mediator2", personCompany2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mlm;
    }

    public MMultiLevelModel createMLMWithClabjects_AttributeRenaming() {
        UseMLMApi mlmApi = new UseMLMApi(TestMultiModelUtil.getInstance().createMultiModelTwoModels2());
        MMultiLevelModel mlm = mlmApi.getMultiLevelModel();
        try {
            mlmApi.createMediator("Mediator1", personCompany1);
            mlmApi.createMediator("Mediator2", personCompany2);
            mlmApi.createClabject("Mediator2",personCompany2 + delimiter + personClass, personCompany1 + delimiter +  personClass);
            mlmApi.createAttributeRenaming("Mediator2",personCompany2 + delimiter + personClass, "name", "newName");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mlm;
    }

    public MMultiLevelModel createMLMWithEmptyClabjects_AttributeRenaming() {
        UseMLMApi mlmApi = new UseMLMApi(TestMultiModelUtil.getInstance().createMultiModelTwoModels2());
        MMultiLevelModel mlm = mlmApi.getMultiLevelModel();
        try {
            mlmApi.createMediator("Mediator1", personCompany1);
            mlmApi.createMediator("Mediator2", personCompany2);
            mlmApi.createClabject("Mediator2",personCompany2 + delimiter + personClass, personCompany1 + delimiter +  personClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mlm;
    }

    public MMultiLevelModel createMLMWithClabjects_AttributeRemoving() {
        return null;
    }

    public MMultiLevelModel createMLMWithEmptyAssoclinks_RoleRenaming() {
        return null;
    }

    public MMultiLevelModel createMLMWithAssoclinks_RoleRenaming_ChangeSingleRole() {
        return null;
    }

    public MMultiLevelModel createMLMWithAssoclinks_RoleRenaming() {
        return null;
    }

    public MMultiLevelModel createMLMWithMediator_complex_1() {
        return null;
    }

    public MMultiLevelModel createMLMWithMediator_complex_2() {
        return null;
    }

    public MMultiLevelModel createMLMWithThreeMediators() {
        return null;
    }

}
