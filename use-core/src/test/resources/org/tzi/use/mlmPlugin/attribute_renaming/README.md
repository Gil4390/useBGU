The following examples will test the credibility of the plugin with attribute renaming feature.

Example 1: (SATISFIABLE) True Positive
the plugin returns SATISFIABLE and the inter-constraint is valid.

Example 2: (SATISFIABLE) False Positive
In this example we need to uncomment 'Example 2' and clabject 'C' attribute renaming, as specified in the '.use' file. (also put 'Example 1' constraint into a comment)
The plugin returns SATISFIABLE, but also stating that the constraint have FAILED.
The reason for that is the plugin doesnt consider the attribute renaming -- so the constraint is invalid. 

