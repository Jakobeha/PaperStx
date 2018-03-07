# Dependent Type System

```
Dependent
- {content:.+} is [content>Raw]

DependentProp
- Const
- let {content:.+} in [Property]
> Property
  | content>Raw
  | Val

FarDependentProp
- let {content:.+} in
  [dep:ExtDependent(Input = content>Raw)] or
  [dep>Output]

ExtDependent
< Input
> Output
- {out1:[A-Za-z]+} {out2:[0-9]+} | ([Input])
> Output
  | out1>Raw
  | out2>Raw

FarConstDependentProp
- [Property] <- [dep:ExtDependent(In = Type)]
> Property
  | dep>Output
  | Def

RewriteExtDependent
< In
> Output
| ExtDependent(Input = In)
> Output
  | self>Output
  | Type
| Val
> Output
  | Val
```

## Top-Level
```
Dependent
- {content:.+} is [content>Raw]

DependentProp
- Const
- let {content:.+} in [DependentProp$0>Property(content>Raw = content>Raw)]

DependentProp$0>Property
< content>Raw
| content>Raw
| Val

FarDependentProp
- let {content:.+} in
  [dep:ExtDependent(Input = content>Raw)] or
  [dep>Output]

ExtDependent
< Input
> Output
- {out1:[A-Za-z]+} {out2:[0-9]+} | ([Input])
> Output = ExpDependent$0>Output(out1>Raw = out1>Raw, out2>Raw = out2>Raw)

ExtDependent$0>Output
< out1>Raw
< out2>Raw
| out1>Raw
| out2>Raw

FarConstDependentProp
- [FarConstDependentProp$0>Property(dep>Output = dep>Output)] <- [dep:ExtDependent(In = Type)]

FarConstDependentProp$0>Property
< dep>Output
| dep>Output
| Def

RewriteExtDependent
< In
> Output
| ExtDependent(Input = In)
> Output = RewriteExtDependent$0>Output(self>Output = self>Output)
| Val
> Output = RewriteExtDependent$1>Output(self>Output = self>Output)

RewriteExtDependent$0>Output
< self>Output
| self>Output
| Type

RewriteExtDependent$1>Output
< self>Output
| Val
```

## Standalone Blocks
```
Dependent
- {content:.+} is [content>Raw]

DependentProp
- Const

DependentProp
- let {content:.+} in [DependentProp$0>Property(content>Raw = content>Raw)]

DependentProp$0>Property
< content>Raw
| content>Raw
| Val

FarDependentProp
- let {content:.+} in
  [dep:ExtDependent(Input = content>Raw)] or
  [dep>Output]

ExtDependent
< Input
> Output = ExtDependent$0>Output(out1>Raw = out1>Raw, out2>Raw = out2>Raw)
- {out1:[A-Za-z]+} {out2:[0-9]+} | ([Input])

ExtDependent$0>Output
< out1>Raw
< out2>Raw
| out1>Raw
| out2>Raw

FarConstDependentProp
- [FarConstDependentProp$0>Property(dep>Output = dep>Output)] <- [dep:ExtDependent(In = Type)]

FarConstDependentProp$0>Property
< dep>Output
- Out: [dep>Output]

FarConstDependentProp$0>Property
< dep>Output
- Def: [Def]

RewriteExtDependent
< In
> Output = RewriteExtDependent$0>Output(self>Output = self>Output)
| ExtDependent(Input = In)

RewriteExtDependent$0>Output
< self>Output
| self>Output
| Type
```

### Data Structure
```
{...} - For verification - not technically neccessary, although used, could be replaced

EnumType - The type of a single block, concrete
= String
* { inputs: Set[DependentType]
*   outputs: Set[DependentType] }
* Color - Type properties - so far color is the only property.

BlockType - The concrete type of a hole - possible concrete types
= String
* { inputs: Set[DependentType]
*   outputs: Set[DependentType] }
* Set[EnumType]

DependentType - An abstract type which resolves
= String

( FunctionType[T] - Rewrites inputs for the (usually dependent) type
  = T
  * Rewrite[T]
  - To resolve, convert dependent type to resolving type, rewrite (unresolved) inputs, then further resolve
  
  Rewrite[T] - Changes dependent types before they're resolved
  = Map[DependentType, FunctionType[T]] )

ProFunctionType - Rewrites inputs and outputs
= DependentType
* inputs: Rewrite[DependentType]
* outputs: Rewrite[DependentType]
- Resolve like a regular function type.

RewriteUnionType - Combines types, rewrites inputs and outputs
= String
* Set[ProFunctionType[DependentType]]
* { inputs: Set[DependentType]
*   outputs: Set[DependentType] }
- To resolve, fully resolve all functions, then concat their enum types

ResolvingType - A type being resolved
= ResolvedType(BlockType)
+ UnresolvedType(RewriteUnionType)

Scope - Resolves types
= Map[DependentType, ResolvingType]

( BlockFrag - A part of a block.
  = StaticText(String) - Immutable - e.g. keywords, core syntax
  + FreeTextHole - Mutable - e.g. bind variable names
    = String 
    * RegExp 
    * instanceBind: String
  + BlockHole - Mutable - e.g. reference variable names
    = Option[Blob]
    * FunctionType[DependentType]
    * instanceBind: String)
  
  Block - An untyped block - a node in the syntax AST
  = Seq[BlockFrag]
  
  RewriteBlock - A block whose outputs are rewritten
  = Block
  * Rewrite[DependentType]
  
  TypedBlock - A block annotated with type information
  = RewriteBlock
  * EnumType
  
  Blob - Can encode arbitrary text (e.g. a user typing, invalid code), "ideally" encodes a block
  = BlockBlob(TypedBlock)
  + FreeBlob(String) )

EnumBlockClass - Directly contains blocks of the type
= EnumType
* Set[RewriteBlock]

UnionBlockClass - Indirectly contains blocks of sub-types
= String
* Set[ProFunctionType]
* { inputs: Set[DependentType]
*   outputs: Set[DependentType] }

EmptyBlockClass - Contains no blocks of any type
= String
* { inputs: Set[DependentType]
*   outputs: Set[DependentType] }

BlockClass - Contains all blocks with the given type
= EnumBlockClass
+ UnionBlockClass
+ EmptyBlockClass

Language - Contains blocks for an AST of a programming language
= Set[BlockClass]

```