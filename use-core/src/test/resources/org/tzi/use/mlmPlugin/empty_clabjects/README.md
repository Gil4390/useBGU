The following examples will test the credibility of the plugin with default inheritance.

Example 1: (UNSATISFIABLE) True Negative
Max number of links of 'AB@ab1' limited to 1, the model is UNSATISFIABLE because we cant connect object 'd1'/'c1' to their inherited roles.

Example 2: (SATISFIABLE) False Positive
Max number of links of 'AB@ab1' limited to 2, this example is FP because the plugin allowed connecting a 1-1 multiplicity with 2 objects, (d1 connects to a1 & c1)

