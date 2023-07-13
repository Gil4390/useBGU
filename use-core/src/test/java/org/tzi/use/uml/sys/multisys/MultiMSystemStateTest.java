package org.tzi.use.uml.sys.multisys;

import junit.framework.TestCase;
import org.junit.Test;
import org.tzi.use.api.UseApiException;
import org.tzi.use.api.UseModelApi;
import org.tzi.use.api.UseMultiSystemApi;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.TestModelUtil;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MSystem;

import javax.naming.OperationNotSupportedException;

public class MultiMSystemStateTest extends TestCase {

    public void testObjectCreation() {
        try{
            UseMultiSystemApi multiApi = new UseMultiSystemApi();

            MModel model_a = TestModelUtil.getInstance().createModelWithClasses();
            MModel model_b = this.createModelWithClasses("ProductShop","Product","Shop");

            MSystem system_a = new MSystem(model_a);
            MSystem system_b = new MSystem(model_b);

            multiApi.addSystemApi(system_a, false);
            multiApi.addSystemApi(system_b, false);

            multiApi.createObjects("PersonCompany", "Person", "person1");
            multiApi.createObjects("ProductShop", "Product", "product1");

            MObject obj1 = multiApi.getApiSafe("PersonCompany").getObject("person1");
            MObject obj2 = multiApi.getApiSafe("ProductShop").getObject("product1");

            assertEquals("person1",obj1.name());
            assertEquals("product1",obj2.name());

        } catch (UseApiException e) {
            throw new Error(e);
        }

    }


    public void testMultipleObjectsCreation() {
        try{
            UseMultiSystemApi multiApi = new UseMultiSystemApi();

            MModel model_a = TestModelUtil.getInstance().createModelWithClasses();
            MModel model_b = this.createModelWithClasses("ProductShop", "Product", "Shop");

            MSystem system_a = new MSystem(model_a);
            MSystem system_b = new MSystem(model_b);

            multiApi.addSystemApi(system_a, false);
            multiApi.addSystemApi(system_b, false);

            MObject[] person_objs = multiApi.createObjects("PersonCompany", "Person", "person1", "person2", "person3");
            MObject[] product_objs = multiApi.createObjects("ProductShop", "Product", "product1", "product2");

            assertEquals(3,person_objs.length);
            assertEquals(2, product_objs.length);

        } catch (UseApiException e) {
            throw new Error(e);
        }

    }

    public void testMultipleObjectDeletion() {
        try{
            UseMultiSystemApi multiApi = new UseMultiSystemApi();

            MModel model_a = this.createModelWithClasses("ModelA", "AClass", "BClass","CClass");
            MModel model_b = this.createModelWithClasses("ModelB", "AClass", "BClass","CClass");

            MSystem system_a = new MSystem(model_a);
            MSystem system_b = new MSystem(model_b);

            multiApi.addSystemApi(system_a, false);
            multiApi.addSystemApi(system_b, false);

            multiApi.createObjects("ModelA", "AClass", "a1","a2");
            multiApi.createObjects("ModelB","BClass","b1","b2");
            multiApi.createObjects("ModelB", "AClass", "a1","a2");

            multiApi.deleteObjects("ModelA", "a1", "a2");
            multiApi.deleteObjects("ModelB", "a1", "a2","b1","b2");

            assertFalse(system_a.state().hasObjects());
            assertFalse(system_b.state().hasObjects());
        } catch (UseApiException e) {
            throw new Error(e);
        }
    }

    public void testMultipleUndoObjectCreation() {
        try{
            UseMultiSystemApi multiApi = new UseMultiSystemApi();

            MModel model_a = this.createModelWithClasses("ModelA", "AClass", "BClass");
            MModel model_b = this.createModelWithClasses("ModelB", "AClass", "BClass");

            MSystem system_a = new MSystem(model_a);
            MSystem system_b = new MSystem(model_b);

            multiApi.addSystemApi(system_a, true);
            multiApi.addSystemApi(system_b, true);

            multiApi.createObjects("ModelA", "AClass", "a1","a2");
            multiApi.createObjects("ModelB","BClass","b1","b2");

            multiApi.undo("ModelA");
            multiApi.undo("ModelB");

            assertTrue(system_a.state().hasObjectWithName("a1") && !system_a.state().hasObjectWithName("a2"));
            assertTrue(system_b.state().hasObjectWithName("b1") && !system_b.state().hasObjectWithName("b2"));
        } catch (UseApiException | OperationNotSupportedException e) {
            throw new Error(e);
        }
    }

    private MModel createModelWithClasses(String modelName, String... classes) {
        try{
            UseModelApi api = new UseModelApi(modelName);
            for(String cls : classes) {
                api.createClass(cls,false);
            }
            return api.getModel();
        } catch (UseApiException e) {
            throw new Error(e);
        }
    }
}
