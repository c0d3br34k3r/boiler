# Built-In Functions

## Basic Functions

### `bool(x)`

Returns the boolean value of its argument.  `0`, `0.0`, `false`, `null`, `''`, `[]`, and `{}` are considered false and all other values are considered true.

### `float(x)`

Converts a numeric type to a floating point number (the `double` type in Java).

### `int(x)`

Converts a numeric type or boolean to an integer (the `int` type in Java).  The integer equivalent of a boolean value is 1 for true and 0 for false.

### `str(x)`

Converts a value to a string by invoking its `toString` method.

### `len(x)`

Returns the length of a string, the size of a list, or the number of entries in a map.

## Math Functions

### `min(seq)` or `min(num*)`

Returns the smallest numeric value in a sequence.  This method accepts either a sequence as a single argument, or two or more numeric arguments.

### `max(seq)` or `max(num*)`

Returns the largest numeric value in a sequence.  This method accepts either a sequence as a single argument, or two or more numeric arguments.

### `abs(num)`

Returns the absolute value of a numeric value.

## Iteration Functions

### `range(stop)` or `range(start, stop, step?)`

Returns a sequence of integers from a starting point, exclusive, up to a stopping point, exclusive, counting by a step value.  If one argument is passed, 0 is the start, the given value is the end, and the step is 1.  Otherwise, the given values are the start, stop, and (optionally) step, respectively.  This functions identically to the `range` built-in type in Python 3.

### `enumerate(seq)`

Converts a sequence to a sequence of pairs, whose first element is the index within the sequence, and whose second element is the item from the original sequence.

For example, `enumerate(['a', 'b', 'c'])` returns `[[0, 'a'], [1, 'b'], [2, 'c']]`.

This functions identicaly to the `enumerate` function in Python 3.

### `zip(seq*)`

Returns a sequence that aggregates elements from two or more sequences.  The nth element of the resulting sequence is a list of the elements in the nth positions in each sequences passed in.

For example, `zip(['a', 'b', 'c'], [4, 5, 6])` returns `[['a', 4], ['b', 5], ['c', 6]]`.

This functions identicaly to the `enumerate` function in Python 3.

### `stream(seq)` (BETA)

Similar to `enumerate`, returns a sequence of triples consisting of the item from the original sequence, a boolean indicating if it is the first item in the sequence, and a second boolean indicating if it is the last item in the sequence.  This can be useful for dealing with iterable objects that have no defined length.

### `keys(map)`

Returns a sequence of the keys in the map.

### `values(map)`

Returns a sequence of the values in the map.

### `entries(map)`

Returns a sequence of the key-value pairs in the map.

## String and Sequence Functions

### `contains(str, substr)` or `contains(seq, x)`

Returns true if the given string contains the given substring, or if the given sequence contains the given value.

### `capitalize(str)`

Returns a string with the first character of the given string in upper case, and the rest in lower case.

### `replace(str, find, replace)`

Returns a string with all instances of a given substring replaced with another string.

### `startsWith(str, prefix)`

Returns true if the given string starts with the given substring.

### `endsWith(str, suffix)`

Returns true if the given string ends with the given substring.

### `indexOf(str, substr, index?)` or `indexOf(seq, x, index?)`

Returns the first index of a substring within a string, or an value within a sequence, searching forwards from the given index.  Returns -1 if none was found.  The value of the index defaults to 0.

### `lastIndexOf(str, substr, index?)` or `lastIndexOf(seq, x, index?)`

Returns the last index of a substring within a string, or an value within a sequence, searching backwards from the given index.  Returns -1 if none was found.  The value of the index defaults to the length of the string or sequence.

### `join(seq, separator)`

Returns a string that concatenates the values in the sequence with interleaving occurences of the separator.

### `split(str, separator)`

Returns a sequence containing the substrings of the given string that occur between occurences of the separator.

### `upper(str)`

Returns a copy of the given string with all letters in upper case.

### `lower(str)`

Returns a copy of the given string with all letters in lower case.

### `trim(str)`

Returns a copy of the given string with all whitespace at the beginning and end removed.

### `collapse(str, replaceChar?)`

Like trim, but also reduces all subsequences of whitespace not at the beginning or end with the replacement character.  By default, the value of `replaceChar` is `' '`.

### `separatorToCamel(str, separator?)`

Converts a string in a separator format to lower camel case.  For example, `separatorToCamel('big-blue-dog', '-')` returns `'bigBlueDog'`.  The default value of `separator` is `'_'`.

### `camelToSeparator(str, separator?)`

Converts a string in lower camel case to a separator format.  For example, `camelToSeparator('bigBlueDog')` returns `'big_blue_dog'`.  The default value of `separator` is `'_'`.

### `pad(str, length, padChar?, align?)`

Returns a string padded to the given length using repetitions of the given character.  A `true` alignment value specifies left-aligned padding; `false` is right-aligned.  The default value of `padChar` is `' '` and the default value of `align` is `true`.

Examples:
- `pad('cat', 5)` returns `'  cat'`
- `pad(42, 4, '0')` returns `'0042'`
- `pad('dog', 6, '*', false)` returns `'dog***'`

## Meta-Functions

### `template(filePath, paramMap?)`

Evaluates a template at the specified path, which may be an absolute path, or relative to the location of the current template.  That template's scope has access to all local variables defined in the current scope, as well as any variables defined by `paramMap`.  Any variables set by the called template (including those in the map) will not affect the value of the variables in the current scope.  The value of `paramMap` defaults to `{}`.

### `textFile(filePath)`

Returns the text content of the file at the specified path, which may be an absolute path, or relative to the location of the current template.

### `locals()`

Returns a map of all local variables in the current scope, associating each variable name with its current value.

### `eval(expression)`

Evaluates the string as an expression, using the same parsing syntax as a regular evaluable segment.  The expression may reference variables in the current scope.

### `uneval(x)`

Returns a JSON-compatible string representation of the value, which can be parsed back to an equivalent value using `eval`.  The argument may be a boolean, string, number, list, or map (lists and maps must contain only these types as well).
