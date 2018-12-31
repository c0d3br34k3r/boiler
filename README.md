# Boiler

A general-purpose templating language that supports for-loops, if-else conditionals, and can process complex evaluable expressions with native JSON support.

For a complete guide to Boiler, check out the wiki!

But if you can't be bothered, here is an example template that should give you a good idea of what the syntax looks like.  This template renders the output of a FizzBuzz program.

```
@{for n in range(100)}
    @{if n % 3 == 0 && n % 5 == 0}
FizzBuzz
    @{else if n % 3 == 0}
Fizz
    @{else if n % 5 == 0}
Buzz
    @{else}
${n}
    @{end}
@{end}
```
