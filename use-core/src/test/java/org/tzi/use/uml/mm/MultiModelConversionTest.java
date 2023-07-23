package org.tzi.use.uml.mm;

import junit.framework.TestCase;

public class MultiModelConversionTest extends TestCase {

    public void testConvertMultiModelWithSingleModel() {
        try {
            MMultiModel multimodel = TestMultiModelUtil.getInstance().createMultiModelSingleModel();

            MModel model = ((MModel)multimodel.models().toArray()[0]);
            MModel convertedModel = multimodel.toMModel();

            assertEquals(convertedModel.name(), multimodel.name());

            for (MClass mClass : model.classes()){
                String newName = model.name() + "_" + mClass.name();
                MClass cls = convertedModel.getClass(newName);
                assertNotNull(cls);
            }


        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testConvertMultiModelWithTwoModels() {
        try {
            MMultiModel multimodel = TestMultiModelUtil.getInstance().createMultiModelTwoModels();

            MModel model1 = ((MModel)multimodel.models().toArray()[0]);
            MModel model2 = ((MModel)multimodel.models().toArray()[1]);
            MModel convertedModel = multimodel.toMModel();

            assertEquals(convertedModel.name(), multimodel.name());

            for (MClass mClass : model1.classes()){
                String newName = model1.name() + "_" + mClass.name();
                MClass cls = convertedModel.getClass(newName);
                assertNotNull(cls);
            }

            for (MClass mClass : model2.classes()){
                String newName = model2.name() + "_" + mClass.name();
                MClass cls = convertedModel.getClass(newName);
                assertNotNull(cls);
            }

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testConvertMultiModelWithTwoModelsWithEnumSameName() {
        try {
            MMultiModel multimodel = TestMultiModelUtil.getInstance().createMultiModelTwoModelsWithClassAndEnumSameName();

            MModel model1 = ((MModel)multimodel.models().toArray()[0]);
            MModel model2 = ((MModel)multimodel.models().toArray()[1]);
            MModel convertedModel = multimodel.toMModel();

            assertEquals(convertedModel.name(), multimodel.name());

            for (MClass mClass : model1.classes()){
                String newName = model1.name() + "_" + mClass.name();
                MClass cls = convertedModel.getClass(newName);
                assertNotNull(cls);
            }

            for (MClass mClass : model2.classes()){
                String newName = model2.name() + "_" + mClass.name();
                MClass cls = convertedModel.getClass(newName);
                assertNotNull(cls);
            }

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testConvertMultiModelWithWithClassAndEnumSameName() {
        try {
            MMultiModel multimodel = TestMultiModelUtil.getInstance().createMultiModelTwoModelsWithEnum();

            MModel model1 = ((MModel)multimodel.models().toArray()[0]);
            MModel model2 = ((MModel)multimodel.models().toArray()[1]);
            MModel convertedModel = multimodel.toMModel();

            assertEquals(convertedModel.name(), multimodel.name());

            for (MClass mClass : model1.classes()){
                String newName = model1.name() + "_" + mClass.name();
                MClass cls = convertedModel.getClass(newName);
                assertNotNull(cls);
            }

            for (MClass mClass : model2.classes()){
                String newName = model2.name() + "_" + mClass.name();
                MClass cls = convertedModel.getClass(newName);
                assertNotNull(cls);
            }

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testConvertMultiModelWithTwoModelsInvSimple() {
        try {
            MMultiModel multimodel = TestMultiModelUtil.getInstance().createMultiModelTwoModelsInvSimple();

            MModel model1 = ((MModel)multimodel.models().toArray()[0]);
            MModel model2 = ((MModel)multimodel.models().toArray()[1]);
            MModel convertedModel = multimodel.toMModel();

            assertEquals(convertedModel.name(), multimodel.name());

            for (MClass mClass : model1.classes()){
                String newName = model1.name() + "_" + mClass.name();
                MClass cls = convertedModel.getClass(newName);
                assertNotNull(cls);
            }

            for (MClass mClass : model2.classes()){
                String newName = model2.name() + "_" + mClass.name();
                MClass cls = convertedModel.getClass(newName);
                assertNotNull(cls);
            }

            System.out.println(convertedModel.classInvariants());


        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testConvertMultiModelWithTwoModelsInvSameName() {
        try {
            MMultiModel multimodel = TestMultiModelUtil.getInstance().createMultiModelTwoModelsInvSameName();

            MModel model1 = ((MModel)multimodel.models().toArray()[0]);
            MModel model2 = ((MModel)multimodel.models().toArray()[1]);
            MModel convertedModel = multimodel.toMModel();

            assertEquals(convertedModel.name(), multimodel.name());

            for (MClass mClass : model1.classes()){
                String newName = model1.name() + "_" + mClass.name();
                MClass cls = convertedModel.getClass(newName);
                assertNotNull(cls);
            }

            for (MClass mClass : model2.classes()){
                String newName = model2.name() + "_" + mClass.name();
                MClass cls = convertedModel.getClass(newName);
                assertNotNull(cls);
            }

            System.out.println(convertedModel.classInvariants());


        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testConvertMultiModelWithTwoModelsAssociationClass() {
        try {
            MMultiModel multimodel = TestMultiModelUtil.getInstance().createMultiModelTwoModelsAssociationClass();

            MModel model1 = ((MModel)multimodel.models().toArray()[0]);
            MModel model2 = ((MModel)multimodel.models().toArray()[1]);
            MModel convertedModel = multimodel.toMModel();

            assertEquals(convertedModel.name(), multimodel.name());

            for (MClass mClass : model1.classes()){
                String newName = model1.name() + "_" + mClass.name();
                MClass cls = convertedModel.getClass(newName);
                assertNotNull(cls);
            }

            for (MClass mClass : model2.classes()){
                String newName = model2.name() + "_" + mClass.name();
                MClass cls = convertedModel.getClass(newName);
                assertNotNull(cls);
            }

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testConvertMultiModelWithTwoModelsAssociationClassWithAttribute() {
        try {
            MMultiModel multimodel = TestMultiModelUtil.getInstance().createMultiModelTwoModelsAssociationClassWithAttribute();

            MModel model1 = ((MModel)multimodel.models().toArray()[0]);
            MModel model2 = ((MModel)multimodel.models().toArray()[1]);
            MModel convertedModel = multimodel.toMModel();

            assertEquals(convertedModel.name(), multimodel.name());

            for (MClass mClass : model1.classes()){
                String newName = model1.name() + "_" + mClass.name();
                MClass cls = convertedModel.getClass(newName);
                assertNotNull(cls);
            }

            for (MClass mClass : model2.classes()){
                String newName = model2.name() + "_" + mClass.name();
                MClass cls = convertedModel.getClass(newName);
                assertNotNull(cls);
            }

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }
}
