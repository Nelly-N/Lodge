# Lodge
Clojure's library: Validate or update a hash map's values with the same structure hash.


### Purpose I wrote
See.

```clj
=> (patrol {:a integer? :b {:ba string? :bc vector?}}
           {:a 20       :b {:ba "ba"    :bc [1 2 3]}})
true
```

and

```clj
=> (patrol {:a integer? :b {:ba string? :bc vector?}}
           {:a 20       :b {:ba "ba"    :bc 3}})
([:b :bc]) ;; it's a key set to access false values with 'get-in.
```

The first arg of 'patrol takes a hash whose values are tester against each value of the second. It have to work as a validation for a hash. A hash whose elements are proper for it should have partol return true. Otherwise, return a list whose element can find all of false values in the hash through 'get-in. For example, (get-in somemap (first the-list-returned)) should returns the wrong value.


### The structure of keys it needs
This is the function, the base to define the all other functions here, which picks those keys out of a hash, named as 'roomlist.

```clj
=> (roomlist {:a 1 :b {:ba 1 :bb {:a 1 :b 2}} :c 3})
[[:a] [:b :ba] [:b :bb :a] [:b :bb :b] [:c]]
```

There are some notes.
The first, a result of (keys the-map) has :b, but (roomlist the-map) doesn't. 'roomlist returns keys to leafs of a hash.
The second, even if an arg is a flat hash, the result keys should be wrapped in vector.

Think this function I defined.

```clj
(defn lodgers [rawmap]
  (map #(get-in rawmap %) (roomlist rawmap))  )
```

This returns all reafs of a hash.

```clj
=> (lodgers {:a 1 :b {:ba 1 :bb {:a 1 :b 2}} :c 3})
(1 1 1 2 3)
```

(I've thought that those names may be not bad, at least rather than borrowing math terms.)


### Built-in functions
Assume them.

```clj
 Directory := a validation hash such like {:a integer? :b string?}.
 Dinners := an update hash such like {:a inc :b dec}, {:a "new" :b "string"}, etc.
 Sublodge := {:ba 1 :bb {:a 1 :b 2}}, {:a 1 :b 2}, etc.
 (where Lodge := {:a 1 :b {:ba 1 :bb {:a 1 :b 2}} :c 3}).
```

I've already shown some functions: patrol, roomlist and lodgers. Almost other functions also use keys out of roomlist. See in quick.

: (submodel? lodge another) :: map -> map -> bool  
 True if both has completely the same keys, meaning that both are the same construct hash.

: (sublodge? greater maybesub) :: map -> map -> bool  
 True if 'greater has submodels of 'maybesub. Note that if 'maybesub is {}, an empty hash, this returns true.

: (subpatrol directory lodge) :: map -> map -> bool | map  
 Allows that 'directory is a sublodge of 'lodge. 'patrol doesn't, demanding that 'directory is a submodel of 'lodge.

: (update-with dinners lodge) :: map -> map -> map  
: (assoc-with  dinners lodge) :: map -> map -> map  
  Replaces values of 'lodge with functions in 'dinners. Both always allow that 'dinners would be a sublodge of 'lodge, as the same as 'update or 'assoc.


### License
MIT


### Author
Nelly N
