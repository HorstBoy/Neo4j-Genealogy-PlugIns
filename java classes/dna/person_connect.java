/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class person_connect {
    @UserFunction
    @Description("Creates a summary for a specific match.")

    public String person_connect_clues(
        @Name("fullname") 
            String fullname
  )
   
         { 
             
        String s =  get_connections(fullname);
         return s;
            }

    
    
    public static void main(String args[]) {
        //get_connections("Sheila K. Evans");
        get_connections("Charles Logan");
    }
    
     public static String get_connections(String fullname) 
    {
        String surname = gen.neo4jlib.neo4j_qry.qry_to_csv("MATCH (n:DNA_Match{fullname:'" + fullname + "'}) RETURN n.surname as surname LIMIT 1").split("\n")[0];
        surname = surname.replace("\"", "");
      
     
        //matches by segment -- the segments
        String cq = "match (m:DNA_Match{fullname:'" + fullname + "'})-[[rs:match_segment]]-(s:Segment) with s,m.fullname as name,rs.p as proband,rs.m as match order by s.Indx with name,proband,match,collect(distinct s.Indx) as segs return proband,match,size(segs) as shared_seg_ct,segs";
                //"match (m:DNA_Match{fullname:'" + fullname + "'})-[[rs:match_segment]]-() with m.fullname as name,rs.p as proband,rs.m as match return name,proband,match";
        int ct = 1;
        String excelFile = gen.excelLib.queries_to_excel.qry_to_excel(cq, fullname + "_explorer", "matches at segments", ct, "", "2:#####;3:#####", "",false,"Cypher query:\n" + cq + "\n\nProband kits with DNA that matches to the report subject with shared segments", false);
        ct= ct +1;
        
        //shared cM
        cq = "match (m:DNA_Match{fullname:'" + fullname + "'})-[[rs:match_by_segment]]-(mm:DNA_Match) with m.fullname as proband, mm.fullname as match,rs.cm as shared_cm,rs.x_cm as x_cm,rs.rel as relationship return proband,match,shared_cm,x_cm,relationship order by shared_cm desc";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "mbs", "shared_cM", ct, "", "2:####.#;3:####.#", excelFile,false, "Cypher query:\n" + cq +"\n\nKits which share segments with (match to) the report subject with their shared cM\nIf there is a relationship between them in the GEDCOM, it is shown.", false);
        ct = ct + 1;
        
        //avatar matches with MSegs
        cq = "with '" + fullname + "' as fn MATCH p=(d:DNA_Match)-[r1:match_segment]->(s1:Segment) where r1.p=fn or r1.m=fn match (s2:Segment)<-[rv:avatar_segment]-(a:Avatar) where ((rv.p=r1.m or rv.p=r1.p) or (rv.m=r1.m or rv.m=r1.p)) and s1.chr=s2.chr and s1.strt_pos<s2.end_pos and s1.end_pos>s2.strt_pos with fn as discovered_match,a.fullname as avatar,apoc.coll.sort(collect (distinct s2.Indx)) as segs return discovered_match,avatar, size(segs) as MSeg_ct, segs as avatar_MSegs order by MSeg_ct desc";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "mbs", "avatar_MSegs", ct, "", "2:####.#;3:####.#", excelFile,false, "Cypher query:\n" + cq +"\n\nKits which share segments with available avatars.", false);
        ct = ct + 1;
       
        cq = "with '" + fullname + "' as fn MATCH p=(d:DNA_Match)-[r1:match_segment]->(s1:Segment) where r1.p=fn or r1.m=fn match (s2:CSeg)<-[rv:avatar_cseg]-(a:Avatar) where s1.chr=s2.chr and s1.strt_pos<s2.end_pos and s1.end_pos>s2.strt_pos with fn as discovered_match,a.fullname as avatar,apoc.coll.sort(collect (distinct s2.Indx)) as segs with discovered_match,avatar, size(segs) as seg_ct, segs as avatar_CSegs match (s3:CSeg) where s3.Indx in avatar_CSegs return discovered_match,avatar, seg_ct, sum(s3.cm) as CSeg_cM, avatar_CSegs order by CSeg_cM";
             gen.excelLib.queries_to_excel.qry_to_excel(cq, "mbs", "avatar_CSegs", ct, "", "2:####.#;3:####.#", excelFile,false, "Cypher query:\n" + cq +"\n\nKits which share segments with available avatars.", false);
        ct = ct + 1;
    
        
        cq = "match (m:DNA_Match{fullname:'" + fullname + "'})-[rs:shared_match]-(mm:DNA_Match) with m.fullname as proband, mm.fullname as match,rs.cm as shared_cm,rs.x_cm as x_cm,rs.rel as relationship,rs.pair_mrca as mrcas,rs.x_cm as gfg_x_cm,rs.x_gen_dist as gfg_x_gen_dist return proband,match,shared_cm,case when x_cm is null then 0 else x_cm end as x_cm,relationship,mrcas";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "shared_matches", "shared_matches", ct, "", "2:####.#;3:####.#", excelFile,false, "Cypher query:\n" + cq + "\n\nShared matches as reported by FTDNA.\nFTDNA X-matches may differ from those reported by GFG for at least two reasons:\n   1. the match does not meet their criteria while it does for GFG\n  2. FTDNA may report an incorrect X-match while GFG uses the family tree to recognize that an X-match is not tenable given the graph topology.\n\nSee the previuos tab to see the GFG X-match data.", false);
        ct = ct + 1;
        
        cq = "MATCH p=(mss:MSS)-[[r:ms_seg]]->(s:Segment)-[[rs:match_segment]]-(m:DNA_Match{fullname:'" + fullname + "'}) with s,s.Indx as Indx, collect(distinct r.mrca) as mrca_rn,collect(distinct mss.fullname) as mrca_name, case when s.phased_anc is null then '~' else s.phased_anc end as phased_anc ,collect(distinct m.fullname) as fn return Indx,mrca_rn,mrca_name, phased_anc,fn order by s.chr,s.strt_pos,s.end_pos";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "sements", "monophylrtic segment detail", ct, "", "2:####.#;3:####.#", excelFile,false, "Cypher query:\n" + cq + "\n\nMonophyletic seqment with the computed most  recent common ancestor(s).", false);
        ct = ct + 1;
        
       cq = "match (m:DNA_Match{fullname:'" + fullname + "'})-[[rs:match_segment]]-(s:Segment)-[[rm:ms_seg]]-(mss:MSS) with m,rs,s,mss order by s.Indx with m.fullname as name,rs.p as proband,rs.m as match,mss.fullname as monophylytic_ancestor,collect(distinct s.Indx) as segs return monophylytic_ancestor,name,proband,match,segs";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "person_explorer", "monophyletic group", ct, "", "", excelFile,false, "Cypher query:\n" +cq + "\n\nMonophytic ancestors with the defining segments and matches\nThese data can help define triangulation groups.", false);
        ct = ct + 1;
        
       cq = "CALL db.index.fulltext.queryNodes('ancestor_surnames_names', '" + surname + "') YIELD node, score WITH score,node.p as match,node.m as match_with_surnames,case when size(node.name)>200 then left(node.name,200) + ' (truncated)' else node.name end as anc_names MATCH (m:DNA_Match{fullname:match})-[rs:match_by_segment]]-(m2:DNA_Match{fullname:match_with_surnames}) return distinct m.fullname as source,case when m.RN is null then '~' else toString(m.RN) end  as source_rn, match_with_surnames,rs.cm as cm,rs.seg_ct as segs,case when rs.rel is null then '.' else rs.rel end as rel,round(score,2) as score,anc_names as ancestor_list order by rel desc,score desc,source";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "anc_surname", "ancestral_surnames", ct, "", "1:#####;3:####.#;4:####;6:###.###", excelFile,false, "Cypher query:\n" + cq +"\n\nThe surname of this report's subject is used to locate in each DNA Kit any of the kit matches who have this suname in their submitted surname list", false);
        ct = ct + 1;
 
        if(gen.neo4jlib.neo4j_info.tg_file!=""){
        cq = "match (m:DNA_Match{fullname:'" + fullname + "'})-[rs:match_tg]-(t:tg) with t,m.fullname as proband,rs.p as match_proband, rs.m as match,rs.seg_ct as seg_ct,count(*) as ct order by t.name with proband,match_proband,match,seg_ct,ct,collect(distinct t.name) as tg,collect(distinct t.Indx) as segs return proband,match_proband,match,size(tg) as tg_ct, tg as tgs,size(segs) as seg_ct,segs";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "tgs", "tgs", ct, "", "3:###.#;3:###.#", excelFile,false,"Cypher query:\n" + cq +"\n\nSummary of triangulation group for the subject of the report.\nThis will not be populated if there is no curated triangulation group file or if their are not matches to the submitted tgs.",false);
        ct = ct + 1;
    }
       cq = "MATCH p=(m1:DNA_Match)-[[r:match_by_segment]]-(m2:DNA_Match) where m1.RN is not null and m2.surname='" + surname + "' and m1<>m2 RETURN m1.fullname as proband, m2.fullname as source_match,r.cm as shared_cm,r.rel as rel  order by shared_cm desc ";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "surname_matches", "surname_matches", ct, "", "2:####.#;2:###.#", excelFile,true, "Cypher query:\n" + cq + "\n\nThis report starts with the project kits (column A) and seeks their matches (column B) with the surnme of the report subject's matches\nThere is no assurance that these mathes are relevant.", false);
        ct = ct + 1;
 
        
        return "person report completed";
    }
}
