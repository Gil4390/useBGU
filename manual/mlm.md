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

#### Display Class Attributes

Displays all attributes of the specified class, including both inherited and renamed attributes.

#### Syntax:

:   `info mlm class attributes ${LEVEL_NAME@CLASS_NAME}`

Example:

:   Get attributes for class C in level L2.

    User input:

    :   `info mlm class attributes L2@C`

    Result:

    :   The attributes shown in the Shell.

            class L2@C
            declared attributes
              attr1 : Integer
            end
            derived attributes
              attr2 : String
            end

#### Display Class Roles

Displays all navigable ends of the specified class.

#### Syntax:

:   `info mlm class roles ${LEVEL_NAME@CLASS_NAME}`

Example:

:   Get roles for class C in level L2.

    User input:

    :   `info mlm class roles L2@C`

    Result:

    :   The roles shown in the Shell.

            class C
            roles
              r1 : L2@D
            end
            all roles
              r2 : L1@B
            end

#### Display Class Mediators

Displays the clabjects and assoclinks associated with the specified class.

#### Syntax:

:   `info mlm class mediators ${LEVEL_NAME@CLASS_NAME}`

Example:

:   Get mediators for a specific class.

    User input:

    :   `info mlm class mediators L2@C`

    Result:

    :   The mediators associated with the specified class will be shown in the Shell.