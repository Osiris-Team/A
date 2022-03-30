# A
A language

### Objects
Each file that contains `A` source code represents one object (must have no file extensions).
```
project
 README.md
 Main   
-folder
  MathUtils
  NumberUtils
--subfolder   
   Person
   Wallet
   ...
```
If we want to use the `MathUtils` object in our `Main` object, add its path to the top:
```A
/folder/MathUtils

MathUtils math = new MathUtils();
```

### Primitives

 - `boolean` has only two possible values: true and false. Represents one bit (0 or 1) of information.
 - `byte` is an 8-bit signed two's complement integer. It has a minimum value of -128 and a maximum value of 127 (inclusive).
 - `short` is a 16-bit signed two's complement integer. It has a minimum value of -32,768 and a maximum value of 32,767 (inclusive).
 - `int` is a 32-bit signed two's complement integer, which has a minimum value of -2^31 and a maximum value of 2^31 -1.
 - `long` is a 64-bit two's complement integer. The signed long has a minimum value of -2^63 and a maximum value of 2^63 -1.
 - `float` is a single-precision 32-bit IEEE 754 floating point.
 - `double` is a double-precision 64-bit IEEE 754 floating point.
 - `char` is a single 16-bit Unicode character. It has a minimum value of '\u0000' (or 0) and a maximum value of '\uffff' (or 65,535 inclusive).
 - `code` is a single code block. 

```A
int a = 1;
```
