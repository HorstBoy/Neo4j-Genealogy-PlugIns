/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.ref;

import gen.neo4jlib.neo4j_info;
import gen.neo4jlib.neo4j_qry;
import java.io.File;
import java.io.FileWriter;
import java.util.regex.Pattern;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class mt_load_genbank {
    @UserFunction
    @Description("Load GenBank data frm Ian Logan webpages.")

    public String genbank_load(
        @Name("filepath") 
            String filepath  )
   
         { 
             
        load_genbank(filepath);
         return "";
            }

    
    
    public static void main(String args[]) {
        load_genbank("logan_mt_hg_snps1.csv");
    }
    
     public static String load_genbank(String fn) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        
        String fny =neo4j_info.Import_Dir + "genbank_hg_snp.csv";
        File fy = new File(fny);
        FileWriter fwy=null;
       try{

        fwy = new FileWriter(fy);
        fwy.write("hg|snp\n");
       }
       catch(Exception e){}

       
        //mt_block, add if new hg and set genbank=1 property for all
        //gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///logan_mt_hg_snps1.csv' as line FIELDTERMINATOR '|' merge (b:mt_block{name:line.hg}) set b.genbank=1");
        
        
        //get distinct snp patterns  n= 5029
        String c[] = gen.neo4jlib.neo4j_qry.qry_to_csv("LOAD CSV WITH HEADERS FROM 'file:///logan_mt_hg_snps1.csv' as line FIELDTERMINATOR '|' return count(*) as ct, line.hg, size(line.snps) as snps_ct, line.snps").split("\n");
        
        for (int i=0; i<c.length; i++)
        {
            String s[] = c[i].split(Pattern.quote(","));
            for (int j=3; j<s.length; j++)
            {
                try
                {
                    fwy.write(s[1].strip().replace("\"", "") + "|" + s[j].strip().replace("\"", "") + "\n");
                }
                catch (Exception e){}
            }
        }
        
        try{
        fwy.flush();
        fwy.close();
        }
        catch(Exception e){}
        
        //add any new snps and to all add property genbank=1
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///genbank_hg_snp.csv' as line FIELDTERMINATOR '|' with line.snp as snp,count(*) as ct with snp,ct where 'T'>=left(snp,1)>='A' and not snp in ['Taylor','Thr..'] merge (v:mt_variant{name:snp}) set v.genbank=1");
        
        gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///genbank_hg_snp.csv' as line FIELDTERMINATOR '|' match (b:mt_block{name:line.hg}) match(v:mt_variant{name:line.snp}) merge(b)-[r:mt_block_snp]->(v) set r.genbank=1");
        
        
        return "";
    }
}
