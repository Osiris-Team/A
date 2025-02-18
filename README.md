# A-Lang
A language is my idea of a perfect programming language, which was
born from the frustration and joy of using languages like Java, C, C++ and JavaScript.

There is an actual compiler written in Java that compiles A => C => binary.
Thus A achieves the same cross-platform performance as C.

Its main aim is to make programming fun and provide higher-level concepts without sacrificing performance.
Functions are variables for example. To be exact, everything in this language is a variable and
you can learn the whole language by reading this file.

If you want to get started right away, download the [A-Sample](https://github.com/Osiris-Team/A-Sample) repo,
open a terminal in that folder and
execute `./a/a` on Linux or `.\a\a.exe` if you are on Windows (this starts the A compiler command-line interface).



## Core Ideas
 - Tiny standard library, to keep the focus on language development. This will change once the language is more mature.
 - Allow easy use of GPU / hardware acceleration, instead of only writing CPU code.
 - Provide cross-platform methods for all input and output devices, like (touch-)screens, keyboards, files, etc.
 - Include a dependency management system and provide an online repository.
 - Include a build-tool and make compilation frictionless.
 - Performance, fast writability, and readability have the same importance.
 - Encourage the use of event listeners.
 - Garbage collected via [bdwgc](https://github.com/ivmai/bdwgc), can be [disabled](#TODO_HOW?).
 - Library developers should benefit the same amount as their users, in terms of language features.
 - Language patterns/features should be simple, consistent, logical, and similar to each other.

## Highlights
 - Modular Object Oriented Programming (MOOP) via [object parts](#object-blocks--parts).
 - Compiles to C and [allows embedding raw C code](#TODO).
 - Everything is a variable and [primitives functionality can be extended](#Extending-functionality).

## Status
- No release yet, still in early development (spec- and implementation-wise). Once the basics are done 1.0 will get released.
- This repository contains the A compiler which is written in Java and misses a bunch of the features mentioned in this file.
However it more or less lays the groundwork and style for the compiler.



## Statements
Multiple statements on a single line must be separated by a semicolon `;` otherwise
the semicolon is optional.
```A
a = 10
b = 20; c = 30
```
<details>
 <summary>How is this still typesafe?</summary>

Because null values are not allowed, every variable has a default value from which we determine the type.
</details>

<details>
 <summary>Issues with readability?</summary>

With this Python-like approach, we do not know if it's the initial usage / declaration of the variable or updating/setting the variable, and would need to rely on IDEs to make that difference clear.
Which I do not really like because of github PRs for example where the code is basically in its raw text form and only has some syntax highlighting.

Thus maybe instead something like this?
```A
new a = 10
new b = 20; new c = 30
```
I think in this case we could directly just insert the type name instead of "new", to increase readability, however for longer types + if we some day get generics
this can also decrease readability.

Another problem with this is that it might seem we also create a "new" object with this keyword even though we only create a new variable/pointer, for example: 
```A
new a = 10
new b = a
```
</details>


## Comments
Everything inside a comment is ignored. 
`//` marks the start of a comment and goes until the end of the line.
```A
a = 1 // Single line comment
```



## Values, Variables and Types
A variable is a value with a name/reference. 
It's a particular set of bits or type of data located in the RAM that can be modified.
Primitive types (listed below) are available in all your code without the need of importing something.

### Numbers
| Usage                 | Name    | Description                                                                                                         |
|-----------------------|--------|---------------------------------------------------------------------------------------------------------------------|
| `v = true; v = false` | boolean | Has only two possible values: true (1) and false (0). Represents one bit of information.                            |
| `v = 1b`              | byte    | 8-bit signed two's complement integer. It has a minimum value of -128 and a maximum value of 127 (inclusive).       |
| `v = 1s`              | short   | 16-bit signed two's complement integer. It has a minimum value of -32,768 and a maximum value of 32,767 (inclusive) |
| `v = 1`               | int     | 32-bit signed two's complement integer, which has a minimum value of -2^31 and a maximum value of 2^31 -1.          |
| `v = 1l`              | long    | 64-bit two's complement integer. The signed long has a minimum value of -2^63 and a maximum value of 2^63 -1.       |
| `v = 1f`              | float   | Single-precision 32-bit IEEE 754 floating point.                                                                    |
| `v = 1d; v = 1.0`     | double  | Double-precision 64-bit IEEE 754 floating point.                                                                    |

### Text
| Usage          | Name   | Description                                                                                                                       |
|---------------|--------|-----------------------------------------------------------------------------------------------------------------------------------|
| `v = "a"`     | char   | Single 16-bit Unicode character. It has a minimum value of '\u0000' (or 0) and a maximum value of '\uffff' (or 65,535 inclusive). |
| `v = "aa"`    | string | String of characters, or more accurately: an array of char with variable length.                                                  |

### Special 
| Usage              | Name       | Description                                                                                       |
|--------------------|------------|---------------------------------------------------------------------------------------------------|
| `v = (){}`         | code       | Single code block                                                                                 |
| `v = ObjectName()` | ObjectName | Comes from the ObjectName.a file you created. If in another folder requires an import to be used. ObjectName is a placeholder for anything you might come up with. |
| `v = (0, 1, 2)` | array/tuple | Fixed size array of elements. Inspired by the function arguments syntax. Supports different types per index/element. |

### Attributes
Variables can have additional/optional attributes which get added 
at the start: `hidden final a = 10`
 - `hidden` hides the variable from other files. 
You can disable this by adding `show hidden` to the start of your file.
 - `final` makes the variable unchangeable after first value assignment.

### Arrays/Tuples
 - `[<size>]` creates an array of the current type, of the specified size (integer type).

### Extending functionality
Primitives are strictly speaking not really primitives but objects located in `./a`, thus you can also extend their functionality by using the object parts feature explained further below. 

Note that primitives must not be imported because the compiler adds a hidden import statement to import everything from `./a`.

<details>
<summary>Why is there no public/private?</summary>
 
All variables are public by default. If you search public and private on GitHub
you will see that public is used around 422 million times and private only 177M times,
thus public is the default, to reduce the amount of code written.
</details>

<details>
<summary>Where is null?</summary>

Gone! Due to the countless runtime errors arising from it and because otherwise
we would need to name the type when defining a variable. This way its ensured all variables
have a default value which lets us determine the type at compile time and gives us the same
benefits as statically typed languages.
</details>


## Code/Scopes

<details>
<summary>Removed</summary>

The feature below will not be included in the language because it clashes with the function type. More specifically because the code within a function is expected
to only run if you call it, but the code in a scope is expected to run always directly and because they look very similar this might cause uneccessary confusion.

A scope is code within brackets `{}`. 
Variables created within a scope are not accessible from outside:
```A
a = 3
{
  b = 0
  // a can be used here
}
// a can be used here
// b cannot be used here
```

Let's say the code above is located in the `Utils` file and we want to access 
it in our `Main` file:
```A
Utils utils = Utils()
utils.a // Can be accessed
utils.b // Error: Cannot be accessed
```
</details>

## Functions
Functions are special code blocks that are held by the `code` variable.
Note that functions are `final` by default due to the limitations by the underlying C language.
```A
b = 0;
setNumber = {
  b = 9
}
setNumber // Does nothing
setNumber() // Executes the code

getNumber = returns int {
  return b
}
result = getNumber() // Executes the code and returns b
```
Parameters can be passed over too like so:
```A
multiply = returns int (int a, int b) {
  return a * b
}
multiply(10, 20)
```
Note that changing a parameter's value in a function,
affects the original variables value:
```A
setTo10 = (int a) {
  a = 10
}
myVariable = 27
setTo10(myVariable)
// myVariable is now 10
```

There are different types of code blocks, which all extend the `code` type: `function, if, if else, else if, for, for each, while`.
These will be explained further below. 

<details>
 <summary>Logic: if, ifElse, elseIf</summary>
 
```A
a = true
b = true
if a { // short version for: if a == true
  a = false // do something if a is true, set it to false for example
} else { // else is optional
  a = false
}

// inline example
if a do a = false 
else do a = false // else is optional

if a > b do a = false
else if b do b = false // elseIf example, also optional

// you can easily make it a variable/function
myLogic = { if a do a = false }
myLogic()
```
</details>

<details>
 <summary>Loops: for, for each, while</summary>
 
```A
numbers = 1, 2, 3, 4
current = 0
for index i = 0; i > numbers.length; i++ {
  current = numbers[i] // access number at index position in numbers array
}

// inline example
for index i = 0; i > numbers.length; i++ do current = numbers[i] 

for each x in numbers {
  current = x
}

// inline example
for each x in numbers do current = x 

for each x in numbers and index i { // for each with optional index
  current = x
}

// inline example
for each x in numbers and index i do current = x

i = 0
while i < numbers.length {
  current = numbers[i]
  i++
}

// inline example
i = 0
while i < numbers.length do current = numbers[i]; i++
```
</details>


### Side effect-free functions and clone
The compiler enforces the usage of double parenthesis when defining and using a function that has no side effects,
meaning that it doesn't update its arguments or other variables/fields of the object (except static fields). For this to work you either only read the argument or clone it and update its clone only.

So we can modify the previous example, which removes the whole purpose of the setTo10 function, however it should clarify everything:
```A
setTo10 = ((clone int a)) {
  a = 10
}
myVariable = 27
setTo10((myVariable))
// myVariable is still 27, because the operation is performed on a clone/copy
```

The `clone` keyword in a function parameter simply is a quality of life addition to reduce code, the previous example would look much worse without it:
```A
setTo10 = ((int a)) {
  a1 = a.clone()
  a1 = 10
}
myVariable = 27
setTo10((myVariable))
// myVariable is still 27, because the operation is performed on a clone/copy
```

<details>
<summary>TODO How to reduce code duplication for libraries?</summary>
 
 Let's say we have a string object and we want to provide a `str.replace("x", "y")` function with replaces all x characters with y in this case,
 we would know that it affects the string directly / itself. Now we also want to provide `str1 = str.replace(("x", "y"))` function which does the same,
 however returns a new string with the changes. Does the library developer really need to define the function twice and code the logic twice?
</details>


### Function overloading
Function overloading is not allowed. 
```a
myFunction = {}
myFunction = (int a) {} // error
```
This ensures that less duplicate documentation is written
and related code is inside the same function.

Optional function parameters should help you out if you allow multiple different types of inputs in your function for example.
This is documented further below.


### Return multiple values
Sometimes you want to return multiple values from a single function.
In most languages you would need to create a new class or a new datatype which can be annoying.
A however has a built in solution for this to make it easier:
```A
myFunction = returns int a, int b {
  return 10, 20
}
a, b = myFunction()
// or
c = 0, d = 0
(c, d) = myFunction()
```



## Null safety and optional parameters
All variables must have a starting/default value when defined, which means
that code like this: `a;` will not work. Thus there is no `null` type in A.

Optional function parameters must have a default value.

You can make parameters optional by writing `optional: var1, var2, etc...`.
Here is an example:
```A
multiply = returns int (int a, int b, optional: int c = 1, int d = 1){ 
  return a * b * c * d
}
multiply(10, 20) // Valid
multiply(10, 20, 30, 40) // Not valid, optional parameters must have the variable name
multiply(10, 20, c:30, d:40); // Valid
multiply(10, 20, c:30) // Valid
multiply(10, 20, d:40) // Valid
```



## The `constructor` keyword
The main paradigm is modular object oriented programming.
Objects get initialized by using the `ObjectName()`
like so:

`Main`
```A
john = Person(63)
peter = Person(35)

john.age // == 63
john.id // == 1

peter.age // == 35
peter.id // == 2

Person.count // == 2
```
`Person`
```A
static count = 0

constructor = (int age) {
    count++
    id = count
}
```
The constructor is the function called to initialize an object:
- There can only be one in a file/object.
- It is similar to a regular function, which means that it can also have parameters,
  but no return type (note that the parameters will be available to the instantiated object if not set to private).
- It gets added by the compiler automatically if not existing (with no parameters).
- Variables defined inside the constructor are only available to the instantiated object.

Note that everything you write outside the constructor will only be executed once, specifically at the first time the file/object is used somewhere else.



## Files/Objects
Each file represents one object.
```
project
 - Main.a
 - MathLib.a
 - folder
    - MathLib.a
    - AnotherLib.a
```
`MathLib` can be used in `Main` like so:
```A
math = MathLib()
```
`MathLib` from /folder can be used in `Main` like so:
This must be done like this, since MathLib exists twice, in the
current folder and in /folder.
```
math = MathLib()
math1 = ./folder/MathLib()
```
Normally you just enter the files'/folders' relative path on the top:
```A
./folder/AnotherLib

// Otherwise you can import the whole folder:
./folder

anotherLib = AnotherLib()
```



## Object
The default object all other objects automatically extend from contains following functions:
- `obj.free()` for optional manual memory management, after calling this the memory for this object is cleared and referencing it is not allowed anymore.
- `obj.clone()` returns a deep copy of the object.


## Object Blocks / Parts
```
/Main.a
/Person.a
/Brain.a
/Hand.a
```
Object Blocks make it possible to break down
an otherwise very large file into multiple smaller files, since an Object Block has access to the code
of all the other related blocks (except for optional Object Blocks).

Besides that it allows "true" functionality extension of existing Objects (for example from an Object of another library, or even primitive types)
with no need of code refactoring.

Brain:
```
part of Person
```

Hand:
```
optional part of Person
```

Usage in Main.a:
```
// How do I use the Person class with its Object Blocks?
p = Person()

// How do I make sure Person gets loaded with all Object Blocks, even optional ones?
p = Person+*()

// How do I only use specific optional Object Blocks?
p = Person+Hand()
```



## Inheritance
There are two ways of inheriting another objects' functionality, namely
via the `inherits` and `implements` keywords.

`inherits <obj1>, <obj2>, ...` lets you use the methods and fields of the inherited object in your current object.
- Their constructors are called in the order they were listed.
- If there are overlapping top-level variables (for example functions with equal names),
you also need to specify the variable name behind the object like so (myVariableName exists inside AnotherObject and AnotherObject2 in this example): `inherits AnotherObject, AnotherObject2+myVariableName`.

`implements <obj1>, <obj2>, ...` lets you use the methods and fields of the implemented object in your current object, but you
must provide your own implementation for all of them.
- Their constructors must also be implemented, and are called in the order they were listed.

```A
inherits AnotherObject, AnotherObject2
implements AnotherObject3, /path/to/AnotherObject4
```



## Project structure and dependencies/libraries
The project root directory is located where your `a` directory is in.
In your code, you can only use/import code that is within that folder.
To reference it in code use `./`.

The A compiler has a dependency management system (DMS) built in.
Currently, it supports A projects hosted and released on GitHub.
With the command `add lib github_repo_url/github_repo_name`, the DMS
will fetch the `src.zip` of the projects' latest release and extract its
contents into `./libs/lib_author/lib_name/lib_version`.

To use that lib in your code, you would simply import it by entering
its path at the top of your file.
```A
./libs/lib_author/lib_name/lib_version/SomeObject
```

Updating libs can be done with the DMS too: `update libs` to update all, or
`update lib github_repo_url/github_repo_name` to update a specific lib.
The DMS will take care of renaming/updating your imports in your code too.
