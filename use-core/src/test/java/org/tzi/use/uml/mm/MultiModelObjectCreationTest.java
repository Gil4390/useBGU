package org.tzi.use.uml.mm;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.tzi.use.api.UseMultiModelApi;
import org.tzi.use.api.UseSystemApi;
import org.tzi.use.api.impl.UseSystemApiUndoable;
import org.tzi.use.uml.sys.MSystem;

import java.io.PrintWriter;

public class MultiModelObjectCreationTest extends TestCase {
    //region object, link creation
    public void testCreateMultiModelWithSingleModelWithObjects() {
        try {
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelSingleModel();

            MSystem system = new MSystem( multiModel );

            UseSystemApi systemApi = UseSystemApi.create(system, false);

            // creation of an object (p1) of the class Person
            systemApi.createObjects("model1@Person", "p1");

            // creation of an object (c1) of the class Company
            systemApi.createObjects("model1@Company", "c1");

            Assert.assertEquals(2, system.state().numObjects());
            Assert.assertEquals(0, system.state().allLinks().size());

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testCreateMultiModelTwoModelsWithObjectsAndLinks1() {
        try {
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelTwoModels2();

            UseMultiModelApi multiApi = new UseMultiModelApi(multiModel);
            UseSystemApiUndoable systemApi = new UseSystemApiUndoable(multiApi);

            MSystem system = systemApi.getSystem();

            // creation of an object (p1) of the class PersonCompany1@Person
            systemApi.createObjects("PersonCompany1@Person", "p1");
            // creation of an object (c1) of the class PersonCompany1@Company
            systemApi.createObjects("PersonCompany1@Company", "c1");
            // creation of a link between p1 and c1 of an association
            systemApi.createLink("PersonCompany1@Job", "p1", "c1");

            // creation of an object (p2) of the class PersonCompany1@Person
            systemApi.createObjects("PersonCompany2@Person", "p2");
            // creation of an object (c2) of the class PersonCompany1@Company
            systemApi.createObjects("PersonCompany2@Company", "c2");

            Assert.assertEquals(4, system.state().numObjects());
            Assert.assertEquals(1, system.state().allLinks().size());

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testCreateMultiModelTwoModelsWithObjectsAndLinks2() {
        try {
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelTwoModels2();

            UseMultiModelApi multiApi = new UseMultiModelApi(multiModel);
            UseSystemApiUndoable systemApi = new UseSystemApiUndoable(multiApi);

            MSystem system = systemApi.getSystem();

            // creation of an object (p1) of the class PersonCompany1@Person
            systemApi.createObjects("PersonCompany1@Person", "p1");
            // creation of an object (c1) of the class PersonCompany1@Company
            systemApi.createObjects("PersonCompany1@Company", "c1");
            // creation of a link between p1 and c1 of an association
            systemApi.createLink("PersonCompany1@Job", "p1", "c1");

            // creation of an object (p2) of the class PersonCompany1@Person
            systemApi.createObjects("PersonCompany1@Person", "p2");
            // creation of an object (c2) of the class PersonCompany1@Company
            systemApi.createObjects("PersonCompany1@Company", "c2");
            // creation of a link between p2 and c2 of an association
            systemApi.createLink("PersonCompany1@Job", "p2", "c2");

            // creation of an object (p3) of the class PersonCompany1@Person
            systemApi.createObjects("PersonCompany2@Person", "p23");
            // creation of an object (c3) of the class PersonCompany1@Company
            systemApi.createObjects("PersonCompany2@Company", "c3");

            Assert.assertEquals(6, system.state().numObjects());
            Assert.assertEquals(2, system.state().allLinks().size());

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testCreateMultiModelTwoModelsWithObjectsAndLinks3_Fail() {
        try {
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelTwoModels2();

            UseMultiModelApi multiApi = new UseMultiModelApi(multiModel);
            UseSystemApiUndoable systemApi = new UseSystemApiUndoable(multiApi);

            MSystem system = systemApi.getSystem();

            // creation of an object (p1) of the class PersonCompany1@Person
            systemApi.createObjects("PersonCompany1@Person", "p1");
            // creation of an object (c1) of the class PersonCompany1@Company
            systemApi.createObjects("PersonCompany1@Company", "c1");
            // creation of a link between p1 and c1 of an association
            systemApi.createLink("PersonCompany1@Job", "p1", "c1");

            // creation of a link between p2 and c2 of an association
            systemApi.createLink("PersonCompany1@Job", "p1", "c1");

            fail("double link creation should not happen");

        } catch (Exception e) {
            assertEquals(e.getMessage(), "Link creation failed!");
        }
    }

    public void testCreateMultiModelTwoModelsWithLinksObject() {
        try {
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelThreeModels();

            UseMultiModelApi multiApi = new UseMultiModelApi(multiModel);
            UseSystemApiUndoable systemApi = new UseSystemApiUndoable(multiApi);

            MSystem system = systemApi.getSystem();

            // creation of an object (p1) of the class model1@Person
            systemApi.createObjects("model1@Person", "p1");
            // creation of an object (c1) of the class model1@Company
            systemApi.createObjects("model1@Company", "c1");
            // creation of a link between p1 and c1 of an association
            systemApi.createLink("model1@Job", "p1", "c1");

            // creation of an object (p2) of the class model2@Person
            systemApi.createObjects("model2@Person", "p2");
            // creation of an object (c2) of the class model2@Company
            systemApi.createObjects("model2@Company", "c2");

            // creation of an object (p3) of the class PersonCompany1@Person
            systemApi.createObjects("model1@Person", "p3");
            // creation of an object (c3) of the class PersonCompany1@Company
            systemApi.createObjects("model1@Company", "c3");
            // creation of a link between p3 and c3 of an association
            systemApi.createLink("model1@Job", "p3", "c3");

            Assert.assertEquals(8, system.state().numObjects());
            Assert.assertEquals(2, system.state().allLinks().size());

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }



    //endregion

    //region invariants

    public void testCreateMultiModelInv1() {
        try {
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelTwoModelsInvSimple2();

            UseMultiModelApi multiApi = new UseMultiModelApi(multiModel);
            UseSystemApiUndoable systemApi = new UseSystemApiUndoable(multiApi);

            MSystem system = systemApi.getSystem();

            systemApi.createObjects("model2@Student", "s1");

            systemApi.createObjects("model1@Employee", "e1");

            systemApi.setAttributeValue("s1","grade","10");
            systemApi.setAttributeValue("e1","salary","100");

            assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("s1","grade","101");

            assertFalse(systemApi.checkState());

            systemApi.setAttributeValue("s1","grade","80");

            assertTrue(systemApi.checkState());



        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testCreateMultiModelInv2() {
        try {
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelWithInvOclIsType();

            UseMultiModelApi multiApi = new UseMultiModelApi(multiModel);
            UseSystemApiUndoable systemApi = new UseSystemApiUndoable(multiApi);

            MSystem system = systemApi.getSystem();

            systemApi.createObjects("model1@A", "a1");
            systemApi.createObjects("model1@B", "b1");
            systemApi.createObjects("model1@C", "c1");
            systemApi.createObjects("model1@Foo", "foo");
            systemApi.createLink("model1@assoc1","a1","foo");

            assertFalse(systemApi.checkState());

            systemApi.createLink("model1@assoc1","c1","foo");

            assertFalse(systemApi.checkState());

            systemApi.createLink("model1@assoc1","b1","foo");

            assertTrue(systemApi.checkState());



        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }


    //endregion

    //region inter-links

    public void testCreateMultiModelInterLinks() {
        try {
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelTwoModelsInterAssociation();

            UseMultiModelApi multiApi = new UseMultiModelApi(multiModel);
            UseSystemApiUndoable systemApi = new UseSystemApiUndoable(multiApi);

            MSystem system = systemApi.getSystem();

            systemApi.createObjects("PersonCompany1@Person", "p1");
            systemApi.createObjects("PersonCompany2@Company", "c1");
            systemApi.createLink("Job","p1","c1");

            assertEquals(1, system.state().allLinks().size());

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    //endregion

    //region inter-invariant

    public void testCreateMultiModelSimpleConstraints1() {
        try {
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelInterConstraintVerySimple();

            UseMultiModelApi multiApi = new UseMultiModelApi(multiModel);
            UseSystemApiUndoable systemApi = new UseSystemApiUndoable(multiApi);


            systemApi.createObjects("model1@Employee", "e1");
            systemApi.createObjects("model2@Student", "s1");

            systemApi.setAttributeValue("s1","grade","10");
            systemApi.setAttributeValue("e1","salary","101");

            assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("s1","grade","101");

            assertFalse(systemApi.checkState());

            systemApi.setAttributeValue("s1","grade","80");

            assertTrue(systemApi.checkState());

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testCreateMultiModelSimpleConstraints2() {
        try {
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelInterConstraintSimple();

            UseMultiModelApi multiApi = new UseMultiModelApi(multiModel);
            UseSystemApiUndoable systemApi = new UseSystemApiUndoable(multiApi);


            systemApi.createObjects("model1@Employee", "e1");
            systemApi.createObjects("model2@Student", "s1");
            systemApi.createObjects("model2@Student", "s2");
            systemApi.createObjects("model2@Student", "s3");

            systemApi.setAttributeValue("e1","salary","100");

            systemApi.setAttributeValue("s1","salary","10");
            systemApi.setAttributeValue("s2","salary","50");
            systemApi.setAttributeValue("s3","salary","101");

            systemApi.createLink("Job","s1","e1");

            assertFalse(systemApi.checkState());

            systemApi.createLink("Job","s2","e1");

            assertTrue(systemApi.checkState());

            systemApi.createLink("Job","s3","e1");

            assertFalse(systemApi.checkState());

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testCreateMultiModelInterConstraintSelfAssoc() {
        try {
            // creation of the system
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelInterConstraintSelfAssociation();

            UseMultiModelApi multiApi = new UseMultiModelApi(multiModel);
            UseSystemApiUndoable systemApi = new UseSystemApiUndoable(multiApi);


            systemApi.createObjects("model1@Employee","e1", "e2", "e3");
            systemApi.setAttributeValue("e1","salary","100");
            systemApi.setAttributeValue("e2","salary","50");
            systemApi.setAttributeValue("e3","salary","70");

            systemApi.createLink("model1@WorksFor", "e1", "e2");
            systemApi.createLink("model1@WorksFor", "e1", "e3");

            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("e1","salary","60");

            Assert.assertFalse(systemApi.checkState());


        } catch ( Exception e ) {
            throw ( new Error( e ) );
        }
    }

    public void testCreateMultiModelWithConstraintOclIsType() {
        try {
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelWithConstraintOclIsType();

            UseMultiModelApi multiApi = new UseMultiModelApi(multiModel);
            UseSystemApiUndoable systemApi = new UseSystemApiUndoable(multiApi);


            systemApi.createObject("model1@Goo", "goo");
            systemApi.createObject("model1@Foo", "foo");
            systemApi.createLink("model1@assoc2", "goo", "foo");

            Assert.assertFalse(systemApi.checkState());

            systemApi.createObject("model1@A", "a");
            systemApi.createLink("model1@assoc1", "a", "foo");
            Assert.assertFalse(systemApi.checkState());
            systemApi.createObject("model1@C", "c");
            systemApi.createLink("model1@assoc1", "c", "foo");
            Assert.assertFalse(systemApi.checkState());

            systemApi.createObject("model1@B", "b");
            systemApi.createLink("model1@assoc1", "b", "foo");
            Assert.assertTrue(systemApi.checkState());


            systemApi.createObjects("model2@Bar", "bar");
            Assert.assertFalse(systemApi.checkState());

            systemApi.createLink("interAssoc1", "a", "bar");
            Assert.assertFalse(systemApi.checkState());

            systemApi.createLink("interAssoc1", "c", "bar");
            Assert.assertFalse(systemApi.checkState());

            systemApi.createLink("interAssoc1", "b", "bar");
            Assert.assertTrue(systemApi.checkState());

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testCreateMultiModelWithConstraintOclIsKind() {
        try {
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelWithConstraintOclIsKind();

            UseMultiModelApi multiApi = new UseMultiModelApi(multiModel);
            UseSystemApiUndoable systemApi = new UseSystemApiUndoable(multiApi);


            systemApi.createObject("model1@Goo", "goo");
            systemApi.createObject("model1@Foo", "foo");
            systemApi.createLink("model1@assoc2", "goo", "foo");

            Assert.assertFalse(systemApi.checkState());

            systemApi.createObject("model1@A", "a");
            systemApi.createLink("model1@assoc1", "a", "foo");
            Assert.assertFalse(systemApi.checkState());
            systemApi.createObject("model1@C", "c");
            systemApi.createLink("model1@assoc1", "c", "foo");
            Assert.assertTrue(systemApi.checkState());

            systemApi.deleteLink("model1@assoc1", new String[]{"c", "foo"});
            Assert.assertFalse(systemApi.checkState());

            systemApi.createObject("model1@B", "b");
            systemApi.createLink("model1@assoc1", "b", "foo");
            Assert.assertTrue(systemApi.checkState());


            systemApi.createObjects("model2@Bar", "bar");
            Assert.assertFalse(systemApi.checkState());

            systemApi.createLink("interAssoc1", "a", "bar");
            Assert.assertFalse(systemApi.checkState());

            systemApi.createLink("interAssoc1", "c", "bar");
            Assert.assertTrue(systemApi.checkState());

            systemApi.deleteLink("interAssoc1", new String[]{"c", "bar"});
            Assert.assertFalse(systemApi.checkState());

            systemApi.createLink("interAssoc1", "b", "bar");
            Assert.assertTrue(systemApi.checkState());

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testCreateMultiModelWithConstraintSelectByType() {
        try {
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelWithConstraintSelectByType();

            UseMultiModelApi multiApi = new UseMultiModelApi(multiModel);
            UseSystemApiUndoable systemApi = new UseSystemApiUndoable(multiApi);


            systemApi.createObject("model1@Goo", "goo");
            systemApi.createObject("model1@Foo", "foo");
            systemApi.createLink("model1@assoc2", "goo", "foo");

            Assert.assertFalse(systemApi.checkState());

            systemApi.createObject("model1@A", "a");
            systemApi.createLink("model1@assoc1", "a", "foo");
            Assert.assertFalse(systemApi.checkState());
            systemApi.createObject("model1@C", "c");
            systemApi.createLink("model1@assoc1", "c", "foo");
            Assert.assertFalse(systemApi.checkState());

            systemApi.createObject("model1@B", "b");
            systemApi.createLink("model1@assoc1", "b", "foo");
            Assert.assertTrue(systemApi.checkState());


            systemApi.createObjects("model2@Bar", "bar");
            Assert.assertFalse(systemApi.checkState());

            systemApi.createLink("interAssoc1", "a", "bar");
            Assert.assertFalse(systemApi.checkState());

            systemApi.createLink("interAssoc1", "c", "bar");
            Assert.assertFalse(systemApi.checkState());

            systemApi.createLink("interAssoc1", "b", "bar");
            Assert.assertTrue(systemApi.checkState());

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testCreateMultiModelWithConstraintSelectByKind() {
        try {
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelWithConstraintSelectByKind();

            UseMultiModelApi multiApi = new UseMultiModelApi(multiModel);
            UseSystemApiUndoable systemApi = new UseSystemApiUndoable(multiApi);


            systemApi.createObject("model1@Goo", "goo");
            systemApi.createObject("model1@Foo", "foo");
            systemApi.createLink("model1@assoc2", "goo", "foo");

            Assert.assertFalse(systemApi.checkState());

            systemApi.createObject("model1@A", "a");
            systemApi.createLink("model1@assoc1", "a", "foo");
            Assert.assertFalse(systemApi.checkState());
            systemApi.createObject("model1@C", "c");
            systemApi.createLink("model1@assoc1", "c", "foo");
            Assert.assertTrue(systemApi.checkState());

            systemApi.deleteLink("model1@assoc1", new String[]{"c", "foo"});
            Assert.assertFalse(systemApi.checkState());

            systemApi.createObject("model1@B", "b");
            systemApi.createLink("model1@assoc1", "b", "foo");
            Assert.assertTrue(systemApi.checkState());


            systemApi.createObjects("model2@Bar", "bar");
            Assert.assertFalse(systemApi.checkState());

            systemApi.createLink("interAssoc1", "a", "bar");
            Assert.assertFalse(systemApi.checkState());

            systemApi.createLink("interAssoc1", "c", "bar");
            Assert.assertTrue(systemApi.checkState());

            systemApi.deleteLink("interAssoc1", new String[]{"c", "bar"});
            Assert.assertFalse(systemApi.checkState());

            systemApi.createLink("interAssoc1", "b", "bar");
            Assert.assertTrue(systemApi.checkState());

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testCreateMultiModelWithConstraintOclAsType() {
        try {
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelWithConstraintOclAsType();

            UseMultiModelApi multiApi = new UseMultiModelApi(multiModel);
            UseSystemApiUndoable systemApi = new UseSystemApiUndoable(multiApi);


            systemApi.createObject("model1@Student", "s1");
            systemApi.createObject("model1@School", "school");
            systemApi.createLink("model1@PartOfSchool", "school", "s1");

            systemApi.setAttributeValue("s1", "ID", "''");
            Assert.assertFalse(systemApi.checkState());

            systemApi.setAttributeValue("s1", "ID", "'123'");
            Assert.assertTrue(systemApi.checkState());


            systemApi.createObjects("model2@University", "university");
            systemApi.createLink("PartOfUni", "university", "s1");
            Assert.assertTrue(systemApi.checkState());

            systemApi.createObject("model1@Student", "s2");
            systemApi.setAttributeValue("s2", "ID", "''");
            systemApi.createLink("PartOfUni", "university", "s2");
            Assert.assertFalse(systemApi.checkState());

            systemApi.setAttributeValue("s2", "ID", "'456'");
            Assert.assertTrue(systemApi.checkState());

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testCreateMultiModelInterConstraintComplex2() {
        try {
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelInterConstraintComplex2();

            UseMultiModelApi multiApi = new UseMultiModelApi(multiModel);
            UseSystemApiUndoable systemApi = new UseSystemApiUndoable(multiApi);

            systemApi.createObjects("model1@Manager", "m1");
            systemApi.setAttributeValue("m1", "salary", "1000");
            systemApi.createObjects("model1@Manager", "m2");
            systemApi.setAttributeValue("m2", "salary", "1000");
            systemApi.createObjects("model2@Student", "s1");
            systemApi.setAttributeValue("s1", "salary", "500");
            systemApi.setAttributeValue("s1", "grade", "95");

            systemApi.createLink("supervising", "m1", "s1");

            systemApi.createObjects("model2@School", "school1");
            systemApi.setAttributeValue("school1", "name", "'BGU'");

            systemApi.createLink("model2@Studies", "s1", "school1");
            systemApi.createLink("study", "m1", "school1");

            systemApi.createObjects("model2@Meeting", "meet1");
            systemApi.createLink("model2@meets", "s1", "meet1");

            systemApi.createLink("empMeets", "m2", "meet1");


            systemApi.setAttributeValue("m2", "level", "'B'");

            assertFalse(systemApi.checkState());

            systemApi.setAttributeValue("m2", "level", "'A'");

            assertTrue(systemApi.checkState());

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }


    //endregion


    //region inter-class

    public void testCreateMultiModelWithInterClass() {
        try {
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelWithInterClass();

            UseMultiModelApi multiApi = new UseMultiModelApi(multiModel);
            UseSystemApiUndoable systemApi = new UseSystemApiUndoable(multiApi);

            systemApi.createObjects("A", "x1");
            systemApi.createObjects("B", "y1");
            systemApi.createObjects("model1@A", "x2");
            systemApi.createObjects("model2@B", "y2");

            systemApi.createLink("A1","x1","x2");
            systemApi.createLink("A2","x1","y1");
            systemApi.createLink("A3","x1","x1");

            assertFalse(systemApi.checkState());

            systemApi.setAttributeValue("x2","number","6");
            systemApi.setAttributeValue("y1","salary","60");

            assertTrue(systemApi.checkState());

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testCreateMultiModelWithInterAssociationClass() {
        try {
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelWithInterAssociationClass();

            UseMultiModelApi multiApi = new UseMultiModelApi(multiModel);
            UseSystemApiUndoable systemApi = new UseSystemApiUndoable(multiApi);

            systemApi.createObject("model1@A","a1");
            systemApi.createObject("model2@B","b1");
            systemApi.createObject("model2@C","c1");
            systemApi.createObject("A","a2");
            systemApi.createObject("B","b2");


            systemApi.createLinkObject("Ass1","ass1","a1","b1");
            systemApi.createLinkObject("Ass2","ass2","a2","b1");
            systemApi.createLinkObject("Ass3","ass3","a2","b2");
            systemApi.createLinkObject("Ass4","ass4","b1","c1");


        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    //endregion
}
