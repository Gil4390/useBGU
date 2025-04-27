# MLM-USE 

MLM-USE is a modeling application for models of MedMLM -- a multilevel modeling approach.

The MLM-USE application is open, in the github repository.

This user manual can be accessed in:

[https://github.com/useocl/use/blob/20f1ab4e3229edcf59e19c5717a73454dba9f243/manual/main.md#L4](https://github.com/useocl/use/blob/20f1ab4e3229edcf59e19c5717a73454dba9f243/manual/main.md#L4)

## Content:

1. Specification of a Multi-Level Model in MLM-USE
2. Running MLM-USE
   - 2.1. MLM-USE Graphical User Interface
   - 2.2. Visual presentation of a MedMLM model in MLM-USE
3. Semantics of MLM-USE Models
   - 3.1. defining object diagrams
   - 3.2. Inheritance of features over instance-of relations
4. Metadata Queries, Model Satisfiability and Well-definedness
   - 4.1. Metadata queries
   - 4.2. Model satisfiability
   - 4.3. Model Well-Definedness
      1. Checking well Definedness
      2. Model mediators elements
      3. Object View of a level class model
- Appendix A: Conflicts and Warnings
- Appendix B: Formal Definition of MedMLM Model Specification

**Running MedMLM model for this manual:**

![MLM Model Example](https://github.com/user-attachments/assets/92c0b416-3a95-4486-a924-ac093f2ffac5)

.use file of the running example:
[https://github.com/Gil4390/useBGU/blob/MLM-USE/use-core/src/test/resources/org/tzi/use/mlmExamples/skeleton.use](https://github.com/Gil4390/useBGU/blob/MLM-USE/use-core/src/test/resources/org/tzi/use/mlmExamples/skeleton.use)

\~ represents the removal of attributes, roles, or constraints within clabjects. Cancellation /renaming refers to a specific clabject-powerClass instance-of relation.

-\> represents the renaming of attributes within clabjects (only attributes can be renamed)

inter associations are bolded

## 1. Specification of a MedMLM Model

A MedMLM model for MLM-USE is specified as a text file which is loaded into MLM-USE. Formal definition of the text specification appears in Appendix B.

An MLM-USE Specification of a model consists of ordered 3-parts:

1. Models definition
2. Inter-level information (associations and constraints)
3. Mediators

Below is the input specification of the example above:

1. 2 models AB and CD;
2. Model AB has 5 classes A, B, A1, B1,B2 and associations *aa1, bb1, ab* between them. Model CD includes classes C, D, E, and association *cd* between them;
3. An interlevel association between classes C and A1;
4. 2 mediators: The mediator of AB is empty, and the mediator of CD describes the clabjects C, D, and assoclink *cd*.

### 1. Models Definition

```
MLM ABCD

model AB

   class A
   attributes
     attr1: Integer 
     attr2: Real
   end
   
   class A1
   attributes
     attr1: String
   end
   
   class B
   attributes
     attr1: Integer
     attr2: String
   end
   
   class B1
   attributes
     attr1: Integer
   end
   
   class B2 < B
   end
   
   association aa1 between
     A[1] role a1
     A1[1] role r1
   end
   
   association bb1 between
     B[1] role b1
     B1[1] role r1
   end
   
   association ab between
     A[*] role a
     B[1] role b
   end
   
   constraints
   context A inv ABMLM:
     self.b.attr2 = 'MLM'
   
   context A1 inv A1B1MLM:
     self.a1.b.r1.attr1 > 5

model CD

   class C
   end
   
   class D
   end
   
   class E < D
   end
   
   association cd between
     C[*] role c
     D[*] role d
   end
```

### 2. Inter-level information

```
inter-associations
association ac between
  CD@C[1..*] role ic
  AB@A1[1..*] role ia
end

inter-constraints
context CD@C inv CB1MLM:
  self.ia->forAll( it | it.attr1 = 'MLM')
```

### 3. Mediators

```
mediator AB < NONE
end

mediator CD < AB
  clabject C : A
     attributes
       ~attr1
       attr2 -> attrC
     roles
       ~r1
     constraints
       ~ABMLM
  end

  clabject C : B
  end

  clabject E : B2
     attributes
       ~attr2
     roles
       ~r1
  end

  clabject D : B
     attributes
       attr1 -> attrD
  end

  assoclink cd : ab
    a -> c
    b -> d
  end
end
```

The notation 'CD < AB' defines the level ordering in the MLM. 'CD' is the lower level.

'AB < NONE' defines the base upper level.

#### **Defining Clabjects:**

C is a clabject with a powerclass of A in the mediator of CD:

```
clabject C : A
end
```

#### a clabject of multiple powerclasses:

```
mediator CD < AB
  clabject C : A
  end
  
  clabject C : B
  end
end
```

#### Default inheritance via instance-of (clabject) relation

By default, all attributes, roles and constraints of a powerclass are inherited by its clabjects, unless otherwise declared, as explained below.

#### Attribute Renaming

Attributes of a powerclass can be renamed in its clabjects.

attribute *attr1* of powerclass B is renamed in clabject D into *attrD*.

(the type of an attribute cannot be changed)

```
attributes
   attr1 -> attrD
```

Note that in clabject C of A, attribute *attr2* is renamed into *attrC* (with type Real). *attr2*, with type string, is still inherited through the instance-of of clabject C of powerClass B.

#### Attribute Removal

Attribute removal is marked using '~'. ~*attr1* marks that clabject C does not inherit *attr1* of powerClass A. However, clabject C of powerClass B, still inherits *attr1*. Objects of C have *attr1*. Removed attributes are not enabled in object diagrams.

```
~attr1
```

#### Role Removal

Role removal is marked using '~'. ~*r1* marks that clabject C does not inherit '*r1*' of powerclass A. Objects of C cannot be linked by role '*r1*' to objects of A1. Note that C inherits role *r1* of its powerClass B. Objects of C can be linked by role *r1* to objects of B1.

Role removal cancels the inheritance of the association of the removed role.

```
roles
   ~r1
```

#### Constraint Removal

Constraint removal is marked using '~'. ~ABMLM marks that this constraint is not inherited: Not applied to objects of clabject C. Constraints A1B1MLM and CA1MLM apply.

```
constraints
   ~ABMLM
```

#### **Defining Assoclinks**

Assoclinks define associations between 2 clabjects as instances of associations between their power classes. Role correspondence (renaming) is marked using '->'.

The instance association ('cd' in the example) has only its declared roles. The roles of the upper association are not inherited! That is: **Assoclink declaration is equal to removal of the roles of the upper association**.

```
assoclink cd : ab
  a -> c
  b -> d
end
```

#### **Summary of clabject inheritance in the example model:**

Clabject C has attributes at*tr1, attr2, attrC*; roles *r1* to B1, *ia* to A1, and *d* to D; constraint CA1MLM.

Clabject D has attributes *attr2*, *attrD*; roles *r1* to B1, and *c* to C*.

Clabject E has attributes *att1, attr2, attrD*; roles *r1* to B1, *c* to C, and *a* to A.

MLM-USE does not enable inheritance conflicts: inheritance of multiple attributes or roles with the same name through multiple subclass or instance-of relations. Such cases are rejected as model conflicts. Model loading is stopped.

Attribute renaming can cause duplication of attributes with the original and the renamed names. Examples are the pairs *attr2* -- *attrC* of C or *attr1* -- *attrD* of E. Such cases result in warnings.

Appendix A at the end of the manual lists the errors and warnings in model specification.

## 2. Running MLM-USE

MLM-USE is an extension of USE for MedMLM models. The interface and all model management operations are those of USE.

Application of MLM-USE opens (as in USE) a command line and a graphical interfaces.

A MedMLM file can be loaded into MLM-USE either through the command line interface or via the graphical interface (the default) that USE opens.

Queries: The command line interface supports additional queries that are not enabled in the graphical interface. Section 4 describes the queries that MLM-USE supports.

### 2.1. Graphical User Interface

![MLM-USE GUI](https://github.com/user-attachments/assets/2aa85067-d636-4335-b29b-7d1ad983203e)

### 2.2. Visual presentation of a MedMLM model in MLM-USE

An MLM-USE model is implemented in USE as a "flat" traditional class model. The graphical representation visualizes the USE implementation as a flat class model.

- Class names are extended with the level name as a prefix: MODEL_NAME@CLASS_NAME
- instance-of relations between a clabject and a powerclass are visualized with a *dashed arrow*. This is different from the subclass (inheritance) relation, which is visualized with a solid arrow. Assoclinks are also marked with dashed arrows to their power associations.
- Inter-associations are visualized with a *bold association line*
- Classes in different levels have *different level colors*

For example the model described in this manual is visualized in MLM-USE as follows:

![MLM-USE Visualization](https://github.com/user-attachments/assets/4136948e-406c-471e-87e4-32e6e7958ebf)


#### MLM-USE does not distinguish the MLM levels. Users must manually organize level visualization. That is, after loading a .use file all classes will be mixed up. However, the user can organize the levels according to the colors of the classes, by dragging and dropping class boxes.

In the example above, classes of level 'AB' are pink and classes of level 'CD' are gray. They were manually placed.

## 3. Semantics of MLM-USE Models

The semantics of an MLM-USE model is given by its legal object diagrams (object models).

The default semantics of the instance-of relations between clabjects and their powerclasses implies the inheritance of all clabject features unless otherwise specified in clabjects or by assoclinks. Inheritance and overriding semantics are defined below, using object diagrams of the introductory MedMLM example above.

### 3.1. Defining object diagrams

An object diagram of an MLM-USE model consists of object diagrams of all internal models. The application treats it as a single object diagram of the flat internal model.

The standard method for defining object diagrams is via the graphic interface.

Objects and links are defined through the toolbar of the GUI by navigating:

State -> Create object...

A small window will appear:


![Create Object Window](https://github.com/user-attachments/assets/92dd0b2d-c319-4b3a-bb16-ff6718295375)


After selecting a class and specifying its name, a new object will appear.

![New Object](https://github.com/user-attachments/assets/248cf3a3-6b27-4c86-8144-2d4ce60f7bc1)


Links are also added via the toolbar. Possible roles are presented:

Select both objects while holding SHIFT, then Right-Click on one of the objects. A window will appear where all the options of link insertion are shown.

![Link Insertion Options](https://github.com/user-attachments/assets/849ccd3d-78af-4e9b-b10e-280ca5905a64)


Objects that belong to classes on different levels are visualized with their level colors.

![Objects with Level Colors](https://github.com/user-attachments/assets/35b6571c-8c5f-4cc1-a51d-2efbadb78ae9)


### 3.2. Checking legality of an object diagram

An object diagram is checked for being legal, i.e., satisfy all model requirements and associated constraints, through the USE command 'check', in the command line interface.

For example, for the object diagram below, 'check' yields a contradiction answer:

![Object Diagram Example](https://github.com/user-attachments/assets/65e8487c-af31-4281-b856-1a9615700721)

![Contradiction Answer](https://github.com/user-attachments/assets/e20a94ed-3b28-47a1-bc99-5a84deb770b1)

The multiplicity restriction on role r1 of class B is violated. The violation can be repaired by adding a new object of type 'AB@B1' and linking it to object b.

A new legality check:

![New Legality Check](https://github.com/user-attachments/assets/a7e75c4f-0f2b-450e-bb52-fb2bdf74bc27)

The response notifies on satisfaction or violations of the invariants (constraints).

### Multiplicity constraints on associations' roles:

Multiplicity on an association role (also termed association-end) is represented as a group of ranges. For example:

![Multiplicity Example](https://github.com/user-attachments/assets/6206d523-ef31-48d1-a345-cb402f8cf365)

multiplicity of [1..3, 5..7] means there are 2 ranges, one from 1 to 3 and another from 5 to 7.

- For an association end to be well-defined the number of objects that relate to it must be in one of the ranges of the multiplicity.

- if an object 'a1' is connected to 3 objects of type B the result is well-defined.

- If the number of objects is not in one of the ranges but is lower than the maximum lower bound of one of the ranges then the association end is partially-well-defined.

- if an object 'a1' is connected to 4 objects of type B the result is partially-well-defined.

- If the number of objects is larger than the maximum upper bound of one of the ranges then the association end is not-well-defined.

- if an object 'a1' is connected to 8 objects of type B the result is not-well-defined.

### 3.3. Inheritance of features over instance-of relations

Inheritance and overriding semantics:

1. Default inheritance: All attributes, roles and constraints of a powerclass are inherited by all of its clabjects.
2. Attribute inheritance can be banned: marked "~" or renamed: marked with "->" .
3. Role inheritance can be banned: marked "~". It means banning links of the association of the banned role between objects of the clabject and the related class.
4. Constraint inheritance can be banned: marked "~".
5. Assoclinks override inheritance of the roles of the upper association = banned role inheritance.

#### Semantics is demonstrated with object diagrams of the running example:

1. The following object diagram includes objects *objC* and *objD* of classes C and D in model CD.

![Object Diagram C and D](https://github.com/user-attachments/assets/3ed8a0aa-856d-46da-8527-32ab0c7f4412)

Objects *objC* and *objD* are linked by roles *c* and *d*. These are the only roles between classes C and D, since roles *a,b*, are banned by the assoclink.

2. An object *objB1* of B1 is added. objC can be linked to objB1 by the inherited role *r1*.

![Object with objB1](https://github.com/user-attachments/assets/f5e504ef-5a90-4140-b411-14c635562b99)

3. Addition of *objA* of class A:

![Object with objA](https://github.com/user-attachments/assets/0bce6e1a-f9f9-42b6-af71-8af3396501c1)

An attempt to link *objD* with *objA* through role '*a'* fails, since inheritance of '*a'* is banned by the assoclink.

4. Addition of *objA1* of class A1, with links to *objA* through association aa1 and *objC* through association ac.

![Object with objA1](https://github.com/user-attachments/assets/16af5529-78c7-4a6b-b23d-2838d8b3774d)

The constraints on classes A and A1 vacuously hold, since there is no link through role *b*. The inter-constraint on C holds.

5. Addition of *objE* of class E with values for all inherited attributes -- *attr1, attr2, attD*, and links for all inherited roles -- *a, c, r1*:

![Object with objE](https://github.com/user-attachments/assets/fb47a065-b96e-4abf-8728-6b42b0a82703)

Note that now the constraints on classes A and A1 apply and are satisfied.

However, multiplicity constraints are not satisfied:

ObjD has 0 r1 links, and objB1 has 2 b1 links.

Indeed, running 'check', yields the following output:

![Check Output](https://github.com/user-attachments/assets/cbe9bd83-3be0-4384-b055-a0c22549e4fd)

### Attribute Conflicts

Attribute inheritance from superClasses or powerClasses can yield attribute naming conflicts: Attribute renaming or removal in mediators can resolve naming conflicts.

![Attribute Conflicts](https://github.com/user-attachments/assets/a06f62f4-3284-44f2-a513-616d526e0dcc)

Appendix A lists warning and conflicts that arise from inheritance.

## 4. Metadata Queries, Model Satisfiability and Well-definedness

### 4.1. Metadata queries

Metadata queries are entered in the command line interface, which is opened when the application is launched:

![Command Line Interface](https://github.com/user-attachments/assets/d04166dd-2bcf-4161-832a-3f06b78b8cea)

MLM-USE commands and queries are explained in the following link:

[**https://github.com/Gil4390/useBGU/blob/MLM-USE-Release/manual/mlm.md**](https://github.com/Gil4390/useBGU/blob/MLM-USE-Release/manual/mlm.md)

### 4.2. Model satisfiability

An MLM-USE model is satisfiable if it has a legal object diagram. That is, its requirements and constraints are not totally contradictory: There exists at least one object diagram that satisfies them.In USE, model satisfiability is checked using **the plugin**, which applies an external solver.

MLM-USE uses the the USE plugin for checking the satisfiability of its models.

In the current version of MLM-USE model satisfiability correctly applies only to a restricted set of features:

1. MedMLM models with empty mediators.
2. MedMLM models with mediators that enable attribute cancellation.
3. MedMLM models that **do not include**:
   - role or constraint cancellation;
   - attribute renaming;
   - assoclinks;

In all other versions of MedMLM models, the result of the satisfiability check night be is not reliable.

**Usage**

Load a .use MedMLM file, and navigate to the following tab:

![Satisfiability Tab](https://github.com/user-attachments/assets/7504cb9b-4054-432d-8e2d-6018253d5b93)

The following window will pop up:

![Satisfiability Window](https://github.com/user-attachments/assets/1ff84b45-74b7-4a01-bc58-0cee1f63ca9e)


Here, you can set the upper and lower boundaries on the number of objects of classes and links of associations. After clicking on 'Validate', the result of the satisfiability check will appear as a text in the bottom of the main USE-MLM window, as shown below.

![Satisfiability Result](https://github.com/user-attachments/assets/66c78aa2-cbf5-4890-8285-b282ab3951d1)

### 4.3. Model well-definedness

A MedMLM model is well-defined if:

1. The mediators define a total ordering of the component class models:
   - the component models are called *levels*;
   - the bottom level is the model that have no pre-descessor in the ordering, e.g., model CD is the running example of the manual;
   - the top level is the model that have no successor in the total ordering, e.g., model AB in the running example;

2. The *object view* of each level is a *partial instance* of the class model in the next higher level.

Well-definedness is a syntactic restriction on MedMLM models that guarantee that a MedMLM model is not an arbitrary collection of models, but is a layered organization of models that reflect the instance-of relation. The dependencies between levels reflect the essential interdependence between the levels.

In MLM-USE, well-definedness of a MedMLM model is done via the graphic interface:

#### Checking Well-Definedness

![Well-Definedness Check](https://github.com/user-attachments/assets/39bb959e-451a-4b28-98bd-c5a8a2bd2e31)

The result appears in the log box in the bottom of the window:

![Well-Definedness Result](https://github.com/user-attachments/assets/fb0805f6-274b-48d5-9b75-33a2b56af792)

***Well-definedness algorithm*** in MLM-USE:

1. Layered ordering of the component models: Already checked when a model is loaded;

2. For every level: *Create an object-view* with respect to the next higher level. The object view considers all *instance-of* relations of clabjects and all *instance-of* relations of assoclinks. Clabject *instance-of* yield objects of their powerClasses. Assoclinks yield links of the upper associations.

The result is an object diagram of the class model in the next higher level.

3. Check the *partial-instance requirement* for the object-view of every level.

If the component models are layered and all levels pass the partial-instance check, the MedMLM model is well-defined.

**Partial-instance algorithm:**

input: A class model ***M*** and an object-diagram ***o***

output: {*legal, partial, illegal*}

1. For every association in ***M***:
   a. every role multiplicity is defined. If not, complement to default: [0..*];
   b. For the two role multiplicities:
      - Maximum bounds check: Number of links in ***o*** obeys the 2 maximum bounds;
      - Minimum bounds check: If the number of links in ***o*** is less-than a minimum bound, mark it;

2. Constraint check: Check that all constraints of ***M*** hold in ***o***;

If all tests are satisfied → *legal*;

If a maximum bound or a constraint is violated → *illegal*;

If some minimum bounds are violated, and all maximum bounds and constraints are satisfied → *partial*;

**Note:** Partial-instance check extends the range of instance checking. The partial value marks that the object diagram is illegal, but chances are that it can be made legal by adding missing objects.

Here it is presented in the context of the well-definedness check. But it can be added to USE, Multi-USE, MLM-USE as a general extension to checking legality of object diagrams.

Examples of checking well-definedness:

![Well-Definedness Example 1](https://github.com/user-attachments/assets/c178e815-92c8-4aeb-9631-1b8f37a0c4a2)

![Well-Definedness Example 2](https://github.com/user-attachments/assets/10ff82d4-2a37-4fe5-a7b1-cccd97c8b424)

![Well-Definedness Example 3](https://github.com/user-attachments/assets/a219b1c5-bc98-4c9f-9f71-d63419209834)

![Well-Definedness Example 4](https://github.com/user-attachments/assets/4bbab6d1-dff9-42ae-b349-cb0e7ca8558d)


## Appendix A: Conflicts and Warnings in Model loading

The following section showcases the possible warnings that can appear while loading an MLM file, and the conditions that led to that warning.

### Warnings

**1. Attribute Renamed and Base Attribute is Inherited**

**Message:**
*Attribute <attribute_name>*
*is renamed to <new_attribute_name>,*
*and the base attribute <attribute_name> is also inherited.*

**Explanation:**

1. An attribute is renamed in a clabject.
2. The base attribute is inherited from a superclass or from a power-class.

**2. Missing Role Accessed by an Invariant**

**Message:**
*Role <role_name>*
*that is accessed by a invariant <invariant-name>*
*on clabject <clabject_name> is missing*

**Explanation:**

1. The missing role might have been removed from a clabject and not inherited from elsewhere.
2. The missing role might haven't been declared.
3. **Suggestion:** If the invariant is inherited from a powerClass of the clabject, It is suggested to consider cancelling its inheritance.

### Conflicts

**1. Missing Attribute Accessed by an Invariant**

**Message:**
*Attribute <attribute_name>*
*that is accessed by invariant <invariant_name>*
*on <clabject_name> is missing*

**Explanation:**

1. The missing attribute might have been removed from a clabject and not inherited from elsewhere.
2. The missing attribute might haven't been declared.
3. **Suggestion:** If the invariant is inherited from a powerclass of the clabject, It is suggested to consider cancelling its inheritance.

**2. Attribute Renamed Causes a Missing Attribute Accessed by an Invariant**

**Message:**
*Attribute <attribute_name>*
*is renamed in clabject <clabject_name>*
*but is accessed by invariant <invariant_name>*

**Explanation:**

1. An attribute is renamed in a clabject.
2. The attribute is accessed by an invariant.
3. **Suggestion:** If the invariant is inherited from a powerclass of the clabject, It is suggested to consider cancelling its inheritance.

**3. Attribute Duplication**

**Message:**
*Attribute <attribute_name>*
*is present in both parent: <parent_class>*
*and child: <child_class>*

**Explanation:**

1. Multiple inheritance can arise from powerclasses or superclasses.
2. **Suggestion:** Check possibility of cancelling or renaming from a powerclass.

## Appendix B: Formal Definition of MedMLM model specification

[explanation on how to define an MLM, similarly to how they explained it in use: https://github.com/useocl/use/blob/20f1ab4e3229edcf59e19c5717a73454dba9f243/manual/main.md#specifying-a-uml-model-with-use](https://github.com/useocl/use/blob/20f1ab4e3229edcf59e19c5717a73454dba9f243/manual/main.md#specifying-a-uml-model-with-use)

### Defining an MLM

Every MLM has a name and a body.

The body of an MLM consists of one or more regular Models and an optional amount of mediators.

### **Multi-Level Model** 

### A multi-level model is the root element in MLM definitions. It encapsulates the core structure and connects mediators and models. Every MLM has a unique name and a multiple core model definition.

#### **Syntax**

```
<multi_level_model> ::= MLM <mlm_name>
<multi_model_core>
{<mediator_definition>}*
EOF
```

#### **Example**

The MLM's name is DeviceManagement.

```
MLM DeviceManagement

model Computer_product
   ...
model PC
   ...

mediator Computer_product < NONE
end

mediator PC < Computer_product
end

```

### **Mediators**

Mediators act as connectors between core models. Each mediator references two names of the models to connect between them, and may include clabjects and assoclinks.

#### **Syntax**

```
<mediator_definition> ::= mediator <level> < <upper_connected_level>
{<clabject_definition> | <assoclink_definition>}*
end
```

#### **Example**

```
mediator PC < Computer_product 
   clabject Device : Hardware
   end
   assoclink pcCon : contain
   end
end
```

### **Clabjects** 

Clabjects represent elements that can change the base elements in a core model like attributes, roles, and constraints.

#### **Syntax**

```
<clabject_definition> ::= clabject <class_name> : <upper_model_class_name>
[attributes {<attribute_rename> | <attribute_cancellation>}]
[roles {<role_cancellation >}]
[constraints {<constraints_cancellation>}]
```

#### **Example**

```
clabject Device : Hardware
   attributes
   ...
   roles
   ...
   constraints
   ...
end
```

### **Attributes Renaming** 

### Attributes in clabjects can be renamed.

#### **Syntax**

```
<attribute_rename> ::=
{ <old_attribute_name> -> <new_attribute_name> }*
```

#### **Example**

```
attributes
   oldHardwareAttributeName1 -> newDeviceAttributeName1
   oldHardwareAttributeName2 -> newDeviceAttributeName2
```

### **Attributes Cancellation** 

### Attributes in clabjects can be canceled to remove inherited attributes.

#### **Syntax**

```
<attribute_cancellation> ::= { ~<attribute_name> }*
```

#### **Example**

```
attributes
   ~unnecessaryHardwareAttribute1
   ~unnecessaryHardwareAttribute2
```

### **Constraints Cancellation** 

Constraints in clabjects can be canceled to remove inherited constraints.

#### **Syntax**

```
<constraints_cancellation> ::= { ~<constraint_name> }*
```

#### **Example**

```
constraints
   ~unnecessaryHardwareConstraint1
   ~unnecessaryHardwareConstraint2
```

### **Role Cancellation** 

Roles in associations can be canceled to remove inherited roles.

#### **Syntax**

```
<roles_cancellation> ::= { ~<role_name> }*
```

#### **Example**

```
roles
   ~unnecessaryHardwareRole1
   ~unnecessaryHardwareRole2
```

### **Assoclinks** 

Assoclinks represent relationships between associations in multiple models. They define bindings between roles defined in upper-level associations.

#### **Syntax**

```
<assoclink_definition> ::= assoclink <association_name> : <upper_model_association_name>
<role1> -> <role1_upper_model>
<role2> -> <role2_upper_model>
```

#### **Example**

```
assoclink pcCon : contain
   parentDevice -> parentHardware
   partDevice -> partHardware
end
```
