/**
 * Copyright 2022-2023
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.load;
    import gen.conn.connTest;
    import gen.neo4jlib.neo4j_info;
    import gen.gedcom.upload_gedcom;
    import gen.dna.load_ftdna_files;
    import gen.tgs.load_tgs_from_template;
    import org.neo4j.procedure.Description;
    import org.neo4j.procedure.Name;
    import org.neo4j.procedure.UserFunction;

public class load_all_files {
    @UserFunction
    @Description("Loads GEDCOM, FTDNA and curated files (GEDCOM-DNA links; triangulation groups) using a lookup file for locations of files. This function calls on several others in the PlugIn, enabling a simplier one-step loading process.")

    public String load_everything(
        @Name("anc_rn") 
            Long anc_rn

    )   
        { 
        String s = load_files(anc_rn);
                return s;
        }
        
        public static void main(String args[]) {
            load_files(1L);
    }
        
    public static String load_files(Long anc_rn) {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();  //initialize variables
        
        ;
        System.out.println("Load GEDCOM");
        upload_gedcom g = new upload_gedcom();
        g.load_gedcom();
        
        
         ;
        System.out.println("Load FTDNA");
        load_ftdna_files f = new load_ftdna_files();
        f.load_ftdna_files();
        
//        try{
//        load_tgs_from_template t = new load_tgs_from_template();
//        t.load_tgs_from_csv();
//        }
//        catch(Exception e){}
//        
//        add x_gen_dist property

        System.out.println("Load FTDNA enhancements");
        load_ftdna_enhancements fe = new load_ftdna_enhancements();
        fe.add_match_segment_properties();
        
        //add cumulative snp to mt-haplotree
        //gen.dna.process_mt_haplotree mt = new gen.dna.process_mt_haplotree();
        //mt.mt_haplotree_cumulative_snps();
  
        
        //gen.endogamy.path_enhancements.add_enhancements(); from endogamy2
        System.out.println("Endogamy knowledge graph");
         gen.endogamy.endogamy_knowledge eke = new gen.endogamy.endogamy_knowledge();
        eke.endogamy_knowledge_graph();
    
  
         System.out.println("Enhancements");
        //create avatars requirements
        try{
        gen.avatar.targeted_anc_enhance tge = new gen.avatar.targeted_anc_enhance();
        tge.add_enhancements(anc_rn);
            
//        gen.avatar.create_avatars cav = new gen.avatar.create_avatars();
//        cav.create_avatar_relatives(anc_rn);
        }
        catch(Exception e){}
        
        System.out.println("Paths");         
        gen.endogamy.path_enhancements penh = new gen.endogamy.path_enhancements();
        penh.create_path_enhancements();
        
   
        
        return "Completed";
    }
}
