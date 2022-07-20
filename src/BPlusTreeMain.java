import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main Application.
 */
public class BPlusTreeMain {

    public static void main(String[] args) {

        /** Read the input file -- input.txt */
        Scanner scan = null;
        try {
            scan = new Scanner(new File("input_test.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }

        /** Read the minimum degree of B+Tree first */

        int degree = scan.nextInt();

        BPlusTree bTree = new BPlusTree(degree);

        /** Reading the database student.csv into B+Tree Node*/
        List<Student> studentsDB = getStudents();
        int totalStudents = studentsDB.size();

        for (Student s : studentsDB) {
            bTree.insert(s);
        }

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
                            long recordID = Long.parseLong(s2.next());

                            Student s = new Student(studentId, age, studentName, major, level, recordID);
                            bTree.insert(s);
                            totalStudents++;

                            break;
                        }
                        case "delete": {
                            long studentId = Long.parseLong(s2.next());
                            boolean result = bTree.delete(studentId);
                            if (result)
                                System.out.println("Student deleted successfully.");
                            else
                                System.out.println("Student deletion failed.");

                            totalStudents--;
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
    }

    private static List<Student> getStudents() {

        List<Student> studentList = new ArrayList<>();

        try {
            Scanner scnr = new Scanner(new File("Student_test.csv"));

            while (scnr.hasNextLine()) {
                String line = scnr.nextLine();
                String[] tokens = line.split(",");
                // CSVFORMAT: StudentID, Name, Major, Level, age, recordNum
                Student student = new Student(Long.parseLong(tokens[0]), Integer.parseInt(tokens[4]), tokens[1], tokens[2], tokens[3], Long.parseLong(tokens[5]));
                studentList.add(student);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return studentList;
    }
}
