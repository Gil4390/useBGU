when defining clabjects we can remove or rename some of the attributes that would be inherited

in this example we can see that class C does not inherit all the attributes from class A,
but instead, the attribute `a1` is renamed to `c1`, and the attribute `a2` is removed.

<img src="clabject_attribute_removal_renaming.jpg" alt="">


    MLM ABCD

    model AB
        class A
            attributes
                a1: String
                a2: String
        end
        
        class B
            attributes
                b1: String
                b2: String
        end
    
        association ab1 between
            A[*] role aa1
            B[*] role bb1
        end
    
    
    model CD
        class C
            attributes
                c: String
        end
    
        class D
            attributes
                d: String
        end
    
        association cd1 between
            C[*] role cc1
            D[*] role dd1
        end
    
        association cd2 between
          C[*] role cc2
          D[*] role dd2
        end
    
    
    mediator AB < NONE
    end
    
    mediator CD < AB
        clabject C : A
            attributes
                a1 -> c1
                ~a2
        end
        
        clabject D : B
        end
    end
