# Examples

Hand-picked, illustrative specifications, kept separate from
`use-core/src/test/resources/...` (test fixtures, tied to specific
assertions) and `use-core/src/main/resources/examples/` (the historical
upstream University-of-Bremen example set). This directory is for examples
whose job is to be *read*, to build intuition for the language -- start
here if you want to see a feature in context rather than in a test string.

Every file here is expected to actually compile with the current compiler.
When you add one, verify it first (e.g. via `catuse-latex-report.sh` for a
CatMLM file, or a quick throwaway compile check for the others) rather than
just writing it out from memory.

## Layout

One subdirectory per language layer -- see `USE.ebnf` / `MLM-USE.ebnf` /
`CatMLM.ebnf` at the repo root for what each layer actually adds:

- **`USE/`** -- plain, single-level USE. No multi-level modeling at all;
  useful as a baseline to contrast against the other two.
- **`MLM-USE/`** -- the base multi-level extension: `MLM`, `mediator`,
  `clabject`, `assoclink`, written out in full (every level and mediator as
  its own block).
- **`CatMLM/`** -- the terser front-end sugar (`category`, the fused
  `clabject` shorthand, `catAtt`/`catConstr`/`catAssociation` tags) that
  desugars into the same models `MLM-USE/` examples produce by hand.

## Current examples

| File | Layer | What it shows |
|---|---|---|
| `USE/Vehicle.use` | USE | Ordinary single-level generalization (`Car < Vehicle`) -- the baseline the other two contrast against. |
| `MLM-USE/VehicleTaxonomy.use` | MLM-USE | A 3-level instantiation chain (`Vehicle` &larr; `Car` &larr; `CarModel`) with attributes, operations, constraints, an attribute rename, and a constraint cancellation, written out in full plain syntax. |
| `CatMLM/VehicleTaxonomy.use` | CatMLM | The same 3-level chain, in CatMLM syntax -- and a worked illustration of what CatMLM genuinely can't express (no operations, no per-clabject rename/removal) compared to the MLM-USE version above. |
| `CatMLM/VehicleCatalog.use` | CatMLM | A small model exercising every `cat*` mechanism at once: `catAtt`, `catConstr`, `catAssociation`, and a clabject instantiating two powerclasses at once. |
| `CatMLM/FusedHeader.use` | CatMLM | The fused classifier/clabject header (`category X < Sup : Power`), the parenthesized multi-item list convention, and silent union across a fused header and a separate `clabject` statement for the same name. |
| `MLM-USE/GraphColoring.use` | MLM-USE | A 3-level clabject chain over a purely graph-theoretic domain (`Vertex` &larr; `ColoredVertex` &larr; `LabeledVertex`) with a self-association (`AdjacentTo`) inherited unchanged across levels. Exercises `inter-constraints` for real: one invariant (`NonNegativeValence`) that can only be stated post-rename, and one (`ProperColoring`) that needs an explicit `oclAsType` downcast because an inherited role keeps its *declared* type across levels, not the type of whatever actually implements it lower down. |
| `MLM-USE/DagRefinement.use` | MLM-USE | An abstract graph (`Node`/`Edge`) refined into a concrete DAG (`OrderedNode`/`DirectedEdge`) via `assoclink` -- the one mechanism no other example here shows -- plus an `Acyclic` inter-constraint that is only expressible once the refinement adds a topological `order` number the abstract level doesn't have. |
| `CatMLM/DagRefinement.use` | CatMLM | The same domain as `MLM-USE/DagRefinement.use`, showing two genuine, unworkaroundable CatMLM gaps side by side: no `assoclink` equivalent (the new association at the lower level has no way to be tied back to the one above it) and no `inter-constraints` section at all (so `Acyclic` cannot be written anywhere in this file). |
| `MLM-USE/InstanceCount.use` | MLM-USE | The direct answer to "how many instances of a class can exist": a plain OCL `allInstances()->size()` bound, which has to live in `inter-constraints` (multiType-qualified) the moment it bounds, or compares, populations across more than one level. |
| `MLM-USE/BoundedDegree.use` | MLM-USE | Two different senses of "how many" kept deliberately distinct: association-end multiplicity (`[0..3]`, a structural bound per object) versus a population bound over `allInstances()` (a bound on how many objects satisfy a condition). |
| `MLM-USE/QualifiedEdge.use` | MLM-USE | A qualified association end (`qualifier(key: Integer)`), refined across a level via `assoclink` rather than inherited unchanged. |
| `MLM-USE/DerivedDegree.use` | MLM-USE | A derived attribute (`derive = ...`) computed from a self-association at one level, inherited unchanged by a clabject at the level below; its bound has to live in `inter-constraints` since a model's own `constraints` block can't see anything inherited via clabject at all, renamed or not. |
| `MLM-USE/TernaryBetween.use` | MLM-USE | A ternary (3-ary) association, inherited whole by a lower-level clabject exactly like a binary self-association would be -- n-ary associations aren't special-cased in the clabject/assoclink machinery. |
| `MLM-USE/InterAssocBridge.use` | MLM-USE | `inter-associations` -- an association connecting classes belonging to two different, mutually-unrelated constituent models directly, with no clabject/mediator relationship between the models at all. |
| `MLM-USE/InterClassHub.use` | MLM-USE | A genuine `inter-classes` class (`System`, owned by no single model) bridged to a per-model class via `inter-associations` -- and the one place a bare (`simpleType`) class name in `inter-constraints` is actually correct, contrasted directly with the `Model@Class` qualification the per-model class still needs. |
| `MLM-USE/InterAssociationClassBridge.use` | MLM-USE | An inter-level `associationclass` (`Membership`, its own attribute and invariant) declared in `inter-classes`, bridging two different constituent models. |
| `MLM-USE/EnumViaInterEnum.use` | MLM-USE | `inter-enums` -- a shared enum type owned by no single model, typing an attribute on a genuine `inter-classes` class. |
| `MLM-USE/OrderedPath.use` | MLM-USE | The `ordered` association-end modifier (a role typed as a `Sequence`, not a `Set`), inherited unchanged across a clabject edge. |
| `MLM-USE/UnionSubsetsRedefine.use` | MLM-USE | Three same-level UML role-refinement modifiers no other example here shows: `redefines` (a subclass narrows an inherited role's type), and `union`/`subsets` (a role declared as the union of, and another as a subset of, related roles). |
| `MLM-USE/OperationPrePost.use` | MLM-USE | `interPrePost` -- a pre/post-condition for an operation, stated from `inter-constraints` and multiType-qualified exactly like an `interInvariant`'s context. |
| `MLM-USE/DeepChain4Levels.use` | MLM-USE | A four-level clabject chain (every other example stops at three), each direct edge renaming-and-cancelling only what its own direct parent owns, capped by one inter-constraint chaining `allInstances()` comparisons across all four levels at once. |
| `MLM-USE/TreeComposition.use` | MLM-USE | The `composition` association kind, modeling a rooted tree via a self-association, plus an `allInstances()->select(...)->size()` population invariant ("exactly one root") that isn't expressible as a per-object invariant or a multiplicity at all. |
| `CatMLM/CatSelfAssocLevels.use` | CatMLM | The smallest possible `catAssociation` + `catConstr` combination: a self-association plus an invariant cancelled for every instantiator. |
| `CatMLM/CatMultiPower.use` | CatMLM | A clabject instantiating two powerclasses at once (`clabject C : (A, B) end`) -- something plain MLM-USE's `clabject` rule genuinely cannot do in a single statement (one parent per `clabject`). |
| `CatMLM/CatLocalSuperInClabject.use` | CatMLM | An alternate spelling of the fused header: a standalone `clabject` statement carrying its own same-level `<` edge (`clabject C < D; C : P end`) for a classifier declared with no clauses of its own. |
| `CatMLM/CatQualifiedAssoc.use` | CatMLM | The `qualifier` association-end modifier on a `catAssociation` -- works unchanged, since `catAssociationDefinition` reuses the same `associationEnd` grammar rule plain associations do. |
| `CatMLM/CatPerLevelInstanceCount.use` | CatMLM | Contrasts with `MLM-USE/InstanceCount.use`: a population bound over `allInstances()` works fine per level, but `catInvariant` only ever takes a `simpleType`, so there is no CatMLM way to compare two different levels' populations in one invariant at all. |

Add new ones as they come up -- keep each one focused on illustrating one
or two specific things well, rather than accumulating everything a model
could possibly demonstrate into a single file.
