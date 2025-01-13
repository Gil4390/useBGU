The following examples will test the credibility of the plugin with role removing feature.

Example 1: (SATISFIABLE) True Positive
Both 'A' and 'B' max object property number is 0, the model is SATISFIABLE because the 1-1 multiplicity of the upper level doesn't exists in this diagram.

Example 2: (UNSATISFIABLE) False Negative
Class 'B' max object property number is 0.
The plugin returns UNSATISFIABLE, but the model is SATISFIABLE: creating 'A', 'C' and 'D' objects and connecting them.

