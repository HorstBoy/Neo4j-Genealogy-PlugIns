/**
 * Copyright 2021-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.ref;

import gen.load.web_file_to_import_folder;
import gen.neo4jlib.neo4j_info;
import gen.neo4jlib.neo4j_qry;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.UserFunction;

/**
 *
 * @author david
 */
public class upload_mt_haplotree {
       @UserFunction
       @Description("Loads the entire mt-haplotree directly from the current FTDNA mt-DNA json refernce file into Neo4j. This json is updated frequently as new variants and haplotree branches are discovered. Source: https://www.familytreedna.com/public/mt-dna-haplotree/get")

     public String upload_FTDNA_mt_haplotree() {
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        //delete prior haplotree data
        gen.neo4jlib.neo4j_qry.qry_write("match ()-[r:mt_block_child]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match ()-[r:mt_block_variant]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match (b:mt_block)-[r]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match (b:mt_block) delete b");
        gen.neo4jlib.neo4j_qry.qry_write("match (v:mt_variant) delete v");
        
        String FileNm = gen.neo4jlib.neo4j_info.Import_Dir + "mt_haplotree.json";
        
        //retrieve online FTDNA Y-haplotree json and place in import directory.
        web_file_to_import_folder.url_file_to_import_dir("https://www.familytreedna.com/public/mt-dna-haplotree/get","mt_haplotree.json");
          
        neo4j_qry.CreateIndex("mt_block", "haplogroupId");
        neo4j_qry.CreateIndex("mt_block", "name");
        neo4j_qry.CreateIndex("mt_variant", "name");
        try{
        neo4j_qry.CreateIndex("DNA_mtMatch", "mtHG");
        neo4j_qry.CreateIndex("DNA_mtMatch", "fullname");
        neo4j_qry.CreateIndex("DNA_Match", "mtHG");
        
        }
        catch (Exception e){}

        try{
            neo4j_qry.CreateCompositeIndex("mt_block","haplogroupId,name,parentId,IsRoot");
            neo4j_qry.CreateCompositeIndex("mt_variant","name,pos,anc,der,region");
        
        }
       catch (Exception e){}
        
        //read, parse  and load json into Neo4j
        File file = new File(FileNm);
        String fileContents="";
 
        try (FileInputStream inputStream = new FileInputStream(file))
        {
            fileContents = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
            
        //read json
        JSONObject jo = new JSONObject(fileContents).getJSONObject("allNodes");
        int jl = jo.length();
        String fny =neo4j_info.Import_Dir + "mt_HT.csv";
        String fnv =neo4j_info.Import_Dir + "mt_HT_variants.csv";
        File fy = new File(fny);
        File fv = new File(fnv);

       try{

        FileWriter fwy = new FileWriter(fy);
        fwy.write("haplogroupId|name|parentId|IsRoot\n");
        FileWriter fwv = new FileWriter(fv);
        fwv.write("variant_name|haplogroupId|pos|anc|der|region\n");
 
        String var = "";
        String rg = "";
        String hid = "";
        String pos = "";
        String anc = "";
        String der = "";

        JSONArray keys = jo.names();
        
        for (int i=0; i< keys.length(); i++) {
            String key = keys.getString (i); 
            JSONObject jo2 = jo.getJSONObject(key);
            try{
                fwy.write(jo2.get("haplogroupId") + "|" + jo2.get("name") + "|" + jo2.get("parentId") + "|" + jo2.get("isRoot") + "\n");
             }
            catch (Exception e)  //no parentId
            {
          }
            
            JSONArray ja3 = jo2.getJSONArray("variants");
            
            for (int k=0; k<ja3.length() ; k++) {
                //fourth level parse
                JSONObject ja4 = ja3.getJSONObject(k);
                try { var = ja4.get("variant") + "|";}
                catch (Exception e) {var = "|";}
                try { hid = jo2.get("haplogroupId") + "|";}
                catch (Exception e) {hid = "0|";}
                try { pos = ja4.get("position") + "|";}
                catch (Exception e) {pos = "0|";}
                try { anc = ja4.get("ancestral") + "|";}
                catch (Exception e) {anc = "|";}
                try { der = ja4.get("derived") + "|";}
                catch (Exception e) {der = "|";}
                 try { rg = ja4.get("region") + "|";}
                catch (Exception e) {rg = "0|";}
                try {
                    fwv.write(var + hid + pos + anc + der + rg +"\n");
                } 
                catch (IOException ex) {
                    //Logger.getLogger(upload_Y_DNA_Haplotree.class.getName()).log(Level.SEVERE, null, ex);
                }
              }  
         
       
        }  
            fwy.flush();
            fwy.close();
            fwv.flush();
            fwv.close();
      
            }
         catch (Exception e) {System.out.println(e.getMessage());}
    
       //Load csv to Neo4j
       String lc = "LOAD CSV WITH HEADERS FROM 'file:///mt_HT.csv' as line FIELDTERMINATOR '|' return line ";
 
       String cq = "merge (b:mt_block{haplogroupId:toInteger(line.haplogroupId),name:toString(line.name),parentId:toInteger(line.parentId),IsRoot:toBoolean(line.IsRoot)})";
       neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 200000);
       
       lc = "LOAD CSV WITH HEADERS FROM 'file:///mt_HT_variants.csv' as line FIELDTERMINATOR '|' return line ";
       cq = "merge (v:mt_variant{name:toString(case when line.variant_name is null then '' else line.variant_name end),pos:toInteger(line.pos),anc:toString(case when line.anc is null then '' else line.anc end),der:toString(case when line.der is null then '' else line.der end),region:toString(case when line.region is null then '' else line.region end)})";
       neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 200000);
       
       
      cq = "match (b:mt_block{haplogroupId:toInteger(line.haplogroupId)}) match (v:mt_variant{name:toString(line.variant_name)}) merge (b)-[r:mt_block_variant]->(v)";
      neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 200000);

       gen.neo4jlib.neo4j_qry.qry_write("MATCH (b1:mt_block) with b1 match (b2:mt_block) where b2.haplogroupId=b1.parentId merge (b2)-[r:mt_block_child]-(b1)");
       
       //Ymatch-block edge
       gen.neo4jlib.neo4j_qry.qry_write("match (y:DNA_mtMatch) where trim(y.mtHG)>' ' with y match (b:mt_block) where b.name=y.mtHG merge (y)-[r:mt_match_block]-(b)");
       
       //match_block
       gen.neo4jlib.neo4j_qry.qry_write("MATCH (m:DNA_Match) where m.mtHG is not null with m match (b:mt_block) where b.name=m.mtHG merge (m)-[mb:mt_match_block]->(b)");
       
       return "Completed";
    }
        
}