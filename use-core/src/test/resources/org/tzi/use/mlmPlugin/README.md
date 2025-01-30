# Plugin Credibility Tests

## Correct Plugin Behavior

Test cases where the plugin performed as expected.

| Feature             | Test Case                                                        | Plugin Result        | Test Link               |
|---------------------|------------------------------------------------------------------|----------------------|---------------------------|
| **Attribute Removing**| Removed attribute related to local constraint                   | CORRECT              | [attribute_removing](attribute_removing) |
| **Empty Clabjects**   | Empty clabjects with 1-1 multiplicity constraint                 | CORRECT, changing max link values of classes affects the result | [empty_clabjects](empty_clabjects) |

## Plugin Failures

Test cases where the plugin did not perform correctly.

| Feature             | Test Case                                                        | Plugin Result        | Test Link               |
|---------------------|------------------------------------------------------------------|----------------------|---------------------------|
| **Assoclink**         | Assoclink with 1-1 multiplicity constraint                       | WRONG, it creates an object diagram that violates the multiplicities. | [assoclink](assoclink) |
| **Role Removing**     | Role removal with 1-1 multiplicity constraint                    | WRONG, UNSATISFIABLE although it is SATISFIABLE | [role_removing](role_removing) |
| **Attribute Renaming**| Attribute renaming related to inter-constraint                  | WRONG, return SATISFIABLE but doesn't take into account the inter-constraint | [attribute_renaming](attribute_renaming) |


---

