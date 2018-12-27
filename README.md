The simplest type of template has one or more placeholders in which the user can insert their own text, similar to how `printf` works, except the placeholders have names rather than simply being indices.

Here is the opening line of Jane Austen's *Pride and Prejudice*, with a few words replaced with variable names, for a "Mad Libs" effect:
```
It is a truth ${adverb} acknowledged, that a ${adjective} man in possession of
a good ${noun1}, must be in want of a ${noun2}.
```

We'll complete our template using this map, displayed here in JSON:
```
{
	"adverb": "accidentally",
	"adjective": "cowardly",
	"noun1": "toothpaste",
	"noun2": "surprise"
}
```

And the resulting output is:
```
It is a truth accidentally acknowledged, that a cowardly man in possession of
a good toothpaste, must be in want of a surprise.
```

So far this isn't anything we can't do without printf, though it does look a little cleaner.