package RModel;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public class Read_Write_Data {
    //create a file with name of relation and the first row is a list of attributes
    public void createRelation(Relation r) {
        try {
            String pathName = "RModel/Relations/" + r.getName() + ".txt";
            File relation = new File(pathName);
            if (relation.createNewFile()) {
                System.out.println("Relation " + relation.getName() + " created.");
                FileWriter fw = new FileWriter(pathName);
                for(int i = 0; i < r.getAttributes().size()-1; i++) {
                    fw.write(r.getAttributes().get(i).getName() + ", ");
                }
                fw.write(r.getAttributes().get(r.getAttributes().size()-1).getName() + "\n");
                fw.close();
            } else {
                System.out.println("Relation " + relation.getName() + " is already existed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //write a list of tuples into relation
    public void writeTuplesToRelation(Relation r, ArrayList<Tuple> tuples) {
        String pathName = "RModel/Relations/" + r.getName() + ".txt";
        try {
            FileWriter fw = new FileWriter(pathName, true);
            String[] attributes = Files.readAllLines(Path.of(pathName)).get(0).split(",");
            for (Tuple t : tuples) {
                for(String attrName : attributes) {
                    if(attrName.equals(attributes[attributes.length-1]))
                        fw.write(t.getAttribute(attrName.trim()).toString() + System.getProperty("line.separator"));
                    else
                        fw.write(t.getAttribute(attrName.trim()).toString() + ", ");
                }
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //write a tuple into relation
    public void writeTuplesToRelation(Relation r, Tuple t) {
        String pathName = "RModel/Relations/" + r.getName() + ".txt";

        try {
            FileWriter fw = new FileWriter(pathName, true);
            String[] attributes = Files.readAllLines(Path.of(pathName)).get(0).split(",");
            for(String attrName : attributes) {
                if(attrName.equals(attributes[attributes.length-1]))
                    fw.write(t.getAttribute(attrName.trim()).toString() + System.getProperty("line.separator"));
                else
                    fw.write(t.getAttribute(attrName.trim()).toString() + ", ");
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //delete tuple from relation
    public void deleteTupleFromRelation(Relation r, Tuple tuple) {
        String lineToDelete = "";
        for(Object o: tuple.getAttributeValues()) {
            lineToDelete += o.toString() + ", ";
        }
        lineToDelete = lineToDelete.substring(0, lineToDelete.length()-2);
        String pathName = "RModel/Relations/" + r.getName() + ".txt";
        File tmp = new File(pathName + ".tmp");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(pathName));
            BufferedWriter write = new BufferedWriter(new FileWriter(tmp));
            String currentLine;
            while((currentLine = reader.readLine()) != null) {
                if(currentLine.equals(lineToDelete)) continue;
                else {
                    write.write(currentLine + System.getProperty("line.separator"));
                }
            }
            write.close();
            reader.close();
            if(new File(pathName).delete()) {
                tmp.renameTo(new File(pathName));
                System.out.println("Delted tuple " + tuple.getTupleValues() + "successfully.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteRelation(Relation r) {
        String pathName = "RModel/Relations/" + r.getName() + ".txt";
        return new File(pathName).delete();
    }

    public void readAllTuplesOfRelation(Relation r) {
        String pathName = "RModel/Relations/" + r.getName() + ".txt";
        try {
            ArrayList<String> allLInes = new ArrayList<String>(Files.readAllLines(Path.of(pathName)));
            for(String s : allLInes)
                System.out.println(s);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        File tmp = new File(pathName + ".tmp");
//        try {
//            BufferedReader reader = new BufferedReader(new FileReader(pathName));
//            BufferedWriter write = new BufferedWriter(new FileWriter(tmp));
//            String currentLine;
//            while((currentLine = reader.readLine()) != null) {
//                if(currentLine.equals(lineToDelete)) continue;
//                else {
//                    write.write(currentLine + System.getProperty("line.separator"));
//                }
//            }
//            write.close();
//            reader.close();
//            if(new File(pathName).delete()) {
//                tmp.renameTo(new File(pathName));
//                System.out.println("Delted tuple " + tuple.getTupleValues() + "successfully.");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


}
