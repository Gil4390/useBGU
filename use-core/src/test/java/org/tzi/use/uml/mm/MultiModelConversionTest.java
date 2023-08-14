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

    public void testConvertMultiModelWithCircularAssoc() {
        try {
            MMultiModel multimodel = TestMultiModelUtil.getInstance().createMultiModelWithCircularAssoc();

            MModel model1 = ((MModel)multimodel.models().toArray()[0]);
            //MModel model2 = ((MModel)multimodel.models().toArray()[1]);
            MModel convertedModel = multimodel.toMModel();

            assertEquals(convertedModel.name(), multimodel.name());

            for (MClass mClass : model1.classes()){
                String newName = model1.name() + "_" + mClass.name();
                MClass cls = convertedModel.getClass(newName);
                assertNotNull(cls);
            }

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    //=============================== inter =========================================

    public void testConvertMultiModelWithTwoModelsInterAssociation() {
        try {
            MMultiModel multimodel = TestMultiModelUtil.getInstance().createMultiModelTwoModelsInterAssociation();

            MModel model1 = ((MModel)multimodel.models().toArray()[0]);
            MModel model2 = ((MModel)multimodel.models().toArray()[1]);
            MModel convertedModel = multimodel.toMModel();

            assertEquals(convertedModel.name(), multimodel.name());

            for (MInterAssociation interAssoc : multimodel.interAssociations()){
                MAssociation assoc = convertedModel.getAssociation(interAssoc.name());
                assertNotNull(assoc);
            }

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testConvertMultiModelMultipleInterAssociation() {
        try {
            MMultiModel multimodel = TestMultiModelUtil.getInstance().createMultiModelMultipleInterAssociation();

            MModel model1 = ((MModel)multimodel.models().toArray()[0]);
            MModel model2 = ((MModel)multimodel.models().toArray()[1]);
            MModel model3 = ((MModel)multimodel.models().toArray()[2]);
            MModel convertedModel = multimodel.toMModel();

            assertEquals(convertedModel.name(), multimodel.name());

            for (MInterAssociation interAssoc : multimodel.interAssociations()){
                MAssociation assoc = convertedModel.getAssociation(interAssoc.name());
                assertNotNull(assoc);
            }

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testConvertMultiModelTwoInterAssociationSameClass() {
        try {
            MMultiModel multimodel = TestMultiModelUtil.getInstance().createMultiModelTwoInterAssociationSameClass();

            MModel model1 = ((MModel)multimodel.models().toArray()[0]);
            MModel model2 = ((MModel)multimodel.models().toArray()[1]);
            MModel convertedModel = multimodel.toMModel();

            assertEquals(convertedModel.name(), multimodel.name());

            for (MInterAssociation interAssoc : multimodel.interAssociations()){
                MAssociation assoc = convertedModel.getAssociation(interAssoc.name());
                assertNotNull(assoc);
            }

            assertEquals(2, convertedModel.associations().size());

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testConvertMultiModelSimpleInterConstraint() {
        try {
            MMultiModel multimodel = TestMultiModelUtil.getInstance().createMultiModelInterConstraintSimple();

            MModel model1 = ((MModel)multimodel.models().toArray()[0]);
            MModel model2 = ((MModel)multimodel.models().toArray()[1]);
            MModel convertedModel = multimodel.toMModel();

            assertEquals(convertedModel.name(), multimodel.name());

            for (MClassInvariant classInv : multimodel.interConstraints()){
                MClassInvariant inv = convertedModel.getClassInvariant(classInv.cls().name()+"::"+classInv.name());
                assertNotNull(inv);
            }

            assertEquals(1, convertedModel.modelClassInvariants().size());

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testConvertMultiModelComplexInterConstraint() {
        try {
            MMultiModel multimodel = TestMultiModelUtil.getInstance().createMultiModelInterConstraintComplex();

            MModel model1 = ((MModel)multimodel.models().toArray()[0]);
            MModel model2 = ((MModel)multimodel.models().toArray()[1]);
            MModel model3 = ((MModel)multimodel.models().toArray()[2]);

            MModel convertedModel = multimodel.toMModel();

            assertEquals(convertedModel.name(), multimodel.name());

            for (MInterAssociation interAssoc : multimodel.interAssociations()){
                MAssociation assoc = convertedModel.getAssociation(interAssoc.name());
                assertNotNull(assoc);
            }

            for (MClassInvariant classInv : multimodel.interConstraints()){
                MClassInvariant inv = convertedModel.getClassInvariant(classInv.cls().name()+"::"+classInv.name());
                assertNotNull(inv);
            }

            assertEquals(1, convertedModel.modelClassInvariants().size());
            assertEquals(3, convertedModel.associations().size());

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }


    //TODO:
    // mm with 2 models and 1 inter assoc
    // mm with 3 models and multiple inter assoc
    // mm with 2 models and 2 inter assoc between the same classes
    // mm with 2 models and simple inter constraint
    // mm with 3 models and simple inter constraint
    // mm with 2 models and complex inter constraint
    // mm with 3 models and complex inter constraint

}
