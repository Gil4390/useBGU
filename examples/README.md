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

Add new ones as they come up -- keep each one focused on illustrating one
or two specific things well, rather than accumulating everything a model
could possibly demonstrate into a single file.
