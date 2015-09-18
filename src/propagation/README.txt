It is important to decide what program representation our static
analysis will operate.  This is important to decide how information
propagates (/data flows).  

More precisely, once we realize there is communication between
functions f1 and f2 (certainly from different files), how we get to
discover that there is communication between components c1 and c2?

Traditional approach:

The traditional approach to solve this problem is to use a call graph
and propagate information across the edges of the graph.  For example,
the info associated to "f1" will be propagated up to the point it
reaches component(s) of the app (same for "f2").  One could find
communication across components after this step.  Unfortunately call
graphs for android are tricky; think about the effects of lifecycle
and system events in call graphs.  A group from OSU has been working
on it for a while (see
http://web.cse.ohio-state.edu/presto/software/gator/).  In principle,
we could use their tool(s) and operate on their call-back call graphs,
but *i think* that defeats our purposes of being very lightweight.  I
suggest to consider/experiment this option in the future.

Non-traditional approach:

An alternative approach (potentially less precise) to using a call
graph is to use a file dependency graph.  A node in this graph denotes
a file in the app where as an edge denotes a dependency across files.
Note this is not as precise as call graphs.  

I suggest to follow this alternative first and quickly see where we
stand.

As for implementation, there are two alternative: - traverse the ASTs
looking for references, and - grep files for references.  Script
"filedeps.sh" is available and implements alternative (c).
