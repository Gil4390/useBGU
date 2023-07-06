package org.tzi.use.uml.sys.multisys;

import junit.framework.TestCase;
import org.tzi.use.api.UseApiException;
import org.tzi.use.api.UseModelApi;
import org.tzi.use.api.UseMultiSystemApi;
import org.tzi.use.uml.mm.MAggregationKind;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.sys.MSystem;

import javax.naming.OperationNotSupportedException;

public class MultiLinksTest extends TestCase {

    public void testMultipleLinkCreation() {
        try {
            MSystem system_a = createSystemWithClassesAndAssocs("ModelA");
            MSystem system_b = createSystemWithClassesAndAssocs("ModelB");

            UseMultiSystemApi multiApi = new UseMultiSystemApi();
            createClassAndLinkObjects(multiApi, system_a, system_b);

            assertEquals("j1",multiApi.getApiSafe("ModelA").getObject("j1").name());
            assertEquals("j2",multiApi.getApiSafe("ModelB").getObject("j2").name());

        } catch (UseApiException e) {
            throw new Error(e);
        }
    }

    public void testMultipleLinkDeletion() {
        try {
            MSystem system_a = createSystemWithClassesAndAssocs("ModelA");
            MSystem system_b = createSystemWithClassesAndAssocs("ModelB");

            UseMultiSystemApi multiApi = new UseMultiSystemApi();
            createClassAndLinkObjects(multiApi, system_a, system_b);

            multiApi.deleteObjects(system_a.model().name(), "j1","p1","c1");
            multiApi.deleteObjects(system_b.model().name(), "j2","p2","c2");


            assertFalse(multiApi.getApiSafe(system_a.model().name()).getSystem().state().hasObjects());
            assertFalse(multiApi.getApiSafe(system_b.model().name()).getSystem().state().hasObjects());
        } catch (UseApiException e) {
            throw new Error(e);
        }
    }

    public void testMultipleConnectedObjectsDeletion() {
        try {
            MSystem system_a = createSystemWithClassesAndAssocs("ModelA");
            MSystem system_b = createSystemWithClassesAndAssocs("ModelB");

            UseMultiSystemApi multiApi = new UseMultiSystemApi();
            createClassAndLinkObjects(multiApi, system_a, system_b);

            multiApi.deleteObjects(system_a.model().name(), "p1");
            multiApi.deleteObjects(system_b.model().name(), "p2");


            assertNull(multiApi.getApiSafe(system_a.model().name()).getObject("j1"));
            assertNull(multiApi.getApiSafe(system_b.model().name()).getObject("j2"));
        } catch (UseApiException e) {
            throw new Error(e);
        }
    }

    public void testMultipleLinkUndo() {
        try {
            MSystem system_a = createSystemWithClassesAndAssocs("ModelA");
            MSystem system_b = createSystemWithClassesAndAssocs("ModelB");

            UseMultiSystemApi multiApi = new UseMultiSystemApi();
            createClassAndLinkObjects(multiApi, system_a, system_b);

            multiApi.undo(system_a.model().name());
            multiApi.undo(system_b.model().name());

            assertFalse(system_a.state().hasObjectWithName("j1"));
            assertFalse(system_b.state().hasObjectWithName("j2"));

        } catch (UseApiException | OperationNotSupportedException e) {
            throw new Error(e);
        }
    }

    public void createClassAndLinkObjects(UseMultiSystemApi multiApi, MSystem system_a, MSystem system_b) {
        try{
            multiApi.addSystemApi(system_a, true);
            multiApi.addSystemApi(system_b, true);
            multiApi.createObjects(system_a.model().name(),"Person", "p1");
            multiApi.createObjects(system_a.model().name(),"Company", "c1");
            multiApi.createObjects(system_b.model().name(),"Person", "p2");
            multiApi.createObjects(system_b.model().name(),"Company", "c2");
            multiApi.createLinkObject(system_a.model().name(),"Job","j1", new String[] {"p1","c1"});
            multiApi.createLinkObject(system_b.model().name(),"Job","j2", new String[] {"p2","c2"});

        } catch (UseApiException e) {
            throw new Error(e);
        }

    }

    public MSystem createSystemWithClassesAndAssocs(String modelName) {
        try {
            UseModelApi api = new UseModelApi(modelName);
            api.createClass("Person", false);
            api.createClass("Company", false);
            api.createAttribute("Company", "name", "String");

            api.createAssociationClass("Job", false,
                    "Person" , "person" , "0..1", MAggregationKind.NONE,
                    "Company", "company", "0..1", MAggregationKind.NONE);

            api.createAttribute( "Job", "salary", "Integer" );
            return new MSystem(api.getModel());
        } catch (UseApiException e) {
            throw new Error(e);
        }
    }

}
