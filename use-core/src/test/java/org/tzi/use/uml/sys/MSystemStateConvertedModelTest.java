package org.tzi.use.uml.sys;

import junit.framework.TestCase;
import org.tzi.use.api.UseApiException;
import org.tzi.use.api.UseModelApi;
import org.tzi.use.api.UseMultiModelApi;
import org.tzi.use.api.impl.UseSystemApiNative;
import org.tzi.use.api.impl.UseSystemApiUndoable;
import org.tzi.use.uml.mm.MAggregationKind;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MMultiModel;

public class MSystemStateConvertedModelTest extends TestCase {


    //TODO: create diagram object, convert
    public void testMultiObjectCreationConvertedModel() {
        try {
            MMultiModel multi1 = createMultiModel();
            MModel convertedModel = multi1.toMModel();
            UseSystemApiUndoable api = new UseSystemApiUndoable(convertedModel);

            api.createObjects("model1@Product","m1@p1");
            api.createObjects("model2@Employee","m2@e1","m2@e2");
            assertTrue(api.getSystem().state().hasObjectWithName("m1@p1"));
            assertTrue(api.getSystem().state().hasObjectWithName("m2@e1"));
            assertTrue(api.getSystem().state().hasObjectWithName("m2@e2"));

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void testMultiObjectDeletionConvertedModel() {
        try {
            MMultiModel multi1 = createMultiModel();
            MModel convertedModel = multi1.toMModel();
            UseSystemApiUndoable api = new UseSystemApiUndoable(convertedModel);

            api.createObjects("model1@Product","m1@p1");
            api.createObjects("model2@Employee","m2@e1","m2@e2");

            api.deleteObjects("m1@p1","m2@e1","m2@e2");
            assertFalse(api.getSystem().state().hasObjects());

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void testMultiUndoConvertedModel() {
        try {
            MMultiModel multi1 = createMultiModel();
            MModel convertedModel = multi1.toMModel();
            UseSystemApiUndoable api = new UseSystemApiUndoable(convertedModel);

            api.createObjects("model1@Product","m1@p1");
            api.createObjects("model2@Employee","m2@e1","m2@e2");

            api.undo();
            assertTrue(api.getSystem().state().hasObjectWithName("m1@p1"));
            assertTrue(api.getSystem().state().hasObjectWithName("m2@e1"));
            assertFalse(api.getSystem().state().hasObjectWithName("m2@e2"));

        } catch (Exception e) {
            throw new Error(e);
        }
    }


    public void testMultiLinksConvertedModel() {
        try{
            MMultiModel multi1 = createMultiModelWithAssocs();
            MModel converted = multi1.toMModel();
            UseSystemApiUndoable api = new UseSystemApiUndoable(converted);
            api.createObjects("model1@Store","s1");
            api.createObjects("model1@Product","p1","p2");

            api.createLink("model1@Goods","s1","p1");
        } catch (Exception e) {
            throw new Error(e);
        }

    }


    private MMultiModel createMultiModelWithAssocs() {
        try {
            MModel model1 = createModelWithClasses("model1", "Store", "Product");
            MModel model2 = createModelWithClasses("model2", "Employee", "Department");
            UseModelApi api1 = new UseModelApi(model1);
            UseModelApi api2 = new UseModelApi(model2);

            api1.createAssociation("Goods", "Store", "store", "1", MAggregationKind.NONE, "Product", "products", "0..*", MAggregationKind.NONE);
            api2.createAssociation("Role", "Employee", "employees", "1..*", MAggregationKind.NONE, "Department", "departments", "1..*", MAggregationKind.NONE);

            UseMultiModelApi multiApi = new UseMultiModelApi("multi1");
            multiApi.addModel(api1.getModel());
            multiApi.addModel(api2.getModel());
            return multiApi.getMultiModel();

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    private MMultiModel createMultiModel() {
        try {
            UseMultiModelApi api = new UseMultiModelApi("multi1");
            MModel model1 = createModelWithClasses("model1", "Product", "Item");
            MModel model2 = createModelWithClasses("model2","Employee", "Department");
            api.addModel(model1);
            api.addModel(model2);
            return api.getMultiModel();
        } catch (Exception e) {
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
