The condensed HapMap data was provided by Jonny Perl and DNA Painter and used with their permission. GFG transformed <a href="https://github.com/dnapainter/apis?fbclid=IwAR0tTmkifa0uK-gFJeDdjpc0GNmlIbVlgoYu6a3oOUfM-nINORjKQZ1WjvU" target="new">their file</a> into the csv file genetmap.csv which is suitable for importing into Neo4j using this query: 

LOAD CSV WITH HEADERS FROM 'file:///cHapMap.csv' as line FIELDTERMINATOR ',' create (c:cHapMap{chr:toInteger(line.chr),pos:toInteger(line.pos),cm:toFloat(line.cm),toFloat(line.increment_cm)})

The file must be in the import directory before running the query. This process is automated for users of Graphs for Genealogy. The file will be downloaded from here and imported using GFG code.

The "condensed" HapMap is discussed further by <a href="https://hapi-dna.org/2020/11/minimal-viable-genetic-maps/?fbclid=IwAR3alJcth1Kpcn5WL8Cl_c-49jloJPSbyOb4TQw2PRvwNhjO-gRaTu_zx34" target="new">Amy Williams</a>.

DNA Painerhas an <a href="https://dnapainter.com/tools/cme?fbclid=IwAR26BX1h9pnXFuXE8qWGaUlGeOB0xqTOOB14GvS6Q2vuRzFuOFr5h-u4mgs" target="new">online rendering</a> of the condensed HaoMap where you can enter a segment and get the cM.

The full HapMap file set is in the folder.

For GFG the graph has cHapMap nodes with properties of chr, pos, cm and incremental_cm. The query to get the cm then becomes:

MATCH (h:cHapMap) where h.chr=1 and 100000000>h.pos/1000>1000  RETURN sum(h.icm)

The query uses the index to find the start point and then traverses the graph to the end, summing up the incremental cM.
