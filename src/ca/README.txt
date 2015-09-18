Component Analysis (CA) answers the following question:

What are the components of this app?  

This is important because presumably existing analysis reports results
at the level of components; not at the level of compilation
units/files.

CA requires the construction of type hierarchy -- to find which
classes from the app inherit from Activity, Receiver, etc.  

For that we can use existing compiler infrastructure (e.g., SOOT) or
we can just traverse the AST of each compilation unit to build type
hierarchy (suggested option).
