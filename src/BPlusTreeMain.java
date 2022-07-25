import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Random;

/**
 * Main Application.
 */
public class BPlusTreeMain {

    public static void main(String[] args) {

        /** Read the input file -- input.txt */
        Scanner scan = null;
        File csvFile = null;
        try {
            csvFile = new File("../Student_test.csv");
            scan = new Scanner(new File("../input_test.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }

        /** Read the minimum degree of B+Tree first */

        int degree = scan.nextInt();

        BPlusTree bTree = new BPlusTree(degree);

        /** Reading the database student.csv into B+Tree Node*/
        List<Student> studentsDB = getStudents(csvFile);
        int totalStudents = studentsDB.size();

        for (Student s : studentsDB) {
            bTree.insert(s);
        }

        Random rand = new Random();

        /** Start reading the operations now from input file*/
        try {
            while (scan.hasNextLine()) {
                Scanner s2 = new Scanner(scan.nextLine());

                while (s2.hasNext()) {

                    String operation = s2.next();
                    System.out.println(operation);

                    switch (operation) {
                        case "insert": {

                            long studentId = Long.parseLong(s2.next());
                            String studentName = s2.next() + " " + s2.next();
                            String major = s2.next();
                            String level = s2.next();
                            int age = Integer.parseInt(s2.next());
                            long recordID;
                            if (s2.hasNext()) {
                                recordID = Long.parseLong(s2.next());
                            }
                            else {
                                recordID = rand.nextInt(100);
                            }
                            Student s = new Student(studentId, age, studentName, major, level, recordID);
                            studentsDB.add(s);  // Add to List
                            bTree.insert(s);    // Add to tree
                            totalStudents++;    // Increment
                            // Add to CSV
                            addToCSV(csvFile, s);
                            break;
                        }
                        case "delete": {
                            long studentId = Long.parseLong(s2.next());
                            boolean result = bTree.delete(studentId);
                            if (result)
                                System.out.println("Student deleted successfully.");
                            else {
                                System.out.println("Student deletion failed.");
                                break;
                            }
                            // Remove student from CSV and List
                            removeFromCSV(csvFile, studentId);
                            studentsDB.removeIf(s -> (s.studentId == studentId));
                            totalStudents = studentsDB.size();
                            break;
                        }
                        case "search": {
                            long studentId = Long.parseLong(s2.next());
                            long recordID = bTree.search(studentId);
                            if (recordID != -1)
                                System.out.println("Student exists in the database at " + recordID);
                            else
                                System.out.println("Student does not exist.");
                            break;
                        }
                        case "print": {
                            List<Long> listOfRecordID = new ArrayList<>();
                            listOfRecordID = bTree.print();
                            System.out.println("List of recordIDs in B+Tree " + listOfRecordID.toString());
                            break;
                        }
                        default:
                            System.out.println("Wrong Operation");
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //BREAKPOINT HERE
        System.out.println("done");
    }

    private static List<Student> getStudents(File fp) {

        List<Student> studentList = new ArrayList<>();

        try {
            Scanner scnr = new Scanner(fp);

            while (scnr.hasNextLine()) {
                String line = scnr.nextLine();
                String[] tokens = line.split(",");
                // CSV FORMAT: StudentID, Name, Major, Level, age, recordNum
                Student student = new Student(Long.parseLong(tokens[0]), Integer.parseInt(tokens[4]), tokens[1], tokens[2], tokens[3], Long.parseLong(tokens[5]));
                studentList.add(student);
            }
            scnr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return studentList;
    }

    /**
     * Use with BPlusTree.insert()
     * Adds student to CSV and studentList
     * DOESN'T ACCOUNT FOR DUPLICATES
     */
    private static void addToCSV(File fp, Student s) {
        try {
            FileWriter fw = new FileWriter(fp, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            pw.println(s.studentId + "," + s.studentName + "," + s.major + "," + s.level + "," + s.age + ","
                    + s.recordId);
            pw.flush();
            pw.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Use with BPlusTree.delete()
     * Removes student from CSV
     * ACCOUNTS FOR DUPLICATES
     */
    private static void removeFromCSV(File fp, long studentId) {
        try {
            File new_file = new File("TEMPCSV.csv");
            if (new_file.exists())
                new_file.delete();

            FileWriter fw = new FileWriter(new_file, false);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            Scanner scan = new Scanner(fp);
            scan.useDelimiter("[,\n]");

            while (scan.hasNextLine()) {
                long currId = Long.parseLong(scan.next());
                if (currId != studentId) {
                    pw.println(currId + scan.nextLine());
                }
                else scan.nextLine();
            }
            scan.close();
            pw.flush();
            pw.close();

            //String csvName = fp.getPath();
            fp.delete();
            new_file.renameTo(fp);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
