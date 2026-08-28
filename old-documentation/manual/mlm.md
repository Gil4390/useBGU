#### Print MLM File

Prints the contents of the mlm.use file.

#### Syntax:

:   `info mlm`

Example:

:   Get the contents of the MLM file.

    User input:

    :   `info mlm`

    Result:

    :   The contents of mlm.use file will be displayed in the Shell.

#### Display MLM Level Hierarchy

Returns a hierarchical list of level names from top to bottom.

#### Syntax:

:   `info mlm levels`

Example:

:   Display the level hierarchy.

    User input:

    :   `info mlm levels`

    Result:

    :   The level hierarchy shown in the Shell.

            NONE
              /\
            Level1
              /\
            Level2

#### Display Level Overview

Displays an overview of the specified level. Can be filtered using flags or shows all information if no flags are provided.

#### Syntax:

:   `info mlm level ${LEVEL_NAME} [-classes] [-associations] [-clabjects] [-powerTypes]`

Example:

:   Get complete information about a specific level.

    User input:

    :   `info mlm level Level1`

    Result:

    :   The complete level information will be shown in the Shell.

#### Display Class Overview

Displays an overview of the specified class. Can be filtered using flags or shows all information if no flags are provided.

#### Syntax:

:   `info mlm class ${LEVEL_NAME@CLASS_NAME} [-attributes] [-roles] [-mediator]`

Examples:

:   Get attributes for class C in level L2.

    User input:

    :   `info mlm class L2@C -attributes`

    Result:

    :   The attributes shown in the Shell.

            class L2@C
            declared attributes
              attr1 : Integer
            end
            all attributes
              attr1 : Integer
              attr2 : String
            end


:   Get roles for class C in level L2.

    User input:

    :   `info mlm class L2@C -roles`

    Result:

    :   The roles shown in the Shell.

            class C
            roles
              r1 : L2@D
            end
            all roles
              r2 : L1@B
            end

:   Get mediator for a specific class.

    User input:

    :   `info mlm class L2@C -mediator`

    Result:

    :   The mediator associated with the specified class will be shown in the Shell.


#### Display Class Inheritance Overview

Displays an overview of the inherited roles or attributes of a class from its powerType class. Can be filtered using flags or shows all information if no flags are provided.

#### Syntax:

:   `info mlm class ${LEVEL_NAME@CLASS_NAME} from ${LEVEL_NAME@CLASS_NAME} [-attributes] [-roles]`


Examples:

:   Get inherited attributes of class C in level L2 from class A in level L1.

    User input:

    :   `info mlm class L2@C from L1@A -attributes`

    Result:

    :   The attributes shown in the Shell.

            derived attributes
              attr1 : Integer
            end

:   Get inherited roles of class C in level L2 from class A in level L1.

    User input:

    :   `info mlm class L2@C -roles`

    Result:

    :   The roles shown in the Shell.

            derived roles
              r1 : L1@D
            end


#### Check Well Definedness

Checks the loaded MLM for well definedness.

#### Syntax:

:   `wd`

Example:

:   Checks the loaded MLM for well definedness

    User input:

    :   `wd`

    Result:

    :   Checking well definedness..
        checking structure...
        checked structure in 0ms.
        checking invariants...
        checked 0 invariants in 0.000s, 0 failures.
        checking structure...
        Multiplicity constraint violation in association `AB@ab1':
        Object `CD@C' of class `AB@A' is connected to 3 objects of class `AB@B'
        at association end `bb1' but the multiplicity is specified as `2'.
        checked structure in 0ms.
        checking invariants...
        checked 0 invariants in 0.000s, 0 failures.
        Result: NotWellDefined