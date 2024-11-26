The following example, asserts that inherited roles and attributes
can be accessed through inter-constraints.

Clabject 'C' of class 'A' inherits by default role 'bb1 : A' through the association 'ab1'.

Therefore, we can use 'bb1' in inter-constraint 'i1'.

We can do a sanity check that the breaking point of the constraint does work, by following the commands below:

makes 'i1' <b>UNSATISFIABLE</b>:
```
  !create c:CD@C
  !create b:AB@B
  !insert (c, b) into AB@ab1
  !set b.b1 := 'x'
```
makes 'i1' <b>SATISFIABLE</b>:
```
  !set b.b1 := 'b'
```
<img src="clabject_with_inter_constraints.png" alt="">


