<h1>Read Me</h1>

    Neo4j-Genealogy-PlugIns
  
Please use the most recently numbered file. This is placed in the Neo4j Import folder and the database restarted. <br><br>

The Neo4j configuration file does not need to be changed with updates <b>IF</b> you enabled it with gen.* white-listing. While white-listing can specify a specific version, you will find it easier to not specify this because you need not update the config file.<br><br>

<b>History</b>




<li><b>v 1.1.14</b> Jan 28, 2023</li>
<ul>
<li>Added mt-DNA functions; still in development. The functions load FTDNA, YFull reference data.</li>
</ul>


<li><b>v 1.1.13</b> Dec 21, 2022</li>
        <ul>
            <li>enhanced endogamy knowledge graph. Blog post is <a href="https://www.wai.md/post/endogramy-ii-paths" target="new">here</a>
              <ul>
                    <li>add fam_path nodes for paths in the family tree</li>
                    <li>add intersect nodes for intersections between paths shared by two individuals.</li>
                    <li>add path_intersect relationship</li>
                    <li>add path_person relationship</li>
                    <li>new function to sort intersect path persons exactly as in the root or parent path: gen.endogamy.sort_subpath()  </li>
                    <li>path segments</li>
                    <li>new function to identify descendant who triangulate with family tree paths</li>
            </ul>
                </li>
        
            <li>moved avatar creation to optional, menu item. </li>
        </ul><br>
 
<ol> 
    <li><b>v 1.1.12</b> Nov 29, 2022</li>
        <ul>
            <li>added endogamy package, described in detail <a href="https://www.wai.md/post/endogamy-i-the-knowledge-graph" target="new">here</a></li>
              <ul>
                    <li>Endogamy knowlesge graph</li>
                    <li>union_parent relationships</li>
                    <li>union cor if persons are related</li>
                    <li>union relationship if persons are related</li>
                    <li>individual persons endogamy report</li>
                    <li>shared ancestors of two individuals</li>
                    <li>coefficient of inbreeding for an individual</li>
            </ul>
        <li>Fixed error in calculating relationship whixh surfaced in endogamy package creation</li>
    </ul>
    <li><b>v 1.1.11</b> Nov 23, 2022</li>
    <ul>
        <li>mt-DNA haplotree cumulative SNPs giving complete picture of all SNP at a branch.</li>
        <li>fixed triangulation method to accomodate change is Neo4j's Graph Data Science triangle algorithm which now rquires undirected graph projection.</li>
        <li>fixed FTDNA file uploard; was previously missing some matches anf segments</li>
        <li>addedd union_parent relationship to support union trees</li>
         <li>fixed shared DNA calculation (COI) to accomodate endogamous family trees (gen.rel.DNA_Shared)</li>
     </ul>   
<li><b>v 1.1.10</b> Oct 18, 2022</li>
    <ul>
        <li>fix DNA coverage calculationcalculation for propositus</li>
        <li>add DNA coverage observed and expected to Avatar report</li>
        <li>fixed GEDCOM import to ignore dates not comlying with GEDCOM 5.5 standard</li>
     </ul>   
<li><b>v 1.1.9</b> Oct 14, 2022</li>
    <ul>
    <li>add comments to explain coding</li>
    <li>function to upload FTDNA STR and SNP data</li>
    <li>add sort property to mt_SNPs, enabling sorts by position</li>
    <li>added counts of Y and BigY testers to Y haplottree nodes</li>
    <li>fixed bug in function to recreate avatars</li>
    <li>fix parental allocation for avatar DNA Painter renderings</li>
    <li>added ht- and Y-haplogroups (actual and inferred) to Person nodes when available; used in family tree reports.</li>
    </ul>
    
<li><b>v 1.1.7</b> Oct 1, 2022</li>
    <ul>
    <li>fix bugs in setup workflows
     <li>function to upload FTDNA STR and SNP data</li>
   </uL>
<li><b>v 1.1.5</b> Sept 29, 2022</li>
 <ul>
    <li>Y-DNA enhancements</li>
        <ul>
             <li>function to computer Y-haplogroups of patrilineal paths in a family tree</li>
            <li>Function to list all Y-haplogroups in a project</li>
            <li>improved report on haplogroups and matches on a clade and its branches. Includes visualization query for Neo4j Browser</li>
        </ul>
 
 </ul>

<li><b>v 1.1.4</b> Sept 14, 2022
<ul>
    <li>adjustments to accomodate new menu items in GFG software v 1.2.4</li>
</ul>
</li>

<li><b>v 1.1.3</b> Sept 13, 2022
    <ul>
        <li>added inferred segments for avatars</li>
        <li>added function to distinguish aunt/uncles from nibling. Used in inferred segments; aunt/uncles cannot be used.</li>
        <li>added function to get the full path to the Neo4j dbms folder</li>
    </ul>
</li>

<li><b>v 1.1.0</b>  August 20, 2022</li>
    <ul>
        <li>reorganized file structure to improve usability</li> 
        <li>simlified Neo4j setup using code to create folders with required file and configuring plugins and Neo4j behavior</li>
        <li>added chromosome browser function which is used in a few current functions; more will be added in next release.</li> 
        <li>added avatars: virtual in silico ancestors and relatives</li> 
            <ul>
                <li>augmented monophyletic segment discovery</li>
                <li>creation of avatars (virtual ancestors and relatives)</li>
                <li>populate avatars with reconstructed DNA monophyletic segments</li>
                <li>merged  segments and calculating their cm using a modified HapMap</li>
                <li>DNA matches of avatars to real world DNA testers</li>
                <li>avatar reports on segments, matches, etc.</li>
            </ul>
         <li>implemented modified HapMap to speed segment cM calculations; automatically uploaded to user Neo4j database with other reference data</li> 
         <li>simplified data loading into single functions that call specific loading functions in sequence</li> 
         <li>added half double cousin report to the double cousin report</li> 
      </ul>
<li><b>v 1.0.17</b>. July 11, 2022</li>
   <ul>
   <li>Added monophyletic segment report, including their finding of new matches in a family line.</li>
   <li>Added Y-DNA descendancy haplotree with Y-matches.</li>
   <li>added capability to find matches to surnames in a person's direct family tree lines.</li>
   <li>added ancestor descendant monophyletic segment searches for new matches.</li>
   <li>fixed x_gen_dist which was missin a few relationships.</li>
   <li>added Y- and mt-HG to ancestral surname report</li>
   <li>new function to add parental side of p and m to match_segment relationship</li>
   <li>New function to report parental origin of segments: paternal, maternal or unknown; some generations done, but still in development.</li>
    </ul>
  <li><b>v 1.0.16</b>. July 11, 2022</li>
   <ul>
   <li>Added Leiden community detection algorithm, which is an improven on the Louvain algorithm. It provides reliable generation of intermediary communities.</li>
   <li>Enhance DNA coverage algorithm. </li>
   <li>Fix to ancestor reconstruction algorithm's generation of a DNA Painter file.</li>
    <li>Update degree centrality to conform to Neo4j Graph Data Science updates</li>
   <li>Fix in surname search function.</li>
</ul>
    <li><b>v 1.0.15</b>. Added function to add DNA testers whose DNA is not in the project. Supported by new Person property at_DNA_tester.</li>
   <li><b>v 1.0.14</b>. Added enforcing creation of the HapMap before loading other data. Added a new function for computing DNA coverage for any ancestor who has descendant DNA testers. </li>
  <li><b>v 1.0.11</b>. New reports identifying dual matches for both autosomal and mitochondrial DNA. Robust triangulation report have been improved. Also upgrade to <a href="https://www.wai.md/product-page/gfg-software">GFG software v 1.0.2</a> to capitalize on its menu driven access to new reporting capabilities.</li>
<li><b>v 1.0.10</b>. Restored match_segment relationship properties describing the relationship between the match-pair including their relationship (1C, H3C, etc), the correlation of relationship (cor), an the common ancestor. Added a new function -- gen.dna.shared_mt_haplogroup -- to find matches who are both at- and mt-DNA matches. </li>
  
</ol> 
  

