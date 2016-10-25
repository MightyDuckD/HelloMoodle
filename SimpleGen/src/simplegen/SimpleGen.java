/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplegen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Simon
 */
public class SimpleGen {

    private static String panel(int id,Kurs k) {
        StringBuilder builder = new StringBuilder();
        builder.append("<div class=\"panel panel-primary\">");
        // Header
        builder.append("<div class=\"panel-heading\">");
        builder.append("<h4  data-toggle=\"collapse\" href=\"#collapse").append(id).append("\"   class=\"panel-title clickable\">");
        builder.append("<a>").append(k.kurs).append("</a>");
        
        builder.append("<a target=\"_blank\" href=\"").append(k.link).append("\" ><span style=\"float: right;\" class=\"glyphicon glyphicon-link\" aria-hidden=\"false\"></span></a>");
        builder.append("</h4>");
        builder.append("</div>");
        // Body
        builder.append("<div id=\"collapse").append(id).append("\" class=\"panel-collapse collapse\">");
        builder.append("<div class=\"panel-body\">").append(genfile(k)).append("</div>\n");
        builder.append("<div class=\"panel-footer\">").append(k.lehrer).append("</div>");
        builder.append("</div>");
        // End        
        builder.append("</div>");
        return builder.toString();
    }

    private static String generiere(Root root) {
        StringBuilder builder = new StringBuilder();
        int id = 1;
        
        builder.append("<div id=\"maingroup\"class=\"well panel-group\">");
        for(Kurs k : root.kurse) {
            builder.append(panel(id++,k));
        }
        builder.append("</div>");

//        builder.append("<table class=\"table table-condensed table-hover\">");
//        for(Kurs k : root.kurse) {
//            builder.append("<tr>");
//            builder.append("<td>").append(k.kurs).append("</td>");
//            builder.append("<td>").append(k.lehrer).append("</td>");
//            builder.append("</tr>");
//        }
//        builder.append("</table>");
        return builder.toString();
    }

    private static String genfile(Kurs k) {
        StringBuilder builder = new StringBuilder();
        
        builder.append("<table class=\"table table-condensed table-hover\">");
        for(Datei d : k.dateien) {
            builder.append("<tr>");
            builder.append("<td class=\"clickable\" onclick=\"window.open('").append(d.link).append("')\">").append(d.name).append("</td>");
            builder.append("</tr>");
        }
        builder.append("</table>");
            
        
        return builder.toString();
    }

    public static class Root {

        private List<Kurs> kurse = new ArrayList<>();

        public List<Kurs> getKurse() {
            return kurse;
        }

    }

    public static class Kurs {

        private String lehrer;
        private String kurs;
        private String link;
        private String color = "primary";
        private List<Datei> dateien = new ArrayList<>();

        public Kurs(String lehrer, String kurs) {
            this.lehrer = lehrer;
            this.kurs = kurs;
            this.link = "";
        }

    }

    public static class Datei {

        private String name, link;

        public Datei(String name, String link) {
            this.name = name;
            this.link = link;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));

        File file = new File("gen.json");
        File outfile = new File("gen.html");
        File tempfile = new File("template.html");
        FileReader reader = new FileReader(file);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Root root = gson.fromJson(reader, Root.class);
        reader.close();

        for (Kurs k : root.getKurse()) {
            System.out.println("Kurs     : " + k.kurs);
            System.out.println("Lehrkraft: " + k.lehrer);
            for (Datei d : k.dateien) {
                System.out.println("   " + d.name + " " + d.link);
            }
        }

//        System.out.println("Soll generiert werden? (Y/n)");
//        if (!"y".equalsIgnoreCase(sysin.readLine())) {
//            return;
//        }

        BufferedReader template = new BufferedReader(new InputStreamReader(new FileInputStream(tempfile), "UTF-8"));
        String line, pre, post, gen;
        StringBuilder full = new StringBuilder();
        while ((line = template.readLine()) != null) {
            full.append(line).append("\n");
        }
        String tag = "<INSERT-HERE>";
        int index = full.indexOf(tag);
        pre = full.substring(0, index);
        post = full.substring(index + tag.length());
        gen = generiere(root);
        System.out.println(gen);
        
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile),"UTF-8"));
        writer.append(pre);
        writer.append(gen);
        writer.append(post);
        writer.close();
//        Root test = new Root();
//        test.getKurse().add(new Kurs("Kronawetter","SYP 5 Theorie"));
//        test.getKurse().get(0).dateien.add(new Datei("01 Einführung","http://moodle.htlstp.ac.at/moodle/mod/resource/view.php?id=13816"));
//        System.out.println(gson.toJson(test));
    }

}
