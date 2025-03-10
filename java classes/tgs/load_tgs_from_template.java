/**
 * Copyright 2021-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tgs;
import gen.neo4jlib.neo4j_qry;
import gen.neo4jlib.file_lib;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

public class load_tgs_from_template {
    @UserFunction
    @Description("Loads curated triangulation group csv file to Neo4j and creates TG nodes and then edges from them to segments, matches and persons. Deletes prior tg nodes and edges before doing so. File path is specified in user's curated private file")

  public String load_curated_tg_file(
        //@Name("CSVFilePath") 
        //    String CSVFilePath
  )
    {
        
        { 
        
        String s = load_tgs_from_csv();
        return s;        
            }

    }

  
    public static void main(String args[]) {
        //gen.conn.connTest.cstatus();
       load_tgs_from_csv();
    }
    
    public static String load_tgs_from_csv() {
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.conn.connTest.cstatus();
        String csvFile ="";
        if(gen.neo4jlib.neo4j_info.tg_file.equals("")){return "";}
        
        csvFile =  gen.neo4jlib.neo4j_info.tg_file;
       gen.neo4jlib.file_lib.parse_chr_containing_csv_save_to_import_folder(csvFile,3);
        
        neo4j_qry.qry_write("match ()-[r]-(t:tg) delete r");
         neo4j_qry.qry_write("match (t:tg)-[r]-() delete r");
        neo4j_qry.qry_write("match (t:tg) delete t");
        
        //delete any existing tg edges to avoid duplications
        neo4j_qry.CreateIndex("tg", "tgid");
        neo4j_qry.CreateIndex("tg", "chr");
        neo4j_qry.CreateIndex("tg", "Indx");
         neo4j_qry.CreateIndex("tg", "strt_pos");
        neo4j_qry.CreateIndex("tg", "end_pos");
        neo4j_qry.CreateIndex("tg", "cm");
        
        neo4j_qry.CreateRelationshipIndex("match_tg", "tgid");
        neo4j_qry.CreateRelationshipIndex("match_tg", "p");
        neo4j_qry.CreateRelationshipIndex("match_tg", "m");
        neo4j_qry.CreateRelationshipIndex("match_tg", "min_cm");
        neo4j_qry.CreateRelationshipIndex("match_tg", "p_rn");
        neo4j_qry.CreateRelationshipIndex("match_tg", "m_rn");
        neo4j_qry.CreateRelationshipIndex("match_tg", "p_anc_rn");
        neo4j_qry.CreateRelationshipIndex("match_tg", "m_anc_rn");
        neo4j_qry.CreateRelationshipIndex("tg_seg", "tgid");
        neo4j_qry.CreateRelationshipIndex("person_tg", "tgid");
 
        //create tg nodes
        String fn =gen.neo4jlib.file_lib.getFileNameFromPath(gen.neo4jlib.neo4j_info.tg_file);
        String cq = "LOAD CSV WITH HEADERS FROM 'file:///" + fn + "' AS line FIELDTERMINATOR '|' merge (t:tg{tgid:toInteger(line.tgid),Indx:toString(case when line.chr is null then '' else line.chr end) + ':' + toString(case when line.strt_pos is null then 0 else line.strt_pos end) + ':' + toString(case when line.end_pos is null then 0 else line.end_pos end) ,chr:toString(line.chr),strt_pos:toInteger(line.strt_pos),end_pos:toInteger(line.end_pos),project:toString(line.project),cm:toFloat(case when line.cm is null then 0.0 else line.cm end), mrca_rn:toInteger(line.mrca_rn)})";
        neo4j_qry.qry_write(cq);
  
        neo4j_qry.qry_write("match (t:tg) where size(t.chr)=1 set t.chr=apoc.text.lpad(t.chr,2,'0')");
        
        //add tg cm property if it is not already populated
        neo4j_qry.qry_write("match (t:tg) where t.cm is null with t,gen.dna.hapmap_cm(t.chr,t.strt_pos,t.end_pos) as cm set t.cm = cm");
        
        
        
        //add tg_seg edge
        cq = "match (t:tg) with t match (s:Segment) where " + gen.neo4jlib.neo4j_info.tg_logic_overlap + " merge (t)-[r:tg_seg{tgid:t.tgid}]-(s)";
        neo4j_qry.qry_write(cq);

        //add match_tg edge
        cq = "match (t:tg)-[:tg_seg]-(s:Segment)-[r:match_segment]-(m:DNA_Match) where (r.p=m.fullname or r.m=m.fullname) and r.cm>=7 and r.snp_ct>=500 with t,m,min(r.cm) as min_cm,max(r.cm) as max_cm,min(r.snp_ct) as min_snp,max(r.snp_ct) as max_snp,count(r) as seg_ct, r.m as rm , r.p as rp, r.p_rn as p_rn,r.m_rn as m_rn,r.p_anc_rn as p_anc_rn,r.m_anc_rn as m_anc_rn,collect(s.Indx) as sc with m,t,t.tgid as tgid,rp as proband, p_rn, p_anc_rn,rm,m_rn, m_anc_rn, min_cm, max_cm, min_snp, max_snp, seg_ct, sc as match merge (m)-[r1:match_tg{tgid:tgid, p:proband,m:rm, min_cm:min_cm, max_cm:max_cm, min_snp:min_snp, max_snp:max_snp, seg_ct:seg_ct}]-(t)";
         neo4j_qry.qry_write(cq);

         
        //add properties to tg_match edge analogous to match_segment edge
        

         cq = "MATCH p=(m:DNA_Match)-[r:match_tg]->(t:tg) where r.p=m.fullname and m.RN is not null set r.p_rn=m.RN";
         neo4j_qry.qry_write(cq);

         cq = "MATCH p=(m:DNA_Match)-[r:match_tg]->() with r match (m2:DNA_Match) where m2.fullname=r.m set r.m_rn=m2.RN ";
         neo4j_qry.qry_write(cq);

        
        //add person_tg edge
        cq = "MATCH p=(m1:DNA_Match)-[r:match_tg]-(t:tg) where m1.RN is not null with distinct m1.RN as rn,t match (p:Person{RN:rn}) match (t2:tg{tgid:t.tgid}) merge (p)-[rt:person_tg{tgid:t.tgid}]-(t2)";
        neo4j_qry.qry_write(cq);

        //give tg a name
        cq = "match (t:tg) with t, toString(t.strt_pos/1000000) as mbs, toString(t.end_pos/1000000) as mbe with t,t.tgid as tgid, t.chr + '-' + apoc.text.lpad(mbs,3,'0') + \"-\" + apoc.text.lpad(mbe,3,'0') as name set t.name=name";
        neo4j_qry.qry_write(cq);
        
        return "Completed tg upload and created edges of tgs to segments, matches and persons.";
    }
}
